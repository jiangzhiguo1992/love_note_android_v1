package com.jiangzg.ita.third;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jiangzg.base.component.application.AppContext;
import com.jiangzg.ita.base.MyApp;

import java.util.List;

/**
 * Created by JZG on 2018/3/21.
 * Fresco工具类
 */
public class FrescoUtils {

    public static void init() {
        Fresco.initialize(MyApp.get());
    }

    public void setData(SimpleDraweeView view) {
        Uri uri = Uri.parse("https://raw.githubusercontent.com/facebook/fresco/gh-pages/static/logo.png");
        view.setImageURI(uri);
    }

    public void getNet(SimpleDraweeView view) {
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(MyApp.get().getResources());
        //GenericDraweeHierarchy hierarchy = builder
        //        .setFadeDuration(300)
        //        .setActualImageColorFilter()
        //        .setActualImageFocusPoint()
        //        .setActualImageScaleType()
        //        .setPlaceholderImage()
        //        .setPlaceholderImageScaleType()
        //        .setFailureImage()
        //        .setFailureImageScaleType()
        //        .setRetryImage()
        //        .setRetryImageScaleType()
        //        .setProgressBarImage()
        //        .setProgressBarImageScaleType()
        //        .setBackground()
        //        .setOverlay()
        //        .setPressedStateOverlay()
        //        .build();
        //view.setHierarchy(hierarchy);
    }


    public String getHttpData(String data) {
        return "http://" + data;
    }

    public String getHttpsData(String data) {
        return "https://" + data;
    }

    public String getFileData(String data) {
        return "file://" + data;
    }

    public String getContentData(String data) {
        return "content://" + data;
    }

    public String getAssetData(String data) {
        return "asset://" + data;
    }

    public String getResData(String data) {
        return "res://" + data;
    }

    public String getBase64Data(String data) {
        return "data:mime/type;base64," + data;
    }
}
