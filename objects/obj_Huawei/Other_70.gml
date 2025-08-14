/// @description Callbacks

if(async_load[? "id"] == HuaweiAds_ASYCN_ID){
	switch(async_load[? "type"]){
		
		#region General Events
		
		case HuaweiAds_INITIALIZED:
			show_message_async("SDK Initialized!");
			with(obj_Banner){
				enabled = true;
			}
			break;
		
		#endregion
			
		#region Banner Events	
		
		case HuaweiAds_BANNER_LOADED:
			// The banner add has been loaded properly
			break;			
		case HuaweiAds_BANNER_CLICKED:
			// The banner has been clicked by the user
			break;		
		case HuaweiAds_BANNER_CLOSED:
			// The banner has been closed by the user
			break;
		
		#endregion
			
		#region Interstitial Events	
		
		case HuaweiAds_INTERSTITIAL_LOADED:
			// The interstitial add has been loaded properly
			with(obj_Interstitial){
				enabled = true;
			}
			break;			
		case HuaweiAds_INTERSTITIAL_FAILEDLOAD:
			var _error_message = async_load[? "value"];
			// The interstitial load failed!
			break;		
		case HuaweiAds_INTERSTITIAL_CLOSED:
			// The interstitial has been closed by the user
			break;
		
		#endregion
		
		#region Rewarded Events
			
		case HuaweiAds_REWARDED_LOADED:
			// The rewarded add has been loaded properly
			with(obj_Rewarded){
				enabled = true;
			}
			break;
		case HuaweiAds_REWARDED_FAILEDLOAD:
			var _error_message = async_load[? "value"];
			// The rewarded video load failed!
			break;		
		case HuaweiAds_REWARDED_CLOSED:
			// The rewarded video has been closed but don't give any reward here!
			break;
		case HuaweiAds_REWARDED_OPENED:
			// The rewarded video has been shown and started playing the video to the player
			break;
		case HuaweiAds_REWARDED_FAILEDTOSHOW:
			// The rewarded video failed in showing the video to the player
			break;
		case HuaweiAds_REWARDED_REWARDED:
			// When the rewarded video finished and gives the reward to the player
			var _coins = async_load[? "value"];
			show_message_async("Reward Unlocked! +" + string(_coins) + " coins!");
			break;
			
		#endregion
	}
}