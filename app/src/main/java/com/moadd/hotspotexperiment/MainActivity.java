package com.moadd.hotspotexperiment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.moadd.hotspotexperiment.api.WifiStatus;
import com.moadd.hotspotexperiment.api.showHotspotsList;
import com.moadd.hotspotexperiment.api.wifiHotSpots;
import com.moadd.hotspotexperiment.datatransfer.WifiSocket;

import java.lang.reflect.Method;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    BroadcastReceiver receiver;
    wifiHotSpots hotutil;
    WifiStatus wifiStatus;
    Button invite,join,hotspotList,sendData;
    private static int result_lavel=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//Permissions for switching on/off wifi access :
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
                // Do stuff here
            }
            else {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        hotutil=new wifiHotSpots(getApplicationContext());
        invite = findViewById(R.id.Invite);
        join   = findViewById(R.id.Join);
        hotspotList=findViewById(R.id.listofhotspots);
        sendData=findViewById(R.id.sendData);
        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WifiSocket ws=new WifiSocket(MainActivity.this);
                ws.sendMessage("192.168.0.104",5000,"Ye lo");
            }
        });
        hotspotList.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
//List of available hotspots displayed here
              /* Intent in=new Intent(MainActivity.this,showHotspotsList.class);
                startActivity(in);*/
                hotutil.setHotSpot("UmarFarooq","jummanbiryani");
                hotutil.startHotSpot(true);
               //hotutil.addWifiNetwork("umar","farooq","WEP");

     /*  MyHotSpotFunctions m= new MyHotSpotFunctions();
       MyHotSpotFunctions.setHotspotName("Umar",MainActivity.this);
                m.turnOnHotspot(MainActivity.this);*/
                }
        });
        invite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
				/*if(hotutil.setHotSpot("SSID",""))
				{
					hotutil.startHotSpot(true);
				}
				*/
                inviteFriend(hotutil);
            }
        });
    join.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            connectToWifi();
        }
    });}
    public void inviteFriend(wifiHotSpots hotutil)
    {
		/*
		if(hotutil.setHotSpot("SSID",""))
		{
			hotutil.startHotSpot(true);
		}
		*/
        hotutil.setAndStartHotSpot(true, "Umrao");
    }
    public void joinFriend(final WifiStatus wifiStatus, final wifiHotSpots hotutil) {
        if (wifiStatus.checkWifi(wifiStatus.IS_WIFI_ON)) {
            hotutil.scanNetworks();
            List<ScanResult> results = hotutil.getHotspotsList();
            for (ScanResult result : results) {
                //Toast.makeText(getApplicationContext(), result.SSID + " " + result.level,
                //        Toast.LENGTH_SHORT).show();
                if (result.SSID.equalsIgnoreCase("SSID")) {

                    Toast.makeText(getApplicationContext(), result.SSID + " Found SSID" + result.level,
                            Toast.LENGTH_SHORT).show();
                    hotutil.connectToHotspot("SSID", "");
                    try {
                        unregisterReceiver(receiver);
                        break;
                    } catch (Exception e) {
                        //error as trying to do unregistering twice?
                    }
                    //hotutil.stopScan();
                }
            }

        } else {
            if (hotutil.isWifiApEnabled())
                hotutil.startHotSpot(false);
            //start wifi.
            wifiStatus.checkWifi(wifiStatus.WIFI_ON);

            receiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    // TODO Auto-generated method stub
                    final String action = intent.getAction();
                    if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                        List<ScanResult> results = hotutil.getHotspotsList();
                        for (ScanResult result : results) {
                            //Toast.makeText(getApplicationContext(), result.SSID + " " + result.level,
                            //        Toast.LENGTH_SHORT).show();
                            if (result.SSID.equalsIgnoreCase("SSID")) {
                                Toast.makeText(getApplicationContext(), "Found SSID", Toast.LENGTH_SHORT).show();
                                if (!hotutil.isConnectToHotSpotRunning)
                                    hotutil.connectToHotspot("SSID", "");
                                try {
                                    unregisterReceiver(receiver);
                                    break;
                                } catch (Exception e) {
                                    //trying to unregister twice? need vary careful about this.
                                }

                            }
                        }
                    }
                }

            };
            IntentFilter mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            registerReceiver(receiver, mIntentFilter);
        }
    }
    public void set(String ssid,String password)
    {
        WifiManager wm = (WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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
    public void connectToWifi(){
        try{
            WifiManager wifiManager = (WifiManager) super.getApplicationContext().getSystemService(android.content.Context.WIFI_SERVICE);
            WifiConfiguration wc = new WifiConfiguration();
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            wc.SSID = "333";
            //wc.preSharedKey = "\"PASSWORD\"";
            wc.status = WifiConfiguration.Status.ENABLED;
            wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wifiManager.setWifiEnabled(true);
            int netId = wifiManager.addNetwork(wc);
           /* if (netId == -1) {
                netId = getExistingNetworkId(SSID);
            }*/
          /*  wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   /* private int getExistingNetworkId(String SSID) {
        WifiManager wifiManager = (WifiManager) super.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration existingConfig : configuredNetworks) {
                if (existingConfig.SSID.equals(SSID)) {
                    return existingConfig.networkId;
                }
            }
        }
        return -1;
    }*/
   /* public  void newSet(String ssid,String password)
    {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = "SomeName";
        wifiConfiguration.preSharedKey = "SomeKey";
        wifiConfiguration.hiddenSSID = false;
        wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wifiConfiguration.allowedKeyManagement.set(4);
        wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        WifiApControl apControl = WifiApControl.getInstance(context);

        apControl.setEnabled(wifiConfiguration, true);
    }*/
}
