/// @description Interstitial
event_inherited();
//
if (HuaweiAds_IsInterstitialLoaded()) {
	HuaweiAds_ShowInterstitial();
	show_message_async("Interstitial was shown.");
}
else {
	show_message_async("Interstitial not loaded!");
}