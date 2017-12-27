package com.moadd.hotspotexperiment;

import android.content.ContentValues;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.lang.reflect.Method;

import static android.content.ContentValues.TAG;

/**
 * Created by moadd on 30-Oct-17.
 */

public class MyHotSpotFunctions {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void turnOnHotspot(Context c){
        WifiManager manager = (WifiManager)c.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        manager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback(){

            @Override
            public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                super.onStarted(reservation);
                Log.d(TAG, "Wifi Hotspot is on now");
            }

            @Override
            public void onStopped() {
                super.onStopped();
                Log.d(TAG, "onStopped: ");
            }

            @Override
            public void onFailed(int reason) {
                super.onFailed(reason);
                Log.d(TAG, "onFailed: ");
            }
        },new Handler());
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void turnOffHotspot(Context c)
    {
        WifiManager manager = (WifiManager)c.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

    }
    public static boolean setHotspotName(String newName, Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);

            wifiConfig.SSID = newName;

            Method setConfigMethod = wifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
            setConfigMethod.invoke(wifiManager, wifiConfig);

            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public void set(Context c,String ssid,String password)
    {
        WifiManager wm = (WifiManager)c.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wifiCon = new WifiConfiguration();
        wifiCon.SSID = ssid;
        wifiCon.preSharedKey = password;
        wifiCon.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        wifiCon.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wifiCon.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wifiCon.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        try
        {
            Method setWifiApMethod = wm.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            boolean apstatus=(Boolean) setWifiApMethod.invoke(wm, wifiCon,true);
        }
        catch (Exception e)
        {
            Log.e(this.getClass().toString(), "", e);
        }
    }
}
