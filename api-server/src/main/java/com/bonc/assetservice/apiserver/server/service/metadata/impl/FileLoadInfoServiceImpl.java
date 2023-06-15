package com.bonc.assetservice.apiserver.server.service.metadata.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonc.assetservice.apiserver.server.service.metadata.IFileLoadInfoService;
import com.bonc.assetservice.metadata.entity.FileLoadInfo;
import com.bonc.assetservice.metadata.mapper.FileLoadInfoMapper;
import org.springframework.stereotype.Service;

/**
 * @Description: file_load_info
 * @Author: jeecg-boot
 * @Date:   2022-06-14
 * @Version: V1.0
 */
@Service
public class FileLoadInfoServiceImpl extends ServiceImpl<FileLoadInfoMapper, FileLoadInfo> implements IFileLoadInfoService {

}
