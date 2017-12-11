package com.android.depend.utils;

import android.app.Dialog;

import com.android.base.R;
import com.android.base.component.application.AppContext;
import com.android.base.string.StringUtils;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
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
public class RetroUtils {

    private HashMap<String, String> mHeaders;
    private Factory mFactory;
    private HttpLoggingInterceptor.Level mLog;
    private String mBaseUrl;

    public RetroUtils(String BaseUrl) {
        this.mBaseUrl = BaseUrl;
        this.mHeaders = new HashMap<>(); // 默认无参
        this.mFactory = Factory.gson; // 默认gson
        this.mLog = HttpLoggingInterceptor.Level.BODY; // 默认打印全部
    }

    /**
     * @param call     请求体
     * @param callBack 请求回调
     * @param <M>      返回数据类型
     */
    public static <M> void enqueue(Call<M> call, CallBack<M> callBack) {
        enqueue(call, null, callBack);
    }

    public static <M> void enqueue(Call<M> call, final Dialog loading, final CallBack<M> callBack) {
        if (loading != null) {
            loading.show();
        }
        call.enqueue(new Callback<M>() {
            @Override
            public void onResponse(Call<M> call, Response<M> response) {
                if (loading != null) {
                    loading.dismiss();
                }
                if (callBack == null) return;
                int code = response.code();
                M result = response.body();
                // 响应处理
                if (code == 200) { // 200成功
                    callBack.onSuccess(result);
                } else { // 响应非200
                    String errorMessage = "";
                    try {
                        ResponseBody errorBody = response.errorBody();
                        errorMessage = errorBody.string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        callBack.onFailure(code, errorMessage);
                    }
                }
            }

            @Override
            public void onFailure(Call<M> call, Throwable t) {
                if (loading != null) {
                    loading.dismiss();
                }
                Class<? extends Throwable> aClass = t.getClass();
                int error;
                if (aClass.equals(java.net.ConnectException.class)) { // 网络环境
                    error = R.string.http_error_connect;
                } else if (aClass.equals(java.net.SocketTimeoutException.class)) { // 超时错误
                    error = R.string.http_error_time;
                } else { // 其他网络错误
                    error = R.string.http_error_request;
                    LogUtils.e(t.toString());
                }
                if (callBack == null) return;
                callBack.onFailure(-1, AppContext.get().getString(error));
            }
        });
    }

    /**
     * @param headers head参数
     */
    public RetroUtils head(final HashMap<String, String> headers) {
        if (headers == null) return this;
        mHeaders = headers;
        return this;

    }

    /**
     * @param log 日志打印开关
     */
    public RetroUtils log(HttpLoggingInterceptor.Level log) {
        if (log == null) return this;
        mLog = log;
        return this;
    }

    /**
     * @param factory 返回数据构造器
     */
    public RetroUtils factory(Factory factory) {
        if (factory == null) return this;
        mFactory = factory;
        return this;
    }

    /**
     * @return 获取call
     */
    public <T extends RetroAPI> T call(Class<T> clz) {
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

    /* http请求回调 */
    public interface CallBack<M> {
        void onSuccess(M result);

        void onFailure(int code, String error);
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
