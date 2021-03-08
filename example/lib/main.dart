import 'package:flutter/material.dart';
import 'dart:async';

import 'package:location_amap/location_amap.dart';
import 'package:location_amap/location_amap_option.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  var location = "unknown";

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    LocationAMap.setApiKey("48e2962929d650b3ac8fba72ccf4f437", "3ae7345c2fa1f24c937576c4ea196f55");
    if (!mounted) return;
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('高德定位插件示例'),
        ),
        body: Center(
          child: Column(
            children: [
              TextButton(
                child: Text('获取定位'),
                onPressed: () {
                  LocationAMap amaplocation = LocationAMap();
                  // 定位参数：是否连续定位
                  LocationAMapOption option = LocationAMapOption(onceLocation: true);
                  // 监听定位回调
                  amaplocation.onLocationChanged().listen((event) {
                    debugPrint('$event');
                    setState(() {
                      location = '${event['longitude']} : ${event['latitude']}';
                    });
                  });
                  // 设置定位参数
                  amaplocation.setLocationOption(option);
                  // 启动定位
                  amaplocation.startLocation();
                },
              ),
              Text(location),
            ],
          ),
        ),
      ),
    );
  }
}
