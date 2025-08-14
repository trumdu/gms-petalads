package ${YYAndroidPackageName};

import ${YYAndroidPackageName}.R;
import com.yoyogames.runner.RunnerJNILib;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import android.widget.AbsoluteLayout;
import android.view.ViewGroup;
import android.widget.Toast;
import java.lang.Exception;
import java.net.URL;
import android.provider.Settings;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;

import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.InterstitialAd;
import com.huawei.hms.ads.reward.Reward;
import com.huawei.hms.ads.reward.RewardAd;
import com.huawei.hms.ads.reward.RewardAdLoadListener;
import com.huawei.hms.ads.reward.RewardAdStatusListener;
import com.huawei.hms.ads.BannerAdSize;
import com.huawei.hms.ads.banner.BannerView;
import com.huawei.hms.ads.RequestOptions;
import com.huawei.hms.ads.NonPersonalizedAd;
import com.huawei.hms.ads.ActivateStyle;
import com.huawei.hms.ads.AdParam.ErrorCode;
import com.huawei.hms.ads.AudioFocusType;
import com.huawei.hms.ads.ContentClassification;
import com.huawei.hms.ads.Gender;
import com.huawei.hms.ads.TagForChild;
import com.huawei.hms.ads.UnderAge;

import androidx.annotation.Nullable;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.Gravity;
import androidx.annotation.NonNull;
import android.widget.FrameLayout;
import android.util.Log;
import java.util.List;
import android.util.DisplayMetrics;
import android.view.Display;

public class HuaweiAdsGM extends RunnerSocial {

  private static final int EVENT_OTHER_SOCIAL = 70;
  private static final String SOCIAL_ASYCN_ID = "HuaweiAds";
  private static final String SDKNAME = "HuaweiAds";
  public static Activity activity = RunnerActivity.CurrentActivity;

	private RelativeLayout layout;

  private String BannerAdId = "";
  private String RewardedAdId = "";
  private String InterstitialAdId = "";

  private RewardAd rewardAd;
  private BannerView bannerView;
  private InterstitialAd interstitialAd;

  private void LogEvent(String message){
    Log.i("yoyo", SDKNAME+": "+message);
  }

  private void SentSocialEvent(String type){
    int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
    RunnerJNILib.DsMapAddString( dsMapIndex, "type", type);
    RunnerJNILib.DsMapAddString( dsMapIndex, "id", SOCIAL_ASYCN_ID);
    RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, EVENT_OTHER_SOCIAL);
  }

  private void SentSocialEvent(String type, double value){
    int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
    RunnerJNILib.DsMapAddDouble( dsMapIndex, "value", value);
    RunnerJNILib.DsMapAddString( dsMapIndex, "type", type);
    RunnerJNILib.DsMapAddString( dsMapIndex, "id", SOCIAL_ASYCN_ID);
    RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, EVENT_OTHER_SOCIAL);
  }

  private void SentSocialEvent(String type, String value){
    int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
    RunnerJNILib.DsMapAddString( dsMapIndex, "value", value);
    RunnerJNILib.DsMapAddString( dsMapIndex, "type", type);
    RunnerJNILib.DsMapAddString( dsMapIndex, "id", SOCIAL_ASYCN_ID);
    RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, EVENT_OTHER_SOCIAL);
  }

  private String GetExtensionOptionsValue(String extensionName, String valueID){
    return RunnerJNILib.extOptGetString(extensionName, valueID);
  }

	public static boolean init_success = false;

	public void HuaweiAds_Initialize() {
		RunnerActivity.ViewHandler.post(new Runnable() {
			public void run() {

				try {
          HwAds.init(activity);
          init_success = true;
          SentSocialEvent("init_success");
          LogEvent("Inited");
          // Init
          rewardInit();
          bannerInit();
          interstitialInit();

				} catch (Exception e) {
          LogEvent("HuaweiAds Init Error: " + e.toString());
				}
			}
		});
	};

  // Interstitial

  private String adErrorCodeToString(int code) {
    switch (code) {
      case 0:
        LogEvent("Internal error.");
        return "INNER";
      case 1:
        LogEvent("Invalid ad request. For example, the ad unit ID is not set or the banner ad dimensions are invalid.");
        return "INVALID_REQUEST";
      case 2:
        LogEvent("Failed to send the ad request due to a network connection error.");
        return "NETWORK_ERROR";
      case 3:
        LogEvent("The ad request is sent successfully, but the server returns a response indicating no available ad assets.");
        return "NO_AD";
      case 4:
        LogEvent("The ad is being loaded and cannot be requested again.");
        return "AD_LOADING";
      case 5:
        LogEvent("The API version is not supported by Ads Kit.");
        return "LOW_API";
      case 6:
        LogEvent("The banner ad has expired.");
        return "BANNER_AD_EXPIRE";
      case 7:
        LogEvent("The banner ad task is removed.");
        return "BANNER_AD_CANCEL";
      case 8:
        LogEvent("The HMS Core (APK) version does not support the setting of AppInfo.");
        return "HMS_NOT_SUPPORT_SET_APP";
      default:
        LogEvent("Undefined error");
        return "UNDEFINED";
    }
  }

  private AdListener interstitialAdListener = new AdListener() {
    @Override
    public void onAdLoaded() {
      LogEvent("Interstitial_onAdLoaded");
      SentSocialEvent("Interstitial_onAdLoaded");
    }

    @Override
    public void onAdFailed(int errorCode) {
      LogEvent("Interstitial_onAdFailed");
      SentSocialEvent("Interstitial_onAdFailed", adErrorCodeToString(errorCode));
    }

    @Override
    public void onAdLeave() {
      LogEvent("Interstitial_onAdLeave");
      SentSocialEvent("Interstitial_onAdLeave");
    }

    @Override
    public void onAdOpened() {
      LogEvent("Interstitial_onAdOpened");
      SentSocialEvent("Interstitial_onAdOpened");
    }

    @Override
    public void onAdClicked() {
      LogEvent("Interstitial_onAdClicked");
      SentSocialEvent("Interstitial_onAdClicked");
    }

    @Override
    public void onAdClosed() {
      LogEvent("Interstitial_onAdClosed");
      SentSocialEvent("Interstitial_onAdClosed");
      loadInterstitialAd();
    }

    @Override
    public void onAdImpression()  {
      LogEvent("Interstitial_onAdImpression");
      SentSocialEvent("Interstitial_onAdImpression");
    }
  };

  private void interstitialInit() {
    
		RunnerActivity.ViewHandler.post(new Runnable() {
			public void run() {
        try {
		
          if(!init_success) {
            return;
          }

          if (GetExtensionOptionsValue(SDKNAME, "interstitial_status").equalsIgnoreCase("disable")) {
            LogEvent("Interstitial disabled.");
            return;
          }

          InterstitialAdId = GetExtensionOptionsValue(SDKNAME,"interstitial_ad_id");
          if (InterstitialAdId.length() > 1) {
            LogEvent("Set Interstitial Ad ID " + InterstitialAdId);
            interstitialAd = new InterstitialAd(activity);
            interstitialAd.setAdId(InterstitialAdId);
            interstitialAd.setAdListener(interstitialAdListener);
            loadInterstitialAd();
            LogEvent("Interstitial inited.");
            
          }
          else {
            LogEvent("Interstitial disabled. Ad ID is empty.");
          }

        } catch (Exception e) {
          LogEvent("Interstitial Init Error: " + e.toString());
        }
      }
    });

  }

  public void loadInterstitialAd() {
    
		RunnerActivity.ViewHandler.post(new Runnable() {
			public void run() {
        try{
		
          if(!init_success) {
            return;
          }
          
          if(InterstitialAdId.length() == 0) {
            return;
          }
          
          if (interstitialAd == null) {
            return;
          }

          AdParam adParam = new AdParam.Builder().build();
          interstitialAd.loadAd(adParam);

        }catch(Exception e){
          LogEvent("Error trying to load Interstitial Ad!");
        }
      }
    });

  }

  public double isInterstitialLoaded() {
    try{

      if(!init_success) {
        return 0;
      }
      
      if(InterstitialAdId.length() == 0) {
        return 0;
      }
      
      if (interstitialAd == null) {
        return 0;
      }

      if (interstitialAd.isLoaded()) {
        return 1;
      }
      
    }catch(Exception e){
      LogEvent("Error trying to check load Interstitial: " + e.getMessage());
    }

    return 0;
  }

  public void showInterstitial() {
    
		RunnerActivity.ViewHandler.post(new Runnable() {
			public void run() {
        try{
		
          if(!init_success) {
            return;
          }
          
          if(InterstitialAdId.length() == 0) {
            return;
          }
          
          if (interstitialAd == null) {
            return;
          }

          if (interstitialAd.isLoaded()) {
            interstitialAd.show();
          } else {
            LogEvent("Interstitial Ad did not load");
          }
        }catch(Exception e){
          LogEvent("Error trying to show Interstitia: " + e.getMessage());
        }
      }
    });

  }

  // Banner
  
  private AdListener bannerAdListener = new AdListener() {
    @Override
    public void onAdLoaded() {
      SentSocialEvent("Banner_onAdLoaded");
      LogEvent("Banner_onAdLoaded");
    }
    @Override
    public void onAdFailed(int errorCode) {
      LogEvent("Banner_onAdFailed");
      SentSocialEvent("Banner_onAdFailed", adErrorCodeToString(errorCode));
    }
    @Override
    public void onAdOpened() {
      LogEvent("Banner_onAdOpened");
      SentSocialEvent("Banner_onAdOpened");
    }
    @Override
    public void onAdClicked() {
      LogEvent("Banner_onAdClicked");
      SentSocialEvent("Banner_onAdClicked");
    }
    @Override
    public void onAdLeave() {
      LogEvent("Banner_onAdLeave");
      SentSocialEvent("Banner_onAdLeave");
    }
    @Override
    public void onAdClosed() {
      LogEvent("Banner_onAdClosed");
      SentSocialEvent("Banner_onAdClosed");
    }
  };

  private void bannerInit() {
    
		RunnerActivity.ViewHandler.post(new Runnable() {
			public void run() {
        try {
		
          if(!init_success) {
            return;
          }

          if (GetExtensionOptionsValue(SDKNAME, "banner_status").equalsIgnoreCase("disable")) {
            LogEvent("Banner disabled.");
            return;
          }

          BannerAdId = GetExtensionOptionsValue(SDKNAME,"banner_ad_id");
          if (BannerAdId.length() > 1) {
            LogEvent("Set Banner Ad ID " + BannerAdId);
            LogEvent("Banner inited.");
          }
          else {
            LogEvent("Banner disabled. Ad ID is empty.");
          }

        } catch (Exception e) {
          LogEvent("Banner Init Error: " + e.toString());
        }
      }
    });
    
  }

  private BannerAdSize getBannerSize() {
    String size = GetExtensionOptionsValue(SDKNAME,"banner_size_type");
		if (size.equalsIgnoreCase("BANNER_SIZE_320_50")) {
      return BannerAdSize.BANNER_SIZE_320_50;
		} else if (size.equalsIgnoreCase("BANNER_SIZE_300_250")) {
      return BannerAdSize.BANNER_SIZE_300_250;
		} else if (size.equalsIgnoreCase("BANNER_SIZE_160_600")) {
      return BannerAdSize.BANNER_SIZE_160_600;
		} else if (size.equalsIgnoreCase("BANNER_SIZE_320_100")) {
      return BannerAdSize.BANNER_SIZE_320_100;
		} else if (size.equalsIgnoreCase("BANNER_SIZE_360_57")) {
      return BannerAdSize.BANNER_SIZE_360_57;
		} else if (size.equalsIgnoreCase("BANNER_SIZE_360_144")) {
      return BannerAdSize.BANNER_SIZE_360_144;
		} else if (size.equalsIgnoreCase("BANNER_SIZE_468_60")) {
      return BannerAdSize.BANNER_SIZE_468_60;
		} else if (size.equalsIgnoreCase("BANNER_SIZE_728_90")) {
      return BannerAdSize.BANNER_SIZE_728_90;
		} else if (size.equalsIgnoreCase("BANNER_SIZE_SMART")) {
      return BannerAdSize.BANNER_SIZE_SMART;
		} else if (size.equalsIgnoreCase("BANNER_SIZE_ADVANCED")) {
      return BannerAdSize.BANNER_SIZE_ADVANCED;
		} else if (size.equalsIgnoreCase("BANNER_SIZE_DYNAMIC")) {
      return BannerAdSize.BANNER_SIZE_DYNAMIC;
		} else {
      return BannerAdSize.BANNER_SIZE_320_50;
		}
	}

  public void showBanner() {

		RunnerActivity.ViewHandler.post(new Runnable() {
			public void run() {
        try {
		
          if(!init_success) {
            return;
          }
          
          if(RewardedAdId.length() == 0) {
            return;
          }
          
          if (bannerView != null) {
            return;
          }

          layout = new RelativeLayout(activity);

          BannerAdSize adSize = getBannerSize();
          RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); 

          // By default, the banner size is measured in "DP". In landscape orientation, the banner takes up too much space.
          if (GetExtensionOptionsValue(SDKNAME, "banner_size_units").equalsIgnoreCase("px")) {
            params.height = (int)(adSize.getHeight() * activity.getResources().getDisplayMetrics().density);
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            LogEvent("Banner set size in PX");
          }
          else {
            LogEvent("Banner set size in DP");
          }

          params.addRule(RelativeLayout.CENTER_HORIZONTAL);
          if (GetExtensionOptionsValue(SDKNAME,"banner_ad_position").equalsIgnoreCase("POSITION_TOP")) {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            LogEvent("Banner position on TOP");
          }
          else {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            LogEvent("Banner position on BOTTOM");
          }

          bannerView = new BannerView(activity);

          layout.addView((View) bannerView, params);
          final ViewGroup rootView = activity.findViewById(android.R.id.content);
          rootView.addView((View) layout);

          bannerView.setAdId(BannerAdId);
          bannerView.setBannerAdSize(adSize);
          bannerView.setBannerRefresh(30);
          bannerView.setAdListener(bannerAdListener);
					bannerView.setVisibility(View.VISIBLE);
          AdParam adParam = new AdParam.Builder().build();
          bannerView.loadAd(adParam); 

          LogEvent("Banner create");

        } catch (Exception e) {
          LogEvent("Banner destroy Error: " + e.toString());
        }
      }
    });

  }

  public void removeBanner() {
    
		RunnerActivity.ViewHandler.post(new Runnable() {
			public void run() {
        try {
		
          if(!init_success) {
            return;
          }
          
          if(BannerAdId.length() == 0) {
            return;
          }
          
          if (bannerView == null) {
            return;
          }

          if (bannerView != null) {

            bannerView.setVisibility(View.GONE);
            layout.removeView(bannerView);
            bannerView.destroy();
            bannerView = null;

            final ViewGroup rootView = activity.findViewById(android.R.id.content);
            rootView.removeView(layout);
            layout = null;

            LogEvent("Banner destroy.");
          }

        } catch (Exception e) {
          LogEvent("Banner destroy Error: " + e.toString());
        }
      }
    });
  }

  // Reward

  private String rewardAdErrorCodeToString(int code) {
    switch (code) {
      case 0:
        LogEvent("Internal error.");
        return "INTERNAL";
      case 1:
        LogEvent("Duplicate ad.");
        return "REUSED";
      case 2:
        LogEvent("The ad has not been loaded.");
        return "NOT_LOADED";
      case 3:
        LogEvent("The rewarded ad is played in the background.");
        return "BACKGROUND";
      default:
        LogEvent("Undefined error");
        return "UNDEFINED";
    }
  }

  private void rewardInit() {
    
		RunnerActivity.ViewHandler.post(new Runnable() {
			public void run() {
        try {
		
          if(!init_success) {
            return;
          }

          if (GetExtensionOptionsValue(SDKNAME, "rewarded_status").equalsIgnoreCase("disable")) {
            LogEvent("Rewarded disabled.");
            return;
          }

          RewardedAdId = GetExtensionOptionsValue(SDKNAME,"rewarded_ad_id");
          if (RewardedAdId.length() > 1) {
            LogEvent("Set Rewarded Ad ID " + RewardedAdId);
            rewardAd = new RewardAd(activity, RewardedAdId);
            loadRewardAd();
            LogEvent("Reward inited.");

          }
          else {
            LogEvent("Rewarded disabled. Ad ID is empty.");
          }

        } catch (Exception e) {
          LogEvent("Rewarded Init Error: " + e.toString());
        }
      }
    });

  }
  
  public void loadRewardAd() {
    
		RunnerActivity.ViewHandler.post(new Runnable() {
			public void run() {
        try{
		
          if(!init_success) {
            return;
          }
          
          if(RewardedAdId.length() == 0) {
            return;
          }
          
          if (rewardAd == null) {
            return;
          }

          AdParam adParam = new AdParam.Builder().build();
          RewardAdLoadListener rewardAdLoadListener = new RewardAdLoadListener() {
              @Override
              public void onRewardAdFailedToLoad(int errorCode) {
                LogEvent("Rewarded_onAdFailedToLoad");
                SentSocialEvent("Rewarded_onAdFailedToLoad", rewardAdErrorCodeToString(errorCode));
              }
  
              @Override
              public void onRewardedLoaded() {
                LogEvent("Rewarded_onAdLoaded");
                SentSocialEvent("Rewarded_onAdLoaded");
              }
          };
          rewardAd.loadAd(adParam, rewardAdLoadListener);
        }catch(Exception e){
          LogEvent("Error trying to load Rewarded Ad!");
        }
      }
    });

  }

  public double isRewardLoaded() {
    try{

      if(!init_success) {
        return 0;
      }
      
      if(RewardedAdId.length() == 0) {
        return 0;
      }
      
      if (rewardAd == null) {
        return 0;
      }

      if (rewardAd.isLoaded()) {
        return 1;
      }
      
    }catch(Exception e){
      LogEvent("Error trying to check load Reward: " + e.getMessage());
    }

    return 0;
  }

  public void showReward() {
    
		RunnerActivity.ViewHandler.post(new Runnable() {
			public void run() {
        try{
		
          if(!init_success) {
            return;
          }
          
          if(RewardedAdId.length() == 0) {
            return;
          }
          
          if (rewardAd == null) {
            return;
          }

          if (rewardAd.isLoaded()) {
            RewardAdStatusListener listener = new RewardAdStatusListener() {
              @Override
              public void onRewardAdClosed() {
                SentSocialEvent("Rewarded_onAdClosed");
                LogEvent("Rewarded_onAdClosed");
                loadRewardAd();
              }

              @Override
              public void onRewardAdFailedToShow(int errorCode) {
                SentSocialEvent("Rewarded_onAdFailedToShow", Integer.toString(errorCode));
                LogEvent("Rewarded_onAdFailedToShow");
                LogEvent("errorCode " + Integer.toString(errorCode));
              }

              @Override
              public void onRewardAdOpened() {
                SentSocialEvent("Rewarded_onAdOpened");
                LogEvent("Rewarded_onAdOpened");
              }

              @Override
              public void onRewarded(Reward reward) {
                int amount = reward.getAmount();
                String name = reward.getName();
                SentSocialEvent("Rewarded_onRewarded", amount);
                LogEvent("Rewarded_onRewarded");
                loadRewardAd();
              }
            };
            rewardAd.show(activity, listener);
          } else {
            LogEvent("Rewarded Ad did not load");
          }
        }catch(Exception e){
          LogEvent("Error trying to show Rewarded: " + e.getMessage());
        }
      }
    });

  }

  //

  public void setPersonalizedAd(double st) {
		
		if(!init_success) {
      return;
    }
    
		RunnerActivity.ViewHandler.post(new Runnable() {
			public void run() {
        try {
          RequestOptions requestOptions = HwAds.getRequestOptions();
          if (st > 0) {
            requestOptions.toBuilder().setNonPersonalizedAd(NonPersonalizedAd.ALLOW_ALL).build();
            LogEvent("Personalized Ad enabled.");
          }
          else {
            requestOptions.toBuilder().setNonPersonalizedAd(NonPersonalizedAd.ALLOW_NON_PERSONALIZED).build();
            LogEvent("Personalized Ad disabled.");
          }

        } catch (Exception e) {
          LogEvent("Banner Init Error: " + e.toString());
        }
      }
    });
    
  }

}
