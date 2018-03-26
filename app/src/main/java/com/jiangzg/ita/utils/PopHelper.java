package com.jiangzg.ita.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.jiangzg.base.view.PopUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;

/**
 * Created by JZG on 2018/3/26.
 * Pop管理类
 */
public class PopHelper {

    public static PopupWindow createBookAlbumCamera(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.pop_select_img_from_book_picture_camera, null);
        final PopupWindow window = PopUtils.createWindow(view);
        RelativeLayout root = view.findViewById(R.id.root);
        LinearLayout llBook = view.findViewById(R.id.llBook);
        LinearLayout llAlbum = view.findViewById(R.id.llAlbum);
        LinearLayout llCamera = view.findViewById(R.id.llCamera);
        LinearLayout llCancel = view.findViewById(R.id.llCancel);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.root:
                        PopUtils.dismiss(window);
                        break;
                    case R.id.llBook:
                        // todo
                        ToastUtils.show("小本本");
                        break;
                    case R.id.llAlbum:
                        // todo
                        ToastUtils.show("相册");
                        break;
                    case R.id.llCamera:
                        // todo
                        ToastUtils.show("拍照");
                        break;
                    case R.id.llCancel:
                        PopUtils.dismiss(window);
                        break;
                }
            }
        };
        root.setOnClickListener(listener);
        llBook.setOnClickListener(listener);
        llAlbum.setOnClickListener(listener);
        llCamera.setOnClickListener(listener);
        llCancel.setOnClickListener(listener);
        return window;
    }
}
