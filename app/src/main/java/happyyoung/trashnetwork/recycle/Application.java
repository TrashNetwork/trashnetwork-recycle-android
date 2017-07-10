package happyyoung.trashnetwork.recycle;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Random;

import happyyoung.trashnetwork.recycle.net.http.HttpApi;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-02-20
 */
public class Application extends com.activeandroid.app.Application {
    public static final String BUNDLE_KEY_USER_LOCATION_DATA = "UserLocationData";
    public static String WEBPAGE_BASE_URL;

    public static String ACTION_LOCATION;
    public static String ACTION_LOGIN;
    public static String ACTION_LOGOUT;
    public static String ACTION_USER_UPDATE;

    public static int RANDOM_COLOR[];
    private static Random colorRandom;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            HttpApi.BASE_URL_V1 = appInfo.metaData.getString("TN_HTTP_API_BASE_URL_V1");
            WEBPAGE_BASE_URL = appInfo.metaData.getString("TN_WEBPAGE_BASE_URL");
        }catch (Exception e){
            e.printStackTrace();
        }

        ACTION_LOCATION = getPackageName() + ".action.LOCATION";
        ACTION_LOGIN = getPackageName() + ".action.LOGIN";
        ACTION_LOGOUT = getPackageName() + ".action.LOGOUT";
        ACTION_USER_UPDATE = getPackageName() + ".action.USER_UPDATE";

        RANDOM_COLOR = new int[]{
                ContextCompat.getColor(this, R.color.red_500),
                ContextCompat.getColor(this, R.color.grey_500),
                ContextCompat.getColor(this, R.color.green_500),
                ContextCompat.getColor(this, R.color.teal_500),
                ContextCompat.getColor(this, R.color.cyan_500),
                ContextCompat.getColor(this, R.color.orange_500),
                ContextCompat.getColor(this, R.color.light_blue_500),
                ContextCompat.getColor(this, R.color.blue_500),
                ContextCompat.getColor(this, R.color.light_green_500),
                ContextCompat.getColor(this, R.color.amber_500),
                ContextCompat.getColor(this, R.color.indigo_500),
                ContextCompat.getColor(this, R.color.pink_500),
                ContextCompat.getColor(this, R.color.purple_500),
                ContextCompat.getColor(this, R.color.deep_purple_500),
                ContextCompat.getColor(this, R.color.blue_grey_500),
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

    public static void checkPermission(Activity activity, String[] permissions){
        ArrayList<String> permCheckList = new ArrayList<>();
        for(String perm : permissions){
            if (ContextCompat.checkSelfPermission(activity, perm) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, perm)) {
                    continue;
                }
                permCheckList.add(perm);
            }
        }
        if(permCheckList.isEmpty())
            return;
        ActivityCompat.requestPermissions(activity, permCheckList.toArray(new String[permCheckList.size()]), 0);
    }
}
