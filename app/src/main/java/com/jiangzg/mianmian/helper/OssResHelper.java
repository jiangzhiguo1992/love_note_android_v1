package com.jiangzg.mianmian.helper;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.mianmian.base.MyApp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by JZG on 2018/5/14.
 * OssResHelper
 */
public class OssResHelper {

    public static final int TYPE_COUPLE_AVATAR = 10;
    public static final int TYPE_COUPLE_WALL = 11;
    public static final int TYPE_BOOK_WHISPER = 20;
    public static final int TYPE_BOOK_DIARY = 21;
    public static final int TYPE_BOOK_ALBUM = 22;
    public static final int TYPE_BOOK_PICTURE = 23;
    public static final int TYPE_BOOK_FOOD = 24;
    public static final int TYPE_BOOK_GIFT = 25;

    // 获取ossKey的文件
    public static File newKeyFile(String objectKey) {
        if (StringUtils.isEmpty(objectKey)) {
            LogUtils.w(OssResHelper.class, "newOutFile", "objectKey == null");
            return null;
        }
        return new File(ResHelper.getOssResDirPath(), objectKey);
    }

    // ossKey的文件是否存在
    public static boolean isKeyFileExists(String objectKey) {
        File file = newKeyFile(objectKey);
        boolean fileExists = FileUtils.isFileExists(file);
        LogUtils.d(OssResHelper.class, "isKeyFileExists", "oss文件存在性: " + objectKey + " = " + fileExists);
        return fileExists;
    }

    // ossKey的文件名
    public static String getKeyFileName(String objectKey) {
        if (StringUtils.isEmpty(objectKey)) {
            LogUtils.w(OssResHelper.class, "getKeyFileName", "objectKey == null");
            return "";
        }
        String[] split = objectKey.split("/");
        return split[split.length - 1].trim();
    }

    // 获取具体oss类型的dir
    public static File getResDir(int ossType) {
        String dirPath = "";
        switch (ossType) {
            case TYPE_COUPLE_AVATAR: // 头像
                dirPath = SPHelper.getOssInfo().getPathCoupleAvatar();
                break;
            case TYPE_COUPLE_WALL: // 墙纸
                dirPath = SPHelper.getOssInfo().getPathCoupleWall();
                break;
            case TYPE_BOOK_WHISPER: // 耳语
                dirPath = SPHelper.getOssInfo().getPathBookWhisper();
                break;
            case TYPE_BOOK_DIARY: // 日记
                dirPath = SPHelper.getOssInfo().getPathBookDiary();
                break;
            case TYPE_BOOK_ALBUM: // 相册
                dirPath = SPHelper.getOssInfo().getPathBookAlbum();
                break;
            case TYPE_BOOK_PICTURE: // 照片
                dirPath = SPHelper.getOssInfo().getPathBookPicture();
                break;
            case TYPE_BOOK_FOOD: // 美食
                dirPath = SPHelper.getOssInfo().getPathBookGift();
                break;
            case TYPE_BOOK_GIFT: // 礼物
                dirPath = SPHelper.getOssInfo().getPathBookGift();
                break;
        }
        if (StringUtils.isEmpty(dirPath)) {
            LogUtils.w(OssResHelper.class, "getResDir", ossType + ": dirPath == null ");
            return null;
        }
        return new File(ResHelper.getOssResDirPath(), dirPath);
    }

    // 刷新type类型的资源，删除没有的key的文件 (一般用于一次性获取到的数据)
    public static void refreshResWithDelNoExist(int type, List<String> ossKeyList) {
        File resDir = getResDir(type);
        FileUtils.createOrExistsDir(resDir);
        refreshOssResWithDelNoExists(resDir, ossKeyList);
    }

    // 刷新type类型的资源，删除过期的文件 (一般用于有分页加载的数据)
    public static void refreshResWithDelExpire(int type, List<String> ossKeyList) {
        File resDir = getResDir(type);
        FileUtils.createOrExistsDir(resDir);
        long currentLong = DateUtils.getCurrentLong();
        long expireAt = currentLong - SPHelper.getLimit().getBookResExpireSec() * 1000;
        refreshOssResWithDelExpire(resDir, ossKeyList, expireAt);
    }

    // 刷新本地oss资源，删除不存在的ossKey
    private static void refreshOssResWithDelNoExists(final File dir, final List<String> ossKeyList) {
        if (!FileUtils.isDir(dir)) {
            LogUtils.w(OssResHelper.class, "refreshOssResWithDelNoExists", "目录不存在");
            return;
        }
        // 开线程
        MyApp.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                List<File> existsFileList = FileUtils.listFilesAndDirInDir(dir, true);
                if (ossKeyList == null || ossKeyList.size() <= 0) {
                    // 没有oss 直接删除对应的目录
                    LogUtils.d(OssResHelper.class, "refreshOssResWithDelNoExists", "目录(" + dir.getName() + "): 删除全部文件");
                    if (existsFileList == null || existsFileList.size() <= 0) {
                        LogUtils.d(OssResHelper.class, "refreshOssResWithDelNoExists", "目录(" + dir.getName() + "): 没有文件");
                    } else {
                        ResHelper.deleteFileListInBackground(existsFileList);
                    }
                    return;
                }
                // 删旧的(放在上面可以少判断点)
                if (existsFileList != null && existsFileList.size() > 0) {
                    // 本地有oss的文件
                    for (File file : existsFileList) {
                        // 文件不存在则直接检查下一个文件
                        if (file == null) continue;
                        boolean find = false;
                        // 检查是不是oss里对应的文件
                        for (String ossKey : ossKeyList) {
                            String name = OssResHelper.getKeyFileName(ossKey);
                            if (file.getName().trim().equals(name)) {
                                // 是对应的文件，直接走下面的流程
                                find = true;
                                break;
                            }
                        }
                        // 都检查完了，不是对应的文件则删除
                        if (!find) {
                            LogUtils.i(OssResHelper.class, "refreshOssResWithDelNoExists", "目录(" + dir.getName() + "): 删不匹配的文件 == " + file.getName());
                            ResHelper.deleteFileInBackground(file);
                        } else {
                            LogUtils.i(OssResHelper.class, "refreshOssResWithDelNoExists", "目录(" + dir.getName() + "): 留下匹配的文件 == " + file.getName());
                        }
                    }
                } else {
                    LogUtils.d(OssResHelper.class, "refreshOssResWithDelNoExists", "目录(" + dir.getName() + "): 无文件");
                }
                // 加新的
                for (String ossKey : ossKeyList) {
                    if (StringUtils.isEmpty(ossKey)) return;
                    if (!OssResHelper.isKeyFileExists(ossKey)) {
                        // 不存在的的直接下载
                        LogUtils.i(OssResHelper.class, "refreshOssResWithDelNoExists", "目录(" + dir.getName() + "): 下载匹配的文件 == " + ossKey);
                        OssHelper.downloadOssFileByKey(ossKey);
                    }
                }
            }
        });
    }

    // 刷新本地oss资源，删除过时的ossKey
    private static void refreshOssResWithDelExpire(final File dir, final List<String> ossKeyList, final long expireAt) {
        if (!FileUtils.isDir(dir)) {
            LogUtils.d(OssResHelper.class, "refreshOssResWithDelExpire", "目录不存在");
            return;
        }
        final String expireTime = DateUtils.getString(expireAt, ConstantUtils.FORMAT_CHINA_Y_M_D_H_M);
        // 开线程
        MyApp.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                List<File> existsFileList = FileUtils.listFilesAndDirInDir(dir, true);
                if (ossKeyList == null || ossKeyList.size() <= 0) {
                    // 没有oss 直接删除对应的目录下超时的
                    LogUtils.d(OssResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 删除全部过期文件");
                    if (existsFileList == null || existsFileList.size() <= 0) {
                        LogUtils.d(OssResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 没有文件");
                    } else {
                        for (File file : existsFileList) {
                            if (file == null) continue;
                            long lastModified = file.lastModified();
                            String lastModifyTime = DateUtils.getString(lastModified, ConstantUtils.FORMAT_CHINA_Y_M_D_H_M);
                            if (lastModified > expireAt) {
                                LogUtils.i(OssResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 留下没过期文件(" + file.getName() + "): 修改时间 == " + lastModifyTime + " , 过期时间 == " + expireTime);
                            } else {
                                LogUtils.i(OssResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 删除已过期文件(" + file.getName() + "): 修改时间 == " + lastModifyTime + " , 过期时间 == " + expireTime);
                                ResHelper.deleteFileInBackground(file);
                            }
                        }
                    }
                    return;
                }
                // 过期的文件 (也就是ossKeyList里不存在的文件)
                List<File> expireFileList = new ArrayList<>();
                // 获取过期的文件(放在上面可以少判断点)
                if (existsFileList != null && existsFileList.size() > 0) {
                    // 本地有oss的文件
                    for (File file : existsFileList) {
                        // 文件不存在则直接检查下一个文件
                        if (file == null) continue;
                        boolean find = false;
                        // 检查是不是oss里对应的文件
                        for (String ossKey : ossKeyList) {
                            String keyFileName = OssResHelper.getKeyFileName(ossKey);
                            if (file.getName().trim().equals(keyFileName.trim())) {
                                // 发现文件，直接走下面的流程
                                find = true;
                                break;
                            }
                        }
                        if (!find) {
                            // 没发现的文件，再检查一下过期时间
                            long lastModified = file.lastModified();
                            String lastModifyTime = DateUtils.getString(lastModified, ConstantUtils.FORMAT_CHINA_Y_M_D_H_M);
                            if (lastModified > expireAt) {
                                LogUtils.i(OssResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 留下没过期文件(" + file.getName() + "): 修改时间 == " + lastModifyTime + " , 过期时间 == " + expireTime);
                            } else {
                                //LogUtils.i(OssResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 删除已过期文件(" + file.getName() + "): 修改时间 == " + lastModifyTime + " , 过期时间 == " + expireTime);
                                expireFileList.add(file);
                            }
                        }
                        //else {
                        // 发现的文件 暂不更新修改时间，下面会统一修改
                        //boolean success = file.setLastModified(DateUtils.getCurrentLong());
                        //if (success) {
                        //    long lastModified = file.lastModified();
                        //    String lastModifyTime = DateUtils.getString(lastModified, ConstantUtils.FORMAT_CHINA_Y_M_D_H_M);
                        //    LogUtils.i(OssResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 更新文件(" + file.getName() + "): 过期时间 == " + lastModifyTime);
                        //} else {
                        //    LogUtils.w(OssResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 更新文件(" + file.getName() + "): 过期时间 == 失败");
                        //}
                        //}
                    }
                } else {
                    LogUtils.d(OssResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + ") 无文件");
                }
                // 加新的 (也就是expireFileList里不存在的文件)
                for (String ossKey : ossKeyList) {
                    if (StringUtils.isEmpty(ossKey)) return;
                    if (!OssResHelper.isKeyFileExists(ossKey)) {
                        // 不存在的的直接下载
                        LogUtils.i(OssResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 下载匹配的文件 == " + ossKey);
                        OssHelper.downloadOssFileByKey(ossKey);
                    } else {
                        // 更新文件修改时间(不要在删旧的循环里更新，会重复)
                        File file = OssResHelper.newKeyFile(ossKey);
                        if (file == null) continue;
                        boolean success = file.setLastModified(DateUtils.getCurrentLong());
                        if (success) {
                            long lastModified = file.lastModified();
                            String lastModifyTime = DateUtils.getString(lastModified, ConstantUtils.FORMAT_CHINA_Y_M_D_H_M);
                            LogUtils.i(OssResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 更新文件(" + file.getName() + "): 过期时间 == " + lastModifyTime);
                        } else {
                            LogUtils.w(OssResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 更新文件(" + file.getName() + "): 过期时间 == 失败");
                        }
                        // 是不是在过期里有
                        if (expireFileList.size() > 0) {
                            ListIterator<File> iterator = expireFileList.listIterator();
                            if (iterator.hasNext()) {
                                File expireFile = iterator.next();
                                if (expireFile.getName().trim().equals(file.getName().trim())) {
                                    // 过期文件里有oss文件，则从过期里删除
                                    LogUtils.d(OssResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 取消删除过期但刚更新的文件(" + file.getName() + ")");
                                    iterator.remove();
                                }
                            }
                        }
                    }
                }
                // 最后删除过期文件
                if (expireFileList.size() > 0) {
                    for (File file : expireFileList) {
                        if (file == null) continue;
                        long lastModified = file.lastModified();
                        String lastModifyTime = DateUtils.getString(lastModified, ConstantUtils.FORMAT_CHINA_Y_M_D_H_M);
                        LogUtils.i(OssResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 删除已过期文件(" + file.getName() + "): 修改时间 == " + lastModifyTime + " , 过期时间 == " + expireTime);
                    }
                    ResHelper.deleteFileListInBackground(expireFileList);
                } else {
                    LogUtils.i(OssResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 没有发现过期文件");
                }
            }
        });
    }

}
