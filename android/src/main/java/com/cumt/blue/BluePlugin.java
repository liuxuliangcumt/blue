package com.cumt.blue;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.Log;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * BluePlugin
 */
public class BluePlugin extends FlutterActivity implements FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("aaa","c创建了");

    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "blue");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (call.method.equals("getPlatformVersion")) {
            Log.d("aaa","onMethodCall getPlatformVersion");

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
                    mBluetoothAdapter.enable();
                } else {
                    mBluetoothAdapter.disable();
                }
                //  result.success(mBluetoothAdapter.enable());
                //todo 开启蓝牙开关监听
                startService(result);


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

    BlueToothStateReceiver blueToothStateReceiver;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(blueToothStateReceiver);

    }

    private void startService(final Result result) {

        //注册广播，蓝牙状态监听
        blueToothStateReceiver = new BlueToothStateReceiver();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(blueToothStateReceiver, filter);
        blueToothStateReceiver.setOnBlueToothStateListener(new BlueToothStateReceiver.OnBlueToothStateListener() {
            @Override
            public void onStateOff() {
                //do something
                result.success(false);
            }

            @Override
            public void onStateOn() {
                //do something
                result.success(true);

            }

            @Override
            public void onStateTurningOn() {
                //do something
            }

            @Override
            public void onStateTurningOff() {
                //do something
            }
        });

    }


}
