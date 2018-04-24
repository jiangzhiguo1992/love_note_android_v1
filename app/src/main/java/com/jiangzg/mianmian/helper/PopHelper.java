package com.jiangzg.mianmian.helper;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentFactory;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.base.view.PopUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;

import java.io.File;

/**
 * Created by JZG on 2018/3/26.
 * Pop管理类
 */
public class PopHelper {

    public static PopupWindow createBookPictureCamera(final Activity activity, final File cameraFile) {
        View view = LayoutInflater.from(activity).inflate(R.layout.pop_select_img_from_book_picture_camera, null);
        final PopupWindow window = PopUtils.createWindow(view);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.root:
                        PopUtils.dismiss(window);
                        ResHelper.deleteFileInBackground(cameraFile);
                        break;
                    case R.id.llBook:
                        PopUtils.dismiss(window);
                        ResHelper.deleteFileInBackground(cameraFile);
                        // TODO
                        ToastUtils.show("小本本");
                        break;
                    case R.id.llPicture:
                        PopUtils.dismiss(window);
                        ResHelper.deleteFileInBackground(cameraFile);
                        PermUtils.requestPermissions(activity, ConsHelper.REQUEST_APP_INFO, PermUtils.picture, new PermUtils.OnPermissionListener() {
                            @Override
                            public void onPermissionGranted(int requestCode, String[] permissions) {
                                Intent picture = IntentFactory.getPicture();
                                ActivityTrans.startResult(activity, picture, ConsHelper.REQUEST_PICTURE);
                            }

                            @Override
                            public void onPermissionDenied(int requestCode, String[] permissions) {

                            }
                        });
                        break;
                    case R.id.llCamera:
                        PopUtils.dismiss(window);
                        PermUtils.requestPermissions(activity, ConsHelper.REQUEST_CAMERA, PermUtils.camera, new PermUtils.OnPermissionListener() {
                            @Override
                            public void onPermissionGranted(int requestCode, String[] permissions) {
                                Intent camera = IntentFactory.getCamera(cameraFile);
                                ActivityTrans.startResult(activity, camera, ConsHelper.REQUEST_CAMERA);
                            }

                            @Override
                            public void onPermissionDenied(int requestCode, String[] permissions) {

                            }
                        });
                        break;
                    case R.id.llCancel:
                        PopUtils.dismiss(window);
                        ResHelper.deleteFileInBackground(cameraFile);
                        break;
                }
            }
        };
        RelativeLayout root = view.findViewById(R.id.root);
        LinearLayout llBook = view.findViewById(R.id.llBook);
        LinearLayout llAlbum = view.findViewById(R.id.llPicture);
        LinearLayout llCamera = view.findViewById(R.id.llCamera);
        LinearLayout llCancel = view.findViewById(R.id.llCancel);
        root.setOnClickListener(listener);
        llBook.setOnClickListener(listener);
        llAlbum.setOnClickListener(listener);
        llCamera.setOnClickListener(listener);
        llCancel.setOnClickListener(listener);
        return window;
    }

    public static PopupWindow createPictureCamera(final Activity activity, final File cameraFile) {
        View view = LayoutInflater.from(activity).inflate(R.layout.pop_select_img_from_picture_camera, null);
        final PopupWindow window = PopUtils.createWindow(view);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.root:
                        PopUtils.dismiss(window);
                        ResHelper.deleteFileInBackground(cameraFile);
                        break;
                    case R.id.llPicture:
                        PopUtils.dismiss(window);
                        ResHelper.deleteFileInBackground(cameraFile);
                        PermUtils.requestPermissions(activity, ConsHelper.REQUEST_APP_INFO, PermUtils.picture, new PermUtils.OnPermissionListener() {
                            @Override
                            public void onPermissionGranted(int requestCode, String[] permissions) {
                                Intent picture = IntentFactory.getPicture();
                                ActivityTrans.startResult(activity, picture, ConsHelper.REQUEST_PICTURE);
                            }

                            @Override
                            public void onPermissionDenied(int requestCode, String[] permissions) {

                            }
                        });
                        break;
                    case R.id.llCamera:
                        PopUtils.dismiss(window);
                        PermUtils.requestPermissions(activity, ConsHelper.REQUEST_CAMERA, PermUtils.camera, new PermUtils.OnPermissionListener() {
                            @Override
                            public void onPermissionGranted(int requestCode, String[] permissions) {
                                Intent camera = IntentFactory.getCamera(cameraFile);
                                ActivityTrans.startResult(activity, camera, ConsHelper.REQUEST_CAMERA);
                            }

                            @Override
                            public void onPermissionDenied(int requestCode, String[] permissions) {

                            }
                        });
                        break;
                    case R.id.llCancel:
                        PopUtils.dismiss(window);
                        ResHelper.deleteFileInBackground(cameraFile);
                        break;
                }
            }
        };
        RelativeLayout root = view.findViewById(R.id.root);
        LinearLayout llAlbum = view.findViewById(R.id.llPicture);
        LinearLayout llCamera = view.findViewById(R.id.llCamera);
        LinearLayout llCancel = view.findViewById(R.id.llCancel);
        root.setOnClickListener(listener);
        llAlbum.setOnClickListener(listener);
        llCamera.setOnClickListener(listener);
        llCancel.setOnClickListener(listener);
        return window;
    }
}
