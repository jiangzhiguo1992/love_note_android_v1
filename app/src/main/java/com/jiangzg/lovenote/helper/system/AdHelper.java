package com.jiangzg.lovenote.helper.system;

import android.content.Context;

import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.model.entity.AdInfo;
import com.qq.e.ads.cfg.BrowserType;
import com.qq.e.ads.cfg.DownAPPConfirmPolicy;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.ads.nativ.NativeExpressMediaListener;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.pi.AdData;
import com.qq.e.comm.util.AdError;

import java.util.List;

/**
 * Created by JZG on 2019-04-25.
 */
public class AdHelper {

    public interface OnAdCallBack {
        void onADLoaded(List<NativeExpressADView> viewList);

        void onADClose(NativeExpressADView view);
    }

    public static boolean canAd(Context context) {
        boolean deviceOk = PermUtils.isPermissionOK(context, PermUtils.deviceInfo);
        boolean appOk = PermUtils.isPermissionOK(context, PermUtils.appInfo);
        return deviceOk && appOk;
    }

    public static void createNativeExpressAD(Context context, int width, int height, int count, OnAdCallBack callBack) {
        if (!canAd(context)) return;
        AdInfo adInfo = SPHelper.getAdInfo();
        String appId = adInfo.getAppId();
        String posId = adInfo.getTopicPostPosId();
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(posId)) return;
        NativeExpressAD ad = new NativeExpressAD(context, new ADSize(width, height), appId, posId, new NativeExpressAD.NativeExpressADListener() {
            @Override
            public void onADLoaded(List<NativeExpressADView> viewList) {
                LogUtils.d(AdHelper.class, "onADLoaded", "onADLoaded");
                if (viewList == null || viewList.size() <= 0) {
                    return;
                }
                for (NativeExpressADView view : viewList) {
                    if (view.getBoundData().getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
                        view.setMediaListener(new NativeExpressMediaListener() {
                            @Override
                            public void onVideoInit(NativeExpressADView nativeExpressADView) {
                                LogUtils.i(AdHelper.class, "onVideoInit", getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
                            }

                            @Override
                            public void onVideoLoading(NativeExpressADView nativeExpressADView) {
                                LogUtils.i(AdHelper.class, "onVideoLoading", getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
                            }

                            @Override
                            public void onVideoReady(NativeExpressADView nativeExpressADView, long l) {
                                LogUtils.i(AdHelper.class, "onVideoReady", getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
                            }

                            @Override
                            public void onVideoStart(NativeExpressADView nativeExpressADView) {
                                LogUtils.i(AdHelper.class, "onVideoStart", getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
                            }

                            @Override
                            public void onVideoPause(NativeExpressADView nativeExpressADView) {
                                LogUtils.i(AdHelper.class, "onVideoPause", getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
                            }

                            @Override
                            public void onVideoComplete(NativeExpressADView nativeExpressADView) {
                                LogUtils.i(AdHelper.class, "onVideoComplete", getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
                            }

                            @Override
                            public void onVideoError(NativeExpressADView nativeExpressADView, AdError error) {
                                LogUtils.w(AdHelper.class, "onVideoError", error.getErrorCode() + "\n" + error.getErrorMsg());
                            }

                            @Override
                            public void onVideoPageOpen(NativeExpressADView nativeExpressADView) {
                                LogUtils.i(AdHelper.class, "onVideoPageOpen", getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
                            }

                            @Override
                            public void onVideoPageClose(NativeExpressADView nativeExpressADView) {
                                LogUtils.i(AdHelper.class, "onVideoPageClose", getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
                            }
                        });
                    }
                }
                if (callBack != null) {
                    callBack.onADLoaded(viewList);
                }
            }

            @Override
            public void onRenderFail(NativeExpressADView nativeExpressADView) {
                LogUtils.w(AdHelper.class, "onRenderFail", nativeExpressADView.toString());
            }

            @Override
            public void onRenderSuccess(NativeExpressADView nativeExpressADView) {
                LogUtils.d(AdHelper.class, "onRenderSuccess", nativeExpressADView.toString() + "\n" + getAdInfo(nativeExpressADView));
            }

            @Override
            public void onADExposure(NativeExpressADView nativeExpressADView) {
                LogUtils.d(AdHelper.class, "onADExposure", nativeExpressADView.toString());
            }

            @Override
            public void onADClicked(NativeExpressADView nativeExpressADView) {
                LogUtils.i(AdHelper.class, "onADClicked", nativeExpressADView.toString());
            }

            @Override
            public void onADClosed(NativeExpressADView nativeExpressADView) {
                LogUtils.d(AdHelper.class, "onADClosed", nativeExpressADView.toString());
                if (callBack != null) {
                    callBack.onADClose(nativeExpressADView);
                }
            }

            @Override
            public void onADLeftApplication(NativeExpressADView nativeExpressADView) {
                LogUtils.d(AdHelper.class, "onADLeftApplication", nativeExpressADView.toString());
            }

            @Override
            public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {
                LogUtils.d(AdHelper.class, "onADOpenOverlay", nativeExpressADView.toString());
            }

            @Override
            public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {
                LogUtils.d(AdHelper.class, "onADCloseOverlay", nativeExpressADView.toString());
            }

            @Override
            public void onNoAD(AdError error) {
                LogUtils.w(AdHelper.class, "onNoAD", error.getErrorCode() + "\n" + error.getErrorMsg());
            }
        });
        ad.setBrowserType(BrowserType.Inner); // app内打开
        ad.setDownAPPConfirmPolicy(DownAPPConfirmPolicy.Default); // 非wifi确认框
        ad.setVideoOption(new VideoOption.Builder()
                .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI) // wifi自动播放
                .setAutoPlayMuted(true) // 自动播放时静音
                .build());
        ad.loadAD(count);
    }

    public static void destroy(NativeExpressADView view) {
        if (view != null) {
            view.destroy();
        }
    }

    private static String getAdInfo(NativeExpressADView nativeExpressADView) {
        AdData adData = nativeExpressADView.getBoundData();
        if (adData != null) {
            StringBuilder infoBuilder = new StringBuilder();
            infoBuilder.append("title:").append(adData.getTitle()).append(",")
                    .append("desc:").append(adData.getDesc()).append(",")
                    .append("patternType:").append(adData.getAdPatternType());
            if (adData.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
                infoBuilder.append(", video info: ")
                        .append(getVideoInfo(adData.getProperty(AdData.VideoPlayer.class)));
            }
            return infoBuilder.toString();
        }
        return null;
    }

    private static String getVideoInfo(AdData.VideoPlayer videoPlayer) {
        if (videoPlayer != null) {
            return "state:" + videoPlayer.getVideoState() + "," +
                    "duration:" + videoPlayer.getDuration() + "," +
                    "position:" + videoPlayer.getCurrentPosition();
        }
        return null;
    }

}
