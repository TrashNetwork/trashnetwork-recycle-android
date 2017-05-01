package happyyoung.trashnetwork.recycle.util;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import happyyoung.trashnetwork.recycle.model.RecyclePoint;
import happyyoung.trashnetwork.recycle.model.User;
import happyyoung.trashnetwork.recycle.model.UserLocation;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-03-20
 */
public class GlobalInfo {
    public static String token;
    public static User user;
    public static UserLocation currentLocation;
    public static List<RecyclePoint> recyclePointList = new ArrayList<>();

    public static void logout(Context context){
        token = null;
        user = null;
        currentLocation = null;
    }

    public static RecyclePoint findRecyclePointById(long recyclePointId){
        for(RecyclePoint t : recyclePointList){
            if(t.getRecyclePointId().equals(recyclePointId))
                return t;
        }
        return null;
    }
}
