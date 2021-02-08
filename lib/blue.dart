import 'dart:convert';

import 'package:flutter/services.dart';

class Blue {
  static const MethodChannel _channel = const MethodChannel('blue');

  static MethodCall _call;

  static void setCall() {
    if (_call == null) {
      _channel.setMethodCallHandler((call) {
        _call = call;
        return call.arguments;
      });
    }
  }

  static MethodCall getMethodCall() {
    setCall();
    return _call;
  }

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

//蓝牙可用否
  static Future<bool> get blueUsable async {
    final bool version = await _channel.invokeMethod('getBlueUsable');

    return version;
  }

  //  getBlueName

  static Future<String> get blueName async {
    final String blueName = await _channel.invokeMethod('getBlueName');
    return blueName;
  }

  // 蓝牙开关状态
  static Future<String> get blueSwitchState async {
    final String blueSwitchState =
        await _channel.invokeMethod('getBlueSwitchState');
    return blueSwitchState;
  }

  // 获取蓝牙打开状态
  static Future<bool> get getBlueIsEnabled async {
    final bool isEnabled = await _channel.invokeMethod('getBlueIsEnabled');
    return isEnabled;
  }

// 蓝牙连接状态
  static Future<String> get blueLinkState async {
    final String blueName = await _channel.invokeMethod('getBlueName');
    return blueName;
  }

  // 打开蓝牙
  static Future<bool> blueOnOff(bool onOff) async {
    final bool isOpen = await _channel.invokeMethod('blueOnOff', onOff);
    return isOpen;
  }

  static Future<List<String>> get getBondedDevices async {
    final List<dynamic> devices =
        await _channel.invokeMethod('getBondedDevices');

    print("蓝牙设备数量 ${devices.length}");
    // print("蓝牙设备数量 ${}");

    // return new List();
    return devices.cast<String>();
  }
//static Future<List<String>> getAvaillList async{

//}
}
