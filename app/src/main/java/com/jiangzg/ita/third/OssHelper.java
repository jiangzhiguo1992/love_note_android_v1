package com.jiangzg.ita.third;

import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.OssInfo;
import com.jiangzg.ita.helper.PrefHelper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by JZG on 2018/3/16.
 * 阿里Oss管理类
 */
public class OssHelper {
    // todo 1.加载前看看oss的token是否快过期

    // oos 对象
    private static OSS ossClient;
    private static String bucket;
    public static String endpoint;
    private static long urlExpire = 60 * 10; // 10分钟

    /**
     * 刷新ossClient
     */
    public static void refreshOssClient() {
        OssInfo ossInfo = PrefHelper.getOssInfo();
        bucket = ossInfo.getBucket();
        endpoint = ossInfo.getEndpoint();
        //String endpoint = "http://" + ossInfo.getRegion() + ".aliyuncs.com";

        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider
                (ossInfo.getAccessKeyId(), ossInfo.getAccessKeySecret(), ossInfo.getSecurityToken());

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // connction time out default 15s
        conf.setSocketTimeout(15 * 1000); // socket timeout，default 15s
        conf.setMaxConcurrentRequest(10); // synchronous request number，default 5
        conf.setMaxErrorRetry(10); // retry，default 2
        //OSSLog.enableLog(); //write local log file ,path is SDCard_path\OSSLog\logs.csv

        ossClient = new OSSClient(MyApp.get(), ossInfo.getEndpoint(), credentialProvider, conf);
    }

    public static String getUrl(final String objKey) {
        try {
            return ossClient.presignConstrainedObjectURL(bucket, objKey, urlExpire);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return "";
    }

    // task.cancel(); // 可以取消任务
    // task.waitUntilFinished(); // 等待直到任务完成
    // GetObjectResult result = task.getResult(); // 阻塞等待结果返回
    public static void uploadObject(String objectKey, String sourcePath) {
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucket, objectKey, sourcePath);

        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        OSSAsyncTask task = ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }

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
                Log.d("Content-Length", "" + result.getContentLength());

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
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });

    }

}
