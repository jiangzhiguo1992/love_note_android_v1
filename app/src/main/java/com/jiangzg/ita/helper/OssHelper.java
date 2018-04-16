package com.jiangzg.ita.helper;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
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
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.OssInfo;

import java.io.File;
import java.io.InputStream;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by JZG on 2018/3/16.
 * 阿里Oss管理类
 */
public class OssHelper {

    private static final String LOG_TAG = "OssHelper";

    // oos 对象
    private static OSS ossClient;
    private static String bucket;

    /**
     * 刷新ossClient
     */
    public static void refreshOssClient() {
        OssInfo ossInfo = SPHelper.getOssInfo();
        bucket = ossInfo.getBucket();
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

    public interface OssUploadCallBack {
        void success(String ossPath);

        void failure(String ossPath);
    }

    public interface OssDownloadCallBack {
        void success(String ossPath);

        void failure(String ossPath);
    }

    // 墙纸
    public static void uploadWall(Activity activity, File source, final OssUploadCallBack callBack) {
        // ossPath
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathCoupleWall = ossInfo.getPathCoupleWall();
        if (StringUtils.isEmpty(pathCoupleWall)) {
            ToastUtils.show(MyApp.get().getString(R.string.access_resource_path_no_exists));
            LogUtils.w(LOG_TAG, "uploadWall: pathCoupleWall == null");
            // 回调
            if (callBack != null) {
                MyApp.get().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failure("");
                    }
                });
            }
            return;
        }
        // objectKey
        final String objectKey = pathCoupleWall + DateUtils.getCurrentString(ConstantUtils.FORMAT_CHINA_Y_M_D__H_M_S_S) + ".jpeg";
        // 不压缩 直接上传
        uploadObject(activity, objectKey, source, true, callBack);
    }

    // 意见
    public static void uploadSuggest(Activity activity, File source, boolean delSource, final OssUploadCallBack callBack) {
        // ossPath
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathSuggest = ossInfo.getPathSuggest();
        if (StringUtils.isEmpty(pathSuggest)) {
            ToastUtils.show(MyApp.get().getString(R.string.access_resource_path_no_exists));
            LogUtils.w(LOG_TAG, "uploadSuggest: pathSuggest == null");
            // 回调
            if (callBack != null) {
                MyApp.get().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failure("");
                    }
                });
            }
            return;
        }
        // 先压缩 再上传
        compressJpeg(activity, pathSuggest, source, delSource, callBack);
    }

    // 头像
    public static void uploadAvatar(Activity activity, File source, final OssUploadCallBack callBack) {
        // ossPath
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathCoupleAvatar = ossInfo.getPathCoupleAvatar();
        if (StringUtils.isEmpty(pathCoupleAvatar)) {
            ToastUtils.show(MyApp.get().getString(R.string.access_resource_path_no_exists));
            LogUtils.w(LOG_TAG, "uploadAvatar: pathCoupleAvatar == null");
            // 回调
            if (callBack != null) {
                MyApp.get().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failure("");
                    }
                });
            }
            return;
        }
        // 先压缩 再上传
        compressJpeg(activity, pathCoupleAvatar, source, true, callBack);
    }

    // 启动压缩
    private static void compressJpeg(final Activity activity, final String uploadPath, final File source,
                                     final boolean delSource, final OssUploadCallBack callBack) {
        // objectKey
        final String objectKey = uploadPath + DateUtils.getCurrentString(ConstantUtils.FORMAT_CHINA_Y_M_D__H_M_S_S) + ".jpeg";
        // file
        if (FileUtils.isFileEmpty(source)) {
            ToastUtils.show(MyApp.get().getString(R.string.upload_file_no_exists));
            LogUtils.w(LOG_TAG, "uploadWallPaper: source == null");
            // 删除异常源文件
            if (delSource) ResHelper.deleteFileInBackground(source);
            // 回调
            if (callBack != null) {
                MyApp.get().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failure(objectKey);
                    }
                });
            }
            return;
        }
        // dialog
        final MaterialDialog progress = DialogHelper.getBuild(activity)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .content(R.string.image_is_compress)
                .progress(true, 0)
                .progressIndeterminateStyle(false)
                .build();
        DialogHelper.setAnim(progress);
        // compress
        Luban.get(MyApp.get())
                .load(source) // 压缩源文件
                .putGear(Luban.THIRD_GEAR) // 设定压缩档次，默认三挡
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        DialogHelper.show(progress);
                    }

                    @Override
                    public void onSuccess(File file) {
                        DialogHelper.dismiss(progress);
                        if (FileUtils.isFileExists(file)) {
                            // 压缩文件有可能不存在
                            if (!file.getAbsolutePath().trim().equals(source.getAbsolutePath().trim())) {
                                // 压缩文件 != 源文件，删除源文件
                                if (delSource) ResHelper.deleteFileInBackground(source);
                            }
                            // upload
                            uploadObject(activity, objectKey, file, delSource, callBack);
                        } else {
                            // upload
                            uploadObject(activity, objectKey, source, delSource, callBack);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(LOG_TAG, "Luban: onError: ", e);
                        DialogHelper.dismiss(progress);
                        // upload
                        uploadObject(activity, objectKey, source, delSource, callBack);
                    }
                })
                .launch();
    }

    // apk
    public static void downloadApk(Activity activity, String objectKey, File target, OssDownloadCallBack callBack) {
        // 直接下载
        downloadObject(activity, objectKey, target, callBack);
    }

    // 墙纸
    public static void downloadWall() {

    }

    // 全屏图
    public static void downloadScreen() {

    }

    // 上传任务
    private static OSSAsyncTask uploadObject(Activity activity, final String objectKey, final File source,
                                             final boolean delSource, final OssUploadCallBack callBack) {
        LogUtils.i(LOG_TAG, "uploadImage: objectKey == " + objectKey);
        // objectKey
        if (StringUtils.isEmpty(objectKey)) {
            ToastUtils.show(MyApp.get().getString(R.string.access_resource_path_no_exists));
            LogUtils.w(LOG_TAG, "uploadObject: objectKey == null");
            // 删除上传的文件
            if (delSource) ResHelper.deleteFileInBackground(source);
            // 回调
            if (callBack != null) {
                MyApp.get().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failure(objectKey);
                    }
                });
            }
            return null;
        }
        // file
        if (FileUtils.isFileEmpty(source)) {
            ToastUtils.show(MyApp.get().getString(R.string.upload_file_no_exists));
            LogUtils.w(LOG_TAG, "uploadObject: source == null");
            // 上传上传的异常文件
            if (delSource) ResHelper.deleteFileInBackground(source);
            // 回调
            if (callBack != null) {
                MyApp.get().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failure(objectKey);
                    }
                });
            }
            return null;
        }
        // dialog
        final MaterialDialog progress = DialogHelper.getBuild(activity)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .content(R.string.are_upload)
                .progress(false, 100)
                .negativeText(R.string.cancel_upload)
                .build();
        DialogHelper.showWithAnim(progress);
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucket, objectKey, source.getAbsolutePath());
        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                //LogUtils.d(LOG_TAG, "uploadObject: currentSize: " + currentSize + " totalSize: " + totalSize);
                if (progress != null && progress.isShowing()) {
                    int percent = (int) (((float) currentSize / (float) totalSize) * 100);
                    progress.setProgress(percent);
                }
            }
        });
        // 开始任务
        final OSSAsyncTask task = ossClient.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                DialogHelper.dismiss(progress);
                // 删除源文件
                if (delSource) ResHelper.deleteFileInBackground(source);
                // 回调
                final String uploadKey = request.getObjectKey();
                LogUtils.i(LOG_TAG, "uploadObject: onSuccess: getObjectKey == " + uploadKey);
                if (callBack != null) {
                    MyApp.get().getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.success(uploadKey);
                        }
                    });
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                DialogHelper.dismiss(progress);
                // 删除源文件
                if (delSource) ResHelper.deleteFileInBackground(source);
                // 打印
                final String uploadKey = request.getObjectKey();
                LogUtils.i(LOG_TAG, "uploadObject: onFailure: getObjectKey == " + uploadKey);
                // 本地异常如网络异常等
                if (clientException != null) {
                    ToastUtils.show(MyApp.get().getString(R.string.upload_fail_please_check_native_net));
                    LogUtils.e(LOG_TAG, "", clientException);
                }
                // 服务异常
                if (serviceException != null) {
                    ToastUtils.show(MyApp.get().getString(R.string.upload_fail_tell_we_this_bug));
                    LogUtils.e(LOG_TAG, "", serviceException);
                    LogUtils.w(LOG_TAG, "RequestId: " + serviceException.getRequestId());
                    LogUtils.w(LOG_TAG, "ErrorCode: " + serviceException.getErrorCode());
                    LogUtils.w(LOG_TAG, "RawMessage: " + serviceException.getRawMessage());
                }
                // 回调
                if (callBack != null) {
                    MyApp.get().getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.failure(uploadKey);
                        }
                    });
                }
            }
        });
        // processDialog
        if (progress != null) {
            progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    LogUtils.d(LOG_TAG, "uploadObject: cancel");
                    taskCancel(task);
                }
            });
        }
        return task;
    }

    // 下载任务
    public static OSSAsyncTask downloadObject(Activity activity, final String objectKey, final File target, final OssDownloadCallBack callBack) {
        LogUtils.i(LOG_TAG, "downloadObject: objectKey == " + objectKey);
        // objectKey
        if (StringUtils.isEmpty(objectKey)) {
            LogUtils.w(LOG_TAG, "downloadObject: objectKey == null");
            // 删除下载文件
            ResHelper.deleteFileInBackground(target);
            ToastUtils.show(MyApp.get().getString(R.string.access_resource_path_no_exists));
            // 回调
            if (callBack != null) {
                MyApp.get().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failure(objectKey);
                    }
                });
            }
            return null;
        }
        // file
        if (!FileUtils.isFileExists(target)) {
            LogUtils.w(LOG_TAG, "downloadObject: source == null");
            // 删除下载文件
            ResHelper.deleteFileInBackground(target);
            ToastUtils.show(MyApp.get().getString(R.string.save_file_no_exists));
            // 回调
            if (callBack != null) {
                MyApp.get().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failure(objectKey);
                    }
                });
            }
            return null;
        }
        // dialog
        final MaterialDialog progress = DialogHelper.getBuild(activity)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .content(R.string.are_download)
                .progress(false, 100)
                .negativeText(R.string.cancel_download)
                .build();
        DialogHelper.showWithAnim(progress);
        // 构造下载文件请求，不是临时url，用key和secret访问，不用签名
        GetObjectRequest get = new GetObjectRequest(bucket, objectKey);
        // 异步下载时可以设置进度回调
        get.setProgressListener(new OSSProgressCallback<GetObjectRequest>() {
            @Override
            public void onProgress(GetObjectRequest request, long currentSize, long totalSize) {
                //LogUtils.d(LOG_TAG, "downloadObject: currentSize: " + currentSize + " totalSize: " + totalSize);
                if (progress != null && progress.isShowing()) {
                    int percent = (int) (((float) currentSize / (float) totalSize) * 100);
                    progress.setProgress(percent);
                }
            }
        });
        // 开始任务
        final OSSAsyncTask task = ossClient.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                final String downloadKey = request.getObjectKey();
                LogUtils.i(LOG_TAG, "downloadObject: onSuccess: getObjectKey == " + downloadKey);
                LogUtils.i(LOG_TAG, "downloadObject: onSuccess: Content-Length == " + result.getContentLength());
                // 开始解析文件
                InputStream inputStream = result.getObjectContent();
                boolean ok = FileUtils.writeFileFromIS(target, inputStream, false);
                // 对话框
                DialogHelper.dismiss(progress);
                // 回调
                if (ok) {
                    // 解析成功
                    if (callBack != null) {
                        MyApp.get().getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.success(downloadKey);
                            }
                        });
                    }
                } else {
                    // toast
                    ToastUtils.show(MyApp.get().getString(R.string.file_resolve_fail_tell_we_this_bug));
                    // 删除源文件
                    ResHelper.deleteFileInBackground(target);
                    // 解析失败
                    if (callBack != null) {
                        MyApp.get().getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.failure(downloadKey);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientException, ServiceException serviceException) {
                DialogHelper.dismiss(progress);
                // 删除源文件
                ResHelper.deleteFileInBackground(target);
                // 打印
                final String downloadKey = request.getObjectKey();
                LogUtils.i(LOG_TAG, "downloadObject: onFailure: getObjectKey == " + downloadKey);
                // 本地异常如网络异常等
                if (clientException != null) {
                    ToastUtils.show(MyApp.get().getString(R.string.download_fail_please_check_native_net));
                    LogUtils.e(LOG_TAG, "", clientException);
                }
                // 服务异常
                if (serviceException != null) {
                    ToastUtils.show(MyApp.get().getString(R.string.download_fail_tell_we_this_bug));
                    LogUtils.e(LOG_TAG, "", serviceException);
                    LogUtils.w(LOG_TAG, "RequestId: " + serviceException.getRequestId());
                    LogUtils.w(LOG_TAG, "ErrorCode: " + serviceException.getErrorCode());
                    LogUtils.w(LOG_TAG, "RawMessage: " + serviceException.getRawMessage());
                }
                // 回调
                if (callBack != null) {
                    MyApp.get().getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.failure(downloadKey);
                        }
                    });
                }
            }
        });
        // processDialog
        if (progress != null) {
            progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    LogUtils.d(LOG_TAG, "downloadObject: cancel");
                    // 删除下载文件
                    ResHelper.deleteFileInBackground(target);
                    // 取消任务
                    taskCancel(task);
                }
            });
        }
        return task;
    }

    private static void taskCancel(OSSAsyncTask task) {
        if (task != null && !task.isCanceled() && !task.isCompleted()) {
            task.cancel();
        }
    }

}
