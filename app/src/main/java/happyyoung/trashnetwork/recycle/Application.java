package happyyoung.trashnetwork.recycle;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.baidu.mapapi.SDKInitializer;

import java.util.Random;

import happyyoung.trashnetwork.recycle.net.http.HttpApi;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-02-20
 */
public class Application extends com.activeandroid.app.Application {
    public static final String BUNDLE_KEY_USER_LOCATION_DATA = "UserLocationData";

    public static String ACTION_LOCATION;
    public static String ACTION_LOGIN;
    public static String ACTION_LOGOUT;

    public static int RANDOM_COLOR[];
    private static Random colorRandom;

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

        RANDOM_COLOR = new int[]{
                getResources().getColor(R.color.red_500),
                getResources().getColor(R.color.grey_500),
                getResources().getColor(R.color.green_500),
                getResources().getColor(R.color.teal_500),
                getResources().getColor(R.color.cyan_500),
                getResources().getColor(R.color.orange_500),
                getResources().getColor(R.color.light_blue_500),
                getResources().getColor(R.color.blue_500),
                getResources().getColor(R.color.light_green_500),
                getResources().getColor(R.color.amber_500),
                getResources().getColor(R.color.indigo_500),
                getResources().getColor(R.color.pink_500),
        };
        colorRandom = new Random(System.currentTimeMillis());
    }

    public static int getRandomColor(){
        return RANDOM_COLOR[colorRandom.nextInt(RANDOM_COLOR.length)];
    }

    public static int generateColorFromStr(String str){
        int sum = 0;
        if(str != null && !str.isEmpty()){
            for(int i = 0; i < str.length(); i++)
                sum += (int)str.charAt(i);
        }
        return RANDOM_COLOR[sum % RANDOM_COLOR.length];
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
