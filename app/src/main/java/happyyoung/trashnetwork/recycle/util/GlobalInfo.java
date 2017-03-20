package happyyoung.trashnetwork.recycle.util;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import happyyoung.trashnetwork.recycle.model.Trash;
import happyyoung.trashnetwork.recycle.model.User;
import happyyoung.trashnetwork.recycle.model.UserLocation;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-03-20
 */
public class GlobalInfo {
    public static String token;
    public static User user;
    public static UserLocation currentLocation;
    public static List<Trash> trashList = new ArrayList<>();

    public static void logout(Context context){
        token = null;
        user = null;
        currentLocation = null;
    }

    public static Trash findTrashById(long trashId){
        for(Trash t : trashList){
            if(t.getTrashId().equals(trashId))
                return t;
        }
        return null;
    }
}
