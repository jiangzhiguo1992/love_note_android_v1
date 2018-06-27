package com.jiangzg.mianmian.helper;

import android.net.Uri;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.mianmian.domain.Album;
import com.jiangzg.mianmian.domain.BaseObj;
import com.jiangzg.mianmian.domain.Diary;
import com.jiangzg.mianmian.domain.Gift;
import com.jiangzg.mianmian.domain.Picture;
import com.jiangzg.mianmian.domain.Whisper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JZG on 2018/4/19.
 * ListHelper
 */
public class ListHelper {

    public static <T extends BaseObj> int findIndexInList(List<T> list, T obj) {
        if (list == null || list.size() <= 0) return -1;
        if (obj == null || obj.getId() == 0) return -1;
        for (int i = 0; i < list.size(); i++) {
            Object item = list.get(i);
            if (item == null) continue;
            if (((BaseObj) item).getId() == obj.getId()) {
                return i;
            }
        }
        return -1;
    }

    public static <A extends BaseQuickAdapter, T extends BaseObj> void removeIndexInAdapter(A adapter, T obj) {
        if (adapter == null) return;
        List data = adapter.getData();
        int index = ListHelper.findIndexInList(data, obj);
        if (index < 0) return;
        adapter.remove(index);
    }

    public static <A extends BaseQuickAdapter, T extends BaseObj> void refreshIndexInAdapter(A adapter, T obj) {
        if (adapter == null) return;
        List data = adapter.getData();
        int index = ListHelper.findIndexInList(data, obj);
        if (index < 0) return;
        adapter.setData(index, obj);
    }

    /**
     * **************************************转换**************************************
     */
    // 集合类型转换(path -> file)
    public static List<File> getFileListByPath(List<String> pathList) {
        List<File> fileList = new ArrayList<>();
        if (pathList == null || pathList.size() <= 0) return fileList;
        for (String path : pathList) {
            fileList.add(new File(path));
        }
        return fileList;
    }

    // 集合类型转换(string -> uri)
    public static ArrayList<Uri> getUriListByString(List<String> strings) {
        ArrayList<Uri> uriList = new ArrayList<>();
        if (strings == null || strings.size() <= 0) return uriList;
        for (String s : strings) {
            uriList.add(Uri.parse(s));
        }
        return uriList;
    }

    // 集合类型转换(Whisper -> ossKey)
    public static ArrayList<String> getOssKeyListByWhisper(List<Whisper> whisperList) {
        ArrayList<String> ossKeyList = new ArrayList<>();
        if (whisperList == null || whisperList.size() <= 0) return ossKeyList;
        for (Whisper whisper : whisperList) {
            if (whisper == null || !whisper.isImage() || StringUtils.isEmpty(whisper.getContent())) {
                continue;
            }
            ossKeyList.add(whisper.getContent());
        }
        return ossKeyList;
    }

    // 集合类型转换(Diary -> ossKey)
    public static ArrayList<String> getOssKeyListByDiary(List<Diary> diaryList) {
        ArrayList<String> ossKeyList = new ArrayList<>();
        if (diaryList == null || diaryList.size() <= 0) return ossKeyList;
        for (Diary diary : diaryList) {
            if (diary == null || diary.getContentImageList() == null || diary.getContentImageList().size() <= 0) {
                continue;
            }
            ossKeyList.addAll(diary.getContentImageList());
        }
        return ossKeyList;
    }

    // 集合类型转换(Album -> ossKey)
    public static ArrayList<String> getOssKeyListByAlbum(List<Album> albumList) {
        ArrayList<String> ossKeyList = new ArrayList<>();
        if (albumList == null || albumList.size() <= 0) return ossKeyList;
        for (Album album : albumList) {
            if (album == null || StringUtils.isEmpty(album.getCover())) {
                continue;
            }
            ossKeyList.add(album.getCover());
        }
        return ossKeyList;
    }

    // 集合类型转换(Picture -> ossKey)
    public static ArrayList<String> getOssKeyListByPicture(List<Picture> pictureList) {
        ArrayList<String> ossKeyList = new ArrayList<>();
        if (pictureList == null || pictureList.size() <= 0) return ossKeyList;
        for (Picture picture : pictureList) {
            if (picture == null || StringUtils.isEmpty(picture.getContentImage())) {
                continue;
            }
            ossKeyList.add(picture.getContentImage());
        }
        return ossKeyList;
    }

    // 集合类型转换(Gift -> ossKey)
    public static ArrayList<String> getOssKeyListByGift(List<Gift> giftList) {
        ArrayList<String> ossKeyList = new ArrayList<>();
        if (giftList == null || giftList.size() <= 0) return ossKeyList;
        for (Gift gift : giftList) {
            if (gift == null || gift.getContentImageList() == null || gift.getContentImageList().size() <= 0) {
                continue;
            }
            ossKeyList.addAll(gift.getContentImageList());
        }
        return ossKeyList;
    }
}
