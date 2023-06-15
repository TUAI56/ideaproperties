package com.bonc.assetservice.apiserver.server.assetquery.validator.validators;

import com.bonc.assetservice.apiserver.data.entity.ResourceInfo;
import com.bonc.assetservice.apiserver.server.assetquery.validator.exception.ValidateException;
import com.bonc.assetservice.apiserver.server.assetquery.vo.sub.FtpInfoVO;
import com.bonc.assetservice.apiserver.server.service.apiserver.IResourceInfoService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName FtpValidator
 * @Author 李维帅
 * @Date 2022/7/5 16:11
 * @Version 1.0
 **/
@Component
public class FtpValidator extends AbstracValidatorBase<FtpInfoVO>{

    private static final List<String> FILE_TYPES = Arrays.asList("txt", "csv");

    private static final List<String> SEPARATOR_TYPES = Arrays.asList(",", "|");

    @Resource
    private IResourceInfoService resourceInfoService;

    public void validate(FtpInfoVO ftpInfoVO) throws ValidateException {
        // 验证资源id 是否存在
        ResourceInfo resourceInfo = resourceInfoService.getById(ftpInfoVO.getResourceId());
        if (resourceInfo == null) {
            throw new ValidateException(String.format("资源id【%s】不存在", ftpInfoVO.getResourceId()));
        }

        //校验fileType的有效值
        if (!FILE_TYPES.contains(ftpInfoVO.getFileType())) {
            throw new ValidateException("不支持的fileType：" + ftpInfoVO.getFileType());
        }

        //校验separator的有效值
        if (!SEPARATOR_TYPES.contains(ftpInfoVO.getSeparator())) {
            throw new ValidateException("不支持的separator：" + ftpInfoVO.getSeparator());
        }

    }

}
