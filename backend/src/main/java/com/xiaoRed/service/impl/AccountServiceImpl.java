package com.xiaoRed.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaoRed.constants.Const;
import com.xiaoRed.entity.dto.Account;
import com.xiaoRed.entity.dto.AccountDetails;
import com.xiaoRed.entity.dto.AccountPrivacy;
import com.xiaoRed.entity.vo.request.*;
import com.xiaoRed.mapper.AccountDetailsMapper;
import com.xiaoRed.mapper.AccountMapper;
import com.xiaoRed.mapper.AccountPrivacyMapper;
import com.xiaoRed.service.AccountService;
import com.xiaoRed.utils.FlowUtil;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * (Account)表服务实现类
 *
 * @author makejava
 * @since 2023-08-09 10:12:45
 */
@Service("accountService")
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Resource
    AmqpTemplate amqpTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    AccountPrivacyMapper privacyMapper;

    @Resource
    AccountDetailsMapper detailsMapper;

    @Resource
    FlowUtil flowUtil;

    @Resource
    PasswordEncoder encoder;

    /**
     *实现loadUserByUsername方法
     *  1. 从数据库查询用户信息（登录功能）
     *  2. 如果查到，就再去查询对应的权限信息（授权功能）
     *  3. 封装成UserDetails对象返回（这要求再去创建一个UserDetails的实现类）
     *  注意：由于实现通过用户名/邮箱登录，因此这里传进来的username参数实际上有可能是邮箱
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = findAccountByNameOrEmail(username);
        if (account == null)
            throw new UsernameNotFoundException("用户名或密码错误");
        //这个User是SpringSecurity提供的
        /* 这里也可以专门封装一个LoginAccount实体类实现UserDetails，
        User要作为这个实体类的属性成员，在下面返回时返回一个LoginAccount对象*/
        return User
                //实际上，放在这个User的username里的可能是Account的username，也可能是email
                .withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    /**
     * 通过用户名/邮箱找到数据库中对应的用户，提供给上面的loadUserByUsername方法
     */
    public Account findAccountByNameOrEmail(String text){
        return this.query()
                .eq("username", text)
                .or()
                .eq("email", text)
                .one();
    }

    /**
     * 发送邮箱验证码
     * @param type 判断在哪个场景下发送验证码：注册邮箱，重置密码，修改邮箱，...
     * @param email 将验证码发送给哪个邮箱
     * @param ip 不能一直请求，需要记录ip地址限制请求频率
     * @return 返回null表示发送成功
     */
    @Override
    public String sendEmailVerifyCode(String type, String email, String ip) {
        //加一把锁，防止同一时间被多次调用，保证同一个IP发送的请求需要排队
        synchronized (ip.intern()){
            if(!this.verifyLimit(ip))//verifyLimit方法返回false，说明该ip已经被限流了
                return "请求频繁，请稍后再试";
            //生成6位验证码
            Random random =new Random();
            int code = random.nextInt(899999) + 100000;//这样保证生成的code一定是6位数
            //将"类型，收件人，验证码"分装在一个map中，作为消息，发送到"mail"消息队列中
            Map<String, Object> data = Map.of("type", type, "email", email, "code", code);
            amqpTemplate.convertAndSend("mail", data);
            //将key前缀:目标邮箱作为key，验证码作为value存入redis，设置3分钟有效
            //后续用户提交填写的验证码，就是和这个redis中的做比较
            stringRedisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_DATA + email, String.valueOf(code), 3, TimeUnit.MINUTES);
            return null;
        }
    }

    /**
     * 注册功能：需要检查验证码是否正确以及邮箱、用户名是否已被注册
     * @param emailRegisterVo 前端注册表单提交的信息封装为vo发过来
     * @return null表示注册成功，否则返回错误信息
     */
    @Override
    public String registerEmailAccount(EmailRegisterVo emailRegisterVo) {
        String username = emailRegisterVo.getUsername();
        String email = emailRegisterVo.getEmail();
        String code = stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA + email);
        if (code == null) return "请先获取验证码";
        if (!code.equals(emailRegisterVo.getCode())) return "验证码错误，请重新输入";
        if (this.existsAccountByEmail(email)) return "此电子邮箱已被其他用户注册，请更换一个新的电子邮箱";
        if (this.existsAccountByUsername(username)) return "此用户名已被其他用户注册，请更换一个新的用户名";
        String password = encoder.encode(emailRegisterVo.getPassword());//密码需要加密后才能存入数据库
        //id已经设置过自动递增，因此传null即可；角色默认就是user
        Account account = new Account(null, username, password, email, "user", null, new Date());
        if (this.save(account)) {
            //注册成功后，redis中对应的那个验证码就没用了，手动删除
            stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email);
            //由于用户详细信息，隐私与用户是强相关的，如果不在注册的时候给默认数据，会有问题（注册后，各种功能都用不了）
            privacyMapper.insert(new AccountPrivacy(account.getId()));
            AccountDetails details = new AccountDetails();
            details.setId(account.getId());
            detailsMapper.insert(details);
            return null;
        }else {
            return "内部错误，请联系管理员";
        }
    }

    /**
     * 重置密码第一步：校验验证码，验证码通过才能进行第二步的重置密码
     * @param  confirmResetVo 将前端请求携带的的邮箱，验证码封装为vo
     * @return 返回null表示校验通过，否则返回错误信息
     */
    @Override
    public String resetCodeConfirm(ConfirmResetVo confirmResetVo) {
        String email = confirmResetVo.getEmail();
        String code = stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA + email);
        if (code == null) return "请先获取验证码";
        if (!code.equals(confirmResetVo.getCode())) return "验证码错误，请重新输入";
        return null;
    }

    /**
     * 重置密码第二步：提交重置的密码，更新数据库。为了防止被人跳过第一步来直接重置，这里依然要对验证码再验证一次
     * @param resetPawVo 将前端请求携带的新密码以及上一步用到的邮箱，验证码封装为vo
     * @return 返回null表示重置成功，否则返回错误信息
     */
    @Override
    public String resetPassword(ResetPawVo resetPawVo) {
        String email = resetPawVo.getEmail();
        //即使第一步校验过验证码了，但还是要对验证码再校验一次
        String verify = this.resetCodeConfirm(new ConfirmResetVo(email, resetPawVo.getCode()));
        if (verify != null) return verify;
        String password = encoder.encode(resetPawVo.getPassword());//密码加密后才能存储到数据库
        boolean update = this.update().eq("email", email).set("password", password).update();
        if (update){
            //更新数据库成功了，表明重置成功，对应的验证码没用了，手动从redis删除
            stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email);
        }
        return null;
    }

    /**
     * 修改账户绑定的电子邮箱功能
     * @param id 执行此操作的账号id
     * @param vo 前端传来的新电子邮箱和验证码封装为vo
     * @return 返回null表示修改成功，修改失败则返回错误信息提示
     */
    @Override
    public String modifyEmail(int id, ModifyEmailVo vo){
        String code = stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA + vo.getEmail()); //拿到redis中存的验证码
        if(code == null) return "请先获取验证码";
        if(!code.equals(vo.getCode())) return "验证码错误，请重新输入";
        Account account = findAccountByNameOrEmail(vo.getEmail());
        if(account!=null && account.getId() != id) return "此电子邮箱已被其他用户使用，请更换一个新的电子邮箱";
        boolean update = this.update()
                .set("email", vo.getEmail())
                .eq("id", id)
                .update();
        if (update){
            //更新数据库成功了，表明修改成功，对应的验证码没用了，手动从redis删除
            stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + vo.getEmail());
        }
        return null;
    }

    /**
     * 修改密码功能【和忘记密码的重置密码功能区分】
     * @param id 账号id
     * @param vo 前端传来的原密码，新密码封装为vo
     * @return 返回null说明修改成功，否则返回错误信息提示
     */
    @Override
    public String changePassword(int id, ChangePawVo vo){
        Account account = findAccountById(id);
        String password_db = account.getPassword(); //拿到数据库中存储的密码
        if (!encoder.matches(vo.getPassword_old(), password_db)) return "原密码不正确，重置密码失败"; //如果原密码匹配不上，则修改失败
        //将新密码加密存储到数据库中
        boolean isSuccess = this.update()
                .eq("id", id)
                .set("password", encoder.encode(vo.getPassword_new()))
                .update();
        return isSuccess ? null : "未知错误，请联系管理员";
    }

    /**
     * 根据用户id从数据库查询出对应用户
     * @param id 用户id
     */
    @Override
    public Account findAccountById(int id) {
        return this.query().eq("id", id).one();
    }

    /**
     * 针对IP地址进行邮件验证码获取限流
     * @param ip 请求的ip地址
     * @return 是否通过限流验证验证，false表示该ip在限流名单中，true表示尚未被限流
     */
    private boolean verifyLimit(String ip) {
        String key = Const.VERIFY_EMAIL_LIMIT + ip;
        return flowUtil.limitOnceCheck(key, 60);//限流的冷却时间设置为1分钟
    }

    /**
     * 判断用户名是否已被注册
     * @param username
     * @return true表示已被注册
     */
    private boolean existsAccountByUsername(String username) {
        return this.baseMapper.exists(Wrappers.<Account>query().eq("username", username));
    }

    /**
     * 判断电子邮箱是否已被注册
     * @param email
     * @return true表示已被注册
     */
    private boolean existsAccountByEmail(String email) {
        return this.baseMapper.exists(Wrappers.<Account>query().eq("email", email));
    }



}

