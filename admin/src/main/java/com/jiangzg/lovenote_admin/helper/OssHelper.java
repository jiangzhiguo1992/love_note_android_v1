package com.jiangzg.lovenote_admin.helper;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.base.MyApp;
import com.jiangzg.lovenote_admin.domain.OssInfo;

/**
 * Created by JZG on 2018/3/16.
 * 阿里Oss管理类
 */
public class OssHelper {

    // oos 对象
    private static OSS ossClient;

    /**
     * 刷新ossClient
     */
    public static void refreshOssClient() {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String expireTime = DateUtils.getStr(ossInfo.getStsExpireTime() * 1000, DateUtils.FORMAT_LINE_M_D_H_M);
        LogUtils.i(OssHelper.class, "refreshOssClient", "sts将在 " + expireTime + " 过期");
        String bucket = ossInfo.getBucket();
        String accessKeyId = ossInfo.getAccessKeyId();
        String accessKeySecret = ossInfo.getAccessKeySecret();
        String securityToken = ossInfo.getSecurityToken();
        // oss信息 也可用OSSFederationCredentialProvider来实现动态更新
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken);
        // oss配置
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000);  // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(5); // 失败后最大重试次数，默认2次
        //OSSLog.enableLog(); // 写入日志文件, 路径SDCard_path\OSSLog\logs.csv
        // oss客户端
        ossClient = new OSSClient(MyApp.get(), ossInfo.getDomain(), credentialProvider, conf);
    }

    private static OSS getOssClient() {
        if (ossClient == null) {
            refreshOssClient();
        }
        return ossClient;
    }

    // 获取obj的访问url
    public static String getUrl(final String objKey) {
        if (StringUtils.isEmpty(objKey)) {
            LogUtils.w(OssHelper.class, "getUrl", "objKey == null");
            return "";
        }
        try {
            OssInfo ossInfo = SPHelper.getOssInfo();
            String bucket = ossInfo.getBucket();
            long urlExpireTime = SPHelper.getOssInfo().getUrlExpireSec() * 1000;
            // 十分钟过期时间
            String url = getOssClient().presignConstrainedObjectURL(bucket, objKey, urlExpireTime);
            LogUtils.d(OssHelper.class, "getUrl", url);
            return url;
        } catch (ClientException e) {
            LogUtils.e(OssHelper.class, "getUrl", e);
        }
        return "";
    }

}
