package com.xiaoRed.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaoRed.entity.dto.ImageStore;
import com.xiaoRed.mapper.ImageStoreMapper;
import com.xiaoRed.service.ImageStoreService;
import org.springframework.stereotype.Service;

/**
 * (ImageStore)表服务实现类
 *
 * @author makejava
 * @since 2023-10-14 10:54:10
 */
@Service("imageStoreService")
public class ImageStoreServiceImpl extends ServiceImpl<ImageStoreMapper, ImageStore> implements ImageStoreService {

}

