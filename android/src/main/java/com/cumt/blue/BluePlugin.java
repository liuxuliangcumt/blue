package com.cumt.blue;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.os.Build;


import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.flutter.Log;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * BluePlugin
 */
public class BluePlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, EventChannel.StreamHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;
    private EventChannel eventChannel;
    Activity activity;


    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "blue");
        channel.setMethodCallHandler(this);
        eventChannel =new EventChannel(flutterPluginBinding.getBinaryMessenger(), "blue");
        eventChannel.setStreamHandler(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
         if (call.method.equals("getPlatformVersion")) {
            Log.d("aaa", "onMethodCall getPlatformVersion");

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


        } else if (call.method.equals("getBondedDevices")) {
            mBluetoothAdapter.getBondedDevices();
            List<String> lists = new ArrayList<>();

            //result.success(lists);
            // BluetoothDevice device =new BluetoothDevice();
            Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : devices) {
                BluetoothDevicBean bean = new BluetoothDevicBean();
                bean.setAddress(device.getAddress());
                bean.setName(device.getName());
                bean.setBondState(device.getBondState());
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", device.getName());
                    jsonObject.put("address", device.getAddress());
                    jsonObject.put("bondState", device.getBondState());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                lists.add(jsonObject.toString());
            }

            result.success(lists);
        } else {

            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);

    }

    BlueToothStateReceiver blueToothStateReceiver;

   /* @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(blueToothStateReceiver);

    }*/

    private void startService(final Result result) {

        //注册广播，蓝牙状态监听
        blueToothStateReceiver = new BlueToothStateReceiver();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        activity.registerReceiver(blueToothStateReceiver, filter);
        blueToothStateReceiver.setOnBlueToothStateListener(new BlueToothStateReceiver.OnBlueToothStateListener() {
            @Override
            public void onStateOff() {
                //do something

                channel.invokeMethod("blueOnOff",false);
                activity.unregisterReceiver(blueToothStateReceiver);


            }

            @Override
            public void onStateOn() {
                //do something
                channel.invokeMethod("blueOnOff",true);
                activity.unregisterReceiver(blueToothStateReceiver);
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


    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        this.activity = binding.getActivity();

    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

    }

    @Override
    public void onDetachedFromActivity() {
        if (blueToothStateReceiver != null) {
            activity.unregisterReceiver(blueToothStateReceiver);
            blueToothStateReceiver = null;
        }

    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {

    }

    @Override
    public void onCancel(Object arguments) {

    }
}
