package happyyoung.trashnetwork.recycle.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Request;

import happyyoung.trashnetwork.recycle.Application;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.net.http.HttpApi;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonListener;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonRequest;
import happyyoung.trashnetwork.recycle.net.model.request.RecycleBottleRequest;
import happyyoung.trashnetwork.recycle.net.model.result.RecycleResult;
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

    public static void recycleBottle(final Context context, long trashId, int quantity){
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.error)
                .setPositiveButton(R.string.action_ok, null)
                .setCancelable(false)
                .create();
        if(GlobalInfo.currentLocation == null ||
                System.currentTimeMillis() - GlobalInfo.currentLocation.getUpdateTime().getTime() > 30 * 1000){
            alertDialog.setMessage(context.getString(R.string.alert_location_outdate));
            alertDialog.show();
            return;
        }
        RecycleBottleRequest request = new RecycleBottleRequest(trashId, quantity,
                GlobalInfo.currentLocation.getLongitude(), GlobalInfo.currentLocation.getLatitude());
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage(context.getString(R.string.alert_waiting));
        pd.setCancelable(false);
        pd.show();
        HttpApi.startRequest(new HttpApiJsonRequest(context, HttpApi.getApiUrl(HttpApi.CreditRecordApi.POST_RECORD_BY_BOTTLE_RECYCLE), Request.Method.POST, GlobalInfo.token, request,
                new HttpApiJsonListener<RecycleResult>(RecycleResult.class) {
                    @Override
                    public void onResponse(RecycleResult data) {
                        pd.dismiss();
                        updateUserInfo(context);
                        alertDialog.setTitle(R.string.action_done);
                        alertDialog.setMessage(String.format(context.getString(R.string.gain_credit_format), data.getCredit()));
                        alertDialog.show();
                    }

                    @Override
                    public boolean onErrorResponse(int statusCode, Result errorInfo) {
                        pd.dismiss();
                        alertDialog.setMessage(errorInfo.getMessage());
                        alertDialog.show();
                        return true;
                    }

                    @Override
                    public boolean onDataCorrupted(Throwable e) {
                        pd.dismiss();
                        return false;
                    }

                    @Override
                    public boolean onNetworkError(Throwable e) {
                        pd.dismiss();
                        return false;
                    }
                }));
    }
}
