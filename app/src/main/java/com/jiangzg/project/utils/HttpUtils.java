package com.jiangzg.project.utils;

import com.android.base.component.application.AppContext;
import com.android.base.string.StringUtils;
import com.android.base.view.widget.ToastUtils;
import com.jiangzg.project.R;
import com.jiangzg.project.service.UpdateService;

import java.util.HashMap;

/**
 * Created by gg on 2017/2/28.
 * 符合本项目的工具类
 */
public class HttpUtils {

    public static boolean noLogin() {
        String userToken = UserUtils.getUser().getUserToken();
        return StringUtils.isEmpty(userToken);
    }

    public static void onFailure(int code, String error) {
        switch (code) {
            case -1: // 请求异常(弹出异常信息)
                ToastUtils.show(error);
                break;
            case 401: // 用户验证失败
                ToastUtils.show(R.string.http_response_error_401);
//                LoginActivity.goActivity(MyApp.get());
                break;
            case 403: // API AliKey 不正确 或者没给
                ToastUtils.show(R.string.http_response_error_403);
                break;
            case 409: // 用户版本过低, 应该禁止用户登录，并提示用户升级
                ToastUtils.show(R.string.http_response_error_409);
                UserUtils.clearUser();
                UpdateService.goService(AppContext.get());
                break;
            case 410: // 用户被禁用,请求数据的时候得到该 ErrorCode, 应该退出应用
                ToastUtils.show(R.string.http_response_error_410);
//                ActivityStack.closeActivities();
                break;
            case 417: // 逻辑错误，必须返回错误信息
//                ToastUtils.get().show(errorMessage);
                break;
            default: // 其他错误
                ToastUtils.show(String.valueOf(code));
                break;
        }
    }

    public static HashMap<String, String> getHead() {
        HashMap<String, String> options = new HashMap<>();
        options.put("Content-Type", "application/json;charset=utf-8");
        options.put("Accept", "application/json");
        return options;
    }

    public static HashMap<String, String> getHeadKey() {
        HashMap<String, String> options = getHead();
        options.put("API_KEY", "");
        return options;
    }

    public static HashMap<String, String> getHeadFull() {
        HashMap<String, String> options = getHeadKey();
        String userToken = UserUtils.getUser().getUserToken();
        options.put("Authorization", userToken);
        return options;
    }

}
