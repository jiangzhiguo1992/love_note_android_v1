package com.android.depend.utils;

import android.content.Context;

import java.io.File;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by gg on 2017/5/8.
 */

public class LuBanUtils {


    /**
     * 鲁班算法压缩(推荐)
     */
    public static void compress(Context context, File src, OnCompressListener listener) {
        Luban.get(context)
                .load(src) // 压缩源文件
                .putGear(Luban.THIRD_GEAR) // 设定压缩档次，默认三挡
                .setCompressListener(listener)
                .launch(); // 启动压缩
    }

}
