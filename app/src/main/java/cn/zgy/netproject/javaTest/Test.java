package cn.zgy.netproject.javaTest;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({Test.HttpOrHttps, Test.UploadFile, Test.DownloadFile})
@Retention(RetentionPolicy.SOURCE)
public @interface Test {

    /**
     * http/https请求
     */
    int HttpOrHttps = 1;

    /**
     * 文件上传
     */
    int UploadFile = 2;

    /**
     * 文件下载
     */
    int DownloadFile = 3;
}
