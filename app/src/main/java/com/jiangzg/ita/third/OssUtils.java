package com.jiangzg.ita.third;

import android.content.Context;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.jiangzg.ita.domain.OssInfo;

/**
 * Created by JZG on 2018/3/16.
 * 阿里Oss管理类
 */
public class OssUtils {
    // todo 1.加载前看看oss的token是否快过期

    // oos 对象
    private static OSS oss;

    /**
     * 初始化oos信息
     */
    private static void init(Context context, OssInfo ossInfo) {
        String endpoint = "http://" + ossInfo.getRegion() + ".aliyuncs.com";

        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider
                (ossInfo.getAccessKeyId(), ossInfo.getAccessKeySecret(), ossInfo.getSecurityToken());

        oss = new OSSClient(context, endpoint, credentialProvider);
    }

}
