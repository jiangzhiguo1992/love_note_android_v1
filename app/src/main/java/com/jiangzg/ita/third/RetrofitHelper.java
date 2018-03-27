package com.jiangzg.ita.third;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.activity.ActivityStack;
import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.base.component.application.AppUtils;
import com.jiangzg.base.component.intent.IntentUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.common.SuggestAddActivity;
import com.jiangzg.ita.activity.user.LoginActivity;
import com.jiangzg.ita.activity.user.UserInfoActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.Version;
import com.jiangzg.ita.helper.PrefHelper;
import com.jiangzg.ita.service.UpdateService;

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

    private static final String LOG_TAG = "RetrofitHelper";

    public static final String APP_KEY = "59fj48dj327fdl19fdi28cas5d20jd83";

    private HashMap<String, String> mHeaders;
    private Factory mFactory;
    private HttpLoggingInterceptor.Level mLog;
    private String mBaseUrl;

    public RetrofitHelper() {
        this.mBaseUrl = API.BASE_URL; //默认api
        this.mHeaders = getJsonHead(); // 默认全参
        this.mFactory = Factory.gson; // 默认gson
        this.mLog = HttpLoggingInterceptor.Level.BODY; // 默认打印全部
    }

    private static HashMap<String, String> getHeadBase() {
        HashMap<String, String> options = new HashMap<>();
        options.put("Accept", "application/json");
        options.put("platform", "android");
        options.put("sign", ""); // todo
        options.put("app_key", APP_KEY);
        options.put("access_token", PrefHelper.getUser().getUserToken());
        return options;
    }

    public static HashMap<String, String> getJsonHead() {
        HashMap<String, String> options = getHeadBase();
        options.put("Content-Type", "application/json;charset=utf-8");
        return options;
    }

    public static HashMap<String, String> getFormHead() {
        HashMap<String, String> options = getHeadBase();
        options.put("Content-Type", "application/json;charset=utf-8");
        return options;
    }

    /* http请求回调 */
    public interface CallBack {
        void onResponse(int code, String message, Result.Data data);

        void onFailure();
    }

    public static void enqueueLoading(final Call<Result> call, MaterialDialog dialog, final CallBack callBack) {
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (call != null && !call.isCanceled()) {
                    call.cancel();
                }
            }
        });
        enqueue(call, dialog, callBack);
    }

    public static void enqueueDelay(Call<Result> call, final long totalWait, final CallBack callBack) {
        if (call == null) {
            LogUtils.e(LOG_TAG, "call == null");
            return;
        }
        final long startTime = DateUtils.getCurrentLong();

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(final Call<Result> call, final Response<Result> response) {
                long endTime = DateUtils.getCurrentLong();
                long between = endTime - startTime;
                if (between >= totalWait) {
                    // 间隔时间太大
                    onResponseCall(call, null, response, callBack);
                } else {
                    // 间隔时间太小
                    MyApp.get().getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onResponseCall(call, null, response, callBack);
                        }
                    }, totalWait - between);
                }
            }

            @Override
            public void onFailure(final Call call, final Throwable t) {
                long endTime = DateUtils.getCurrentLong();
                long between = endTime - startTime;
                if (between >= totalWait) {
                    // 间隔时间太大
                    onFailureCall(call, null, t, callBack);
                } else {
                    // 间隔时间太小
                    MyApp.get().getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onFailureCall(call, null, t, callBack);
                        }
                    }, totalWait - between);
                }
            }
        });
    }

    /**
     * @param call     请求体
     * @param callBack 请求回调
     */
    public static void enqueue(Call<Result> call, final Dialog loading, final CallBack callBack) {
        if (call == null) {
            LogUtils.e(LOG_TAG, "call == null");
            return;
        }
        if (loading != null && !loading.isShowing()) {
            loading.show();
        }

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                onResponseCall(call, loading, response, callBack);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                onFailureCall(call, loading, t, callBack);
            }
        });
    }

    private static void onResponseCall(Call<Result> call, Dialog loading, Response<Result> response, CallBack callBack) {
        if (loading != null) loading.dismiss();
        // 获取body
        Result body = checkBody(response);
        // status基本为统一处理的逻辑 code为私有处理逻辑
        int status = response.code();
        int code = body.getCode();
        String message = body.getMessage();
        Result.Data data = body.getData();
        // 回调
        if (callBack != null) {
            if (status == 200) {
                callBack.onResponse(code, message, data);
            } else {
                callBack.onFailure();
            }
        }
        // status处理
        final Activity top = ActivityStack.getTop();
        switch (status) {
            case 200: // 成功,和417的区别就是有回调
            case 417: // 逻辑错误，必须返回错误信息
                checkCode(body);
                break;
            case 401: // 用户验证失败
                if (top == null) return;
                LoginActivity.goActivity(top);
                break;
            case 403: // 禁止访问,key错误
            case 410: // 用户被禁用,应该退出应用
            case 503: // 服务器维护
                if (top == null) return;
                new MaterialDialog.Builder(top)
                        .content(message)
                        .cancelable(false)
                        .canceledOnTouchOutside(false)
                        .autoDismiss(true)
                        .positiveText(top.getString(R.string.i_know))
                        .dismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                AppUtils.appExit();
                            }
                        })
                        .build().show();
                break;
            case 408: // 请求超时
                if (top == null) return;
                new MaterialDialog.Builder(top)
                        .content(top.getString(R.string.http_error_time_maybe_setting_wrong))
                        .cancelable(true)
                        .canceledOnTouchOutside(true)
                        .autoDismiss(true)
                        .positiveText(top.getString(R.string.go_to_setting))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Intent netSettings = IntentUtils.getNetSettings();
                                ActivityTrans.start(top, netSettings);
                            }
                        })
                        .negativeText(top.getString(R.string.i_know))
                        .build()
                        .show();
                break;
            case 409: // 用户版本过低,提示用户升级
                if (data == null) return;
                List<Version> versionList = data.getVersionList();
                UpdateService.showUpdateDialog(versionList);
                break;
            case 500: // 服务器异常
                if (top == null) return;
                new MaterialDialog.Builder(top)
                        .content(top.getString(R.string.server_error))
                        .cancelable(false)
                        .canceledOnTouchOutside(false)
                        .autoDismiss(true)
                        .positiveText(top.getString(R.string.i_know))
                        .dismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                AppUtils.appExit();
                            }
                        })
                        .build().show();
                break;
            default: // 404,405...其他错误
                if (top == null) return;
                new MaterialDialog.Builder(top)
                        .content(message)
                        .cancelable(true)
                        .canceledOnTouchOutside(true)
                        .autoDismiss(true)
                        .positiveText(top.getString(R.string.i_want_feedback))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                SuggestAddActivity.goActivity(top);
                            }
                        })
                        .negativeText(top.getString(R.string.i_know))
                        .build()
                        .show();
                break;
        }
    }

    private static void onFailureCall(Call call, Dialog loading, Throwable t, CallBack callBack) {
        if (loading != null) loading.dismiss();
        if (callBack != null) callBack.onFailure();
        Class<? extends Throwable> clz = t.getClass();
        int error;
        if (clz.equals(java.net.ConnectException.class)) { // 网络环境
            error = R.string.http_error_connect;
        } else if (clz.equals(java.net.SocketTimeoutException.class)) { // 超时错误
            error = R.string.http_error_time;
        } else { // 其他网络错误
            error = R.string.http_error_request;
            LogUtils.e(t.toString());
        }
        ToastUtils.show(error);
    }

    private static Result checkBody(Response<Result> response) {
        int status = response.code();
        String errorStr = "";
        Result body = new Result();
        if (status == 200) {
            body = response.body();
        } else {
            try {
                errorStr = response.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (StringUtils.isEmpty(errorStr)) {
                body.setCode(Result.ResultCodeToast);
                body.setMessage(MyApp.get().getString(R.string.err_data_null));
            } else if (errorStr.startsWith("{")) {
                body = GsonUtils.getGson().fromJson(errorStr, Result.class);
            } else {
                body.setCode(Result.ResultCodeToast);
                body.setMessage(MyApp.get().getString(R.string.err_data_parse));
                LogUtils.e(LOG_TAG, errorStr);
            }
        }
        return body;
    }

    private static void checkCode(Result body) {
        Activity top = ActivityStack.getTop();
        int code = body.getCode();
        String message = body.getMessage();
        if (code == Result.ResultCodeOK) {
            return;
        } else if (code == Result.ResultCodeToast) { // toast
            ToastUtils.show(message);
        } else if (code == Result.ResultCodeDialog) { // dialog
            if (top == null) return;
            new MaterialDialog.Builder(top)
                    .content(message)
                    .positiveText(top.getString(R.string.i_know))
                    .cancelable(true)
                    .canceledOnTouchOutside(true)
                    .autoDismiss(true)
                    .build()
                    .show();
        } else if (code == Result.ResultCodeNoUserInfo) { // userInfo
            ToastUtils.show(message);
            if (top == null) return;
            UserInfoActivity.goActivity(top);
        } else if (code == Result.ResultCodeNoCP) { // cp
            ToastUtils.show(message);
            // todo
        } else if (code == Result.ResultCodeNoVIP) { // vip
            ToastUtils.show(message);
            // todo
        }
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
                        if (log.startsWith("{") || log.startsWith("[")) {
                            LogUtils.json(log);
                        } else {
                            LogUtils.d(log);
                        }
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
