package com.cumt.blue;

import android.bluetooth.BluetoothAdapter;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * BluePlugin
 */
public class BluePlugin implements FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "blue");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (call.method.equals("getPlatformVersion")) {

            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("getBlueUsable")) {
            if (mBluetoothAdapter == null) {
                // 说明此设备不支持蓝牙操作
                result.success(false);
            } else {
                result.success(true);
            }
        } else if (call.method.equals("getBlueName")) {
            if (mBluetoothAdapter != null) {
                result.success(mBluetoothAdapter.getName());
            } else {
                result.success("不支持蓝牙");
            }
        } else if (call.method.equals("getBlueSwitchState")) {
            // 获取蓝牙开关状态
            if (mBluetoothAdapter != null) {
                result.success(mBluetoothAdapter.getState() + "");
            } else {
                result.success("不支持蓝牙");
            }
        } else if (call.method.equals("getBlueIsEnabled")) {
            // 获取蓝牙开关状态
            if (mBluetoothAdapter != null) {
                result.success(mBluetoothAdapter.isEnabled());
            } else {
                result.success("不支持蓝牙");
            }
        } else if (call.method.equals("blueOnOff")) {
            // 获取蓝牙开关状态
            if (mBluetoothAdapter != null) {
                boolean onOff = call.arguments();
                if (onOff) {
                    result.success(mBluetoothAdapter.enable());
                } else {
                    result.success(mBluetoothAdapter.disable());
                }
                //  result.success(mBluetoothAdapter.enable());
            } else {
                result.success("不支持蓝牙");
            }


        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }
}
