/// @description Rewarded
event_inherited();
//
if (HuaweiAds_IsRewardLoaded()) {
	HuaweiAds_ShowReward();
	show_message_async("Reward was shown.");
}
else {
	show_message_async("Reward not loaded!");
}