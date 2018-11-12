package com.jiangzg.lovenote.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.application.AppUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentFactory;
import com.jiangzg.base.system.LanguageUtils;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.couple.CouplePairActivity;
import com.jiangzg.lovenote.activity.settings.SuggestAddActivity;
import com.jiangzg.lovenote.activity.user.SplashActivity;
import com.jiangzg.lovenote.activity.user.UserInfoActivity;
import com.jiangzg.lovenote.base.MyApp;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.domain.Version;
import com.jiangzg.lovenote.service.UpdateService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by gg on 2017/4/10.
 * Retrofit管理工具类
 */
public class RetrofitHelper {

    private HashMap<String, String> mHeaders;
    private Factory mFactory;
    private HttpLoggingInterceptor.Level mLog;
    private String mBaseUrl;

    public RetrofitHelper() {
        this.mBaseUrl = API.BASE_URL; //默认api
        this.mHeaders = getHeadJson(); // 默认全参
        this.mFactory = Factory.gson; // 默认gson
        this.mLog = HttpLoggingInterceptor.Level.BODY; // 默认打印全部
    }

    public static HashMap<String, String> getHead() {
        HashMap<String, String> options = new HashMap<>();
        options.put("Accept", "application/json");
        options.put("access_token", SPHelper.getMe().getUserToken());
        options.put("app_key", "59fj48dj327fdl19fdi28cas5d20jd83");
        options.put("platform", "android");
        options.put("sign", AppInfo.get().getSHA1());
        options.put("language", LanguageUtils.getLocale().getLanguage());
        return options;
    }

    public static HashMap<String, String> getHeadJson() {
        HashMap<String, String> options = getHead();
        options.put("Content-Type", "application/json;charset=utf-8");
        return options;
    }

    public static HashMap<String, String> getHeadForm() {
        HashMap<String, String> options = getHead();
        options.put("Content-Type", "application/x-www-form-urlencoded");
        return options;
    }

    /**
     * @param baseURL head参数
     */
    public RetrofitHelper baseURL(String baseURL) {
        if (StringUtils.isEmpty(baseURL)) return this;
        this.mBaseUrl = baseURL;
        return this;

    }

    /**
     * @param headers head参数
     */
    public RetrofitHelper head(HashMap<String, String> headers) {
        if (headers == null) return this;
        mHeaders = headers;
        return this;

    }

    /**
     * @param factory 返回数据构造器
     */
    public RetrofitHelper factory(Factory factory) {
        if (factory == null) return this;
        mFactory = factory;
        return this;
    }

    /**
     * @param log 日志打印开关
     */
    public RetrofitHelper log(HttpLoggingInterceptor.Level log) {
        if (log == null) return this;
        mLog = log;
        return this;
    }

    /**
     * @return 获取call
     */
    public <T extends API> T call(Class<T> clz) {
        if (clz == null) return null;
        Interceptor head = getHead(mHeaders); // head
        Interceptor log = getLogInterceptor(mLog); // log
        OkHttpClient client = getClient(head, log); // client
        Converter.Factory factory = getFactory(mFactory); // factory
        Retrofit retrofit = getRetrofit(mBaseUrl, client, factory); // retrofit
        return retrofit.create(clz); // call
    }

    public static void cancel(Call call) {
        if (call == null) return;
        if (!call.isCanceled()) {
            call.cancel();
        }
        call = null;
    }

    /**
     * @param call     请求体
     * @param callBack 请求回调
     */
    public static void enqueue(final Call<Result> call, final MaterialDialog loading, final CallBack callBack) {
        if (call == null) {
            LogUtils.w(RetrofitHelper.class, "enqueue", "call == null");
            return;
        }
        // 对话框
        if (loading != null) {
            loading.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    cancel(call);
                }
            });
            MyApp.get().getHandler().post(new Runnable() {
                @Override
                public void run() {
                    DialogUtils.show(loading);
                }
            });
        }
        // 开始请求
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                onResponseCall(loading, response, callBack);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                onFailureCall(loading, t, callBack);
            }
        });
    }

    /* http请求回调 */
    public interface CallBack {
        void onResponse(int code, String message, Result.Data data);

        void onFailure(int code, String message, Result.Data data);
    }

    // 成功回调
    private static void onResponseCall(Dialog loading, Response<Result> response, CallBack callBack) {
        if (loading != null) DialogUtils.dismiss(loading);
        if (response == null) return;
        // 获取body
        String errorStr = "";
        Result body;
        int status = response.code();
        if (status == 200) {
            body = response.body();
        } else {
            try {
                errorStr = response.errorBody().string();
            } catch (IOException e) {
                LogUtils.e(RetrofitHelper.class, "onResponseCall", e);
            }
            if (StringUtils.isEmpty(errorStr)) {
                body = new Result();
                body.setCode(Result.RESULT_CODE_TOAST);
                body.setMessage(MyApp.get().getString(R.string.err_data_null));
            } else if (errorStr.startsWith("{")) {
                body = GsonHelper.get().fromJson(errorStr, Result.class);
            } else {
                body = new Result();
                body.setCode(Result.RESULT_CODE_TOAST);
                body.setMessage(MyApp.get().getString(R.string.err_data_parse));
                LogUtils.w(RetrofitHelper.class, "onResponseCall", errorStr);
            }
        }
        int code = body.getCode();
        String message = body.getMessage();
        Result.Data data = body.getData();
        // status处理 status基本为统一处理的逻辑 code为私有处理逻辑
        final Activity top = ActivityStack.getTop();
        switch (status) {
            case 200: // 成功
                ToastUtils.show(message);
                break;
            case 401: // 用户验证失败
                if (ActivityStack.isActivityFinish(top)) return;
                SplashActivity.goActivity(top);
                break;
            case 403: // 禁止访问,key错误
            case 404: // url不存在
            case 405: // method错误
            case 406: // 用户被禁用,应该退出应用
                if (ActivityStack.isActivityFinish(top)) return;
                MaterialDialog dialogMsg = DialogHelper.getBuild(top)
                        .cancelable(true)
                        .canceledOnTouchOutside(false)
                        .content(message)
                        .positiveText(R.string.i_want_feedback)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                SuggestAddActivity.goActivity(top);
                            }
                        })
                        .negativeText(R.string.i_know)
                        .build();
                DialogHelper.showWithAnim(dialogMsg);
                break;
            case 408: // 请求超时
                if (ActivityStack.isActivityFinish(top)) return;
                MaterialDialog dialog408 = DialogHelper.getBuild(top)
                        .cancelable(true)
                        .canceledOnTouchOutside(true)
                        .content(R.string.http_error_time_maybe_setting_wrong)
                        .positiveText(R.string.go_to_setting)
                        .negativeText(R.string.i_know)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Intent netSettings = IntentFactory.getNetSettings();
                                ActivityTrans.start(top, netSettings);
                            }
                        })
                        .build();
                DialogHelper.showWithAnim(dialog408);
                break;
            case 409: // 用户版本过低,提示用户升级
                if (data == null) return;
                List<Version> versionList = data.getVersionList();
                UpdateService.showUpdateDialog(versionList);
                break;
            case 417: // 逻辑错误，必须返回错误信息
                check417Code(top, body);
                break;
            case 500: // 服务器异常
                if (ActivityStack.isActivityFinish(top)) return;
                MaterialDialog dialog500 = DialogHelper.getBuild(top)
                        .cancelable(true)
                        .canceledOnTouchOutside(false)
                        .content(R.string.server_error)
                        .positiveText(R.string.i_know)
                        .dismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                AppUtils.appExit();
                            }
                        })
                        .build();
                DialogHelper.showWithAnim(dialog500);
                break;
            case 503: // 服务器维护
                if (ActivityStack.isActivityFinish(top)) return;
                MaterialDialog dialog503 = DialogHelper.getBuild(top)
                        .cancelable(false)
                        .canceledOnTouchOutside(false)
                        .content(message + "\n" + SPHelper.getCommonConst().getOfficialGroup())
                        .positiveText(R.string.i_know)
                        .dismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                AppUtils.appExit();
                            }
                        })
                        .build();
                DialogHelper.showWithAnim(dialog503);
                break;
            default: // 其他错误
                if (ActivityStack.isActivityFinish(top)) return;
                MaterialDialog dialogDef = DialogHelper.getBuild(top)
                        .cancelable(true)
                        .canceledOnTouchOutside(false)
                        .content(message)
                        .positiveText(R.string.i_want_feedback)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                SuggestAddActivity.goActivity(top);
                            }
                        })
                        .negativeText(R.string.i_know)
                        .build();
                DialogHelper.showWithAnim(dialogDef);
                break;
        }
        // 回调
        if (callBack != null) {
            if (status == 200) {
                callBack.onResponse(code, message, data);
            } else {
                callBack.onFailure(code, message, data);
            }
        }
    }

    private static void check417Code(final Activity top, Result body) {
        int code = body.getCode();
        String message = body.getMessage();
        Result.Data data = body.getData();
        if (code == Result.RESULT_CODE_TOAST) { // toast
            ToastUtils.show(message);
        } else if (code == Result.RESULT_CODE_DIALOG) { // dialog
            if (ActivityStack.isActivityFinish(top)) return;
            MaterialDialog dialog = DialogHelper.getBuild(top)
                    .cancelable(true)
                    .canceledOnTouchOutside(false)
                    .content(message)
                    .positiveText(R.string.i_know)
                    .build();
            DialogHelper.showWithAnim(dialog);
        } else if (code == Result.RESULT_CODE_NO_USER_INFO) { // userInfo
            ToastUtils.show(message);
            if (ActivityStack.isActivityFinish(top)) return;
            UserInfoActivity.goActivity(top, data.getUser());
        } else if (code == Result.RESULT_CODE_NO_CP) { // cp
            ToastUtils.show(message);
            if (ActivityStack.isActivityFinish(top)) return;
            CouplePairActivity.goActivity(top);
        }
    }

    // 失败回调
    private static void onFailureCall(Dialog loading, Throwable t, CallBack callBack) {
        DialogUtils.dismiss(loading);
        LogUtils.w(RetrofitHelper.class, "onFailureCall", t.toString());
        Class<? extends Throwable> clz = t.getClass();
        String error;
        if (clz.equals(java.net.ConnectException.class)) { // 网络环境
            error = MyApp.get().getString(R.string.http_error_connect);
        } else if (clz.equals(java.net.SocketTimeoutException.class)) { // 超时错误
            error = MyApp.get().getString(R.string.http_error_time);
        } else if (clz.equals(java.net.SocketException.class)) { // 请求取消
            error = "";
        } else { // 其他网络错误
            error = MyApp.get().getString(R.string.http_error_request);
        }
        ToastUtils.show(error);
        if (callBack != null) callBack.onFailure(-1, error, null);
    }

    /* 获取head */
    private Interceptor getHead(final HashMap<String, String> headParams) {
        return new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request.Builder builder = chain.request().newBuilder();
                for (String key : headParams.keySet()) {
                    builder.addHeader(key, headParams.get(key));
                }
                Request request = builder.build();
                return chain.proceed(request);
            }
        };
    }

    /* 获取日志拦截器 */
    private Interceptor getLogInterceptor(HttpLoggingInterceptor.Level log) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(
                new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        String log = message.trim();
                        if (StringUtils.isEmpty(log)) return;
                        LogUtils.d(RetrofitHelper.class, "getLogInterceptor", log);
                    }
                });
        loggingInterceptor.setLevel(log);
        return loggingInterceptor;
    }

    /* 获取OKHttp的client */
    private OkHttpClient getClient(Interceptor... interceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 添加拦截器
        for (Interceptor interceptor : interceptors) {
            if (interceptor != null) {
                builder.addInterceptor(interceptor);
            }
        }
        return builder.build();
    }

    /* 数据解析构造器 */
    private Converter.Factory getFactory(Factory factory) {
        if (factory == Factory.gson) {
            return GsonConverterFactory.create();
        } else if (factory == Factory.string) {
            return ScalarsConverterFactory.create();
        } else if (factory == Factory.empty) {
            return null;
        }
        return null;
    }

    /* 获取Retrofit实例 */
    private Retrofit getRetrofit(String baseURL, OkHttpClient client, Converter.Factory factory) {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(baseURL); // host
        if (factory != null) {
            builder.addConverterFactory(factory); // 解析构造器
        }
        builder.client(client); // client
        return builder.build();
    }

    /* 数据构造器 */
    public enum Factory {
        empty, string, gson
    }

//
//    public RequestBody string2PartBody(String body) {
//        return RequestBody.create(MediaType.parse("text/plain"), body);
//    }
//
//    public String file2PartKey(File file) {
//        return "file\";filename=\"" + file.getName();
//    }
//
//    public RequestBody img2PartBody(File file) {
//        return RequestBody.create(MediaType.parse("image/jpeg"), file);
//    }

}
