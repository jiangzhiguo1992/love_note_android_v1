package com.jiangzg.mianmian.helper;

import android.net.Uri;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.mianmian.domain.Album;
import com.jiangzg.mianmian.domain.BaseObj;
import com.jiangzg.mianmian.domain.Diary;
import com.jiangzg.mianmian.domain.Food;
import com.jiangzg.mianmian.domain.Gift;
import com.jiangzg.mianmian.domain.Picture;
import com.jiangzg.mianmian.domain.TravelAlbum;
import com.jiangzg.mianmian.domain.TravelDiary;
import com.jiangzg.mianmian.domain.TravelFood;
import com.jiangzg.mianmian.domain.TravelPlace;
import com.jiangzg.mianmian.domain.TravelVideo;
import com.jiangzg.mianmian.domain.Video;
import com.jiangzg.mianmian.domain.Whisper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JZG on 2018/4/19.
 * ListHelper
 */
public class ListHelper {

    public static <T extends BaseObj> int findIndexByIdInList(List<T> list, T obj) {
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

    public static <A extends BaseQuickAdapter, T extends BaseObj> void removeObjInAdapter(A adapter, T obj) {
        if (adapter == null || obj == null) return;
        List data = adapter.getData();
        int index = ListHelper.findIndexByIdInList(data, obj);
        if (index < 0 || index >= data.size()) return;
        adapter.remove(index);
    }

    public static <A extends BaseQuickAdapter, T extends BaseObj> void refreshObjInAdapter(A adapter, T obj) {
        if (adapter == null || obj == null) return;
        List data = adapter.getData();
        int index = ListHelper.findIndexByIdInList(data, obj);
        if (index < 0 || index >= data.size()) return;
        adapter.setData(index, obj);
    }

    /**
     * **************************************oss转换**************************************
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

    // 集合类型转换(Food -> ossKey)
    public static ArrayList<String> getOssKeyListByFood(List<Food> foodList) {
        ArrayList<String> ossKeyList = new ArrayList<>();
        if (foodList == null || foodList.size() <= 0) return ossKeyList;
        for (Food food : foodList) {
            if (food == null || food.getContentImageList() == null || food.getContentImageList().size() <= 0) {
                continue;
            }
            ossKeyList.addAll(food.getContentImageList());
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

    /**
     * **************************************travel转换**************************************
     */
    // 获取travel中要上传的placeList
    public static List<TravelPlace> getTravelPlaceListByOld(List<TravelPlace> oldList, List<TravelPlace> adapterList) {
        List<TravelPlace> returnList = new ArrayList<>();
        if (oldList == null) {
            oldList = new ArrayList<>();
        }
        // 检查原来的数据
        if (oldList.size() > 0) {
            for (TravelPlace travelPlace : oldList) {
                if (travelPlace == null || travelPlace.getId() <= 0) {
                    continue;
                }
                // 对比新旧数据，清理旧数据
                int index = findIndexByIdInList(adapterList, travelPlace);
                // 已存在数据需要给id
                TravelPlace newPlace = new TravelPlace();
                newPlace.setId(travelPlace.getId());
                if (index < 0 || index >= adapterList.size()) {
                    // 新数据里不存在，说明想要删掉
                    newPlace.setStatus(BaseObj.STATUS_DELETE);
                } else {
                    // 新数据里有，说明想要保留
                    newPlace.setStatus(BaseObj.STATUS_VISIBLE);
                }
                returnList.add(newPlace);
            }
        }
        // 检查添加的数据
        if (adapterList != null && adapterList.size() > 0) {
            // 先转换成数据集合
            for (TravelPlace travelPlace : adapterList) {
                if (travelPlace == null) continue; // 这里不检查id
                // 对比新旧数据，添加新数据
                int index = findIndexByIdInList(oldList, travelPlace);
                if (index < 0 || index >= oldList.size()) {
                    // 新数据不给id
                    TravelPlace newPlace = new TravelPlace();
                    newPlace.setStatus(BaseObj.STATUS_VISIBLE);
                    newPlace.setHappenAt(travelPlace.getHappenAt());
                    newPlace.setContentText(travelPlace.getContentText());
                    newPlace.setLongitude(travelPlace.getLongitude());
                    newPlace.setLatitude(travelPlace.getLatitude());
                    newPlace.setAddress(travelPlace.getAddress());
                    newPlace.setCityId(travelPlace.getCityId());
                    // 不存在，加进去
                    returnList.add(newPlace);
                }
                // 存在就不要更新了，会覆盖id
            }
        }
        return returnList;
    }

    // 在adapter中显示的album
    public static ArrayList<Album> getAlbumListByTravel(List<TravelAlbum> travelAlbumList, boolean checkStatus) {
        ArrayList<Album> albumList = new ArrayList<>();
        if (travelAlbumList == null || travelAlbumList.size() <= 0) return albumList;
        for (TravelAlbum travelAlbum : travelAlbumList) {
            if (travelAlbum == null || travelAlbum.getAlbum() == null || travelAlbum.getAlbum().getId() <= 0) {
                // 所有的album都是已经已存在的，必须有id
                continue;
            }
            if (checkStatus && travelAlbum.getStatus() <= BaseObj.STATUS_DELETE) continue;
            albumList.add(travelAlbum.getAlbum());
        }
        return albumList;
    }

    // 获取travel中要上传的albumList
    public static List<TravelAlbum> getTravelAlbumListByOld(List<TravelAlbum> travelAlbumList, List<Album> albumList) {
        List<TravelAlbum> returnList = new ArrayList<>();
        if (travelAlbumList == null) {
            travelAlbumList = new ArrayList<>();
        }
        // 检查原来的数据
        if (travelAlbumList.size() > 0) {
            for (TravelAlbum travelAlbum : travelAlbumList) {
                if (travelAlbum == null || travelAlbum.getAlbum() == null || travelAlbum.getAlbum().getId() <= 0) {
                    continue;
                }
                // 对比新旧数据，清理旧数据
                int index = findIndexByIdInList(albumList, travelAlbum.getAlbum());
                // 已存在数据需要给id
                TravelAlbum newAlbum = new TravelAlbum();
                newAlbum.setId(travelAlbum.getId());
                newAlbum.setAlbumId(travelAlbum.getAlbumId());
                if (index < 0 || index >= albumList.size()) {
                    // 新数据里不存在，说明想要删掉
                    newAlbum.setStatus(BaseObj.STATUS_DELETE);
                } else {
                    // 新数据里有，说明想要保留
                    newAlbum.setStatus(BaseObj.STATUS_VISIBLE);
                }
                returnList.add(newAlbum);
            }
        }
        // 检查添加的数据
        if (albumList != null && albumList.size() > 0) {
            // 先转换成数据集合
            List<Album> albums = getAlbumListByTravel(travelAlbumList, false);
            for (Album album : albumList) {
                if (album == null || album.getId() <= 0) continue;
                // 对比新旧数据，添加新数据
                int index = findIndexByIdInList(albums, album);
                if (index < 0 || index >= albums.size()) {
                    // 新数据不给id
                    TravelAlbum newAlbum = new TravelAlbum();
                    newAlbum.setStatus(BaseObj.STATUS_VISIBLE);
                    newAlbum.setAlbumId(album.getId());
                    // 不存在，加进去
                    returnList.add(newAlbum);
                }
                // 存在就不要更新了，会覆盖id
            }
        }
        return returnList;
    }

    // 在adapter中显示的video
    public static ArrayList<Video> getVideoListByTravel(List<TravelVideo> travelVideoList, boolean checkStatus) {
        ArrayList<Video> videoList = new ArrayList<>();
        if (travelVideoList == null || travelVideoList.size() <= 0) return videoList;
        for (TravelVideo travelVideo : travelVideoList) {
            if (travelVideo == null || travelVideo.getVideo() == null || travelVideo.getVideo().getId() <= 0) {
                // 所有的video都是已经已存在的，必须有id
                continue;
            }
            if (checkStatus && travelVideo.getStatus() <= BaseObj.STATUS_DELETE) continue;
            videoList.add(travelVideo.getVideo());
        }
        return videoList;
    }

    // 获取travel中要上传的videoList
    public static List<TravelVideo> getTravelVideoListByOld(List<TravelVideo> travelVideoList, List<Video> videoList) {
        List<TravelVideo> returnList = new ArrayList<>();
        if (travelVideoList == null) {
            travelVideoList = new ArrayList<>();
        }
        // 检查原来的数据
        if (travelVideoList.size() > 0) {
            for (TravelVideo travelVideo : travelVideoList) {
                if (travelVideo == null || travelVideo.getVideo() == null || travelVideo.getVideo().getId() <= 0) {
                    continue;
                }
                // 对比新旧数据，清理旧数据
                int index = findIndexByIdInList(videoList, travelVideo.getVideo());
                // 已存在数据需要给id
                TravelVideo newVideo = new TravelVideo();
                newVideo.setId(travelVideo.getId());
                newVideo.setVideoId(travelVideo.getVideoId());
                if (index < 0 || index >= videoList.size()) {
                    // 新数据里不存在，说明想要删掉
                    newVideo.setStatus(BaseObj.STATUS_DELETE);
                } else {
                    // 新数据里有，说明想要保留
                    newVideo.setStatus(BaseObj.STATUS_VISIBLE);
                }
                returnList.add(newVideo);
            }
        }
        // 检查添加的数据
        if (videoList != null && videoList.size() > 0) {
            // 先转换成数据集合
            List<Video> videos = getVideoListByTravel(travelVideoList, false);
            for (Video video : videoList) {
                if (video == null || video.getId() <= 0) continue;
                // 对比新旧数据，添加新数据
                int index = findIndexByIdInList(videos, video);
                if (index < 0 || index >= videos.size()) {
                    // 新数据不给id
                    TravelVideo newVideo = new TravelVideo();
                    newVideo.setStatus(BaseObj.STATUS_VISIBLE);
                    newVideo.setVideoId(video.getId());
                    // 不存在，加进去
                    returnList.add(newVideo);
                }
                // 存在就不要更新了，会覆盖id
            }
        }
        return returnList;
    }

    // 在adapter中显示的food
    public static ArrayList<Food> getFoodListByTravel(List<TravelFood> travelFoodList, boolean checkStatus) {
        ArrayList<Food> foodList = new ArrayList<>();
        if (travelFoodList == null || travelFoodList.size() <= 0) return foodList;
        for (TravelFood travelFood : travelFoodList) {
            if (travelFood == null || travelFood.getFood() == null || travelFood.getFood().getId() <= 0) {
                // 所有的food都是已经已存在的，必须有id
                continue;
            }
            if (checkStatus && travelFood.getStatus() <= BaseObj.STATUS_DELETE) continue;
            foodList.add(travelFood.getFood());
        }
        return foodList;
    }

    // 获取travel中要上传的foodList
    public static List<TravelFood> getTravelFoodListByOld(List<TravelFood> travelFoodList, List<Food> foodList) {
        List<TravelFood> returnList = new ArrayList<>();
        if (travelFoodList == null) {
            travelFoodList = new ArrayList<>();
        }
        // 检查原来的数据
        if (travelFoodList.size() > 0) {
            for (TravelFood travelFood : travelFoodList) {
                if (travelFood == null || travelFood.getFood() == null || travelFood.getFood().getId() <= 0) {
                    continue;
                }
                // 对比新旧数据，清理旧数据
                int index = findIndexByIdInList(foodList, travelFood.getFood());
                // 已存在数据需要给id
                TravelFood newFood = new TravelFood();
                newFood.setId(travelFood.getId());
                newFood.setFoodId(travelFood.getFoodId());
                if (index < 0 || index >= foodList.size()) {
                    // 新数据里不存在，说明想要删掉
                    newFood.setStatus(BaseObj.STATUS_DELETE);
                } else {
                    // 新数据里有，说明想要保留
                    newFood.setStatus(BaseObj.STATUS_VISIBLE);
                }
                returnList.add(newFood);
            }
        }
        // 检查添加的数据
        if (foodList != null && foodList.size() > 0) {
            // 先转换成数据集合
            List<Food> foods = getFoodListByTravel(travelFoodList, false);
            for (Food food : foodList) {
                if (food == null || food.getId() <= 0) continue;
                // 对比新旧数据，添加新数据
                int index = findIndexByIdInList(foods, food);
                if (index < 0 || index >= foods.size()) {
                    // 新数据不给id
                    TravelFood newFood = new TravelFood();
                    newFood.setStatus(BaseObj.STATUS_VISIBLE);
                    newFood.setFoodId(food.getId());
                    // 不存在，加进去
                    returnList.add(newFood);
                }
                // 存在就不要更新了，会覆盖id
            }
        }
        return returnList;
    }

    // 在adapter中显示的diary
    public static ArrayList<Diary> getDiaryListByTravel(List<TravelDiary> travelDiaryList, boolean checkStatus) {
        ArrayList<Diary> diaryList = new ArrayList<>();
        if (travelDiaryList == null || travelDiaryList.size() <= 0) return diaryList;
        for (TravelDiary travelDiary : travelDiaryList) {
            if (travelDiary == null || travelDiary.getDiary() == null || travelDiary.getDiary().getId() <= 0) {
                // 所有的diary都是已经已存在的，必须有id
                continue;
            }
            if (checkStatus && travelDiary.getStatus() <= BaseObj.STATUS_DELETE) continue;
            diaryList.add(travelDiary.getDiary());
        }
        return diaryList;
    }

    // 获取travel中要上传的diaryList
    public static List<TravelDiary> getTravelDiaryListByOld(List<TravelDiary> travelDiaryList, List<Diary> diaryList) {
        List<TravelDiary> returnList = new ArrayList<>();
        if (travelDiaryList == null) {
            travelDiaryList = new ArrayList<>();
        }
        // 检查原来的数据
        if (travelDiaryList.size() > 0) {
            for (TravelDiary travelDiary : travelDiaryList) {
                if (travelDiary == null || travelDiary.getDiary() == null || travelDiary.getDiary().getId() <= 0) {
                    continue;
                }
                // 对比新旧数据，清理旧数据
                int index = findIndexByIdInList(diaryList, travelDiary.getDiary());
                // 已存在数据需要给id
                TravelDiary newDiary = new TravelDiary();
                newDiary.setId(travelDiary.getId());
                newDiary.setDiaryId(travelDiary.getDiaryId());
                if (index < 0 || index >= diaryList.size()) {
                    // 新数据里不存在，说明想要删掉
                    newDiary.setStatus(BaseObj.STATUS_DELETE);
                } else {
                    // 新数据里有，说明想要保留
                    newDiary.setStatus(BaseObj.STATUS_VISIBLE);
                }
                returnList.add(newDiary);
            }
        }
        // 检查添加的数据
        if (diaryList != null && diaryList.size() > 0) {
            // 先转换成数据集合
            List<Diary> diaries = getDiaryListByTravel(travelDiaryList, false);
            for (Diary diary : diaryList) {
                if (diary == null || diary.getId() <= 0) continue;
                // 对比新旧数据，添加新数据
                int index = findIndexByIdInList(diaries, diary);
                if (index < 0 || index >= diaries.size()) {
                    // 新数据不给id
                    TravelDiary newDiary = new TravelDiary();
                    newDiary.setStatus(BaseObj.STATUS_VISIBLE);
                    newDiary.setDiaryId(diary.getId());
                    // 不存在，加进去
                    returnList.add(newDiary);
                }
                // 存在就不要更新了，会覆盖id
            }
        }
        return returnList;
    }

}
