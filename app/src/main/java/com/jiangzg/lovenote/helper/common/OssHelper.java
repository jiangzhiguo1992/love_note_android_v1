package com.jiangzg.lovenote.helper.common;

import android.app.Activity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.more.VipActivity;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.entity.OssInfo;
import com.jiangzg.lovenote.model.entity.User;

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

    // oos 对象
    private static OSS ossClient;

    /**
     * 刷新ossClient
     */
    public static void refreshOssClient() {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String expireTime = DateUtils.getStr(TimeHelper.getJavaTimeByGo(ossInfo.getStsExpireTime()), DateUtils.FORMAT_LINE_M_D_H_M);
        LogUtils.i(OssHelper.class, "refreshOssClient", "sts将在 " + expireTime + " 过期");
        // oss信息 也可用OSSFederationCredentialProvider来实现动态更新
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(ossInfo.getAccessKeyId(),
                ossInfo.getAccessKeySecret(), ossInfo.getSecurityToken());
        // oss配置
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(600 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(60 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(10); // 最大并发请求数，默认5个
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
            // 十分钟过期时间
            String url = getOssClient().presignConstrainedObjectURL(ossInfo.getBucket(), objKey,
                    ossInfo.getUrlExpireSec() * 1000);
            LogUtils.d(OssHelper.class, "getUrl", url);
            return url;
        } catch (ClientException e) {
            LogUtils.e(OssHelper.class, "getUrl", e);
            // 刷新oss
            ApiHelper.ossInfoUpdate();
        }
        return "";
    }

    /**
     * *****************************************上传/下载*****************************************
     */
    public interface OssUploadCallBack {
        void success(File source, String ossKey);

        void failure(File source, String errMsg);
    }

    public interface OssUploadsCallBack {
        void success(List<File> sourceList, List<String> ossKeyList, List<String> successList);

        void failure(List<File> sourceList, String errMsg, int index);
    }

    public interface OssDownloadCallBack {
        void success(String ossKey, File target);

        void failure(String ossKey, String errMsg);
    }

    public interface OssUploadProgressCallBack {
        void toast(String msg);

        void start();

        void progress(PutObjectRequest request, long currentSize, long totalSize);

        void end();
    }

    public interface OssUploadsProgressCallBack {
        void toast(String msg, int index);

        void start(int index);

        void progress(PutObjectRequest request, long currentSize, long totalSize, int index);

        void end(int index);
    }

    public interface OssDownloadProgressCallBack {
        void toast(String msg);

        void start();

        void progress(GetObjectRequest request, long currentSize, long totalSize);

        void end();
    }

    // 给后台看的 所以用CST时区
    private static String createExtensionKey(String dir, File source) {
        String uuid = StringUtils.getUUID(8);
        String extension = FileUtils.getFileExtension(source);
        return dir + DateUtils.getCurrentStr(DateUtils.FORMAT_CHINA_Y_M_D__H_M_S_S) + "-" + uuid + extension;
    }

    // 给后台看的 所以用CST时区 TODO
    private static String createLogKey(String dir, File source) {
        String uuid = StringUtils.getUUID(8);
        String extension = FileUtils.getFileExtension(source);
        return dir + DateUtils.getCurrentStr(DateUtils.FORMAT_CHINA_Y_M_D__H_M_S_S) + "-" + uuid + extension;
    }

    // 取消任务
    private static void taskCancel(OSSAsyncTask task) {
        if (task != null && !task.isCanceled() && !task.isCompleted()) {
            task.cancel();
        }
    }

    /**
     * *****************************************下载*****************************************
     */
    // 下载任务 前台
    public static void downloadFileInForeground(Activity activity, String ossKey, final File target,
                                                final OssDownloadCallBack callBack) {
        // dialog
        final MaterialDialog progress = DialogHelper.getBuild(activity)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .content(R.string.are_download)
                .progress(false, 100)
                .negativeText(R.string.cancel_download)
                .build();
        // 开始上传
        OSSAsyncTask task = downloadFile(ossKey, target, new OssDownloadProgressCallBack() {
            @Override
            public void toast(String msg) {
                ToastUtils.show(msg);
            }

            @Override
            public void start() {
                DialogHelper.showWithAnim(progress);
            }

            @Override
            public void progress(GetObjectRequest request, long currentSize, long totalSize) {
                LogUtils.d(OssHelper.class, "downloadFileInForeground", "currentSize: " + currentSize + " --- totalSize: " + totalSize);
                if (progress != null && progress.isShowing()) {
                    int percent = (int) (((float) currentSize / (float) totalSize) * 100);
                    progress.setProgress(percent);
                }
            }

            @Override
            public void end() {
                DialogUtils.dismiss(progress);
            }
        }, callBack);
        // cancel
        progress.setOnCancelListener(dialog -> {
            ResHelper.deleteFileInBackground(target);
            taskCancel(task);
        });
    }

    // 下载任务 后台
    public static void downloadFileInBackground(String ossKey, final File target, boolean toast,
                                                final OssDownloadCallBack callBack) {
        downloadFile(ossKey, target, new OssDownloadProgressCallBack() {
            @Override
            public void toast(String msg) {
                if (toast) ToastUtils.show(msg);
            }

            @Override
            public void start() {
            }

            @Override
            public void progress(GetObjectRequest request, long currentSize, long totalSize) {
            }

            @Override
            public void end() {
            }
        }, callBack);
    }

    // 下载任务 base
    private static OSSAsyncTask downloadFile(final String ossKey, final File target,
                                             final OssDownloadProgressCallBack progress,
                                             final OssDownloadCallBack callBack) {
        // ossKey
        if (StringUtils.isEmpty(ossKey)) {
            LogUtils.w(OssHelper.class, "downloadFile", "ossKey == null");
            // 删除下载文件
            ResHelper.deleteFileInBackground(target);
            String msg = MyApp.get().getString(R.string.access_resource_path_no_exists);
            if (progress != null) {
                progress.toast(msg);
            }
            if (callBack != null) {
                MyApp.get().getHandler().post(() -> callBack.failure(ossKey, msg));
            }
            return null;
        }
        // target
        if (target == null) {
            LogUtils.i(OssHelper.class, "downloadFile", "target == null");
            String msg = MyApp.get().getString(R.string.save_file_no_exists);
            if (progress != null) {
                progress.toast(msg);
            }
            if (callBack != null) {
                MyApp.get().getHandler().post(() -> callBack.failure(ossKey, msg));
            }
            return null;
        }
        // 构造下载文件请求，不是临时url，用key和secret访问，不用签名
        String bucket = SPHelper.getOssInfo().getBucket();
        GetObjectRequest get = new GetObjectRequest(bucket, ossKey);
        // 异步下载时设置进度回调
        get.setProgressListener((request, currentSize, totalSize) -> {
            if (progress != null) progress.progress(request, currentSize, totalSize);
        });
        // client
        OSS client = getOssClient();
        if (client == null) {
            LogUtils.w(OssHelper.class, "downloadFile", "client == null");
            String msg = MyApp.get().getString(R.string.download_fail_tell_we_this_bug);
            if (progress != null) {
                progress.toast(msg);
            }
            if (callBack != null) {
                MyApp.get().getHandler().post(() -> callBack.failure(ossKey, msg));
            }
            return null;
        }
        // progress
        if (progress != null) MyApp.get().getHandler().post(progress::start);
        // 开始任务
        FileUtils.createFileByDeleteOldFile(target);
        LogUtils.i(OssHelper.class, "downloadFile", "ossKey = " + ossKey + " <---> target = " + target.getAbsolutePath());
        return client.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                final String downloadKey = request.getObjectKey();
                LogUtils.i(OssHelper.class, "downloadFile", "onSuccess: getObjectKey = " + downloadKey);
                // 开始解析文件
                InputStream inputStream = result.getObjectContent();
                boolean ok = FileUtils.writeFileFromIS(target, inputStream, false);
                // progress必须在这后面
                if (progress != null) MyApp.get().getHandler().post(progress::end);
                if (ok) {
                    if (callBack != null) {
                        MyApp.get().getHandler().post(() -> callBack.success(downloadKey, target));
                    }
                } else {
                    ResHelper.deleteFileInBackground(target);
                    String msg = MyApp.get().getString(R.string.file_resolve_fail_tell_we_this_bug);
                    if (progress != null) {
                        progress.toast(msg);
                    }
                    if (callBack != null) {
                        MyApp.get().getHandler().post(() -> callBack.failure(downloadKey, msg));
                    }
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientException, ServiceException serviceException) {
                if (progress != null) MyApp.get().getHandler().post(progress::end);
                final String downloadKey = request.getObjectKey();
                LogUtils.w(OssHelper.class, "downloadFile", "onFailure: getObjectKey == " + downloadKey);

                ResHelper.deleteFileInBackground(target);
                ApiHelper.ossInfoUpdate();

                String errMsg = MyApp.get().getString(R.string.download_fail_tell_we_this_bug);
                if (clientException != null) {
                    // 本地异常如网络异常等
                    LogUtils.w(OssHelper.class, "downloadFile", clientException.getMessage());
                    errMsg = MyApp.get().getString(R.string.download_fail_please_check_native_net);
                }
                if (serviceException != null) {
                    // 服务异常
                    LogUtils.w(OssHelper.class, "downloadObject", serviceException.getRawMessage());
                    LogUtils.w(OssHelper.class, "downloadObject", "serviceException = " + serviceException.toString());
                    errMsg = MyApp.get().getString(R.string.download_fail_tell_we_this_bug);
                }
                if (progress != null) {
                    progress.toast(errMsg);
                }
                if (callBack != null) {
                    String finalErrMsg = errMsg;
                    MyApp.get().getHandler().post(() -> callBack.failure(downloadKey, finalErrMsg));
                }
            }
        });
    }

    /**
     * *****************************************单文件上传*****************************************
     */
    // 上传任务 启动压缩 + 后缀名 + 前台
    private static void uploadMiniExtFileInForeground(final Activity activity, final String ossDirPath,
                                                      final File source, final OssUploadCallBack callBack) {
        // file
        if (FileUtils.isFileEmpty(source)) {
            LogUtils.w(OssHelper.class, "uploadMiniExtFileInForeground", "source == null");
            String msg = MyApp.get().getString(R.string.upload_file_no_exists);
            ToastUtils.show(msg);
            // 回调
            if (callBack != null) {
                MyApp.get().getHandler().post(() -> callBack.failure(source, msg));
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
                        String size = ConvertUtils.byte2FitSize(source.length());
                        LogUtils.d(OssHelper.class, "uploadMiniExtFileInForeground", " 压缩前大小: " + source.getName() + " = " + size);
                    }

                    @Override
                    public void onSuccess(File file) {
                        DialogUtils.dismiss(progress);
                        if (FileUtils.isFileExists(file)) {
                            // 压缩文件有可能不存在
                            //if (!file.getAbsolutePath().trim().equals(source.getAbsolutePath().trim())) {
                            // 压缩后的文件 != 源文件，但是在内部cache中，交给系统和用户
                            //ResHelper.deleteFileInBackground(source);
                            //}
                            String size = ConvertUtils.byte2FitSize(file.length());
                            LogUtils.d(OssHelper.class, "uploadMiniExtFileInForeground", " 压缩后大小: " + source.getName() + " = " + size);
                            // upload
                            uploadExtFileInForeground(activity, ossDirPath, file, callBack);
                        } else {
                            String size = ConvertUtils.byte2FitSize(source.length());
                            LogUtils.d(OssHelper.class, "uploadMiniExtFileInForeground", " 压缩失败大小: " + source.getName() + " = " + size);
                            // upload
                            uploadExtFileInForeground(activity, ossDirPath, source, callBack);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        LogUtils.e(OssHelper.class, "uploadMiniExtFileInForeground", t);
                        DialogUtils.dismiss(progress);
                        // upload
                        uploadExtFileInForeground(activity, ossDirPath, source, callBack);
                    }
                })
                .launch();
    }

    // 上传任务 后缀名 + 前台
    private static void uploadExtFileInForeground(Activity activity, final String ossDirPath,
                                                  final File source, final OssUploadCallBack callBack) {
        // ossDirPath
        if (StringUtils.isEmpty(ossDirPath)) {
            LogUtils.w(OssHelper.class, "uploadExtFileInForeground", "ossDirPath == null");
            String msg = MyApp.get().getString(R.string.access_resource_path_no_exists);
            ToastUtils.show(msg);
            if (callBack != null) {
                MyApp.get().getHandler().post(() -> callBack.failure(source, msg));
            }
            return;
        }
        // objectKey
        String objectKey = createExtensionKey(ossDirPath, source);
        // 开始时上传
        uploadFileInForeground(activity, source, objectKey, callBack);
    }

    // 上传任务 前台
    private static void uploadFileInForeground(Activity activity, final File source,
                                               String ossKey, final OssUploadCallBack callBack) {
        // dialog
        final MaterialDialog progress = DialogHelper.getBuild(activity)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .content(R.string.are_upload)
                .progress(false, 100)
                .negativeText(R.string.cancel_upload)
                .build();
        // 开始上传
        OSSAsyncTask task = uploadFile(source, ossKey, new OssUploadProgressCallBack() {
            @Override
            public void toast(String msg) {
                ToastUtils.show(msg);
            }

            @Override
            public void start() {
                DialogHelper.showWithAnim(progress);

            }

            @Override
            public void progress(PutObjectRequest request, long currentSize, long totalSize) {
                LogUtils.d(OssHelper.class, "uploadFileInForeground", "currentSize: " + currentSize + " --- totalSize: " + totalSize);
                if (progress != null && progress.isShowing()) {
                    int percent = (int) (((float) currentSize / (float) totalSize) * 100);
                    progress.setProgress(percent);
                }
            }

            @Override
            public void end() {
                DialogUtils.dismiss(progress);
            }
        }, callBack);
        // cancel
        progress.setOnCancelListener(dialog -> taskCancel(task));
    }

    // 上传任务 后台
    private static void uploadFileInBackground(final File source, String ossKey, boolean toast,
                                               final OssUploadCallBack callBack) {
        uploadFile(source, ossKey, new OssUploadProgressCallBack() {
            @Override
            public void toast(String msg) {
                if (toast) ToastUtils.show(msg);
            }

            @Override
            public void start() {
            }

            @Override
            public void progress(PutObjectRequest request, long currentSize, long totalSize) {
            }

            @Override
            public void end() {
            }
        }, callBack);
    }

    // 上传任务 base
    private static OSSAsyncTask uploadFile(final File source, String ossKey,
                                           final OssUploadProgressCallBack progress,
                                           final OssUploadCallBack callBack) {
        // source
        if (FileUtils.isFileEmpty(source)) {
            LogUtils.i(OssHelper.class, "uploadFile", "source == null");
            String msg = MyApp.get().getString(R.string.upload_file_no_exists);
            if (progress != null) {
                progress.toast(msg);
            }
            if (callBack != null) {
                MyApp.get().getHandler().post(() -> callBack.failure(source, msg));
            }
            return null;
        }
        // ossKey
        if (StringUtils.isEmpty(ossKey)) {
            LogUtils.w(OssHelper.class, "uploadFile", "ossKey == null");
            String msg = MyApp.get().getString(R.string.access_resource_path_no_exists);
            if (progress != null) {
                progress.toast(msg);
            }
            if (callBack != null) {
                MyApp.get().getHandler().post(() -> callBack.failure(source, msg));
            }
            return null;
        }
        // 构造上传请求
        String bucket = SPHelper.getOssInfo().getBucket();
        PutObjectRequest put = new PutObjectRequest(bucket, ossKey, source.getAbsolutePath());
        // 异步上传时可以设置进度回调
        put.setProgressCallback((request, currentSize, totalSize) -> {
            if (progress != null) progress.progress(request, currentSize, totalSize);
        });
        // client
        OSS client = getOssClient();
        if (client == null) {
            LogUtils.w(OssHelper.class, "uploadFile", "client == null");
            String msg = MyApp.get().getString(R.string.upload_fail_tell_we_this_bug);
            if (progress != null) {
                progress.toast(msg);
            }
            if (callBack != null) {
                MyApp.get().getHandler().post(() -> callBack.failure(source, msg));
            }
            return null;
        }
        // progress
        if (progress != null) MyApp.get().getHandler().post(progress::start);
        // 开始任务
        LogUtils.i(OssHelper.class, "uploadFile", "source = " + source.getAbsolutePath() + " <---> ossKey = " + ossKey);
        return client.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                if (progress != null) MyApp.get().getHandler().post(progress::end);
                final String uploadKey = request.getObjectKey();
                LogUtils.i(OssHelper.class, "uploadFile", "onSuccess: objectKey = " + uploadKey);
                if (callBack != null) {
                    MyApp.get().getHandler().post(() -> callBack.success(source, uploadKey));
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                if (progress != null) MyApp.get().getHandler().post(progress::end);
                final String uploadKey = request.getObjectKey();
                LogUtils.w(OssHelper.class, "uploadFile", "onFailure: objectKey == " + uploadKey);

                ApiHelper.ossInfoUpdate();

                String errMsg = MyApp.get().getString(R.string.upload_fail_tell_we_this_bug);
                if (clientException != null) {
                    // 本地异常如网络异常等
                    LogUtils.w(OssHelper.class, "uploadFile", clientException.getMessage());
                    errMsg = MyApp.get().getString(R.string.upload_fail_please_check_native_net);
                }
                if (serviceException != null) {
                    // 服务异常
                    LogUtils.w(OssHelper.class, "uploadFile", serviceException.getRawMessage());
                    LogUtils.w(OssHelper.class, "uploadFile", "serviceException = " + serviceException.toString());
                    errMsg = MyApp.get().getString(R.string.upload_fail_tell_we_this_bug);
                }
                if (progress != null) {
                    progress.toast(errMsg);
                }
                if (callBack != null) {
                    String finalErrMsg = errMsg;
                    MyApp.get().getHandler().post(() -> callBack.failure(source, finalErrMsg));
                }
            }
        });
    }

    /**
     * *****************************************多文件上传*****************************************
     */
    // 启动多张压缩
    private static void uploadMiniExtFilesInForeground(final Activity activity, final String ossDirPath,
                                                       final List<File> sourceList, final boolean canMiss,
                                                       final OssUploadsCallBack callBack) {
        MaterialDialog progress = DialogHelper.getBuild(activity)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .content(R.string.image_is_compress)
                .progress(true, 0)
                .progressIndeterminateStyle(false)
                .build();
        uploadMiniExtFilesInForeground(activity, progress, sourceList, ossDirPath, 0, canMiss, callBack);
    }

    private static void uploadMiniExtFilesInForeground(final Activity activity, final MaterialDialog progress,
                                                       final List<File> sourceList, final String ossDirPath,
                                                       final int index, final boolean canMiss,
                                                       final OssUploadsCallBack callBack) {
        // index
        if (sourceList == null || sourceList.size() <= 0 || sourceList.size() <= index) {
            LogUtils.w(OssHelper.class, "uploadMiniExtFilesInForeground", "index = " + index + " -- sourceList == null");
            String msg = MyApp.get().getString(R.string.not_found_upload_file);
            ToastUtils.show(msg);
            DialogUtils.dismiss(progress);
            if (callBack != null) {
                MyApp.get().getHandler().post(() -> callBack.failure(sourceList, msg, index));
            }
            return;
        }
        // ossDirPath
        if (StringUtils.isEmpty(ossDirPath)) {
            LogUtils.w(OssHelper.class, "uploadMiniExtFilesInForeground", "index = " + index + " -- ossDirPath == null");
            String msg = MyApp.get().getString(R.string.access_resource_path_no_exists);
            ToastUtils.show(msg);
            DialogUtils.dismiss(progress);
            if (callBack != null) {
                MyApp.get().getHandler().post(() -> callBack.failure(sourceList, msg, index));
            }
            return;
        }
        // source
        final File source = sourceList.get(index);
        if (FileUtils.isFileEmpty(source)) {
            LogUtils.w(OssHelper.class, "uploadMiniExtFilesInForeground", "index = " + index + " -- source == null");
            String msg = MyApp.get().getString(R.string.upload_file_no_exists);
            ToastUtils.show(msg);
            DialogUtils.dismiss(progress);
            if (callBack != null) {
                MyApp.get().getHandler().post(() -> callBack.failure(sourceList, msg, index));
            }
            return;
        }
        // progress
        if (progress != null) {
            MyApp.get().getHandler().post(() -> {
                String colonShow = MyApp.get().getString(R.string.are_compress_space_holder_holder);
                String progressShow = String.format(Locale.getDefault(), colonShow, index + 1, sourceList.size());
                progress.setContent(progressShow);
                if (index <= 0) {
                    DialogHelper.showWithAnim(progress);
                }
            });
        }
        // compress
        Luban.get(MyApp.get())
                .load(source) // 压缩源文件
                .putGear(Luban.THIRD_GEAR) // 设定压缩档次，默认三挡
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        String size = ConvertUtils.byte2FitSize(source.length());
                        LogUtils.d(OssHelper.class, "uploadMiniExtFilesInForeground", " 压缩前大小: " + source.getName() + " = " + size);
                        // dialog上面已经显示了
                    }

                    @Override
                    public void onSuccess(File file) {
                        // 替换sourceList中被压缩的文件
                        if (FileUtils.isFileExists(file)) {
                            // 压缩完的文件为新文件
                            String size = ConvertUtils.byte2FitSize(file.length());
                            LogUtils.d(OssHelper.class, "uploadMiniExtFilesInForeground", "压缩后大小: " + source.getName() + " = " + size);
                            sourceList.set(index, file);
                        } else {
                            // 压缩完保存的还是源文件
                            String size = ConvertUtils.byte2FitSize(source.length());
                            LogUtils.d(OssHelper.class, "uploadMiniExtFilesInForeground", "压缩失败大小: " + source.getName() + " = " + size);
                            sourceList.set(index, source);
                        }
                        // upload
                        if (index < sourceList.size() - 1) {
                            // 没压缩完
                            uploadMiniExtFilesInForeground(activity, progress, sourceList, ossDirPath, index + 1, canMiss, callBack);
                        } else {
                            // 全压缩完
                            DialogUtils.dismiss(progress);
                            uploadExtFilesInForeground(activity, sourceList, ossDirPath, canMiss, callBack);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(OssHelper.class, "uploadMiniExtFilesInForeground", e);
                        // upload
                        if (index < sourceList.size() - 1) {
                            // 没压缩完
                            uploadMiniExtFilesInForeground(activity, progress, sourceList, ossDirPath, index + 1, canMiss, callBack);
                        } else {
                            // 全压缩完
                            DialogUtils.dismiss(progress);
                            uploadExtFilesInForeground(activity, sourceList, ossDirPath, canMiss, callBack);
                        }
                    }
                })
                .launch();
    }

    // 上传任务 后缀名 + 前台
    private static void uploadExtFilesInForeground(Activity activity, final List<File> sourceList,
                                                   final String ossDirPath, final boolean canMiss,
                                                   final OssUploadsCallBack callBack) {
        // ossDirPath
        if (StringUtils.isEmpty(ossDirPath)) {
            LogUtils.w(OssHelper.class, "uploadExtFilesInForeground", "ossDirPath == null");
            String msg = MyApp.get().getString(R.string.access_resource_path_no_exists);
            ToastUtils.show(msg);
            if (callBack != null) {
                MyApp.get().getHandler().post(() -> callBack.failure(sourceList, msg, 0));
            }
            return;
        }
        // ossKeyList
        ArrayList<String> ossKeyList = new ArrayList<>();
        if (sourceList != null) {
            for (File source : sourceList) {
                if (FileUtils.isFileEmpty(source)) {
                    ossKeyList.add("");
                    continue;
                }
                String objectKey = createExtensionKey(ossDirPath, source);
                ossKeyList.add(objectKey);
            }
        }
        // 开始上传
        uploadFilesInForeground(activity, sourceList, ossKeyList, canMiss, callBack);
    }

    // 上传多任务 前台
    public static void uploadFilesInForeground(Activity activity, final List<File> sourceList,
                                               final List<String> ossKeyList, final boolean canMiss,
                                               final OssUploadsCallBack callBack) {
        // dialog
        final MaterialDialog progress = DialogHelper.getBuild(activity)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .content(R.string.are_upload)
                .progress(false, 100)
                .negativeText(R.string.cancel_upload)
                .build();
        // 开始上传
        OSSAsyncTask task = uploadFiles(sourceList, ossKeyList, 0, canMiss, new ArrayList<>(), new OssUploadsProgressCallBack() {
            @Override
            public void toast(String msg, int index) {
                ToastUtils.show(msg);
            }

            @Override
            public void start(int index) {
                if (progress == null || sourceList == null) return;
                String colonShow = MyApp.get().getString(R.string.are_upload_space_holder_holder);
                String progressShow = String.format(Locale.getDefault(), colonShow, index + 1, sourceList.size());
                progress.setContent(progressShow);
                if (index <= 0) {
                    DialogHelper.showWithAnim(progress);
                }
            }

            @Override
            public void progress(PutObjectRequest request, long currentSize, long totalSize, int index) {
                LogUtils.d(OssHelper.class, "uploadFilesInForeground", "index : " + index + " --- currentSize: " + currentSize + " --- totalSize: " + totalSize);
                if (progress != null && progress.isShowing()) {
                    int percent = (int) (((float) currentSize / (float) totalSize) * 100);
                    progress.setProgress(percent);
                }
            }

            @Override
            public void end(int index) {
                DialogUtils.dismiss(progress);
            }
        }, callBack);
        // cancel
        progress.setOnCancelListener(dialog -> taskCancel(task));
    }

    // 上传多任务 后台
    public static void uploadFilesInBackground(final List<File> sourceList, final List<String> ossKeyList,
                                               final boolean canMiss, boolean toast,
                                               final OssUploadsCallBack callBack) {
        uploadFiles(sourceList, ossKeyList, 0, canMiss, new ArrayList<>(), new OssUploadsProgressCallBack() {
            @Override
            public void toast(String msg, int index) {
                if (toast) ToastUtils.show(msg);
            }

            @Override
            public void start(int index) {
            }

            @Override
            public void progress(PutObjectRequest request, long currentSize, long totalSize, int index) {
            }

            @Override
            public void end(int index) {
            }
        }, callBack);
    }

    // 上传多任务 base
    private static OSSAsyncTask uploadFiles(final List<File> sourceList, final List<String> ossKeyList,
                                            final int index, final boolean canMiss, final List<String> successList,
                                            final OssUploadsProgressCallBack progress,
                                            final OssUploadsCallBack callBack) {
        // sourceList
        if (sourceList == null || sourceList.size() <= 0 || sourceList.size() <= index) {
            LogUtils.w(OssHelper.class, "uploadFiles", "index = " + index + " <---> sourceList.size == " + (sourceList == null ? 0 : sourceList.size()));
            String msg = MyApp.get().getString(R.string.not_found_upload_file);
            if (progress != null) {
                progress.toast(msg, index);
                progress.end(index);
            }
            if (canMiss && successList != null && successList.size() > 0) {
                // 是否允许丢失
                MyApp.get().getHandler().post(() -> callBack.success(sourceList, ossKeyList, successList));
            } else if (callBack != null) {
                // 直接返回
                MyApp.get().getHandler().post(() -> callBack.failure(sourceList, msg, index));
            }
            return null;
        }
        // ossKeyList
        if (ossKeyList == null || ossKeyList.size() <= 0 || ossKeyList.size() <= index) {
            LogUtils.w(OssHelper.class, "uploadFiles", "index = " + index + " <---> ossKeyList.size == " + (ossKeyList == null ? 0 : ossKeyList.size()));
            String msg = MyApp.get().getString(R.string.access_resource_path_no_exists);
            if (progress != null) {
                progress.toast(msg, index);
                progress.end(index);
            }
            if (canMiss && successList != null && successList.size() > 0) {
                // 是否允许丢失
                MyApp.get().getHandler().post(() -> callBack.success(sourceList, ossKeyList, successList));
            } else if (callBack != null) {
                // 直接返回
                MyApp.get().getHandler().post(() -> callBack.failure(sourceList, msg, index));
            }
            return null;
        }
        // source
        final File source = sourceList.get(index);
        if (FileUtils.isFileEmpty(source)) {
            LogUtils.w(OssHelper.class, "uploadFiles", "index == " + index + " <---> source == null");
            String msg = MyApp.get().getString(R.string.upload_file_no_exists);
            if (progress != null) {
                progress.toast(msg, index);
            }
            // 后续上传
            if (canMiss && index < sourceList.size() - 1) {
                return uploadFiles(sourceList, ossKeyList, index + 1, canMiss, successList, progress, callBack);
            }
            if (progress != null) {
                progress.end(index);
            }
            if (canMiss && successList != null && successList.size() > 0) {
                // 是否允许丢失
                MyApp.get().getHandler().post(() -> callBack.success(sourceList, ossKeyList, successList));
            } else if (callBack != null) {
                // 直接结束
                MyApp.get().getHandler().post(() -> callBack.failure(sourceList, msg, index));
            }
            return null;
        }
        // ossKey
        final String ossKey = ossKeyList.get(index);
        if (StringUtils.isEmpty(ossKey)) {
            LogUtils.w(OssHelper.class, "uploadFiles", "index == " + index + " <---> ossKey == null");
            String msg = MyApp.get().getString(R.string.access_resource_path_no_exists);
            if (progress != null) {
                progress.toast(msg, index);
            }
            // 后续上传
            if (canMiss && index < sourceList.size() - 1) {
                return uploadFiles(sourceList, ossKeyList, index + 1, canMiss, successList, progress, callBack);
            }
            if (progress != null) {
                progress.end(index);
            }
            if (canMiss && successList != null && successList.size() > 0) {
                // 是否允许丢失
                MyApp.get().getHandler().post(() -> callBack.success(sourceList, ossKeyList, successList));
            } else if (callBack != null) {
                // 直接结束
                MyApp.get().getHandler().post(() -> callBack.failure(sourceList, msg, index));
            }
            return null;
        }
        // 构造上传请求
        String bucket = SPHelper.getOssInfo().getBucket();
        PutObjectRequest put = new PutObjectRequest(bucket, ossKey, source.getAbsolutePath());
        // 异步上传时设置进度回调
        put.setProgressCallback((request, currentSize, totalSize) -> {
            if (progress != null) progress.progress(request, currentSize, totalSize, index);
        });
        // client
        OSS client = getOssClient();
        if (client == null) {
            LogUtils.w(OssHelper.class, "uploadFiles", "client == null");
            String msg = MyApp.get().getString(R.string.upload_fail_tell_we_this_bug);
            if (progress != null) {
                progress.toast(msg, index);
                progress.end(index);
            }
            if (canMiss && successList != null && successList.size() > 0) {
                // 是否允许丢失
                MyApp.get().getHandler().post(() -> callBack.success(sourceList, ossKeyList, successList));
            } else if (callBack != null) {
                // 直接结束
                MyApp.get().getHandler().post(() -> callBack.failure(sourceList, msg, index));
            }
            return null;
        }
        // progress
        if (progress != null) MyApp.get().getHandler().post(() -> progress.start(index));
        // 开始上传第index个
        LogUtils.i(OssHelper.class, "uploadFiles", "index = " + index + " <---> source = " + source.getAbsolutePath() + " <---> ossKey = " + ossKey);
        return client.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                final String uploadKey = request.getObjectKey();
                LogUtils.i(OssHelper.class, "uploadFiles", "onSuccess: index = " + index + " <---> objectKey = " + uploadKey);

                successList.add(uploadKey);

                if (index < sourceList.size() - 1) {
                    // 没上传完毕
                    uploadFiles(sourceList, ossKeyList, index + 1, canMiss, successList, progress, callBack);
                } else {
                    if (progress != null) {
                        MyApp.get().getHandler().post(() -> progress.end(index));
                    }
                    if (callBack != null) {
                        MyApp.get().getHandler().post(() -> callBack.success(sourceList, ossKeyList, successList));
                    }
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                final String uploadKey = request.getObjectKey();
                LogUtils.i(OssHelper.class, "uploadFiles", "onFailure: index = " + index + " <---> objectKey = " + uploadKey);

                ApiHelper.ossInfoUpdate();

                String errMsg = MyApp.get().getString(R.string.upload_fail_tell_we_this_bug);
                if (clientException != null) {
                    // 本地异常如网络异常等
                    LogUtils.w(OssHelper.class, "uploadFiles", clientException.getMessage());
                    errMsg = MyApp.get().getString(R.string.upload_fail_please_check_native_net);
                }
                if (serviceException != null) {
                    // 服务异常
                    LogUtils.w(OssHelper.class, "uploadFiles", serviceException.getRawMessage());
                    LogUtils.w(OssHelper.class, "uploadFiles", "serviceException = " + serviceException.toString());
                    errMsg = MyApp.get().getString(R.string.upload_fail_tell_we_this_bug);
                }
                if (progress != null) {
                    progress.toast(errMsg, index);
                }
                // 后续上传
                if (canMiss && index < sourceList.size() - 1) {
                    uploadFiles(sourceList, ossKeyList, index + 1, canMiss, successList, progress, callBack);
                    return;
                }
                if (progress != null) {
                    MyApp.get().getHandler().post(() -> progress.end(index));
                }
                if (canMiss && successList != null && successList.size() > 0) {
                    // 是否允许丢失
                    MyApp.get().getHandler().post(() -> callBack.success(sourceList, ossKeyList, successList));
                } else if (callBack != null) {
                    // 直接结束
                    String finalErrMsg = errMsg;
                    MyApp.get().getHandler().post(() -> callBack.failure(sourceList, finalErrMsg, index));
                }
            }
        });
    }

    // 上传任务(有对话框)
    //private static OSSAsyncTask uploadJpegList(final Activity activity, final String ossDirPath,
    //                                           final List<File> sourceList, final OssUploadsCallBack callBack) {
    //    MaterialDialog progress = DialogHelper.getBuild(activity)
    //            .cancelable(false)
    //            .canceledOnTouchOutside(false)
    //            .content(R.string.are_upload)
    //            .progress(false, 100)
    //            .negativeText(R.string.cancel_upload)
    //            .build();
    //    return uploadJpegList(progress, ossDirPath, sourceList, 0, new ArrayList<>(), callBack);
    //}
    //
    // 上传任务
    //private static OSSAsyncTask uploadJpegList(final MaterialDialog progress, final String ossDirPath,
    //                                           final List<File> sourceList, final int currentIndex,
    //                                           final List<String> ossPathList, final OssUploadsCallBack callBack) {
    // currentIndex
    //if (sourceList == null || sourceList.size() <= 0 || sourceList.size() <= currentIndex) {
    //    ToastUtils.show(MyApp.get().getString(R.string.not_found_upload_file));
    //    DialogUtils.dismiss(progress);
    //    LogUtils.w(OssHelper.class, "uploadJpegList", "currentIndex = " + currentIndex + " -- sourceList == null");
    //    MyApp.get().getHandler().post(() -> callBack.failure(sourceList, ""));
    //    return null;
    //}
    // ossDirPath
    //if (StringUtils.isEmpty(ossDirPath)) {
    //    ToastUtils.show(MyApp.get().getString(R.string.access_resource_path_no_exists));
    //    DialogUtils.dismiss(progress);
    //    LogUtils.w(OssHelper.class, "uploadJpegList", "currentIndex = " + currentIndex + " -- ossDirPath == null");
    //    // 回调
    //    if (callBack != null) {
    //        MyApp.get().getHandler().post(() -> callBack.failure(sourceList, ""));
    //    }
    //    return null;
    //}
    //LogUtils.i(OssHelper.class, "uploadJpegList", "currentIndex = " + currentIndex + " -- ossDirPath = " + ossDirPath);
    // file
    //final File source = sourceList.get(currentIndex);
    //if (FileUtils.isFileEmpty(source)) {
    //    ToastUtils.show(MyApp.get().getString(R.string.upload_file_no_exists));
    //    DialogUtils.dismiss(progress);
    //    LogUtils.w(OssHelper.class, "uploadJpegList", "currentIndex == " + currentIndex + " -- source == null");
    //    // 回调
    //    if (callBack != null) {
    //        MyApp.get().getHandler().post(() -> callBack.failure(sourceList, ""));
    //    }
    //    return null;
    //}
    // progress
    //if (progress != null) {
    //    MyApp.get().getHandler().post(() -> {
    //        String colonShow = MyApp.get().getString(R.string.are_upload_space_holder_holder);
    //        String progressShow = String.format(Locale.getDefault(), colonShow, currentIndex + 1, sourceList.size());
    //        progress.setContent(progressShow);
    //        if (currentIndex <= 0) {
    //            DialogHelper.showWithAnim(progress);
    //        }
    //    });
    //}
    // objectKey生成
    //String objectKey = createExtensionKey(ossDirPath, source);
    // 构造上传请求
    //String bucket = SPHelper.getOssInfo().getBucket();
    //PutObjectRequest put = new PutObjectRequest(bucket, objectKey, source.getAbsolutePath());
    // 异步上传时可以设置进度回调
    //put.setProgressCallback((request, currentSize, totalSize) -> {
    //    //LogUtils.d(LOG_TAG, "uploadJpegList: currentSize: " + currentSize + " totalSize: " + totalSize);
    //    if (progress != null && progress.isShowing()) {
    //        int percent = (int) (((float) currentSize / (float) totalSize) * 100);
    //        progress.setProgress(percent);
    //    }
    //});
    // 开始任务
    //final OSSAsyncTask task = getOssClient().asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
    //    @Override
    //    public void onSuccess(PutObjectRequest request, PutObjectResult result) {
    //        final String uploadKey = request.getObjectKey();
    //        LogUtils.i(OssHelper.class, "uploadJpegList", "onSuccess: currentIndex = " + currentIndex + " -- getObjectKey = " + uploadKey);
    //        ossPathList.add(uploadKey);
    //        if (currentIndex < sourceList.size() - 1) {
    //            // 没上传完毕
    //            uploadJpegList(progress, ossDirPath, sourceList, currentIndex + 1, ossPathList, callBack);
    //        } else {
    //            // 已上传完毕
    //            DialogUtils.dismiss(progress);
    //            // 回调
    //            if (callBack != null) {
    //                MyApp.get().getHandler().post(() -> callBack.success(sourceList, ossPathList));
    //            }
    //        }
    //    }
    //
    //    @Override
    //    public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
    //        DialogUtils.dismiss(progress);
    //        // 打印
    //        final String uploadKey = request.getObjectKey();
    //        LogUtils.w(OssHelper.class, "uploadJpegList", "onFailure: currentIndex = " + currentIndex + " -- getObjectKey = " + uploadKey);
    //        // 刷新oss
    //        ApiHelper.ossInfoUpdate();
    //        // 本地异常如网络异常等
    //        if (clientException != null) {
    //            ToastUtils.show(MyApp.get().getString(R.string.upload_fail_please_check_native_net));
    //            LogUtils.w(OssHelper.class, "uploadJpegList", clientException.getMessage());
    //            refreshOssClient();
    //        }
    //        // 服务异常
    //        if (serviceException != null) {
    //            ToastUtils.show(MyApp.get().getString(R.string.upload_fail_tell_we_this_bug));
    //            LogUtils.w(OssHelper.class, "uploadJpegList", serviceException.getRawMessage());
    //            LogUtils.w(OssHelper.class, "uploadJpegList", "serviceException = " + serviceException.toString());
    //        }
    //        // 回调
    //        if (callBack != null) {
    //            MyApp.get().getHandler().post(() -> callBack.failure(sourceList, ""));
    //        }
    //    }
    //});
    // processDialog
    //if (progress != null) {
    //    progress.setOnCancelListener(dialog -> {
    //        LogUtils.i(OssHelper.class, "uploadJpegList", "cancel");
    //        taskCancel(task);
    //    });
    //}
    //return task;
    //}

    /**
     * *****************************************具体上传*****************************************
     */
    // 上传本地Log日志，然后再删除这些日志 TODO 直接用uploadList方法
    public static void uploadLog() {
        File logDir = LogUtils.getLogDir();
        List<File> fileList = FileUtils.listFilesAndDirInDir(logDir, true);
        // 不存在不上传
        if (fileList != null && fileList.size() > 0) {
            OssInfo ossInfo = SPHelper.getOssInfo();
            String pathLog = ossInfo.getPathLog();
            // 开始遍历上传
            for (final File file : fileList) {
                String prefix = String.valueOf(Long.MAX_VALUE - DateUtils.getCurrentLong());
                String name = FileUtils.getFileNameNoExtension(file);
                User me = SPHelper.getMe();
                String userId = me == null ? "" : String.valueOf(me.getId());
                String extension = FileUtils.getFileExtension(file);
                String fileName = prefix + "_" + name + "_" + userId + extension;
                String ossFilePath = pathLog + fileName;
                uploadFileInBackground(file, ossFilePath, false, new OssUploadCallBack() {
                    @Override
                    public void success(File source, String ossPath) {
                        // 记得删除
                        FileUtils.deleteFile(file);
                    }

                    @Override
                    public void failure(File source, String errMsg) {
                    }
                });
            }
        }
    }

    // 意见 (压缩)
    public static void uploadSuggest(Activity activity, final File source, final OssUploadCallBack callBack) {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathSuggest = ossInfo.getPathSuggest();
        uploadMiniExtFileInForeground(activity, pathSuggest, source, callBack);
    }

    // 头像 (压缩 + 持久化)
    public static void uploadAvatar(Activity activity, final File source, final OssUploadCallBack callBack) {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathCoupleAvatar = ossInfo.getPathCoupleAvatar();
        uploadMiniExtFileInForeground(activity, pathCoupleAvatar, source, callBack);
    }

    // 墙纸 (限制 + 持久化)
    public static void uploadWall(Activity activity, final File source, final OssUploadCallBack callBack) {
        long wallPaperSize = SPHelper.getVipLimit().getWallPaperSize();
        if (source != null && source.length() >= wallPaperSize) {
            String sizeFormat = ConvertUtils.byte2FitSize(wallPaperSize);
            String format = String.format(Locale.getDefault(), activity.getString(R.string.image_too_large_cant_over_holder), sizeFormat);
            ToastUtils.show(format);
            if (callBack != null) {
                callBack.failure(source, "");
            }
            // vip跳转
            VipActivity.goActivity(activity);
            return;
        }
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathCoupleWall = ossInfo.getPathCoupleWall();
        uploadExtFileInForeground(activity, pathCoupleWall, source, callBack);
    }

    // 音频 (持久化)
    public static void uploadAudio(Activity activity, final File source, final OssUploadCallBack callBack) {
        long audioSize = SPHelper.getVipLimit().getAudioSize();
        if (source != null && source.length() >= audioSize) {
            String sizeFormat = ConvertUtils.byte2FitSize(audioSize);
            String format = String.format(Locale.getDefault(), activity.getString(R.string.audio_too_large_cant_over_holder), sizeFormat);
            ToastUtils.show(format);
            if (callBack != null) {
                callBack.failure(source, "");
            }
            // vip跳转
            VipActivity.goActivity(activity);
            return;
        }
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathNoteAudio = ossInfo.getPathNoteAudio();
        uploadExtFileInForeground(activity, pathNoteAudio, source, callBack);
    }

    // 视频封面 (压缩 +  持久化)
    public static void uploadVideoThumb(Activity activity, final File source, final OssUploadCallBack callBack) {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathNoteVideoThumb = ossInfo.getPathNoteVideoThumb();
        uploadMiniExtFileInForeground(activity, pathNoteVideoThumb, source, callBack);
    }

    // 视频 (持久化)
    public static void uploadVideo(Activity activity, final File source, final OssUploadCallBack callBack) {
        long videoSize = SPHelper.getVipLimit().getVideoSize();
        if (source != null && source.length() >= videoSize) {
            String sizeFormat = ConvertUtils.byte2FitSize(videoSize);
            String format = String.format(Locale.getDefault(), activity.getString(R.string.video_too_large_cant_over_holder), sizeFormat);
            ToastUtils.show(format);
            if (callBack != null) {
                callBack.failure(source, "");
            }
            // vip跳转
            VipActivity.goActivity(activity);
            return;
        }
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathNoteVideo = ossInfo.getPathNoteVideo();
        uploadExtFileInForeground(activity, pathNoteVideo, source, callBack);
    }

    // 相册 (压缩 + 持久化)
    public static void uploadAlbum(Activity activity, final File source, final OssUploadCallBack callBack) {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathNoteAlbum = ossInfo.getPathNoteAlbum();
        uploadMiniExtFileInForeground(activity, pathNoteAlbum, source, callBack);
    }

    // 照片 (限制 + 持久化)
    public static void uploadPicture(Activity activity, final List<String> sourceList, final OssUploadsCallBack callBack) {
        long imageSize = SPHelper.getVipLimit().getPictureSize();
        final List<File> fileList = ListHelper.getFileListByPath(sourceList);
        boolean overLimit = false;
        for (File file : fileList) {
            if (FileUtils.isFileEmpty(file)) continue;
            if (file.length() >= imageSize) {
                overLimit = true;
                break;
            }
        }
        if (overLimit) {
            String sizeFormat = ConvertUtils.byte2FitSize(imageSize);
            String format = String.format(Locale.getDefault(), activity.getString(R.string.image_too_large_cant_over_holder), sizeFormat);
            ToastUtils.show(format);
            if (callBack != null) {
                callBack.failure(fileList, "");
            }
            // vip跳转
            VipActivity.goActivity(activity);
            return;
        }
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathNotePicture = ossInfo.getPathNotePicture();
        uploadJpegList(activity, pathNotePicture, fileList, callBack);
    }

    // 耳语 (压缩 + 持久化)
    public static void uploadWhisper(Activity activity, final File source, final OssUploadCallBack callBack) {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathNoteWhisper = ossInfo.getPathNoteWhisper();
        uploadMiniExtFileInForeground(activity, pathNoteWhisper, source, callBack);
    }

    // 日记 (限制 + 持久化)
    public static void uploadDiary(Activity activity, final List<String> sourceList, final OssUploadsCallBack callBack) {
        long imageSize = SPHelper.getVipLimit().getDiaryImageSize();
        final List<File> fileList = ListHelper.getFileListByPath(sourceList);
        boolean overLimit = false;
        for (File file : fileList) {
            if (FileUtils.isFileEmpty(file)) continue;
            if (file.length() >= imageSize) {
                overLimit = true;
                break;
            }
        }
        if (overLimit) {
            String sizeFormat = ConvertUtils.byte2FitSize(imageSize);
            String format = String.format(Locale.getDefault(), activity.getString(R.string.image_too_large_cant_over_holder), sizeFormat);
            ToastUtils.show(format);
            if (callBack != null) {
                callBack.failure(fileList, "");
            }
            // vip跳转
            VipActivity.goActivity(activity);
            return;
        }
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathNoteDiary = ossInfo.getPathNoteDiary();
        uploadJpegList(activity, pathNoteDiary, fileList, callBack);
    }

    // 美食 (压缩 + 持久化)
    public static void uploadFood(Activity activity, final List<String> sourceList, final OssUploadsCallBack callBack) {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathNoteFood = ossInfo.getPathNoteFood();
        List<File> fileList = ListHelper.getFileListByPath(sourceList);
        compressJpegList(activity, pathNoteFood, fileList, callBack);
    }

    // 礼物 (压缩 + 持久化)
    public static void uploadGift(Activity activity, final List<String> sourceList, final OssUploadsCallBack callBack) {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathNoteGift = ossInfo.getPathNoteGift();
        List<File> fileList = ListHelper.getFileListByPath(sourceList);
        compressJpegList(activity, pathNoteGift, fileList, callBack);
    }

    // 电影 (压缩 + 持久化)
    public static void uploadMovie(Activity activity, final List<String> sourceList, final OssUploadsCallBack callBack) {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathNoteMovie = ossInfo.getPathNoteMovie();
        List<File> fileList = ListHelper.getFileListByPath(sourceList);
        compressJpegList(activity, pathNoteMovie, fileList, callBack);
    }

    // 帖子 (压缩)
    public static void uploadTopicPost(Activity activity, final List<String> sourceList, final OssUploadsCallBack callBack) {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathTopicPost = ossInfo.getPathTopicPost();
        List<File> fileList = ListHelper.getFileListByPath(sourceList);
        compressJpegList(activity, pathTopicPost, fileList, callBack);
    }

    // 作品 (压缩)
    public static void uploadMoreMatch(Activity activity, final File source, final OssUploadCallBack callBack) {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathMoreMatch = ossInfo.getPathMoreMatch();
        uploadMiniExtFileInForeground(activity, pathMoreMatch, source, callBack);
    }

}
