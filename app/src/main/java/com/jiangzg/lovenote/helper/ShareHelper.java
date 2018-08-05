package com.jiangzg.lovenote.helper;

import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.domain.Post;

/**
 * Created by JZG on 2018/4/28.
 * 分享
 * TODO 微信，朋友圈，QQ，QQ空间，微博
 */
public class ShareHelper {

    public static void shareBigImg(String objectKey) {
        if (StringUtils.isEmpty(objectKey)) return;
        // TODO 分享链接的话 链接会过期 分享本地文件的话 用fresco？
        // TODO 建议这里生成的url过期时间长一点
        ToastUtils.show("分享大图");
    }

    public static void shareTopicPost(Post post) {
        if (post == null) return;
        // TODO
        ToastUtils.show("分享帖子");
    }
}
