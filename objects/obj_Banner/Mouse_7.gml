/// @description Banner
event_inherited();
//
if (!is_shown) {
	HuaweiAds_ShowBanner();
	show_message_async("Banner was shown.");
}
else {
	HuaweiAds_HideBanner();
	show_message_async("Banner was hidden.");
}
is_shown = !is_shown;