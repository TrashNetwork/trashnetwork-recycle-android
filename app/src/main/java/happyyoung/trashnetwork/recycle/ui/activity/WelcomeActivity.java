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
import happyyoung.trashnetwork.recycle.util.DatabaseUtil;
import happyyoung.trashnetwork.recycle.util.GlobalInfo;

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
                    enterMainActivity(false);
                    return;
                }
                LoginUserRecord lur = records.get(0);
                if(lur.getToken() == null || lur.getToken().isEmpty())
                    enterMainActivity(false);
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
                    public void onResponse(Result data) {
                        GlobalInfo.token = lur.getToken();
                        GlobalInfo.user = new User(lur.getUserId(), lur.getUserName());
                        DatabaseUtil.updateLoginRecord(GlobalInfo.user, GlobalInfo.token);
                        enterMainActivity(true);
                    }

                    @Override
                    public boolean onErrorResponse(int statusCode, Result errorInfo) {
                        if(errorInfo.getResultCode() == 401){
                            Toast.makeText(WelcomeActivity.this, errorInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            enterMainActivity(false);
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public boolean onDataCorrupted(Throwable e) {
                        enterMainActivity(false);
                        return false;
                    }

                    @Override
                    public boolean onNetworkError(Throwable e) {
                        enterMainActivity(false);
                        return false;
                    }
                }));
    }

    private void enterMainActivity(boolean login){
        Intent intent = new Intent(this, MainActivity.class);
        if(login)
            intent.putExtra(MainActivity.BUNDLE_KEY_UPDATE_USER_INFO, true);
        startActivity(intent);
        finish();
    }
}
