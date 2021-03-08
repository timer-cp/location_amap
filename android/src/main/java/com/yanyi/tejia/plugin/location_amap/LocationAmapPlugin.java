package com.yanyi.tejia.plugin.location_amap;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.amap.api.location.AMapLocationClient;

import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * LocationAmapPlugin
 */
public class LocationAmapPlugin implements FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler {
    private static final String CHANNEL_METHOD_LOCATION = "location_amap_method";
    private static final String CHANNEL_STREAM_LOCATION = "location_amap_event";
    MethodChannel methodChannel;
    EventChannel eventChannel;

    private Context mContext = null;

    private EventChannel.EventSink mEventSink = null;

    private Map<String, LocationAmapClientImpl> locationClientMap = new HashMap<>(10);

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        switch (call.method) {
            case "setApiKey":
                setApiKey((Map) call.arguments);
                break;
            case "setLocationOption":
                setLocationOption((Map) call.arguments);
                break;
            case "startLocation":
                startLocation((Map) call.arguments);
                break;
            case "stopLocation":
                stopLocation((Map) call.arguments);
                break;
            case "destroy":
                destroy((Map) call.arguments);
                break;
            default:
                result.notImplemented();
                break;

        }
    }

    @Override
    public void onListen(Object o, EventChannel.EventSink eventSink) {
        mEventSink = eventSink;
    }

    @Override
    public void onCancel(Object o) {
        for (Map.Entry<String, LocationAmapClientImpl> entry : locationClientMap.entrySet()) {
            entry.getValue().stopLocation();
        }
        mEventSink = null;
    }

    /**
     * 开始定位
     */
    private void startLocation(Map argsMap) {
        if (null == locationClientMap) {
            locationClientMap = new HashMap<>(10);
        }

        String pluginKey = getPluginKeyFromArgs(argsMap);
        if (TextUtils.isEmpty(pluginKey)) {
            return;
        }

        LocationAmapClientImpl locationClientImp;
        if (!locationClientMap.containsKey(pluginKey)) {
            locationClientImp = new LocationAmapClientImpl(mContext, pluginKey, mEventSink);
            locationClientMap.put(pluginKey, locationClientImp);
        } else {
            locationClientImp = getLocationClientImp(argsMap);
        }

        if (null != locationClientImp) {
            locationClientImp.startLocation();
        }
    }


    /**
     * 停止定位
     */
    private void stopLocation(Map argsMap) {
        LocationAmapClientImpl locationClientImp = getLocationClientImp(argsMap);
        if (null != locationClientImp) {
            locationClientImp.stopLocation();
        }
    }

    /**
     * 销毁
     *
     * @param argsMap
     */
    private void destroy(Map argsMap) {
        LocationAmapClientImpl locationClientImp = getLocationClientImp(argsMap);
        if (null != locationClientImp) {
            locationClientImp.destroy();
        }
    }

    /**
     * 设置apikey
     *
     * @param apiKeyMap
     */
    private void setApiKey(Map apiKeyMap) {
        if (null != apiKeyMap) {
            if (apiKeyMap.containsKey("android")
                    && !TextUtils.isEmpty((String) apiKeyMap.get("android"))) {
                AMapLocationClient.setApiKey((String) apiKeyMap.get("android"));
            }
        }
    }

    /**
     * 设置定位参数
     *
     * @param argsMap
     */
    private void setLocationOption(Map argsMap) {
        LocationAmapClientImpl locationClientImp = getLocationClientImp(argsMap);
        if (null != locationClientImp) {
            locationClientImp.setLocationOption(argsMap);
        }
    }


    @Override
    public void onAttachedToEngine(FlutterPluginBinding binding) {
        if (null == mContext) {
            mContext = binding.getApplicationContext();

            /**
             * 方法调用通道
             */
            methodChannel = new MethodChannel(binding.getBinaryMessenger(), CHANNEL_METHOD_LOCATION);
            methodChannel.setMethodCallHandler(this);

            /**
             * 回调监听通道
             */
            eventChannel = new EventChannel(binding.getBinaryMessenger(), CHANNEL_STREAM_LOCATION);
            eventChannel.setStreamHandler(this);
        }

    }

    public static void registerWith(Registrar registrar) {
        final MethodChannel methodChannel = new MethodChannel(registrar.messenger(), CHANNEL_METHOD_LOCATION);
        methodChannel.setMethodCallHandler(new LocationAmapPlugin());

        final EventChannel eventChannel = new EventChannel(registrar.messenger(), CHANNEL_STREAM_LOCATION);
        eventChannel.setStreamHandler(new LocationAmapPlugin());
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
        for (Map.Entry<String, LocationAmapClientImpl> entry : locationClientMap.entrySet()) {
            entry.getValue().destroy();
        }
        methodChannel.setMethodCallHandler(null);
        eventChannel.setStreamHandler(null);
    }

    private LocationAmapClientImpl getLocationClientImp(Map argsMap) {
        if (null == locationClientMap || locationClientMap.size() <= 0) {
            return null;
        }
        String pluginKey = null;
        if (null != argsMap) {
            pluginKey = (String) argsMap.get("pluginKey");
        }
        if (TextUtils.isEmpty(pluginKey)) {
            return null;
        }

        return locationClientMap.get(pluginKey);
    }

    private String getPluginKeyFromArgs(Map argsMap) {
        String pluginKey = null;
        try {
            if (null != argsMap) {
                pluginKey = (String) argsMap.get("pluginKey");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return pluginKey;
    }
}
