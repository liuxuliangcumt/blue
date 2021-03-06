import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:blue/blue.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String _blueUsable, _blueName, _blueSwitchState;
  bool _blueOpenState = false;

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion, blueName, blueSwitchState;
    bool blueUsable, blueOpenState;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await Blue.platformVersion;
      blueUsable = await Blue.blueUsable;
      blueName = await Blue.blueName;
      blueSwitchState = await Blue.blueSwitchState;
      blueOpenState = await Blue.getBlueIsEnabled;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }
    /*  if(Blue.getMethodCall().method=="blueOnOff"){
      setState(() {
        _blueOpenState=Blue.getMethodCall().arguments;

      });
    }*/

    Blue.getMethodChannel.setMethodCallHandler((call) {
      print("接收到Android 消息");
      if (call.method == "blueOnOff") {
        setState(() {
          _blueOpenState = call.arguments;
        });
      }
      return;
    });

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
      _blueUsable = blueUsable.toString();
      _blueName = blueName;
      _blueSwitchState = blueSwitchState;
      _blueOpenState = blueOpenState;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              CustomPaint(painter: CustomPainter(),),
              Text('Running on: $_platformVersion\n'),
              Text('蓝牙可用否 : $_blueUsable\n'),
              Text('蓝牙名字 : $_blueName\n'),
              Text('蓝牙开关状态 : $_blueSwitchState\n'),
              Switch(
                value: _blueOpenState,
                onChanged: (v) {
                  openBlue(v);
                },
              ),
              MaterialButton(
                color: Colors.blue,
                onPressed: () {
                  getDevices();
                },
                child: Text("获取设备列表"),
              )
            ],
          ),
        ),
      ),
    );
  }

  void openBlue(bool onOff) async {
    await Blue.blueOnOff(onOff);
    bool blueOpenState = await Blue.getBlueIsEnabled;
    print("蓝牙开关回调： $blueOpenState");
    setState(() {
      _blueOpenState = blueOpenState;
    });
  }

  void getDevices() async {
    List<String> devices = await Blue.getBondedDevices;
    print("蓝牙设备数量");
    print(devices?.length);
    for (int i = 0; i < devices.length; i++) {
      print(devices[i]);
    }
  }
}
