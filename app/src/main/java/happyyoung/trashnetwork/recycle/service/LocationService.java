package happyyoung.trashnetwork.recycle.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.util.Date;

import happyyoung.trashnetwork.recycle.Application;
import happyyoung.trashnetwork.recycle.model.UserLocation;
import happyyoung.trashnetwork.recycle.util.GlobalInfo;
import happyyoung.trashnetwork.recycle.util.GsonUtil;

public class LocationService extends Service implements AMapLocationListener {
    private final String TAG = "LocationService";
    private final int LOCATE_INTERVAL = 5000;

    private AMapLocationClient locationClient = null;
    private long lastLocTime = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        locationClient = new AMapLocationClient(getApplicationContext());
        AMapLocationClientOption opt = new AMapLocationClientOption()
                .setInterval(LOCATE_INTERVAL)
                .setKillProcess(true)
                .setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy)
                .setNeedAddress(true);
        opt.setWifiActiveScan(true);
        opt.setMockEnable(false);
        opt.setHttpTimeOut(15000);
        opt.setLocationCacheEnable(true);
        locationClient.setLocationOption(opt);
        locationClient.setLocationListener(this);
        locationClient.startLocation();
    }


    @Override
    public void onDestroy() {
        locationClient.stopLocation();
        locationClient.onDestroy();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(aMapLocation == null)
            return;
        if (aMapLocation.getErrorCode() == 0) {
            if(aMapLocation.getTime() < lastLocTime)
                return;
            lastLocTime = aMapLocation.getTime();
            UserLocation newLoc = new UserLocation(aMapLocation.getLongitude(),
                    aMapLocation.getLatitude(), new Date(),
                    aMapLocation.getAddress());
            sendLocation(newLoc);
        }else {
            Log.e(TAG, "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:"
                    + aMapLocation.getErrorInfo());
        }
    }

    private void sendLocation(UserLocation newLocation) {
        GlobalInfo.currentLocation = newLocation;
        Intent intent = new Intent(Application.ACTION_LOCATION);
        intent.addCategory(getPackageName());
        intent.putExtra(Application.BUNDLE_KEY_USER_LOCATION_DATA, GsonUtil.getGson().toJson(newLocation));
        sendBroadcast(intent);
    }
}
