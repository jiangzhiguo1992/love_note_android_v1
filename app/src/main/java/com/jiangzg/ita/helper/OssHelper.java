package com.jiangzg.ita.helper;

import android.content.DialogInterface;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.OssInfo;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by JZG on 2018/3/16.
 * 阿里Oss管理类
 */
public class OssHelper {

    private static final String LOG_TAG = "OssHelper";

    // oos 对象
    private static OSS ossClient;
    public static String bucket;
    public static String endpoint;

    /**
     * 刷新ossClient
     */
    public static void refreshOssClient() {
        OssInfo ossInfo = SPHelper.getOssInfo();
        bucket = ossInfo.getBucket();
        endpoint = ossInfo.getEndpoint();
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
        ossClient = new OSSClient(MyApp.get(), ossInfo.getEndpoint(), credentialProvider, conf);
    }

    // 获取obj的访问url
    public static String getUrl(final String objKey) {
        if (StringUtils.isEmpty(objKey)) {
            LogUtils.w(LOG_TAG, "getUrl: objKey == null");
            return "";
        }
        try {
            // 十分钟过期时间
            String url = ossClient.presignConstrainedObjectURL(bucket, objKey, 60 * 10);
            LogUtils.i(LOG_TAG, "getUrl: " + url);
            return url;
        } catch (ClientException e) {
            LogUtils.e(LOG_TAG, "refreshOssClient", e);
        }
        return "";
    }

    public interface OssCallBack {
        void success(String loadPath);

        void failure(String errorPath);
    }

    public static OSSAsyncTask uploadAvatar(MaterialDialog process, String sourcePath, final OssCallBack callBack) {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathCoupleAvatar = ossInfo.getPathCoupleAvatar();
        if (StringUtils.isEmpty(pathCoupleAvatar)) return null;
        String path = pathCoupleAvatar + DateUtils.getCurrentString(ConstantUtils.FORMAT_CHINA_Y_M_D_H_M_S_S);
        return uploadImage(process, path, sourcePath, callBack);
    }

    // 上传任务
    public static OSSAsyncTask uploadImage(final MaterialDialog process, String objectKey, String sourcePath, final OssCallBack callBack) {
        DialogHelper.show(process);

        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucket, objectKey, sourcePath);

        //ObjectMetadata metadata = new ObjectMetadata();
        //// 指定Content-Type
        //metadata.setContentType("application/octet-stream");
        //// user自定义metadata
        //metadata.addUserMetadata("x-oss-meta-name1", "value1");
        //put.setMetadata();

        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                LogUtils.d(LOG_TAG, "uploadImage: currentSize: " + currentSize + " totalSize: " + totalSize);
                if (process != null && process.isShowing()) {
                    int percent = (int) (((float) currentSize / (float) totalSize) * 100);
                    process.setProgress(percent);
                }
            }
        });
        final OSSAsyncTask task = ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                if (process != null && process.isShowing()) {
                    process.dismiss();
                }
                String uploadKey = request.getObjectKey();
                LogUtils.i(LOG_TAG, "uploadImage: onSuccess: getObjectKey == " + uploadKey);
                if (callBack != null) {
                    callBack.success(uploadKey);
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                if (process != null && process.isShowing()) {
                    process.dismiss();
                }
                String uploadKey = request.getObjectKey();
                LogUtils.i(LOG_TAG, "uploadImage: onFailure: getObjectKey == " + uploadKey);
                if (callBack != null) {
                    callBack.failure(uploadKey);
                }
                // 本地异常如网络异常等
                LogUtils.e(LOG_TAG, "", clientException);
                LogUtils.e(LOG_TAG, "", serviceException);
                // 服务异常
                if (serviceException != null) {
                    // todo toast
                    LogUtils.w("ErrorCode", serviceException.getErrorCode());
                    LogUtils.w("RequestId", serviceException.getRequestId());
                    LogUtils.w("HostId", serviceException.getHostId());
                    LogUtils.w("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        // processDialog
        if (process != null) {
            process.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    LogUtils.d(LOG_TAG, "uploadImage: cancel");
                    taskCancel(task);
                }
            });
        }
        return task;
    }

    // todo
    // task.cancel(); // 可以取消任务
    // task.waitUntilFinished(); // 等待直到任务完成
    // GetObjectResult result = task.getResult(); // 阻塞等待结果返回
    public static void downloadObject(String objectKey) {
        // 构造下载文件请求
        GetObjectRequest get = new GetObjectRequest(bucket, objectKey);

        OSSAsyncTask task = ossClient.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                LogUtils.d("Content-Length", "" + result.getContentLength());

                InputStream inputStream = result.getObjectContent();

                byte[] buffer = new byte[2048];
                int len;

                try {
                    while ((len = inputStream.read(buffer)) != -1) {
                        // 处理下载的数据
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    LogUtils.w("ErrorCode", serviceException.getErrorCode());
                    LogUtils.w("RequestId", serviceException.getRequestId());
                    LogUtils.w("HostId", serviceException.getHostId());
                    LogUtils.w("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }

    public static void taskCancel(OSSAsyncTask task) {
        if (task != null && !task.isCanceled() && !task.isCompleted()) {
            task.cancel();
        }
    }

}
