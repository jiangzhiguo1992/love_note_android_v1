package com.jiangzg.mianmian.helper;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.mianmian.base.MyApp;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Diary;
import com.jiangzg.mianmian.domain.Picture;
import com.jiangzg.mianmian.domain.WallPaper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

/**
 * Created by JZG on 2018/5/14.
 * OssResHelper
 */
public class OssResHelper {

    private static final String LOG_TAG = "OssResHelper";

    // 获取ossKey的文件
    public static File newKeyFile(String objectKey) {
        if (StringUtils.isEmpty(objectKey)) {
            LogUtils.w(LOG_TAG, "newOutFile: objectKey == null");
            return null;
        }
        LogUtils.i(LOG_TAG, "newOutFile: objectKey == " + objectKey);
        return new File(ResHelper.getOssOutDirPath(), objectKey);
    }

    // 获取ossKey的文件所在目录的所有文件
    public static List<File> getKeyParentFiles(String objectKey) {
        File file = newKeyFile(objectKey);
        if (file == null) {
            LogUtils.w(LOG_TAG, "getDirFilesByKey: file == null");
            return new ArrayList<>();
        }
        //StringBuilder builder = new StringBuilder();
        //String[] split = objectKey.split("/");
        //for (int i = 0; i < split.length - 1; i++) {
        //    // 不要最后一个
        //    builder.append(split[i]).append("/");
        //}
        //String diaPath = builder.toString();
        //File dir = new File(AppInfo.get().getOutFilesDir(), diaPath);
        File dir = file.getParentFile();
        FileUtils.createOrExistsDir(dir);
        return FileUtils.listFilesAndDirInDir(dir, true);
    }

    // ossKey的文件是否存在的文件名
    public static String getKeyFileName(String objectKey) {
        if (StringUtils.isEmpty(objectKey)) {
            LogUtils.w(LOG_TAG, "getKeyFileName: objectKey == null");
            return "";
        }
        String[] split = objectKey.split("/");
        return split[split.length - 1].trim();
    }

    // ossKey的文件是否存在
    public static boolean isKeyFileExists(String objectKey) {
        File file = newKeyFile(objectKey);
        boolean fileExists = FileUtils.isFileExists(file);
        LogUtils.i(LOG_TAG, "isKeyFileExists: " + objectKey + " == " + fileExists);
        return fileExists;
    }

    // 刷新本地oss资源，删除不存在的ossKey
    private static void refreshOssResWithDelNoExists(final File dir, final List<String> ossKeyList) {
        // 开线程
        MyApp.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                List<File> existsFileList = FileUtils.listFilesAndDirInDir(dir, true);
                if (ossKeyList == null || ossKeyList.size() <= 0) {
                    // 没有oss 直接删除对应的目录
                    LogUtils.w(LOG_TAG, "refreshOssResWithDelNoExists: ossKeyList == null");
                    ResHelper.deleteFileListInBackground(existsFileList);
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
                            LogUtils.w(LOG_TAG, "refreshOssResWithDelNoExists: 删除匹配的oss文件 == " + file.getAbsolutePath());
                            ResHelper.deleteFileInBackground(file);
                        } else {
                            LogUtils.i(LOG_TAG, "refreshOssResWithDelNoExists: 发现匹配的oss文件 == " + file.getAbsolutePath());
                        }
                    }
                } else {
                    LogUtils.w(LOG_TAG, "refreshOssResWithDelNoExists: 存储目录不存在");
                }
                // 加新的
                for (String ossKey : ossKeyList) {
                    if (StringUtils.isEmpty(ossKey)) return; // TODO
                    if (!OssResHelper.isKeyFileExists(ossKey)) {
                        // 不存在的的直接下载
                        LogUtils.w(LOG_TAG, "refreshOssResWithDelNoExists: 下载匹配的oss对象 == " + ossKey);
                        OssHelper.downloadFileByKey(ossKey);
                    }
                }
            }
        });
    }

    // 刷新本地oss资源，删除过时的ossKey
    private static void refreshOssResWithDelExpire(final File dir, final List<String> ossKeyList, final long expireAt) {
        // 开线程
        MyApp.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                List<File> existsFileList = FileUtils.listFilesAndDirInDir(dir, true);
                if (ossKeyList == null || ossKeyList.size() <= 0) {
                    // 没有oss 直接删除对应的目录下超时的
                    LogUtils.w(LOG_TAG, "refreshOssResWithDelExpire: ossKeyList == null");
                    if (existsFileList == null || existsFileList.size() <= 0) return;
                    for (File file : existsFileList) {
                        if (file == null) continue;
                        long lastModified = file.lastModified();
                        if (lastModified < expireAt) {
                            LogUtils.w(LOG_TAG, "refreshOssResWithDelExpire: 删除过期的oss文件: 过期时间== " + DateUtils.getString(lastModified, ConstantUtils.FORMAT_CHINA_Y_M_D_H_M));
                            ResHelper.deleteFileInBackground(file);
                        }
                    }
                    return;
                }
                // 过期的文件
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
                        // 没发现的文件，再检查一下过期时间
                        if (!find) {
                            long lastModified = file.lastModified();
                            if (lastModified < expireAt) {
                                LogUtils.w(LOG_TAG, "refreshOssResWithDelExpire: 发现过期的oss文件: 过期时间== " + DateUtils.getString(lastModified, ConstantUtils.FORMAT_CHINA_Y_M_D_H_M));
                                expireFileList.add(file);
                            } else {
                                LogUtils.w(LOG_TAG, "refreshOssResWithDelExpire: 发现正常的oss文件: 修改时间== " + DateUtils.getString(lastModified, ConstantUtils.FORMAT_CHINA_Y_M_D_H_M));
                            }
                        } else {
                            LogUtils.w(LOG_TAG, "refreshOssResWithDelExpire: 发现匹配的oss文件 == " + file.getAbsolutePath());
                        }
                    }
                } else {
                    LogUtils.w(LOG_TAG, "refreshOssResWithDelExpire: 存储目录不存在");
                }
                // 加新的
                for (String ossKey : ossKeyList) {
                    if (StringUtils.isEmpty(ossKey)) return; // TODO
                    if (!OssResHelper.isKeyFileExists(ossKey)) {
                        // 不存在的的直接下载
                        LogUtils.w(LOG_TAG, "refreshOssResWithDelExpire: 下载匹配的oss对象 == " + ossKey);
                        OssHelper.downloadFileByKey(ossKey);
                    } else {
                        // 更新文件修改时间(不要在删旧的循环里更新，会重复)
                        File file = OssResHelper.newKeyFile(ossKey);
                        if (file == null) continue;
                        boolean success = file.setLastModified(DateUtils.getCurrentLong());
                        if (success) {
                            long lastModified = file.lastModified();
                            LogUtils.w(LOG_TAG, "refreshOssResWithDelExpire: 修改获取的oss文件: 修改时间 == " + DateUtils.getString(lastModified, ConstantUtils.FORMAT_CHINA_Y_M_D_H_M));
                        }
                        // 是不是在过期里有
                        if (expireFileList.size() > 0) {
                            ListIterator<File> iterator = expireFileList.listIterator();
                            if (iterator.hasNext()) {
                                File expireFile = iterator.next();
                                if (expireFile.getName().trim().equals(file.getName().trim())) {
                                    // 过期文件里有oss文件，则从过期里删除
                                    iterator.remove();
                                    LogUtils.w(LOG_TAG, "refreshOssResWithDelExpire: 取消删除正常的oss文件 == " + file.getAbsolutePath());
                                }
                            }
                        }
                    }
                }
                // 最后删除过期文件
                ResHelper.deleteFileListInBackground(expireFileList);
            }
        });
    }

    /**
     * ****************************************Apk****************************************
     */
    public static File newApkDir() {
        return new File(ResHelper.getOssOutDirPath(), "apk");
    }

    public static File newApkFile(String versionName) {
        File apkFile = new File(OssResHelper.newApkDir(), versionName + ".apk");
        LogUtils.i(LOG_TAG, "createApkFile: " + apkFile.getAbsolutePath());
        return apkFile;
    }

    /**
     * ****************************************Avatar****************************************
     */
    // 存放avatar的目录
    public static File newAvatarDir() {
        String pathCoupleAvatar = SPHelper.getOssInfo().getPathCoupleAvatar();
        if (StringUtils.isEmpty(pathCoupleAvatar)) {
            LogUtils.w(LOG_TAG, "getAvatarDir: pathCoupleAvatar == null");
            return null;
        }
        return new File(ResHelper.getOssOutDirPath(), pathCoupleAvatar);
    }

    // 刷新本地的avatar
    public static void refreshAvatarRes() {
        File avatarDir = OssResHelper.newAvatarDir();
        Couple couple = SPHelper.getCouple();
        List<String> imageList = new ArrayList<>();
        imageList.add(couple.getCreatorAvatar());
        imageList.add(couple.getInviteeAvatar());
        refreshOssResWithDelNoExists(avatarDir, imageList);
    }

    /**
     * ****************************************WallPaper****************************************
     */
    // 获取wp目录里的所有文件
    public static File getWallPaperDir() {
        String pathCoupleWall = SPHelper.getOssInfo().getPathCoupleWall();
        if (StringUtils.isEmpty(pathCoupleWall)) {
            LogUtils.w(LOG_TAG, "getWallPaperDir: pathCoupleWall == null");
            return null;
        }
        return new File(ResHelper.getOssOutDirPath(), pathCoupleWall);
    }

    // 获取随机的wp
    public static File getWallPaperRandom() {
        File wallPaperDir = getWallPaperDir();
        List<File> fileList = FileUtils.listFilesAndDirInDir(wallPaperDir, true);
        if (fileList == null || fileList.size() <= 0) {
            LogUtils.i(LOG_TAG, "getWallPaperRandom: 没有发现WallPaper的存储目录");
            return null;
        }
        Random random = new Random();
        int nextInt = random.nextInt(fileList.size());
        return fileList.get(nextInt);
    }

    // 刷新本地的wp
    public static void refreshWallPaperRes() {
        File wallPaperDir = OssResHelper.getWallPaperDir();
        WallPaper wallPaper = SPHelper.getWallPaper();
        List<String> imageList = wallPaper.getContentImageList();
        refreshOssResWithDelNoExists(wallPaperDir, imageList);
    }

    /**
     * ****************************************Diary****************************************
     */
    // 获取diary目录里的所有文件
    public static File getDiaryDir() {
        String pathBookDiary = SPHelper.getOssInfo().getPathBookDiary();
        if (StringUtils.isEmpty(pathBookDiary)) {
            LogUtils.w(LOG_TAG, "getDiaryDir: pathBookDiary == null");
            return null;
        }
        return new File(ResHelper.getOssOutDirPath(), pathBookDiary);
    }

    // 刷新本地的diary
    public static void refreshDiaryRes(final List<Diary> diaryList) {
        File diaryDir = OssResHelper.getDiaryDir();
        ArrayList<String> ossKeyList = ConvertHelper.getOssKeyListByDiary(diaryList);
        long currentLong = DateUtils.getCurrentLong();
        long expireAt = currentLong - SPHelper.getLimit().getBookResExpireSec() * 1000;
        refreshOssResWithDelExpire(diaryDir, ossKeyList, expireAt);
    }

    /**
     * ****************************************Picture****************************************
     */
    // 获取picture目录里的所有文件
    public static File getPictureDir() {
        String pathBookPicture = SPHelper.getOssInfo().getPathBookPicture();
        if (StringUtils.isEmpty(pathBookPicture)) {
            LogUtils.w(LOG_TAG, "getPictureDir: pathBookPicture == null");
            return null;
        }
        return new File(ResHelper.getOssOutDirPath(), pathBookPicture);
    }

    // 刷新本地的picture
    public static void refreshPictureRes(List<Picture> pictureList) {
        File pictureDir = OssResHelper.getPictureDir();
        ArrayList<String> ossKeyList = ConvertHelper.getOssKeyListByPicture(pictureList);
        long currentLong = DateUtils.getCurrentLong();
        long expireAt = currentLong - SPHelper.getLimit().getBookResExpireSec() * 1000;
        refreshOssResWithDelExpire(pictureDir, ossKeyList, expireAt);
    }

}
