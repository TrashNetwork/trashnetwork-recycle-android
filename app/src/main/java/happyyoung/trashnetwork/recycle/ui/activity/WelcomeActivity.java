package happyyoung.trashnetwork.recycle.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.android.volley.Request;

import java.util.List;

import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.database.model.LoginUserRecord;
import happyyoung.trashnetwork.recycle.model.User;
import happyyoung.trashnetwork.recycle.net.http.HttpApi;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonListener;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonRequest;
import happyyoung.trashnetwork.recycle.net.model.result.Result;
import happyyoung.trashnetwork.recycle.net.model.result.UserResult;
import happyyoung.trashnetwork.recycle.util.DatabaseUtil;
import happyyoung.trashnetwork.recycle.util.GlobalInfo;
import happyyoung.trashnetwork.recycle.util.HttpUtil;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-02-12
 */
public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        final List<LoginUserRecord> records = DatabaseUtil.findAllLoginUserRecords(1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(records.isEmpty()) {
                    enterMainActivity();
                    return;
                }
                LoginUserRecord lur = records.get(0);
                if(lur.getToken() == null || lur.getToken().isEmpty())
                    enterMainActivity();
                else
                    quickLogin(lur);
            }
        }, 1000);
    }

    private void quickLogin(final LoginUserRecord lur){
        String url = HttpApi.getApiUrl(HttpApi.AccountApi.CHECK_LOGIN, "" + lur.getUserId());
        HttpApi.startRequest(new HttpApiJsonRequest(getApplicationContext(), url, Request.Method.GET, lur.getToken(), null,
                new HttpApiJsonListener<Result>(Result.class) {
                    @Override
                    public void onDataResponse(Result data) {
                        GlobalInfo.token = lur.getToken();
                        HttpUtil.updateUserInfo(WelcomeActivity.this, new HttpApiJsonListener<UserResult>(UserResult.class) {
                            @Override
                            public void onDataResponse(UserResult data) {
                                GlobalInfo.user = data.getUser();
                                GlobalInfo.user.setUserName(lur.getUserName());
                                DatabaseUtil.updateLoginRecord(GlobalInfo.user, GlobalInfo.token);
                                enterMainActivity();
                            }

                            @Override
                            public void onErrorResponse() {
                                enterMainActivity();
                            }

                            @Override
                            public boolean onErrorDataResponse(int statusCode, Result errorInfo) {
                                Toast.makeText(WelcomeActivity.this, errorInfo.getMessage(), Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        });
                    }

                    @Override
                    public void onErrorResponse() {
                        enterMainActivity();
                    }

                    @Override
                    public boolean onErrorDataResponse(int statusCode, Result errorInfo) {
                        Toast.makeText(WelcomeActivity.this, errorInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }));
    }

    private void enterMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
