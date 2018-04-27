package com.jiangzg.mianmian.helper;

import android.app.Activity;
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
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.MyApp;
import com.jiangzg.mianmian.domain.OssInfo;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        ossClient = new OSSClient(MyApp.get(), ossInfo.getDomain(), credentialProvider, conf);
    }

    // 获取obj的访问url
    public static String getUrl(final String objKey) {
        if (ossClient == null) return "";
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
        void success(File source, String ossPath);

        void failure(File source, String errMsg);
    }

    public interface OssUploadsCallBack {
        void success(List<File> sourceList, List<String> ossPathList);

        void failure(List<File> sourceList, String errMsg);
    }

    public interface OssDownloadCallBack {
        void success(String ossPath);

        void failure(String ossPath);
    }

    // 墙纸 (裁剪)(本地有持久缓存，就不压缩了)
    public static void uploadWall(Activity activity, final File source, final OssUploadCallBack callBack) {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathCoupleWall = ossInfo.getPathCoupleWall();
        uploadJpeg(activity, pathCoupleWall, source, callBack);
    }

    // 意见 (压缩)
    public static void uploadSuggest(Activity activity, final File source, final OssUploadCallBack callBack) {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathSuggest = ossInfo.getPathSuggest();
        compressJpeg(activity, pathSuggest, source, callBack);
    }

    // 头像 (裁剪+压缩)
    public static void uploadAvatar(Activity activity, final File source, final OssUploadCallBack callBack) {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathCoupleAvatar = ossInfo.getPathCoupleAvatar();
        compressJpeg(activity, pathCoupleAvatar, source, callBack);
    }

    // 日记 (无)(一天一次，就不压缩了)
    public static void uploadDiary(Activity activity, final List<String> sourceList, final OssUploadsCallBack callBack) {
        final List<File> fileList = ConvertHelper.getFileListByPath(sourceList);
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathBookDiary = ossInfo.getPathBookDiary();
        uploadJpegs(activity, pathBookDiary, fileList, callBack);
    }

    // 耳语 (压缩)
    public static void uploadWhisper(Activity activity, final File source, final OssUploadCallBack callBack) {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathBookWhisper = ossInfo.getPathBookWhisper();
        compressJpeg(activity, pathBookWhisper, source, callBack);
    }

    // apk
    public static void downloadApk(Activity activity, String objectKey, File target, OssDownloadCallBack callBack) {
        downloadObject(activity, objectKey, target, callBack);
    }

    // TODO 墙纸
    public static void downloadWall() {

    }

    // TODO 全屏图
    public static void downloadScreen() {

    }

    /**
     * *****************************************单图上传*****************************************
     */
    // 启动压缩
    private static void compressJpeg(final Activity activity, final String ossDirPath,
                                     final File source, final OssUploadCallBack callBack) {
        // file
        if (FileUtils.isFileEmpty(source)) {
            ToastUtils.show(MyApp.get().getString(R.string.upload_file_no_exists));
            LogUtils.w(LOG_TAG, "compressJpeg: source == null");
            // 回调
            if (callBack != null) {
                MyApp.get().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failure(source, "");
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
        // compress
        Luban.get(MyApp.get())
                .load(source) // 压缩源文件
                .putGear(Luban.THIRD_GEAR) // 设定压缩档次，默认三挡
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        DialogHelper.showWithAnim(progress);
                    }

                    @Override
                    public void onSuccess(File file) {
                        DialogHelper.dismiss(progress);
                        if (FileUtils.isFileExists(file)) {
                            // 压缩文件有可能不存在
                            //if (!file.getAbsolutePath().trim().equals(source.getAbsolutePath().trim())) {
                            // 压缩后的文件 != 源文件，但是在内部cache中，交给系统和用户
                            //ResHelper.deleteFileInBackground(source);
                            //}
                            // upload
                            uploadJpeg(activity, ossDirPath, file, callBack);
                        } else {
                            // upload
                            uploadJpeg(activity, ossDirPath, source, callBack);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(LOG_TAG, "Luban: onError == ", e);
                        DialogHelper.dismiss(progress);
                        // upload
                        uploadJpeg(activity, ossDirPath, source, callBack);
                    }
                })
                .launch();
    }

    // 上传任务
    private static OSSAsyncTask uploadJpeg(Activity activity, final String ossDirPath,
                                           final File source, final OssUploadCallBack callBack) {
        return uploadObject(activity, ossDirPath, source, "jpeg", callBack);
    }

    // 上传任务
    private static OSSAsyncTask uploadObject(Activity activity, final String ossDirPath, final File source,
                                             String suffix, final OssUploadCallBack callBack) {
        LogUtils.i(LOG_TAG, "uploadObject: ossDirPath: " + ossDirPath);
        // ossDirPath
        if (StringUtils.isEmpty(ossDirPath)) {
            ToastUtils.show(MyApp.get().getString(R.string.access_resource_path_no_exists));
            LogUtils.w(LOG_TAG, "uploadObject: ossDirPath == null");
            // 回调
            if (callBack != null) {
                MyApp.get().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failure(source, "");
                    }
                });
            }
            return null;
        }
        // file
        if (FileUtils.isFileEmpty(source)) {
            ToastUtils.show(MyApp.get().getString(R.string.upload_file_no_exists));
            LogUtils.w(LOG_TAG, "uploadObject: source == null");
            // 回调
            if (callBack != null) {
                MyApp.get().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failure(source, "");
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
        // objectKey
        final String objectKey = getObjectKey(ossDirPath, suffix);
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
                // 回调
                final String uploadKey = request.getObjectKey();
                LogUtils.i(LOG_TAG, "uploadObject: onSuccess: getObjectKey == " + uploadKey);
                if (callBack != null) {
                    MyApp.get().getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.success(source, uploadKey);
                        }
                    });
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                DialogHelper.dismiss(progress);
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
                            callBack.failure(source, "");
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

    /**
     * *****************************************多图上传*****************************************
     */
    // 启动多张压缩
    private static void compressJpegs(final Activity activity, final String ossDirPath,
                                      final List<File> sourceList, final OssUploadsCallBack callBack) {
        MaterialDialog progress = DialogHelper.getBuild(activity)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .content(R.string.image_is_compress)
                .progress(true, 0)
                .progressIndeterminateStyle(false)
                .build();
        compressJpegs(activity, progress, ossDirPath, sourceList, 0, callBack);
    }

    private static void compressJpegs(final Activity activity, final MaterialDialog progress,
                                      final String ossDirPath, final List<File> sourceList, final int currentIndex,
                                      final OssUploadsCallBack callBack) {
        // currentIndex
        if (sourceList == null || sourceList.size() <= 0 || sourceList.size() <= currentIndex) {
            ToastUtils.show(MyApp.get().getString(R.string.not_found_upload_file));
            LogUtils.w(LOG_TAG, "compressJpegs: currentIndex == " + currentIndex + " -- sourceList == null");
            MyApp.get().getHandler().post(new Runnable() {
                @Override
                public void run() {
                    callBack.failure(sourceList, "");
                }
            });
            return;
        }
        // ossDirPath
        if (StringUtils.isEmpty(ossDirPath)) {
            ToastUtils.show(MyApp.get().getString(R.string.access_resource_path_no_exists));
            LogUtils.w(LOG_TAG, "compressJpegs: currentIndex == " + currentIndex + " -- ossDirPath == null");
            // 回调
            if (callBack != null) {
                MyApp.get().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failure(sourceList, "");
                    }
                });
            }
            return;
        }
        // file
        final File source = sourceList.get(currentIndex);
        if (FileUtils.isFileEmpty(source)) {
            ToastUtils.show(MyApp.get().getString(R.string.upload_file_no_exists));
            LogUtils.w(LOG_TAG, "compressJpegs: currentIndex == " + currentIndex + " -- source == null");
            // 回调
            if (callBack != null) {
                MyApp.get().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failure(sourceList, "");
                    }
                });
            }
            return;
        }
        // progress
        if (progress != null) {
            String colonShow = MyApp.get().getString(R.string.are_compress_space_holder_holder);
            String progressShow = String.format(Locale.getDefault(), colonShow, currentIndex + 1, sourceList.size());
            progress.setContent(progressShow);
            if (currentIndex <= 0) {
                DialogHelper.showWithAnim(progress);
            }
        }
        // compress
        Luban.get(MyApp.get())
                .load(source) // 压缩源文件
                .putGear(Luban.THIRD_GEAR) // 设定压缩档次，默认三挡
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // dialog上面已经显示了
                    }

                    @Override
                    public void onSuccess(File file) {
                        // 替换sourceList中被压缩的文件
                        if (FileUtils.isFileExists(file)) {
                            // 压缩完的文件为新文件
                            sourceList.set(currentIndex, file);
                        } else {
                            // 压缩完保存的还是源文件
                            sourceList.set(currentIndex, source);
                        }
                        // upload
                        if (currentIndex < sourceList.size() - 1) {
                            // 没压缩完
                            compressJpegs(activity, progress, ossDirPath, sourceList, currentIndex + 1, callBack);
                        } else {
                            // 全压缩完
                            DialogHelper.dismiss(progress);
                            uploadJpegs(activity, ossDirPath, sourceList, callBack);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(LOG_TAG, "Luban: currentIndex == " + currentIndex + " -- onError == ", e);
                        // upload
                        if (currentIndex < sourceList.size() - 1) {
                            // 没压缩完
                            compressJpegs(activity, progress, ossDirPath, sourceList, currentIndex + 1, callBack);
                        } else {
                            // 全压缩完
                            DialogHelper.dismiss(progress);
                            uploadJpegs(activity, ossDirPath, sourceList, callBack);
                        }
                    }
                })
                .launch();
    }

    // 上传任务
    private static OSSAsyncTask uploadJpegs(final Activity activity, final String ossDirPath,
                                            final List<File> sourceList, final OssUploadsCallBack callBack) {
        return uploadObjects(activity, ossDirPath, sourceList, "jpeg", callBack);
    }

    // 上传任务(有对话框)
    private static OSSAsyncTask uploadObjects(final Activity activity, final String ossDirPath,
                                              final List<File> sourceList, String suffix, final OssUploadsCallBack callBack) {
        MaterialDialog progress = DialogHelper.getBuild(activity)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .content(R.string.are_upload)
                .progress(false, 100)
                .negativeText(R.string.cancel_upload)
                .build();
        return uploadObjects(progress, ossDirPath, sourceList, 0, new ArrayList<String>(), suffix, callBack);
    }

    // 上传任务
    private static OSSAsyncTask uploadObjects(final MaterialDialog progress, final String ossDirPath,
                                              final List<File> sourceList, final int currentIndex, final List<String> ossPathList,
                                              final String suffix, final OssUploadsCallBack callBack) {
        LogUtils.w(LOG_TAG, "uploadObjects: currentIndex: " + currentIndex + " -- ossDirPath: " + ossDirPath);
        // currentIndex
        if (sourceList == null || sourceList.size() <= 0 || sourceList.size() <= currentIndex) {
            ToastUtils.show(MyApp.get().getString(R.string.not_found_upload_file));
            LogUtils.w(LOG_TAG, "uploadObjects: currentIndex == " + currentIndex + " -- sourceList == null");
            MyApp.get().getHandler().post(new Runnable() {
                @Override
                public void run() {
                    callBack.failure(sourceList, "");
                }
            });
            return null;
        }
        // ossDirPath
        if (StringUtils.isEmpty(ossDirPath)) {
            ToastUtils.show(MyApp.get().getString(R.string.access_resource_path_no_exists));
            LogUtils.w(LOG_TAG, "uploadObjects: currentIndex == " + currentIndex + " -- ossDirPath == null");
            // 回调
            if (callBack != null) {
                MyApp.get().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failure(sourceList, "");
                    }
                });
            }
            return null;
        }
        // file
        final File source = sourceList.get(currentIndex);
        if (FileUtils.isFileEmpty(source)) {
            ToastUtils.show(MyApp.get().getString(R.string.upload_file_no_exists));
            LogUtils.w(LOG_TAG, "uploadObjects: currentIndex == " + currentIndex + " -- source == null");
            // 回调
            if (callBack != null) {
                MyApp.get().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.failure(sourceList, "");
                    }
                });
            }
            return null;
        }
        // progress
        if (progress != null) {
            String colonShow = MyApp.get().getString(R.string.are_upload_space_holder_holder);
            String progressShow = String.format(Locale.getDefault(), colonShow, currentIndex + 1, sourceList.size());
            progress.setContent(progressShow);
            if (currentIndex <= 0) {
                DialogHelper.showWithAnim(progress);
            }
        }
        // objectKey生成
        final String objectKey = getObjectKey(ossDirPath, suffix);
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
                final String uploadKey = request.getObjectKey();
                LogUtils.i(LOG_TAG, "uploadObjects: onSuccess: currentIndex == " + currentIndex + " -- getObjectKey == " + uploadKey);
                ossPathList.add(uploadKey);
                if (currentIndex < sourceList.size() - 1) {
                    // 没上传完毕
                    uploadObjects(progress, ossDirPath, sourceList, currentIndex + 1, ossPathList, suffix, callBack);
                } else {
                    // 已上传完毕
                    DialogHelper.dismiss(progress);
                    // 回调
                    if (callBack != null) {
                        MyApp.get().getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.success(sourceList, ossPathList);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                DialogHelper.dismiss(progress);
                // 打印
                final String uploadKey = request.getObjectKey();
                LogUtils.i(LOG_TAG, "uploadObjects: onFailure: currentIndex == " + currentIndex + " -- getObjectKey == " + uploadKey);
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
                            callBack.failure(sourceList, "");
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
                    LogUtils.d(LOG_TAG, "uploadObjects: cancel");
                    taskCancel(task);
                }
            });
        }
        return task;
    }

    /**
     * *****************************************下载*****************************************
     */
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
            FileUtils.createFileByDeleteOldFile(target);
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

    // 给后台看的 所以用CST时区
    private static String getObjectKey(String dir, String suffix) {
        return dir + DateUtils.getCurrentCSTString(ConstantUtils.FORMAT_CHINA_Y_M_D__H_M_S_S) + "." + suffix;
    }
}
