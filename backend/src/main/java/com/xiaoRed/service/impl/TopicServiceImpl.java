package com.xiaoRed.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaoRed.constants.Const;
import com.xiaoRed.entity.dto.*;
import com.xiaoRed.entity.vo.request.AddCommentVo;
import com.xiaoRed.entity.vo.request.TopicCreateVo;
import com.xiaoRed.entity.vo.request.TopicUpdateVo;
import com.xiaoRed.entity.vo.response.CommentVo;
import com.xiaoRed.entity.vo.response.TopicDetailVo;
import com.xiaoRed.entity.vo.response.TopicPreviewVo;
import com.xiaoRed.entity.vo.response.TopicTopVo;
import com.xiaoRed.mapper.*;
import com.xiaoRed.service.NotificationService;
import com.xiaoRed.service.TopicService;
import com.xiaoRed.utils.CacheUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {

    @Resource
    TopicTypeMapper topicTypeMapper;
    @Resource
    AccountMapper accountMapper;
    @Resource
    AccountDetailsMapper accountDetailsMapper;
    @Resource
    AccountPrivacyMapper accountPrivacyMapper;
    @Resource
    TopicCommentMapper topicCommentMapper;
    @Resource
    NotificationService notificationService;

    @Resource
    StringRedisTemplate template;
    @Resource
    CacheUtil cacheUtil;

    //service调用之前就先获取到所有帖子类型的id，方便后续的校验工作（比如createTopic方法，校验类型id是否合法）
    private Set<Integer> types = null;
    @PostConstruct
    private void initTypes(){
        types = this.listTypes()
                .stream()
                .map(TopicType::getId)
                .collect(Collectors.toSet());
    }

    /**
     * 返回所有帖子类型的id，类型名，描述，颜色。即de_topic_type表中的数据
     */
    @Override
    public List<TopicType> listTypes() {
        return topicTypeMapper.selectList(null);
    }

    /**
     * 发表帖子功能
     * 前置工作：对帖子类型id，长度做校验，并对请求做限流，限制发文频率
     * @param uid 帖子作者的id
     * @param vo 前端发送来的发表帖子参数包装为一个TopicCreateVo对象
     * @return 返回空表示创建成功，否则返回错误提示
     */
    @Override
    public String createTopic(int uid, TopicCreateVo vo){
        //验证帖子长度是否超出文章长度限制，文章最多20000字
        if(!contentLimitCheck(vo.getContent(), 20000))
            return "文章内容超出长度限制，发文失败！";
        //验证文章类型(用户正常操作肯定不会有问题的，因此触发这种情况的，一般都是故意来搞攻击的)
        if(!types.contains(vo.getType()))
            return "文章类型非法，发文失败";
        //todo:限制发文频率


        Topic topic = new Topic();
        BeanUtils.copyProperties(vo, topic); //将vo的属性拷贝给topic对应的同名属性
        topic.setContent(vo.getContent().toJSONString()); //content要先把json格式转换为字符串
        topic.setUid(uid);
        topic.setTime(new Date());
        if(this.save(topic)){
            //一旦有新帖子发布，有关帖子列表的所有缓存立即删除
            cacheUtil.deleteCachePattern(Const.FORUM_TOPIC_PREVIEW_CACHE + "*");
            return null;
        }else{
            return "内部错误，请联系管理员";
        }
    }

    /**
     * 修改文章
     * @param uid 当前用户id，用于校验，只能修改自己发表的帖子
     * @param vo 前端发送来的修改帖子参数包装为一个TopicUpdateVo对象
     */
    @Override
    public String updateTopic(int uid, TopicUpdateVo vo) {
        //验证文章长度是否超出长度限制，文章最多20000字
        if(!contentLimitCheck(vo.getContent(), 20000))
            return "文章内容超出长度限制，发文失败！";
        //验证文章类型(用户正常操作肯定不会有问题的，因此触发这种情况的，一般都是故意来搞攻击的)
        if(!types.contains(vo.getType()))
            return "文章类型非法，发文失败";

        baseMapper.update(null, Wrappers.<Topic>update()
                .eq("uid", uid)
                .eq("id", vo.getId())
                .set("content", vo.getContent().toString())
                .set("type", vo.getType())
        );
        return null;
    }

    /**
     * 发表评论
     * @param uid 发表评论的用户
     * @param vo 前端传来的添加评论的参数封装为vo
     */
    @Override
    public String createComment(int uid, AddCommentVo vo) {
        //todo:限制发评论的频率：1分钟只能发表两次评论

        //验证评论长度是否超出长度限制，评论最多2000字
        if(!contentLimitCheck(JSONObject.parseObject(vo.getContent()), 2000))
            return "评论内容超出长度限制，评论失败！";
        //封装并发表评论
        TopicComment comment = new TopicComment();
        BeanUtils.copyProperties(vo, comment); //把同名属性拷贝到comment
        comment.setUid(uid);
        comment.setTime(new Date());
        topicCommentMapper.insert(comment);
        //然后发送通知提醒
        Topic topic = baseMapper.selectById(vo.getTid()); //拿到这个被评论的文章
        Account account = accountMapper.selectById(uid); //拿到评论者
        if(vo.getQuote() > 0){ //如果发表的这条评论引用了其他评论
            TopicComment com = topicCommentMapper.selectById(vo.getQuote()); //获取被引用的评论
            if(Objects.equals(account.getId(), com.getUid())){ //如果被引用的评论不是自己的，则向被引用评论的用户发消息提醒
                notificationService.addNotification(
                        com.getUid(),
                        "您有新的评论回复",
                        account.getPassword() + "回复了你的评论，快去看看吧!",
                        "success",
                        "/index/topic-detail/"+com.getTid()
                );
            }else if(Objects.equals(account.getId(), topic.getUid())){ //如果发表此次评论的用户不是该帖的作者，则向该帖作者发送消息提醒
                notificationService.addNotification(
                        topic.getUid(),
                        "您的帖子有新评论",
                        account.getPassword() + "评论了你的帖子：" + topic.getTitle()+", 快去看看吧!",
                        "success",
                        "/index/topic-detail/"+topic.getId()
                );
            }
        }
        return null;
    }

    /**
     * 展示帖子评论
     * @param tid 帖子列表
     * @param pageNum 页号，mp分页器从0开始
     */
    @Override
    public List<CommentVo> comments(int tid, int pageNum) {
        Page<TopicComment> page = Page.of(pageNum, 10);
        topicCommentMapper.selectPage(page, Wrappers.<TopicComment>query().eq("tid", tid));
        return page.getRecords().stream().map(dto -> { //对于查询到的页中每一条TopicComment评论
            CommentVo vo = new CommentVo();
            BeanUtils.copyProperties(dto, vo); //先将同名属性拷贝到vo
            //如果该评论引用了其他评论，则需要获取到被引用评论的文本(精简版)，然后set到vo中
            if (dto.getQuote() > 0){
                TopicComment comment = topicCommentMapper.selectOne(Wrappers.<TopicComment>query()
                        .eq("id", dto.getQuote()).orderByAsc("time")); //先获取该评论引用的评论
                if(comment != null){ //如果能获取得到，那么正常进行后续操作
                    JSONObject object = JSONObject.parseObject(comment.getContent());
                    StringBuilder builder = new StringBuilder();
                    this.getBriefContent(object.getJSONArray("ops"), builder, ignore->{});
                    vo.setQuote(builder.toString());
                }else { //如果没有获取到，说明被引用的评论被删除了
                    vo.setQuote("此评论已被删除");
                }
            }
            //封装评论者的用户信息，和帖子详情相同，要考虑用户的隐私设置
            CommentVo.User user = new CommentVo.User();
            this.fillUserDetailsByPrivacy(user, dto.getUid());
            vo.setUser(user);
            //最终得到一条用于展示在前端的评论vo
            return vo;
        }).toList();
    }

    /**
     * 删除评论功能
     * @param id 评论id
     * @param uid 当前用户id，用于校验，只能删除自己的评论，不能删除别人的评论
     */
    @Override
    public void deleteComment(int id, int uid) {
        topicCommentMapper.delete(Wrappers.<TopicComment>query()
                .eq("id", id).eq("uid", uid));
    }

    /**
     * 根据选定查第几页和选定的帖子类型展示帖子列表
     * @param pageNum 展示的是第几页
     * @param type 展示的帖子类型，全选则为0
     */
    @Override
    public List<TopicPreviewVo> listTopicByPage(int pageNum, int type){
        String key = Const.FORUM_TOPIC_PREVIEW_CACHE + pageNum + ":" + type;
        List<TopicPreviewVo> previewVoList = cacheUtil.takeListFromCache(key, TopicPreviewVo.class); //先去缓存里拿
        if (previewVoList != null) return previewVoList; //从缓存中取到，就不用再去数据库里请求了

        Page<Topic> page = Page.of(pageNum, 10); //mybatis-plus的分页器页号是从1开始的
        if(type == 0)
            baseMapper.selectPage(page, Wrappers.<Topic>query().orderByDesc("time"));
        else
            baseMapper.selectPage(page, Wrappers.<Topic>query().eq("type", type).orderByDesc("time"));
        List<Topic> topics = page.getRecords();
        if(topics.isEmpty()) return null;
        previewVoList = topics.stream().map(this::resolveToPreview).toList(); //转化为TopicPreviewVo的列表
        cacheUtil.saveListToCache(key, previewVoList, 60); //拿到要求的帖子列表后，先存到缓存中
        return previewVoList;
    }

    /**
     * 展示置顶帖子
     * 帖子的top字段：0不置顶，1置顶
     */
    @Override
    public List<TopicTopVo> listTopTopics(){
        List<Topic> topics = baseMapper.selectList(Wrappers.<Topic>query()
                .select("id", "title", "time")
                .eq("top", 1));
        return topics.stream().map(topic ->{
            TopicTopVo vo = new TopicTopVo();
            BeanUtils.copyProperties(topic, vo);
            return vo;
        }).toList();
    }

    /**
     * 展示用户的收藏列表
     * @param uid 用户id
     */
    @Override
    public List<TopicPreviewVo> listTopicCollects(int uid) {
        return baseMapper.collectTopics(uid)
                .stream()
                .map(topic -> {
                   TopicPreviewVo vo = new TopicPreviewVo();
                   BeanUtils.copyProperties(topic, vo);
                   return vo;
                })
                .toList();
    }

    /**
     * 帖子详情
     * 帖子详情包含帖子信息和用户信息，不要联表查询，分开获取即可
     * @param uid 当前用户id
     * @param tid 帖子id
     */
    @Override
    public TopicDetailVo getTopic(int uid, int tid) {
        TopicDetailVo vo = new TopicDetailVo();
        //封装帖子详情的帖子相关信息
        Topic topic = baseMapper.selectById(tid);
        BeanUtils.copyProperties(topic, vo);
        TopicDetailVo.Interact interact = new TopicDetailVo.Interact(
                hasInteract(tid, uid, "like"),
                hasInteract(tid, uid, "collect")
        );
        vo.setInteract(interact);
        //封装帖子详情的用户相关信息
        TopicDetailVo.User user = new TopicDetailVo.User();
        vo.setUser(this.fillUserDetailsByPrivacy(user, topic.getUid()));
        return vo;
    }

    /**
     * 交互功能：点赞，收藏
     * 由于论坛交互数据（如点赞、收藏等）更新可能会非常频繁，更新信息实时到MySQL不太现实
     * 所以需要用Redis做缓冲并在合适的时机一次性入库一段时间内的数据
     * 当数据更新到来时，会创建一个新的定时任务，此任务会在一段时间之后执行，将全部Redis暂时缓存的信息一次性加入到数据库，从而缓解MySQL压力
     * 如果在定时任务已经设定期间，又有新的更新到来，仅更新Redis不创建新的延时任务
     * @param interact 交互操作信息
     * @param state 交互操作的状态：点赞/取消点赞，收藏/取消收藏
     */
    @Override
    public void interact(Interact interact, boolean state){
        String type = interact.getType();
        synchronized (type.intern()){ //数据入库过程中，不允许新数据到来(防止可能的数据丢失)
            //这里存到哈希表里，因为点赞/收藏无论外面点多少次，就只有两种结果，相同key的在哈希表中不断覆盖，就留下最终的那次即可
            template.opsForHash().put(type, interact.toKey(),Boolean.toString(state)); //键=type:tid:uid，键值=state
            this.saveInteractSchedule(type); //利用定时任务进行持久化
        }
    }

    private final Map<String, Boolean> state = new HashMap<>(); //记录该类型的任务是否已经开始计时了
    ScheduledExecutorService service = Executors.newScheduledThreadPool(2);
    /**
     * 定时任务
     */
    private void saveInteractSchedule(String type){
        //该类型的任务还没有开始计时/压根就没有这个任务，则创建定时任务
        if(!state.getOrDefault(type, false)){
            state.put(type, true);
            service.schedule(()->{
                this.saveInteract(type);
                state.put(type, false);
            }, 3, TimeUnit.SECONDS); //3s的定时任务
        }
    }

    /**
     * 交互信息持久化
     * 将缓存中点赞/收藏的交互信息一次性入库，将缓存中取消点赞/取消收藏的交互信息从数据库中一次性删除
     * @param type 本次持久化的交互信息的类型
     */
    private void saveInteract(String type){
        synchronized (type.intern()){ //数据入库过程中，不允许新数据到来(防止可能的数据丢失)
            List<Interact> checked =  new LinkedList<>(); //存储缓存中“勾选”的交互信息(即点赞/收藏这种正向的操作)
            List<Interact> cancel = new LinkedList<>(); //存储缓存中”取消“的交互信息
            template.opsForHash().entries(type).forEach((k, v) -> {
                if(Boolean.parseBoolean(v.toString())) //拿到是键值state为true，正向操作
                    checked.add(Interact.parseInteract(k.toString(), type));
                else
                    cancel.add(Interact.parseInteract(k.toString(), type));
            });
            if(!checked.isEmpty())
                baseMapper.addInteract(checked, type);
            if(!cancel.isEmpty())
                baseMapper.deleteInteract(cancel, type);
            template.delete(type);

        }

    }

    /**
     * 判断该用户对帖子是否有点赞/收藏
     * 因为有可能数据还没有入库，所以先从缓存里找，如果缓存有对应的key，则直接返回对应的键值
     * 缓存里没有key，才从数据库中找
     * @param tid 帖子id
     * @param uid 用户id
     * @param type 交互类型
     */
    private boolean hasInteract(int tid, int uid, String type){
        String key = tid + ":" + uid;
        //如果缓存里面有，则直接从缓存里拿
        if(template.opsForHash().hasKey(type, key)){
            return Boolean.parseBoolean(template.opsForHash().entries(type).get(key).toString());
        }
        return baseMapper.userInteractCount(tid, uid, type) > 0; //缓存里没有，才从数据库里拿
    }

    /**
     * 获得帖子详情需要的用户信息并返回
     * 注意：需要考虑用户的隐私设置
     * @param target 列表详情需要的用户信息
     * @param uid 用户id
     */
    private <T> T fillUserDetailsByPrivacy(T target, int uid){
        Account account = accountMapper.selectById(uid);
        AccountDetails details = accountDetailsMapper.selectById(uid);
        AccountPrivacy accountPrivacy = accountPrivacyMapper.selectById(uid);
        String[] ignores = accountPrivacy.hiddenFields(); //获得隐私设置中，需要隐藏的属性的字段名
        BeanUtils.copyProperties(account, target, ignores); //将同名属性的值拷贝给target，ignores中的字段将被忽略，不会被拷贝，起到隐私保护作用
        if (details!=null)
            BeanUtils.copyProperties(details, target, ignores); //将同名属性的值拷贝给target，ignores中的字段将被忽略，不会被拷贝，起到隐私保护作用
        return target;
    }

    /**
     * 校验文本内容长度是否超出限制
     * @param max 长度限制
     * @param content 前端发送过来的帖子内容，其实帖子真正的文本存储在ops这个JSON数组下的insert字段
     * @return 返回true表示通过校验，返回false表示超出长度限制，校验不通过
     */
    private boolean contentLimitCheck(JSONObject content, int max){
        if(content==null) return false;
        long length = 0;
        for(Object op : content.getJSONArray("ops")){ //取出ops这个JSON数组
            length += JSONObject.from(op).getString("insert").length(); //从这个数组中拿到insert字段的内容，记录长度
            if(length>max) return false;
        }
        return true;
    }

    /**
     * 将Topic类型的对象转换为TopicPreviewVo类型的对象
     * 同名属性可以直接bean拷贝
     * 主要就是TopicPreviewVo的text属性和images属性
     * 帖子文本存储在Topic的Content属性的ops数组中的insert字段里，由于只是展示在列表中，只展示前300个字符即可
     * 图片存储在Topic的Content属性的ops数组中的insert字段下的image字段里
     */
    private TopicPreviewVo resolveToPreview(Topic topic){
        TopicPreviewVo vo = new TopicPreviewVo();
        BeanUtils.copyProperties(accountMapper.selectById(topic.getUid()), vo); //用户信息单独查到，把需要的复制给vo
        BeanUtils.copyProperties(topic, vo); //先把topic中的同名属性复制给vo
        vo.setLike(baseMapper.interactCount(topic.getId(), "like"));//点赞量
        vo.setCollect(baseMapper.interactCount(topic.getId(), "collect"));//收藏量
        List<String> images = new ArrayList<>();
        StringBuilder previewText = new StringBuilder();
        JSONArray ops = JSONObject.parseObject(topic.getContent()).getJSONArray("ops"); //先拿到ops数组
        this.getBriefContent(ops, previewText, obj->images.add(obj.toString()));
        //这里还要切割出前300个字符，因为之前取insert时，可能很长，直接就超300了
        vo.setText(previewText.length() > 300 ? previewText.substring(0, 300) : previewText.toString());
        vo.setImages(images);
        return vo;
    }

    /**
     * 根据文本获取其精简版，前300个字
     * @param ops 从富文本编辑器拿到的文本
     * @param previewText 最终构造出来的精简版本
     * @param imageHandler 帖子列表的vo需要展示图片，要用到
     */
    private void getBriefContent(JSONArray ops, StringBuilder previewText, Consumer<Object> imageHandler) {
        for (Object ob : ops) {
            Object insert = JSONObject.from(ob).get("insert");
            if (insert instanceof String text) { //如果insert里直接就是文本，那其实就是帖子内容
                if (previewText.length() >= 300) continue;
                previewText.append(text);
            } else if (insert instanceof Map<?, ?> map) { //如果insert里还是一个JSON(这里用Map代替，JSON本质就是一个map)，则大概率是图片
                //还是要判断一下，有image字段，才拿
                Optional.ofNullable(map.get("image"))
                        .ifPresent(imageHandler);
            }
        }
    }
}



