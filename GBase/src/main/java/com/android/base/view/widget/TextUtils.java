package com.android.base.view.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

/**
 * Created by gg on 2017/4/17.
 * 文本管理类
 */
public class TextUtils {

    /**
     * textView.setCompoundDrawables(null, null, null, null);
     */
    public static Drawable getDrawable(Context context, int draResId) {
        Drawable icon = ContextCompat.getDrawable(context, draResId);
        icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
        return icon;
    }

    /**
     * 底划线
     */
    public static void setLineBottom(TextView view) {
        view.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        view.getPaint().setAntiAlias(true);
    }

    /**
     * 中划线
     */
    public static void setLineCenter(TextView view) {
        view.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        view.getPaint().setAntiAlias(true);
    }

    /**
     * 连接点击
     */
    public static void setLinkClick(TextView view) {
        view.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 加载html
     */
    public static void setHtml(Context context, TextView view, String content) {
        view.setText(Html.fromHtml(content, new HtmlText(context, view), null));
    }

    private static class HtmlText implements Html.ImageGetter {

        private Context mContext;
        private TextView mTextView;

        public HtmlText(Context context, TextView textView) {
            this.mTextView = textView;
            this.mContext = context;
        }

        @Override
        public Drawable getDrawable(String source) {
            final URLDrawable drawable = new URLDrawable();
//            Glide.with(mContext)
//                    .load(source)
//                    .asBitmap()
//                    .into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                            drawable.bitmap = resource;
//                            // 设置图片大小，不然会和文字重叠，注意是resource的大小
//                            drawable.setBounds(0, 0, resource.getWidth(), resource.getHeight());
//                            // 收到图片后记得刷新textView
//                            mTextView.invalidate();
//                            // 这行不加会造成图片尺寸不对
//                            mTextView.setText(mTextView.getText());
//                        }
//                    });
            return drawable;
        }

        private class URLDrawable extends BitmapDrawable {
            protected Bitmap bitmap;

            @Override
            public void draw(Canvas canvas) {
                if (bitmap != null) {
                    canvas.drawBitmap(bitmap, 0, 0, getPaint());
                }
            }
        }
    }
}
