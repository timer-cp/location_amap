# amaplocation

高德地图Flutter插件包，使用Java和OC开发.

## Getting Started

* 1 设置各平台定位Key
```
Amaplocation.setApiKey("xxxx1", "xxx2");
```
* 2 配置相关权限
[Android参考](https://lbs.amap.com/api/android-location-sdk/gettingstarted)
[iOS参考](https://lbs.amap.com/api/ios-location-sdk/gettingstarted)

* 3 使用
```
Amaplocation amaplocation = Amaplocation();
// 定位参数：是否连续定位
AMapLocationOption option = AMapLocationOption(onceLocation: false);
// 监听定位回调
amaplocation.onLocationChanged().listen((event) {
   debugPrint('$event');
});
// 启动定位
amaplocation.startLocation();
// 设置定位参数
amaplocation.setLocationOption(option);
```