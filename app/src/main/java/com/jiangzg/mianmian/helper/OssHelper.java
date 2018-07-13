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
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.BroadcastUtils;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.DialogUtils;
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

    // oos 对象
    private static OSS ossClient;
    private static String bucket;

    /**
     * 刷新ossClient
     */
    public static void refreshOssClient() {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String expireTime = DateUtils.getString(TimeHelper.getJavaTimeByGo(ossInfo.getExpireTime()), ConstantUtils.FORMAT_LINE_M_D_H_M);
        LogUtils.i(OssHelper.class, "refreshOssClient", "sts将在 " + expireTime + " 过期");
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
            // 十分钟过期时间
            String url = getOssClient().presignConstrainedObjectURL(bucket, objKey, 60 * 10);
            LogUtils.d(OssHelper.class, "getUrl", url);
            return url;
        } catch (ClientException e) {
            LogUtils.e(OssHelper.class, "getUrl", e);
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

    // 给后台看的 所以用CST时区
    private static String createJpegKey(String dir) {
        String uuid = StringUtils.getUUID(8);
        return dir + DateUtils.getCurrentString(ConstantUtils.FORMAT_CHINA_Y_M_D__H_M_S_S) + "-" + uuid + ".jpeg";
    }

    // 给后台看的 所以用CST时区
    private static String createExtensionKey(String dir, File source) {
        String uuid = StringUtils.getUUID(8);
        String extension = FileUtils.getFileExtension(source);
        return dir + DateUtils.getCurrentString(ConstantUtils.FORMAT_CHINA_Y_M_D__H_M_S_S) + "-" + uuid + extension;
    }

    // 取消任务
    private static void taskCancel(OSSAsyncTask task) {
        if (task != null && !task.isCanceled() && !task.isCompleted()) {
            task.cancel();
        }
    }

    /**
     * *****************************************单文件上传*****************************************
     */
    // 启动压缩
    private static void compressJpeg(final Activity activity, final String ossDirPath,
                                     final File source, final OssUploadCallBack callBack) {
        // file
        if (FileUtils.isFileEmpty(source)) {
            ToastUtils.show(MyApp.get().getString(R.string.upload_file_no_exists));
            LogUtils.w(OssHelper.class, "compressJpeg", "source == null");
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
                        String size = ConvertUtils.byte2FitSize(source.length());
                        LogUtils.d(OssHelper.class, "compressJpeg", " 压缩前大小: " + source.getName() + " = " + size);
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
                            LogUtils.d(OssHelper.class, "compressJpeg", " 压缩后大小: " + source.getName() + " = " + size);
                            // upload
                            uploadJpeg(activity, ossDirPath, file, callBack);
                        } else {
                            String size = ConvertUtils.byte2FitSize(source.length());
                            LogUtils.d(OssHelper.class, "compressJpeg", " 压缩后大小: " + source.getName() + " = " + size);
                            // upload
                            uploadJpeg(activity, ossDirPath, source, callBack);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        LogUtils.e(OssHelper.class, "compressJpeg", t);
                        DialogUtils.dismiss(progress);
                        // upload
                        uploadJpeg(activity, ossDirPath, source, callBack);
                    }
                })
                .launch();
    }

    // 上传任务 图片
    private static OSSAsyncTask uploadJpeg(Activity activity, final String ossDirPath, final File source, final OssUploadCallBack callBack) {
        // ossDirPath
        if (StringUtils.isEmpty(ossDirPath)) {
            ToastUtils.show(MyApp.get().getString(R.string.access_resource_path_no_exists));
            LogUtils.w(OssHelper.class, "uploadJpeg", "ossDirPath == null");
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
        // objectKey
        String objectKey = createJpegKey(ossDirPath);
        // 开始时上传
        return uploadFileWithDialogToast(activity, source, objectKey, callBack);
    }

    // 上传任务 根据后缀名
    private static OSSAsyncTask uploadExtension(Activity activity, final String ossDirPath, final File source, final OssUploadCallBack callBack) {
        // ossDirPath
        if (StringUtils.isEmpty(ossDirPath)) {
            ToastUtils.show(MyApp.get().getString(R.string.access_resource_path_no_exists));
            LogUtils.w(OssHelper.class, "uploadAudio", "ossDirPath == null");
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
        // objectKey
        String objectKey = createExtensionKey(ossDirPath, source);
        // 开始时上传
        return uploadFileWithDialogToast(activity, source, objectKey, callBack);
    }

    // 上传任务 有对话框和toast
    private static OSSAsyncTask uploadFileWithDialogToast(Activity activity, final File source, String ossFilePath, final OssUploadCallBack callBack) {
        // dialog
        MaterialDialog progress = DialogHelper.getBuild(activity)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .content(R.string.are_upload)
                .progress(false, 100)
                .negativeText(R.string.cancel_upload)
                .build();
        // 开始时上传
        return uploadFile(progress, true, source, ossFilePath, callBack);
    }

    // 上传任务 root
    private static OSSAsyncTask uploadFile(final MaterialDialog progress, final boolean toast, final File source,
                                           String ossFilePath, final OssUploadCallBack callBack) {
        // file
        if (FileUtils.isFileEmpty(source)) {
            if (toast) {
                ToastUtils.show(MyApp.get().getString(R.string.upload_file_no_exists));
            }
            LogUtils.i(OssHelper.class, "uploadFile", "source == null");
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
        // ossFilePath
        if (StringUtils.isEmpty(ossFilePath)) {
            if (toast) {
                ToastUtils.show(MyApp.get().getString(R.string.access_resource_path_no_exists));
            }
            LogUtils.w(OssHelper.class, "uploadFile", "ossFilePath == null");
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
        LogUtils.i(OssHelper.class, "uploadFile", "ossFilePath = " + ossFilePath);
        // dialog
        if (progress != null && !progress.isShowing()) {
            MyApp.get().getHandler().post(new Runnable() {
                @Override
                public void run() {
                    DialogHelper.showWithAnim(progress);
                }
            });
        }
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucket, ossFilePath, source.getAbsolutePath());
        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                //LogUtils.d(LOG_TAG, "uploadFile: currentSize: " + currentSize + " totalSize: " + totalSize);
                if (progress != null && progress.isShowing()) {
                    int percent = (int) (((float) currentSize / (float) totalSize) * 100);
                    progress.setProgress(percent);
                }
            }
        });
        // 开始任务
        final OSSAsyncTask task = getOssClient().asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                DialogUtils.dismiss(progress);
                // 回调
                final String uploadKey = request.getObjectKey();
                LogUtils.i(OssHelper.class, "uploadFile", "onSuccess: objectKey = " + uploadKey);
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
                DialogUtils.dismiss(progress);
                // 打印
                final String uploadKey = request.getObjectKey();
                LogUtils.w(OssHelper.class, "uploadFile", "onFailure: objectKey == " + uploadKey);
                // 本地异常如网络异常等
                if (clientException != null) {
                    if (toast) {
                        ToastUtils.show(MyApp.get().getString(R.string.upload_fail_please_check_native_net));
                    }
                    LogUtils.e(OssHelper.class, "uploadFile", clientException);
                }
                // 服务异常
                if (serviceException != null) {
                    if (toast) {
                        ToastUtils.show(MyApp.get().getString(R.string.upload_fail_tell_we_this_bug));
                    }
                    LogUtils.e(OssHelper.class, "uploadFile", serviceException);
                    LogUtils.w(OssHelper.class, "uploadFile", "serviceException = " + serviceException.toString());
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
                    LogUtils.i(OssHelper.class, "uploadFile", "cancel");
                    taskCancel(task);
                }
            });
        }
        return task;
    }

    /**
     * *****************************************多文件上传*****************************************
     */
    // 启动多张压缩
    private static void compressJpegList(final Activity activity, final String ossDirPath,
                                         final List<File> sourceList, final OssUploadsCallBack callBack) {
        MaterialDialog progress = DialogHelper.getBuild(activity)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .content(R.string.image_is_compress)
                .progress(true, 0)
                .progressIndeterminateStyle(false)
                .build();
        compressJpegList(activity, progress, ossDirPath, sourceList, 0, callBack);
    }

    private static void compressJpegList(final Activity activity, final MaterialDialog progress,
                                         final String ossDirPath, final List<File> sourceList, final int currentIndex,
                                         final OssUploadsCallBack callBack) {
        // currentIndex
        if (sourceList == null || sourceList.size() <= 0 || sourceList.size() <= currentIndex) {
            ToastUtils.show(MyApp.get().getString(R.string.not_found_upload_file));
            DialogUtils.dismiss(progress);
            LogUtils.w(OssHelper.class, "compressJpegList", "currentIndex = " + currentIndex + " -- sourceList == null");
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
            DialogUtils.dismiss(progress);
            LogUtils.w(OssHelper.class, "compressJpegList", "currentIndex = " + currentIndex + " -- ossDirPath == null");
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
            DialogUtils.dismiss(progress);
            LogUtils.w(OssHelper.class, "compressJpegList", "currentIndex = " + currentIndex + " -- source == null");
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
            MyApp.get().getHandler().post(new Runnable() {
                @Override
                public void run() {
                    String colonShow = MyApp.get().getString(R.string.are_compress_space_holder_holder);
                    String progressShow = String.format(Locale.getDefault(), colonShow, currentIndex + 1, sourceList.size());
                    progress.setContent(progressShow);
                    if (currentIndex <= 0) {
                        DialogHelper.showWithAnim(progress);
                    }
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
                        LogUtils.d(OssHelper.class, "compressJpegList", " 压缩前大小: " + source.getName() + " = " + size);
                        // dialog上面已经显示了
                    }

                    @Override
                    public void onSuccess(File file) {
                        // 替换sourceList中被压缩的文件
                        if (FileUtils.isFileExists(file)) {
                            // 压缩完的文件为新文件
                            String size = ConvertUtils.byte2FitSize(file.length());
                            LogUtils.d(OssHelper.class, "compressJpegList", "压缩后大小: " + source.getName() + " = " + size);
                            sourceList.set(currentIndex, file);
                        } else {
                            // 压缩完保存的还是源文件
                            String size = ConvertUtils.byte2FitSize(source.length());
                            LogUtils.d(OssHelper.class, "compressJpegList", "压缩后大小: " + source.getName() + " = " + size);
                            sourceList.set(currentIndex, source);
                        }
                        // upload
                        if (currentIndex < sourceList.size() - 1) {
                            // 没压缩完
                            compressJpegList(activity, progress, ossDirPath, sourceList, currentIndex + 1, callBack);
                        } else {
                            // 全压缩完
                            DialogUtils.dismiss(progress);
                            uploadJpegList(activity, ossDirPath, sourceList, callBack);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(OssHelper.class, "compressJpegList", e);
                        // upload
                        if (currentIndex < sourceList.size() - 1) {
                            // 没压缩完
                            compressJpegList(activity, progress, ossDirPath, sourceList, currentIndex + 1, callBack);
                        } else {
                            // 全压缩完
                            DialogUtils.dismiss(progress);
                            uploadJpegList(activity, ossDirPath, sourceList, callBack);
                        }
                    }
                })
                .launch();
    }

    // 上传任务(有对话框)
    private static OSSAsyncTask uploadJpegList(final Activity activity, final String ossDirPath,
                                               final List<File> sourceList, final OssUploadsCallBack callBack) {
        MaterialDialog progress = DialogHelper.getBuild(activity)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .content(R.string.are_upload)
                .progress(false, 100)
                .negativeText(R.string.cancel_upload)
                .build();
        return uploadJpegList(progress, ossDirPath, sourceList, 0, new ArrayList<String>(), callBack);
    }

    // 上传任务
    private static OSSAsyncTask uploadJpegList(final MaterialDialog progress, final String ossDirPath,
                                               final List<File> sourceList, final int currentIndex,
                                               final List<String> ossPathList, final OssUploadsCallBack callBack) {
        // currentIndex
        if (sourceList == null || sourceList.size() <= 0 || sourceList.size() <= currentIndex) {
            ToastUtils.show(MyApp.get().getString(R.string.not_found_upload_file));
            DialogUtils.dismiss(progress);
            LogUtils.w(OssHelper.class, "uploadJpegList", "currentIndex = " + currentIndex + " -- sourceList == null");
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
            DialogUtils.dismiss(progress);
            LogUtils.w(OssHelper.class, "uploadJpegList", "currentIndex = " + currentIndex + " -- ossDirPath == null");
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
        LogUtils.i(OssHelper.class, "uploadJpegList", "currentIndex = " + currentIndex + " -- ossDirPath = " + ossDirPath);
        // file
        final File source = sourceList.get(currentIndex);
        if (FileUtils.isFileEmpty(source)) {
            ToastUtils.show(MyApp.get().getString(R.string.upload_file_no_exists));
            DialogUtils.dismiss(progress);
            LogUtils.w(OssHelper.class, "uploadJpegList", "currentIndex == " + currentIndex + " -- source == null");
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
            MyApp.get().getHandler().post(new Runnable() {
                @Override
                public void run() {
                    String colonShow = MyApp.get().getString(R.string.are_upload_space_holder_holder);
                    String progressShow = String.format(Locale.getDefault(), colonShow, currentIndex + 1, sourceList.size());
                    progress.setContent(progressShow);
                    if (currentIndex <= 0) {
                        DialogHelper.showWithAnim(progress);
                    }
                }
            });
        }
        // objectKey生成
        String objectKey = createJpegKey(ossDirPath);
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
        final OSSAsyncTask task = getOssClient().asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                final String uploadKey = request.getObjectKey();
                LogUtils.i(OssHelper.class, "uploadJpegList", "onSuccess: currentIndex = " + currentIndex + " -- getObjectKey = " + uploadKey);
                ossPathList.add(uploadKey);
                if (currentIndex < sourceList.size() - 1) {
                    // 没上传完毕
                    uploadJpegList(progress, ossDirPath, sourceList, currentIndex + 1, ossPathList, callBack);
                } else {
                    // 已上传完毕
                    DialogUtils.dismiss(progress);
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
                DialogUtils.dismiss(progress);
                // 打印
                final String uploadKey = request.getObjectKey();
                LogUtils.w(OssHelper.class, "uploadJpegList", "onFailure: currentIndex = " + currentIndex + " -- getObjectKey = " + uploadKey);
                // 本地异常如网络异常等
                if (clientException != null) {
                    ToastUtils.show(MyApp.get().getString(R.string.upload_fail_please_check_native_net));
                    LogUtils.e(OssHelper.class, "uploadJpegList", clientException);
                }
                // 服务异常
                if (serviceException != null) {
                    ToastUtils.show(MyApp.get().getString(R.string.upload_fail_tell_we_this_bug));
                    LogUtils.e(OssHelper.class, "uploadJpegList", serviceException);
                    LogUtils.w(OssHelper.class, "uploadJpegList", "serviceException = " + serviceException.toString());
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
                    LogUtils.i(OssHelper.class, "uploadJpegList", "cancel");
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
    public static OSSAsyncTask downloadObject(final MaterialDialog progress, final boolean toast, final String objectKey, final File target, final OssDownloadCallBack callBack) {
        // objectKey
        if (StringUtils.isEmpty(objectKey)) {
            // dialog
            DialogUtils.dismiss(progress);
            LogUtils.w(OssHelper.class, "downloadObject", "objectKey == null");
            // 删除下载文件
            ResHelper.deleteFileInBackground(target);
            if (toast) {
                ToastUtils.show(MyApp.get().getString(R.string.access_resource_path_no_exists));
            }
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
        LogUtils.i(OssHelper.class, "downloadObject", "objectKey = " + objectKey);
        // file
        if (target == null) {
            // dialog
            DialogUtils.dismiss(progress);
            LogUtils.w(OssHelper.class, "downloadObject", "target == null");
            if (toast) {
                ToastUtils.show(MyApp.get().getString(R.string.save_file_no_exists));
            }
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
        FileUtils.createFileByDeleteOldFile(target);
        // dialog
        MyApp.get().getHandler().post(new Runnable() {
            @Override
            public void run() {
                DialogHelper.showWithAnim(progress);
            }
        });
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
        final OSSAsyncTask task = getOssClient().asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                final String downloadKey = request.getObjectKey();
                LogUtils.i(OssHelper.class, "downloadObject", "onSuccess: getObjectKey = " + downloadKey);
                // 开始解析文件
                InputStream inputStream = result.getObjectContent();
                boolean ok = FileUtils.writeFileFromIS(target, inputStream, false);
                // 对话框
                DialogUtils.dismiss(progress);
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
                    if (toast) {
                        ToastUtils.show(MyApp.get().getString(R.string.file_resolve_fail_tell_we_this_bug));
                    }
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
                DialogUtils.dismiss(progress);
                // 删除源文件
                ResHelper.deleteFileInBackground(target);
                // 打印
                final String downloadKey = request.getObjectKey();
                LogUtils.w(OssHelper.class, "downloadObject", "onFailure: getObjectKey == " + downloadKey);
                // 本地异常如网络异常等
                if (clientException != null) {
                    if (toast) {
                        ToastUtils.show(MyApp.get().getString(R.string.download_fail_please_check_native_net));
                        LogUtils.e(OssHelper.class, "downloadObject", clientException);
                    }
                }
                // 服务异常
                if (serviceException != null) {
                    if (toast) {
                        ToastUtils.show(MyApp.get().getString(R.string.download_fail_tell_we_this_bug));
                    }
                    LogUtils.e(OssHelper.class, "downloadObject", serviceException);
                    LogUtils.w(OssHelper.class, "downloadObject", "serviceException = " + serviceException.toString());
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
                    LogUtils.d(OssHelper.class, "downloadObject", "cancel");
                    // 删除下载文件
                    ResHelper.deleteFileInBackground(target);
                    // 取消任务
                    taskCancel(task);
                }
            });
        }
        return task;
    }

    /**
     * *****************************************具体对接*****************************************
     */
    // apk
    public static void downloadApk(Activity activity, String objectKey, File target, OssDownloadCallBack callBack) {
        MaterialDialog progress = null;
        if (activity != null) {
            progress = DialogHelper.getBuild(activity)
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .content(R.string.are_download)
                    .progress(false, 100)
                    .negativeText(R.string.cancel_download)
                    .build();
        }
        downloadObject(progress, true, objectKey, target, callBack);
    }

    // oss缓存文件下载
    public static void downloadOssFileByKey(String objectKey) {
        File file = OssResHelper.newKeyFile(objectKey);
        downloadObject(null, false, objectKey, file, null);
    }

    // 全屏图下载
    public static void downloadBigImage(final Activity activity, final String objectKey) {
        if (StringUtils.isEmpty(objectKey)) {
            LogUtils.w(OssHelper.class, "downloadBigImage", "下载文件不存在！");
            ToastUtils.show(MyApp.get().getString(R.string.download_file_no_exists));
            return;
        }
        // 权限检查
        PermUtils.requestPermissions(activity, ConsHelper.REQUEST_APP_INFO, PermUtils.appInfo, new PermUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted(int requestCode, String[] permissions) {
                final File target = ResHelper.newSdCardImageFile(objectKey);
                // 目标文件创建检查
                if (target == null) {
                    ToastUtils.show(MyApp.get().getString(R.string.file_create_fail));
                    return;
                }
                // 目标文件重复检查
                String format = MyApp.get().getString(R.string.already_download_to_colon_holder);
                final String sucToast = String.format(Locale.getDefault(), format, target.getAbsoluteFile());
                if (FileUtils.isFileExists(target) && target.length() > 0) {
                    LogUtils.d(OssHelper.class, "downloadBigImage", "下载文件已存在！");
                    ToastUtils.show(sucToast);
                    return;
                } else {
                    FileUtils.deleteFile(target); // 清除空文件
                }
                // 在OssRes中是否存在(book之类的缓存)
                if (OssResHelper.isKeyFileExists(objectKey)) {
                    File source = OssResHelper.newKeyFile(objectKey);
                    boolean copy = FileUtils.copyOrMoveFile(source, target, false);
                    if (copy) {
                        LogUtils.d(OssHelper.class, "downloadBigImage", "文件复制成功！");
                        BroadcastUtils.refreshMediaImageInsert(ResHelper.PROVIDER_AUTH, target);
                        ToastUtils.show(sucToast);
                        return;
                    }
                    LogUtils.w(OssHelper.class, "downloadBigImage", "文件复制失败！");
                } else {
                    LogUtils.d(OssHelper.class, "downloadBigImage", "下载本地没有的文件");
                }
                // 开始下载
                downloadObject(null, true, objectKey, target, new OssDownloadCallBack() {
                    @Override
                    public void success(String ossPath) {
                        // 下载完通知图库媒体
                        BroadcastUtils.refreshMediaImageInsert(ResHelper.PROVIDER_AUTH, target);
                        ToastUtils.show(sucToast);
                    }

                    @Override
                    public void failure(String ossPath) {
                    }
                });
            }

            @Override
            public void onPermissionDenied(int requestCode, String[] permissions) {
                DialogHelper.showGoPermDialog(activity);
            }
        });
    }

    // 上传本地Log日志，然后再删除这些日志
    public static void uploadLog() {
        File logDir = LogUtils.getLogDir();
        List<File> fileList = FileUtils.listFilesAndDirInDir(logDir, true);
        // 不存在不上传
        if (fileList != null && fileList.size() > 0) {
            OssInfo ossInfo = SPHelper.getOssInfo();
            String pathLog = ossInfo.getPathLog();
            // 开始遍历上传
            for (final File file : fileList) {
                String name = FileUtils.getFileNameNoExtension(file);
                String userId = String.valueOf(SPHelper.getMe().getId());
                String extension = FileUtils.getFileExtension(file);
                String fileName = name + "_" + userId + extension;
                String ossFilePath = pathLog + fileName;
                uploadFile(null, false, file, ossFilePath, new OssUploadCallBack() {
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
        compressJpeg(activity, pathSuggest, source, callBack);
    }

    // 头像 (裁剪 + 压缩 + 持久化)
    public static void uploadAvatar(Activity activity, final File source, final OssUploadCallBack callBack) {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathCoupleAvatar = ossInfo.getPathCoupleAvatar();
        compressJpeg(activity, pathCoupleAvatar, source, callBack);
    }

    // 墙纸 (持久化)
    public static void uploadWall(Activity activity, final File source, final OssUploadCallBack callBack) {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathCoupleWall = ossInfo.getPathCoupleWall();
        uploadJpeg(activity, pathCoupleWall, source, callBack);
    }

    // 耳语 (压缩 + 持久化)
    public static void uploadWhisper(Activity activity, final File source, final OssUploadCallBack callBack) {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathBookWhisper = ossInfo.getPathBookWhisper();
        compressJpeg(activity, pathBookWhisper, source, callBack);
    }

    // 日记 (限制大小 + 持久化)
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
            String imageSizeFormat = ConvertUtils.byte2FitSize(imageSize);
            String format = String.format(Locale.getDefault(), activity.getString(R.string.image_too_large_cant_over_holder), imageSizeFormat);
            ToastUtils.show(format);
            if (callBack != null) {
                callBack.failure(fileList, "");
            }
            return;
        }
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathBookDiary = ossInfo.getPathBookDiary();
        uploadJpegList(activity, pathBookDiary, fileList, callBack);
    }

    // 相册 (压缩 + 持久化)
    public static void uploadAlbum(Activity activity, final File source, final OssUploadCallBack callBack) {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathBookAlbum = ossInfo.getPathBookAlbum();
        compressJpeg(activity, pathBookAlbum, source, callBack);
    }

    // 照片 (限制大小 + 持久化)
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
            String imageSizeFormat = ConvertUtils.byte2FitSize(imageSize);
            String format = String.format(Locale.getDefault(), activity.getString(R.string.image_too_large_cant_over_holder), imageSizeFormat);
            ToastUtils.show(format);
            if (callBack != null) {
                callBack.failure(fileList, "");
            }
            return;
        }
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathBookPicture = ossInfo.getPathBookPicture();
        uploadJpegList(activity, pathBookPicture, fileList, callBack);
    }

    // 音频 (限制大小 +  持久化)
    public static void uploadAudio(Activity activity, final File source, final OssUploadCallBack callBack) {
        long audioSize = SPHelper.getVipLimit().getAudioSize();
        if (source != null && source.length() >= audioSize) {
            String audioSizeFormat = ConvertUtils.byte2FitSize(audioSize);
            String format = String.format(Locale.getDefault(), activity.getString(R.string.audio_too_large_cant_over_holder), audioSizeFormat);
            ToastUtils.show(format);
            if (callBack != null) {
                callBack.failure(source, "");
            }
            return;
        }
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathBookAudio = ossInfo.getPathBookAudio();
        uploadExtension(activity, pathBookAudio, source, callBack);
    }

    // 视频封面 (压缩 +  持久化)
    public static void uploadVideoThumb(Activity activity, final File source, final OssUploadCallBack callBack) {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathBookVideoThumb = ossInfo.getPathBookVideoThumb();
        compressJpeg(activity, pathBookVideoThumb, source, callBack);
    }

    // 视频 (限制大小 +  持久化)
    public static void uploadVideo(Activity activity, final File source, final OssUploadCallBack callBack) {
        long videoSize = SPHelper.getVipLimit().getVideoSize();
        if (source != null && source.length() >= videoSize) {
            String videoSizeFormat = ConvertUtils.byte2FitSize(videoSize);
            String format = String.format(Locale.getDefault(), activity.getString(R.string.video_too_large_cant_over_holder), videoSizeFormat);
            ToastUtils.show(format);
            if (callBack != null) {
                callBack.failure(source, "");
            }
            return;
        }
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathBookVideo = ossInfo.getPathBookVideo();
        uploadExtension(activity, pathBookVideo, source, callBack);
    }

    // 美食 (压缩 + 持久化)
    public static void uploadFood(Activity activity, final List<String> sourceList, final OssUploadsCallBack callBack) {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathBookFood = ossInfo.getPathBookFood();
        List<File> fileList = ListHelper.getFileListByPath(sourceList);
        compressJpegList(activity, pathBookFood, fileList, callBack);
    }

    // 礼物 (压缩 + 持久化)
    public static void uploadGift(Activity activity, final List<String> sourceList, final OssUploadsCallBack callBack) {
        OssInfo ossInfo = SPHelper.getOssInfo();
        String pathBookGift = ossInfo.getPathBookGift();
        List<File> fileList = ListHelper.getFileListByPath(sourceList);
        compressJpegList(activity, pathBookGift, fileList, callBack);
    }

}
