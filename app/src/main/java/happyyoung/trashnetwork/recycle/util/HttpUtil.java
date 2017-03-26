package happyyoung.trashnetwork.recycle.util;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Request;

import happyyoung.trashnetwork.recycle.Application;
import happyyoung.trashnetwork.recycle.net.http.HttpApi;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonListener;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonRequest;
import happyyoung.trashnetwork.recycle.net.model.result.Result;
import happyyoung.trashnetwork.recycle.net.model.result.UserResult;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-03-26
 */
public class HttpUtil {
    public static void updateUserInfo(final Context context){
        HttpApi.startRequest(new HttpApiJsonRequest(context, HttpApi.getApiUrl(HttpApi.AccountApi.SELF_INFO), Request.Method.GET, GlobalInfo.token,
                null, new HttpApiJsonListener<UserResult>(UserResult.class) {
            @Override
            public void onResponse(UserResult data) {
                String userName = GlobalInfo.user.getUserName();
                GlobalInfo.user = data.getUser();
                GlobalInfo.user.setUserName(userName);
                Intent intent = new Intent(Application.ACTION_USER_UPDATE);
                intent.addCategory(context.getPackageName());
                context.sendBroadcast(intent);
            }
        }));
    }
}
