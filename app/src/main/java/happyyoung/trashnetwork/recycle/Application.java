package happyyoung.trashnetwork.recycle;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.baidu.mapapi.SDKInitializer;

import happyyoung.trashnetwork.recycle.net.http.HttpApi;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-02-20
 */
public class Application extends com.activeandroid.app.Application {
    public static final String BUNDLE_KEY_USER_LOCATION_DATA = "UserLocationData";

    public static String ACTION_LOCATION;
    public static String ACTION_LOGIN;
    public static String ACTION_LOGOUT;

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            HttpApi.BASE_URL_V1 = appInfo.metaData.getString("TN_HTTP_API_BASE_URL_V1");
        }catch (Exception e){
            e.printStackTrace();
        }

        ACTION_LOCATION = getPackageName() + ".action.LOCATION";
        ACTION_LOGIN = getPackageName() + ".action.LOGIN";
        ACTION_LOGOUT = getPackageName() + ".action.LOGOUT";
    }

    public static void checkPermission(Activity activity, String permission){
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return;
            }
            ActivityCompat.requestPermissions(activity, new String[]{permission}, 0);
        }
    }
}
