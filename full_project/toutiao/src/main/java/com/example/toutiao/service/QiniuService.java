package com.example.toutiao.service;

import com.alibaba.fastjson.JSONObject;
import com.example.toutiao.util.ToutiaoUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
public class QiniuService {
    private static final Logger logger = LoggerFactory.getLogger(QiniuService.class);
    //
    String ACCESS_KEY = "mrrHPyFoNkRRJg79ZmFRP9QuI90htjC0eJhTTA27";
    String SECRET_KEY = "4Kj10Fv2EWAVk-lYNKg0kVzIAWZ99JVfz6CJPP-F";
    //
    String bucketname = "nowcoder";

    //
    Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    //
    UploadManager uploadManager = new UploadManager();

    private static String QINIU_IMAGE_DOMAIN = "pp6yh4e47.bkt.clouddn.com";

    //
    public String getUpToken() {
        return auth.uploadToken(bucketname);
    }

    public String saveImage(MultipartFile file) throws IOException {
        try {
            int dotPos = file.getOriginalFilename().lastIndexOf(".");
            if (dotPos < 0) {
                return null;
            }
            String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
            if (!ToutiaoUtil.isFileAllowed(fileExt)) {
                return null;
            }

            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
            //
            Response res = uploadManager.put(file.getBytes(), fileName, getUpToken());
            //
            if (res.isOK() && res.isJson()) {
                return ToutiaoUtil.QINIU_DOMAIN + JSONObject.parseObject(res.bodyString()).get("key");
            } else {
                logger.error("七牛异常:" + res.bodyString());
                return null;
            }
        } catch (QiniuException e) {
            //
            logger.error("七牛异常" + e.getMessage());
            return null;
        }
    }

}
