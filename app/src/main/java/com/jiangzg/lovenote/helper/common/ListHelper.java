package com.jiangzg.lovenote.helper.common;

import android.net.Uri;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.fragment.main.TopicFragment;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.entity.Album;
import com.jiangzg.lovenote.model.entity.BaseObj;
import com.jiangzg.lovenote.model.entity.Diary;
import com.jiangzg.lovenote.model.entity.Food;
import com.jiangzg.lovenote.model.entity.Gift;
import com.jiangzg.lovenote.model.entity.Movie;
import com.jiangzg.lovenote.model.entity.Post;
import com.jiangzg.lovenote.model.entity.PostKindInfo;
import com.jiangzg.lovenote.model.entity.PostSubKindInfo;
import com.jiangzg.lovenote.model.entity.SouvenirAlbum;
import com.jiangzg.lovenote.model.entity.SouvenirDiary;
import com.jiangzg.lovenote.model.entity.SouvenirFood;
import com.jiangzg.lovenote.model.entity.SouvenirGift;
import com.jiangzg.lovenote.model.entity.SouvenirMovie;
import com.jiangzg.lovenote.model.entity.SouvenirTravel;
import com.jiangzg.lovenote.model.entity.SouvenirVideo;
import com.jiangzg.lovenote.model.entity.Travel;
import com.jiangzg.lovenote.model.entity.TravelAlbum;
import com.jiangzg.lovenote.model.entity.TravelDiary;
import com.jiangzg.lovenote.model.entity.TravelFood;
import com.jiangzg.lovenote.model.entity.TravelMovie;
import com.jiangzg.lovenote.model.entity.TravelPlace;
import com.jiangzg.lovenote.model.entity.TravelVideo;
import com.jiangzg.lovenote.model.entity.Video;

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
    public static ArrayList<File> getFileListByPath(List<String> pathList) {
        ArrayList<File> fileList = new ArrayList<>();
        if (pathList == null || pathList.size() <= 0) return fileList;
        for (String path : pathList) {
            if (StringUtils.isEmpty(path)) continue;
            fileList.add(new File(path));
        }
        return fileList;
    }

    // 集合类型转换(file -> path)
    public static ArrayList<String> getPathListByFile(List<File> fileList) {
        ArrayList<String> pathList = new ArrayList<>();
        if (fileList == null || fileList.size() <= 0) return pathList;
        for (File file : fileList) {
            if (file == null || StringUtils.isEmpty(file.getAbsolutePath())) continue;
            pathList.add(file.getAbsolutePath());
        }
        return pathList;
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

    /**
     * **************************************travel转换**************************************
     */
    // 获取travel中要上传的placeList
    public static ArrayList<TravelPlace> getTravelPlaceListByOld(List<TravelPlace> oldList, List<TravelPlace> adapterList) {
        ArrayList<TravelPlace> returnList = new ArrayList<>();
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
            if (checkStatus && travelAlbum.isDelete()) continue;
            albumList.add(travelAlbum.getAlbum());
        }
        return albumList;
    }

    // 获取travel中要上传的albumList
    public static ArrayList<TravelAlbum> getTravelAlbumListByOld(List<TravelAlbum> travelAlbumList, List<Album> albumList) {
        ArrayList<TravelAlbum> returnList = new ArrayList<>();
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
            if (checkStatus && travelVideo.isDelete()) continue;
            videoList.add(travelVideo.getVideo());
        }
        return videoList;
    }

    // 获取travel中要上传的videoList
    public static ArrayList<TravelVideo> getTravelVideoListByOld(List<TravelVideo> travelVideoList, List<Video> videoList) {
        ArrayList<TravelVideo> returnList = new ArrayList<>();
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
            if (checkStatus && travelFood.isDelete()) continue;
            foodList.add(travelFood.getFood());
        }
        return foodList;
    }

    // 获取travel中要上传的foodList
    public static ArrayList<TravelFood> getTravelFoodListByOld(List<TravelFood> travelFoodList, List<Food> foodList) {
        ArrayList<TravelFood> returnList = new ArrayList<>();
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

    // 在adapter中显示的movie
    public static ArrayList<Movie> getMovieListByTravel(List<TravelMovie> travelMovieList, boolean checkStatus) {
        ArrayList<Movie> movieList = new ArrayList<>();
        if (travelMovieList == null || travelMovieList.size() <= 0) return movieList;
        for (TravelMovie travelMovie : travelMovieList) {
            if (travelMovie == null || travelMovie.getMovie() == null || travelMovie.getMovie().getId() <= 0) {
                // 所有的movie都是已经已存在的，必须有id
                continue;
            }
            if (checkStatus && travelMovie.isDelete()) continue;
            movieList.add(travelMovie.getMovie());
        }
        return movieList;
    }

    // 获取travel中要上传的movieList
    public static ArrayList<TravelMovie> getTravelMovieListByOld(List<TravelMovie> travelMovieList, List<Movie> movieList) {
        ArrayList<TravelMovie> returnList = new ArrayList<>();
        if (travelMovieList == null) {
            travelMovieList = new ArrayList<>();
        }
        // 检查原来的数据
        if (travelMovieList.size() > 0) {
            for (TravelMovie travelMovie : travelMovieList) {
                if (travelMovie == null || travelMovie.getMovie() == null || travelMovie.getMovie().getId() <= 0) {
                    continue;
                }
                // 对比新旧数据，清理旧数据
                int index = findIndexByIdInList(movieList, travelMovie.getMovie());
                // 已存在数据需要给id
                TravelMovie newMovie = new TravelMovie();
                newMovie.setId(travelMovie.getId());
                newMovie.setMovieId(travelMovie.getMovieId());
                if (index < 0 || index >= movieList.size()) {
                    // 新数据里不存在，说明想要删掉
                    newMovie.setStatus(BaseObj.STATUS_DELETE);
                } else {
                    // 新数据里有，说明想要保留
                    newMovie.setStatus(BaseObj.STATUS_VISIBLE);
                }
                returnList.add(newMovie);
            }
        }
        // 检查添加的数据
        if (movieList != null && movieList.size() > 0) {
            // 先转换成数据集合
            List<Movie> movies = getMovieListByTravel(travelMovieList, false);
            for (Movie movie : movieList) {
                if (movie == null || movie.getId() <= 0) continue;
                // 对比新旧数据，添加新数据
                int index = findIndexByIdInList(movies, movie);
                if (index < 0 || index >= movies.size()) {
                    // 新数据不给id
                    TravelMovie newMovie = new TravelMovie();
                    newMovie.setStatus(BaseObj.STATUS_VISIBLE);
                    newMovie.setMovieId(movie.getId());
                    // 不存在，加进去
                    returnList.add(newMovie);
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
            if (checkStatus && travelDiary.isDelete()) continue;
            diaryList.add(travelDiary.getDiary());
        }
        return diaryList;
    }

    // 获取travel中要上传的diaryList
    public static ArrayList<TravelDiary> getTravelDiaryListByOld(List<TravelDiary> travelDiaryList, List<Diary> diaryList) {
        ArrayList<TravelDiary> returnList = new ArrayList<>();
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

    /**
     * **************************************souvenir转换**************************************
     */
    // 获取souvenir中的year的travelList
    public static ArrayList<SouvenirTravel> getSouvenirTravelListByYear(List<SouvenirTravel> souvenirTravelList, int year) {
        ArrayList<SouvenirTravel> returnList = new ArrayList<>();
        if (souvenirTravelList == null || souvenirTravelList.size() <= 0) {
            return returnList;
        }
        for (SouvenirTravel souvenirTravel : souvenirTravelList) {
            if (year == souvenirTravel.getYear()) {
                returnList.add(souvenirTravel);
            }
        }
        return returnList;
    }

    // 在adapter中显示的travel
    public static ArrayList<Travel> getTravelListBySouvenir(List<SouvenirTravel> souvenirTravelList, boolean checkStatus) {
        ArrayList<Travel> travelList = new ArrayList<>();
        if (souvenirTravelList == null || souvenirTravelList.size() <= 0) return travelList;
        for (SouvenirTravel souvenirTravel : souvenirTravelList) {
            if (souvenirTravel == null || souvenirTravel.getTravel() == null || souvenirTravel.getTravel().getId() <= 0) {
                // 所有的travel都是已经已存在的，必须有id
                continue;
            }
            if (checkStatus && souvenirTravel.isDelete()) continue;
            travelList.add(souvenirTravel.getTravel());
        }
        return travelList;
    }

    // 获取souvenir中要上传的travelList
    public static ArrayList<SouvenirTravel> getSouvenirTravelListByOld(List<SouvenirTravel> souvenirTravelList, List<Travel> travelList) {
        ArrayList<SouvenirTravel> returnList = new ArrayList<>();
        if (souvenirTravelList == null) {
            souvenirTravelList = new ArrayList<>();
        }
        // 检查原来的数据
        if (souvenirTravelList.size() > 0) {
            for (SouvenirTravel souvenirTravel : souvenirTravelList) {
                if (souvenirTravel == null || souvenirTravel.getTravel() == null || souvenirTravel.getTravel().getId() <= 0) {
                    continue;
                }
                // 对比新旧数据，清理旧数据
                int index = findIndexByIdInList(travelList, souvenirTravel.getTravel());
                // 已存在数据需要给id
                SouvenirTravel newTravel = new SouvenirTravel();
                newTravel.setId(souvenirTravel.getId());
                newTravel.setTravelId(souvenirTravel.getTravelId());
                if (index < 0 || index >= travelList.size()) {
                    // 新数据里不存在，说明想要删掉
                    newTravel.setStatus(BaseObj.STATUS_DELETE);
                } else {
                    // 新数据里有，说明想要保留
                    newTravel.setStatus(BaseObj.STATUS_VISIBLE);
                }
                returnList.add(newTravel);
            }
        }
        // 检查添加的数据
        if (travelList != null && travelList.size() > 0) {
            // 先转换成数据集合
            List<Travel> travels = getTravelListBySouvenir(souvenirTravelList, false);
            for (Travel travel : travelList) {
                if (travel == null || travel.getId() <= 0) continue;
                // 对比新旧数据，添加新数据
                int index = findIndexByIdInList(travels, travel);
                if (index < 0 || index >= travels.size()) {
                    // 新数据不给id
                    SouvenirTravel newTravel = new SouvenirTravel();
                    newTravel.setStatus(BaseObj.STATUS_VISIBLE);
                    newTravel.setTravelId(travel.getId());
                    // 不存在，加进去
                    returnList.add(newTravel);
                }
                // 存在就不要更新了，会覆盖id
            }
        }
        return returnList;
    }

    // 获取souvenir中的year的giftList
    public static ArrayList<SouvenirGift> getSouvenirGiftListByYear(List<SouvenirGift> souvenirGiftList, int year) {
        ArrayList<SouvenirGift> returnList = new ArrayList<>();
        if (souvenirGiftList == null || souvenirGiftList.size() <= 0) {
            return returnList;
        }
        for (SouvenirGift souvenirGift : souvenirGiftList) {
            if (year == souvenirGift.getYear()) {
                returnList.add(souvenirGift);
            }
        }
        return returnList;
    }

    // 在adapter中显示的gift
    public static ArrayList<Gift> getGiftListBySouvenir(List<SouvenirGift> souvenirGiftList, boolean checkStatus) {
        ArrayList<Gift> giftList = new ArrayList<>();
        if (souvenirGiftList == null || souvenirGiftList.size() <= 0) return giftList;
        for (SouvenirGift souvenirGift : souvenirGiftList) {
            if (souvenirGift == null || souvenirGift.getGift() == null || souvenirGift.getGift().getId() <= 0) {
                // 所有的gift都是已经已存在的，必须有id
                continue;
            }
            if (checkStatus && souvenirGift.isDelete()) continue;
            giftList.add(souvenirGift.getGift());
        }
        return giftList;
    }

    // 获取souvenir中要上传的giftList
    public static ArrayList<SouvenirGift> getSouvenirGiftListByOld(List<SouvenirGift> souvenirGiftList, List<Gift> giftList) {
        ArrayList<SouvenirGift> returnList = new ArrayList<>();
        if (souvenirGiftList == null) {
            souvenirGiftList = new ArrayList<>();
        }
        // 检查原来的数据
        if (souvenirGiftList.size() > 0) {
            for (SouvenirGift souvenirGift : souvenirGiftList) {
                if (souvenirGift == null || souvenirGift.getGift() == null || souvenirGift.getGift().getId() <= 0) {
                    continue;
                }
                // 对比新旧数据，清理旧数据
                int index = findIndexByIdInList(giftList, souvenirGift.getGift());
                // 已存在数据需要给id
                SouvenirGift newGift = new SouvenirGift();
                newGift.setId(souvenirGift.getId());
                newGift.setGiftId(souvenirGift.getGiftId());
                if (index < 0 || index >= giftList.size()) {
                    // 新数据里不存在，说明想要删掉
                    newGift.setStatus(BaseObj.STATUS_DELETE);
                } else {
                    // 新数据里有，说明想要保留
                    newGift.setStatus(BaseObj.STATUS_VISIBLE);
                }
                returnList.add(newGift);
            }
        }
        // 检查添加的数据
        if (giftList != null && giftList.size() > 0) {
            // 先转换成数据集合
            List<Gift> gifts = getGiftListBySouvenir(souvenirGiftList, false);
            for (Gift gift : giftList) {
                if (gift == null || gift.getId() <= 0) continue;
                // 对比新旧数据，添加新数据
                int index = findIndexByIdInList(gifts, gift);
                if (index < 0 || index >= gifts.size()) {
                    // 新数据不给id
                    SouvenirGift newGift = new SouvenirGift();
                    newGift.setStatus(BaseObj.STATUS_VISIBLE);
                    newGift.setGiftId(gift.getId());
                    // 不存在，加进去
                    returnList.add(newGift);
                }
                // 存在就不要更新了，会覆盖id
            }
        }
        return returnList;
    }

    // 获取souvenir中的year的albumList
    public static ArrayList<SouvenirAlbum> getSouvenirAlbumListByYear(List<SouvenirAlbum> souvenirAlbumList, int year) {
        ArrayList<SouvenirAlbum> returnList = new ArrayList<>();
        if (souvenirAlbumList == null || souvenirAlbumList.size() <= 0) {
            return returnList;
        }
        for (SouvenirAlbum souvenirAlbum : souvenirAlbumList) {
            if (year == souvenirAlbum.getYear()) {
                returnList.add(souvenirAlbum);
            }
        }
        return returnList;
    }

    // 在adapter中显示的album
    public static ArrayList<Album> getAlbumListBySouvenir(List<SouvenirAlbum> souvenirAlbumList, boolean checkStatus) {
        ArrayList<Album> albumList = new ArrayList<>();
        if (souvenirAlbumList == null || souvenirAlbumList.size() <= 0) return albumList;
        for (SouvenirAlbum souvenirAlbum : souvenirAlbumList) {
            if (souvenirAlbum == null || souvenirAlbum.getAlbum() == null || souvenirAlbum.getAlbum().getId() <= 0) {
                // 所有的album都是已经已存在的，必须有id
                continue;
            }
            if (checkStatus && souvenirAlbum.isDelete()) continue;
            albumList.add(souvenirAlbum.getAlbum());
        }
        return albumList;
    }

    // 获取souvenir中要上传的albumList
    public static ArrayList<SouvenirAlbum> getSouvenirAlbumListByOld(List<SouvenirAlbum> souvenirAlbumList, List<Album> albumList) {
        ArrayList<SouvenirAlbum> returnList = new ArrayList<>();
        if (souvenirAlbumList == null) {
            souvenirAlbumList = new ArrayList<>();
        }
        // 检查原来的数据
        if (souvenirAlbumList.size() > 0) {
            for (SouvenirAlbum souvenirAlbum : souvenirAlbumList) {
                if (souvenirAlbum == null || souvenirAlbum.getAlbum() == null || souvenirAlbum.getAlbum().getId() <= 0) {
                    continue;
                }
                // 对比新旧数据，清理旧数据
                int index = findIndexByIdInList(albumList, souvenirAlbum.getAlbum());
                // 已存在数据需要给id
                SouvenirAlbum newAlbum = new SouvenirAlbum();
                newAlbum.setId(souvenirAlbum.getId());
                newAlbum.setAlbumId(souvenirAlbum.getAlbumId());
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
            List<Album> albums = getAlbumListBySouvenir(souvenirAlbumList, false);
            for (Album album : albumList) {
                if (album == null || album.getId() <= 0) continue;
                // 对比新旧数据，添加新数据
                int index = findIndexByIdInList(albums, album);
                if (index < 0 || index >= albums.size()) {
                    // 新数据不给id
                    SouvenirAlbum newAlbum = new SouvenirAlbum();
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

    // 获取souvenir中的year的videoList
    public static ArrayList<SouvenirVideo> getSouvenirVideoListByYear(List<SouvenirVideo> souvenirVideoList, int year) {
        ArrayList<SouvenirVideo> returnList = new ArrayList<>();
        if (souvenirVideoList == null || souvenirVideoList.size() <= 0) {
            return returnList;
        }
        for (SouvenirVideo souvenirVideo : souvenirVideoList) {
            if (year == souvenirVideo.getYear()) {
                returnList.add(souvenirVideo);
            }
        }
        return returnList;
    }

    // 在adapter中显示的video
    public static ArrayList<Video> getVideoListBySouvenir(List<SouvenirVideo> souvenirVideoList, boolean checkStatus) {
        ArrayList<Video> videoList = new ArrayList<>();
        if (souvenirVideoList == null || souvenirVideoList.size() <= 0) return videoList;
        for (SouvenirVideo souvenirVideo : souvenirVideoList) {
            if (souvenirVideo == null || souvenirVideo.getVideo() == null || souvenirVideo.getVideo().getId() <= 0) {
                // 所有的video都是已经已存在的，必须有id
                continue;
            }
            if (checkStatus && souvenirVideo.isDelete()) continue;
            videoList.add(souvenirVideo.getVideo());
        }
        return videoList;
    }

    // 获取souvenir中要上传的videoList
    public static ArrayList<SouvenirVideo> getSouvenirVideoListByOld(List<SouvenirVideo> souvenirVideoList, List<Video> videoList) {
        ArrayList<SouvenirVideo> returnList = new ArrayList<>();
        if (souvenirVideoList == null) {
            souvenirVideoList = new ArrayList<>();
        }
        // 检查原来的数据
        if (souvenirVideoList.size() > 0) {
            for (SouvenirVideo souvenirVideo : souvenirVideoList) {
                if (souvenirVideo == null || souvenirVideo.getVideo() == null || souvenirVideo.getVideo().getId() <= 0) {
                    continue;
                }
                // 对比新旧数据，清理旧数据
                int index = findIndexByIdInList(videoList, souvenirVideo.getVideo());
                // 已存在数据需要给id
                SouvenirVideo newVideo = new SouvenirVideo();
                newVideo.setId(souvenirVideo.getId());
                newVideo.setVideoId(souvenirVideo.getVideoId());
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
            List<Video> videos = getVideoListBySouvenir(souvenirVideoList, false);
            for (Video video : videoList) {
                if (video == null || video.getId() <= 0) continue;
                // 对比新旧数据，添加新数据
                int index = findIndexByIdInList(videos, video);
                if (index < 0 || index >= videos.size()) {
                    // 新数据不给id
                    SouvenirVideo newVideo = new SouvenirVideo();
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

    // 获取souvenir中的year的foodList
    public static ArrayList<SouvenirFood> getSouvenirFoodListByYear(List<SouvenirFood> souvenirFoodList, int year) {
        ArrayList<SouvenirFood> returnList = new ArrayList<>();
        if (souvenirFoodList == null || souvenirFoodList.size() <= 0) {
            return returnList;
        }
        for (SouvenirFood souvenirFood : souvenirFoodList) {
            if (year == souvenirFood.getYear()) {
                returnList.add(souvenirFood);
            }
        }
        return returnList;
    }

    // 在adapter中显示的food
    public static ArrayList<Food> getFoodListBySouvenir(List<SouvenirFood> souvenirFoodList, boolean checkStatus) {
        ArrayList<Food> foodList = new ArrayList<>();
        if (souvenirFoodList == null || souvenirFoodList.size() <= 0) return foodList;
        for (SouvenirFood souvenirFood : souvenirFoodList) {
            if (souvenirFood == null || souvenirFood.getFood() == null || souvenirFood.getFood().getId() <= 0) {
                // 所有的food都是已经已存在的，必须有id
                continue;
            }
            if (checkStatus && souvenirFood.isDelete()) continue;
            foodList.add(souvenirFood.getFood());
        }
        return foodList;
    }

    // 获取souvenir中要上传的foodList
    public static ArrayList<SouvenirFood> getSouvenirFoodListByOld(List<SouvenirFood> souvenirFoodList, List<Food> foodList) {
        ArrayList<SouvenirFood> returnList = new ArrayList<>();
        if (souvenirFoodList == null) {
            souvenirFoodList = new ArrayList<>();
        }
        // 检查原来的数据
        if (souvenirFoodList.size() > 0) {
            for (SouvenirFood souvenirFood : souvenirFoodList) {
                if (souvenirFood == null || souvenirFood.getFood() == null || souvenirFood.getFood().getId() <= 0) {
                    continue;
                }
                // 对比新旧数据，清理旧数据
                int index = findIndexByIdInList(foodList, souvenirFood.getFood());
                // 已存在数据需要给id
                SouvenirFood newFood = new SouvenirFood();
                newFood.setId(souvenirFood.getId());
                newFood.setFoodId(souvenirFood.getFoodId());
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
            List<Food> foods = getFoodListBySouvenir(souvenirFoodList, false);
            for (Food food : foodList) {
                if (food == null || food.getId() <= 0) continue;
                // 对比新旧数据，添加新数据
                int index = findIndexByIdInList(foods, food);
                if (index < 0 || index >= foods.size()) {
                    // 新数据不给id
                    SouvenirFood newFood = new SouvenirFood();
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

    // 获取souvenir中的year的movieList
    public static ArrayList<SouvenirMovie> getSouvenirMovieListByYear(List<SouvenirMovie> souvenirMovieList, int year) {
        ArrayList<SouvenirMovie> returnList = new ArrayList<>();
        if (souvenirMovieList == null || souvenirMovieList.size() <= 0) {
            return returnList;
        }
        for (SouvenirMovie souvenirMovie : souvenirMovieList) {
            if (year == souvenirMovie.getYear()) {
                returnList.add(souvenirMovie);
            }
        }
        return returnList;
    }

    // 在adapter中显示的movie
    public static ArrayList<Movie> getMovieListBySouvenir(List<SouvenirMovie> souvenirMovieList, boolean checkStatus) {
        ArrayList<Movie> movieList = new ArrayList<>();
        if (souvenirMovieList == null || souvenirMovieList.size() <= 0) return movieList;
        for (SouvenirMovie souvenirMovie : souvenirMovieList) {
            if (souvenirMovie == null || souvenirMovie.getMovie() == null || souvenirMovie.getMovie().getId() <= 0) {
                // 所有的movie都是已经已存在的，必须有id
                continue;
            }
            if (checkStatus && souvenirMovie.isDelete()) continue;
            movieList.add(souvenirMovie.getMovie());
        }
        return movieList;
    }

    // 获取souvenir中要上传的movieList
    public static ArrayList<SouvenirMovie> getSouvenirMovieListByOld(List<SouvenirMovie> souvenirMovieList, List<Movie> movieList) {
        ArrayList<SouvenirMovie> returnList = new ArrayList<>();
        if (souvenirMovieList == null) {
            souvenirMovieList = new ArrayList<>();
        }
        // 检查原来的数据
        if (souvenirMovieList.size() > 0) {
            for (SouvenirMovie souvenirMovie : souvenirMovieList) {
                if (souvenirMovie == null || souvenirMovie.getMovie() == null || souvenirMovie.getMovie().getId() <= 0) {
                    continue;
                }
                // 对比新旧数据，清理旧数据
                int index = findIndexByIdInList(movieList, souvenirMovie.getMovie());
                // 已存在数据需要给id
                SouvenirMovie newMovie = new SouvenirMovie();
                newMovie.setId(souvenirMovie.getId());
                newMovie.setMovieId(souvenirMovie.getMovieId());
                if (index < 0 || index >= movieList.size()) {
                    // 新数据里不存在，说明想要删掉
                    newMovie.setStatus(BaseObj.STATUS_DELETE);
                } else {
                    // 新数据里有，说明想要保留
                    newMovie.setStatus(BaseObj.STATUS_VISIBLE);
                }
                returnList.add(newMovie);
            }
        }
        // 检查添加的数据
        if (movieList != null && movieList.size() > 0) {
            // 先转换成数据集合
            List<Movie> movies = getMovieListBySouvenir(souvenirMovieList, false);
            for (Movie movie : movieList) {
                if (movie == null || movie.getId() <= 0) continue;
                // 对比新旧数据，添加新数据
                int index = findIndexByIdInList(movies, movie);
                if (index < 0 || index >= movies.size()) {
                    // 新数据不给id
                    SouvenirMovie newMovie = new SouvenirMovie();
                    newMovie.setStatus(BaseObj.STATUS_VISIBLE);
                    newMovie.setMovieId(movie.getId());
                    // 不存在，加进去
                    returnList.add(newMovie);
                }
                // 存在就不要更新了，会覆盖id
            }
        }
        return returnList;
    }

    // 获取souvenir中的year的diaryList
    public static ArrayList<SouvenirDiary> getSouvenirDiaryListByYear(List<SouvenirDiary> souvenirDiaryList, int year) {
        ArrayList<SouvenirDiary> returnList = new ArrayList<>();
        if (souvenirDiaryList == null || souvenirDiaryList.size() <= 0) {
            return returnList;
        }
        for (SouvenirDiary souvenirDiary : souvenirDiaryList) {
            if (year == souvenirDiary.getYear()) {
                returnList.add(souvenirDiary);
            }
        }
        return returnList;
    }

    // 在adapter中显示的diary
    public static ArrayList<Diary> getDiaryListBySouvenir(List<SouvenirDiary> souvenirDiaryList, boolean checkStatus) {
        ArrayList<Diary> diaryList = new ArrayList<>();
        if (souvenirDiaryList == null || souvenirDiaryList.size() <= 0) return diaryList;
        for (SouvenirDiary souvenirDiary : souvenirDiaryList) {
            if (souvenirDiary == null || souvenirDiary.getDiary() == null || souvenirDiary.getDiary().getId() <= 0) {
                // 所有的diary都是已经已存在的，必须有id
                continue;
            }
            if (checkStatus && souvenirDiary.isDelete()) continue;
            diaryList.add(souvenirDiary.getDiary());
        }
        return diaryList;
    }

    // 获取souvenir中要上传的diaryList
    public static ArrayList<SouvenirDiary> getSouvenirDiaryListByOld(List<SouvenirDiary> souvenirDiaryList, List<Diary> diaryList) {
        ArrayList<SouvenirDiary> returnList = new ArrayList<>();
        if (souvenirDiaryList == null) {
            souvenirDiaryList = new ArrayList<>();
        }
        // 检查原来的数据
        if (souvenirDiaryList.size() > 0) {
            for (SouvenirDiary souvenirDiary : souvenirDiaryList) {
                if (souvenirDiary == null || souvenirDiary.getDiary() == null || souvenirDiary.getDiary().getId() <= 0) {
                    continue;
                }
                // 对比新旧数据，清理旧数据
                int index = findIndexByIdInList(diaryList, souvenirDiary.getDiary());
                // 已存在数据需要给id
                SouvenirDiary newDiary = new SouvenirDiary();
                newDiary.setId(souvenirDiary.getId());
                newDiary.setDiaryId(souvenirDiary.getDiaryId());
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
            List<Diary> diaries = getDiaryListBySouvenir(souvenirDiaryList, false);
            for (Diary diary : diaryList) {
                if (diary == null || diary.getId() <= 0) continue;
                // 对比新旧数据，添加新数据
                int index = findIndexByIdInList(diaries, diary);
                if (index < 0 || index >= diaries.size()) {
                    // 新数据不给id
                    SouvenirDiary newDiary = new SouvenirDiary();
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

    /**
     * **************************************topic转换**************************************
     */
    // getPostKindInfo
    public static PostKindInfo getPostKindInfo(int kind) {
        if (TopicFragment.postKindInfoList == null || TopicFragment.postKindInfoList.size() <= 0) {
            return null;
        }
        for (PostKindInfo info : TopicFragment.postKindInfoList) {
            if (info == null) continue;
            if (info.getKind() == kind) {
                return info;
            }
        }
        return null;
    }

    // getPostSubKindInfo
    public static PostSubKindInfo getPostSubKindInfo(PostKindInfo kindInfo, int subKind) {
        if (kindInfo == null || kindInfo.getPostSubKindInfoList() == null || kindInfo.getPostSubKindInfoList().size() <= 0) {
            return null;
        }
        for (PostSubKindInfo info : kindInfo.getPostSubKindInfoList()) {
            if (info == null) continue;
            if (info.getKind() == subKind) {
                return info;
            }
        }
        return null;
    }

    // getPostSubKindInfoListPush 获取可发表的subKindList
    public static List<PostSubKindInfo> getPostSubKindInfoListPush(PostKindInfo kindInfo) {
        List<PostSubKindInfo> returnList = new ArrayList<>();
        if (kindInfo == null || kindInfo.getPostSubKindInfoList() == null || kindInfo.getPostSubKindInfoList().size() <= 0) {
            return returnList;
        }
        List<PostSubKindInfo> subKindInfoList = kindInfo.getPostSubKindInfoList();
        for (PostSubKindInfo subKindInfo : subKindInfoList) {
            if (subKindInfo == null) continue;
            if (subKindInfo.isPush()) {
                returnList.add(subKindInfo);
            }
        }
        return returnList;
    }

    // isPostSubKindInfoPush kind是否可以发表
    public static boolean isPostSubKindInfoPush(PostKindInfo kindInfo, int subKind) {
        List<PostSubKindInfo> pushList = getPostSubKindInfoListPush(kindInfo);
        if (pushList == null || pushList.size() <= 0) {
            return false;
        }
        for (PostSubKindInfo subKindInfo : pushList) {
            if (subKindInfo == null) continue;
            if (subKindInfo.getKind() == subKind) {
                return subKindInfo.isPush();
            }
        }
        return false;
    }

    // getIndexInPostSubKindInfoListPush
    public static int getIndexInPostSubKindInfoListPush(PostKindInfo kindInfo, int subKind) {
        List<PostSubKindInfo> pushList = getPostSubKindInfoListPush(kindInfo);
        if (pushList == null || pushList.size() <= 0) {
            return -1;
        }
        for (int i = 0; i < pushList.size(); i++) {
            PostSubKindInfo subKindInfo = pushList.get(i);
            if (subKindInfo == null) continue;
            if (subKindInfo.getKind() == subKind) {
                return i;
            }
        }
        return 0;
    }

    // getPostSubKindInfoListPushShow 获取subKind的显示list
    public static List<String> getPostSubKindInfoListPushShow(PostKindInfo kindInfo) {
        List<String> returnList = new ArrayList<>();
        List<PostSubKindInfo> pushList = getPostSubKindInfoListPush(kindInfo);
        if (pushList == null || pushList.size() <= 0) {
            return returnList;
        }
        for (PostSubKindInfo subKindInfo : pushList) {
            if (subKindInfo == null) continue;
            if (subKindInfo.isPush()) {
                returnList.add(subKindInfo.getName());
            }
        }
        return returnList;
    }

    // getPostTagListShow 获取post的tagList
    public static List<String> getPostTagListShow(PostKindInfo kindInfo, Post post, boolean kind, boolean subKind) {
        List<String> showList = new ArrayList<>();
        if (post == null) return showList;
        boolean top = post.isTop();
        boolean official = post.isOfficial();
        boolean well = post.isWell();
        boolean hot = post.isHot();
        boolean mine = post.isMine();
        boolean isTa = post.isOur() && !post.isMine();
        boolean report = post.isReport();
        // show
        if (kind || subKind) {
            if (kind && kindInfo != null) {
                showList.add(kindInfo.getName());
            }
            PostSubKindInfo subKindInfo = ListHelper.getPostSubKindInfo(kindInfo, post.getSubKind());
            if (subKind && subKindInfo != null) {
                showList.add(subKindInfo.getName());
            }
        }
        if (top) showList.add(MyApp.get().getString(R.string.top));
        if (official) showList.add(MyApp.get().getString(R.string.administrators));
        if (well) showList.add(MyApp.get().getString(R.string.well));
        if (hot) showList.add(MyApp.get().getString(R.string.hot));
        if (mine) showList.add(MyApp.get().getString(R.string.me_de));
        if (isTa) showList.add(MyApp.get().getString(R.string.ta_de));
        if (report) showList.add(MyApp.get().getString(R.string.already_report));
        return showList;
    }

}
