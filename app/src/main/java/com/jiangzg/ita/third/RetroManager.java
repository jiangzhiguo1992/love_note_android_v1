package com.jiangzg.ita.third;

import android.app.Dialog;

import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.activity.ActivityStack;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.LoginActivity;
import com.jiangzg.ita.activity.NoCpActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.utils.UserUtils;

import java.io.IOException;
import java.util.HashMap;

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
public class RetroManager {

    private static final String LOG_TAG = "RetroManager";

    public static final String APP_KEY = "59fj48dj327fdl19fdi28cas5d20jd83";

    private HashMap<String, String> mHeaders;
    private Factory mFactory;
    private HttpLoggingInterceptor.Level mLog;
    private String mBaseUrl;

    public RetroManager() {
        this.mBaseUrl = API.BASE_URL; //默认api
        this.mHeaders = getHead(); // 默认全参
        this.mFactory = Factory.gson; // 默认gson
        this.mLog = HttpLoggingInterceptor.Level.BODY; // 默认打印全部
    }

    public static HashMap<String, String> getHead() {
        HashMap<String, String> options = new HashMap<>();
        options.put("Content-Type", "application/json;charset=utf-8");
        options.put("Accept", "application/json");
        options.put("APP_KEY", APP_KEY);
        options.put("Authorization", UserUtils.getUser().getUserToken());
        return options;
    }

    /* http请求回调 */
    public interface CallBack {
        void onResponse(int code, Result.Data data);

        void onFailure();
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
        if (loading != null) {
            loading.show();
        }
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (loading != null) loading.dismiss();
                int status = response.code();
                String errorStr = "";
                Result body = new Result();
                if (status == 200) {
                    body = response.body();
                } else {
                    try {
                        errorStr = response.errorBody().string();
                        if (!StringUtils.isEmpty(errorStr) && errorStr.startsWith("{")) {
                            body = GsonUtils.getGson().fromJson(errorStr, Result.class);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                String message = body.getMessage();
                int code = body.getCode();
                Result.Data data = body.getData();
                ToastUtils.show(message); //toast
                if (callBack != null) {
                    if (status == 200) {
                        callBack.onResponse(code, data);
                    } else {
                        callBack.onFailure();
                    }
                }
                switch (status) { //httpCode
                    case 200:
                        break;
                    case 401: // 用户验证失败
                        LoginActivity.goActivity(ActivityStack.getTop());
                        break;
                    case 409: // 用户版本过低, 应该禁止用户登录，并提示用户升级
                        // todo checkUpdate
                        break;
                    case 410: // 用户被禁用,请求数据的时候得到该 ErrorCode, 应该退出应用
                        // todo 先弹提示框，后退出
                        break;
                    case 417: // 逻辑错误，必须返回错误信息
                        // todo switch code
                        switch (code) {
                            case Result.ResultStatusNoCp:
                                NoCpActivity.goActivity(ActivityStack.getTop());
                                break;
                        }
                        break;
                    case 403: //禁止访问，key错误
                    case 404: //资源异常
                    case 408: //请求超时
                    case 500: //服务器异常
                    case 503: //服务器维护
                    default: // 其他错误
                        LogUtils.json(errorStr);
                        break;
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
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
        });
    }

    /**
     * @param baseURL head参数
     */
    public RetroManager baseURL(String baseURL) {
        if (StringUtils.isEmpty(baseURL)) return this;
        this.mBaseUrl = baseURL;
        return this;

    }

    /**
     * @param headers head参数
     */
    public RetroManager head(HashMap<String, String> headers) {
        if (headers == null) return this;
        mHeaders = headers;
        return this;

    }

    /**
     * @param factory 返回数据构造器
     */
    public RetroManager factory(Factory factory) {
        if (factory == null) return this;
        mFactory = factory;
        return this;
    }

    /**
     * @param log 日志打印开关
     */
    public RetroManager log(HttpLoggingInterceptor.Level log) {
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
