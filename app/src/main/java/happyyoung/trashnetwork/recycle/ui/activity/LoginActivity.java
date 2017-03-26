package happyyoung.trashnetwork.recycle.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import happyyoung.trashnetwork.recycle.Application;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.database.model.LoginUserRecord;
import happyyoung.trashnetwork.recycle.net.PublicResultCode;
import happyyoung.trashnetwork.recycle.net.http.HttpApi;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonListener;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonRequest;
import happyyoung.trashnetwork.recycle.net.model.request.LoginRequest;
import happyyoung.trashnetwork.recycle.net.model.request.RegisterRequest;
import happyyoung.trashnetwork.recycle.net.model.result.LoginResult;
import happyyoung.trashnetwork.recycle.net.model.result.Result;
import happyyoung.trashnetwork.recycle.util.DatabaseUtil;
import happyyoung.trashnetwork.recycle.util.GlobalInfo;

public class LoginActivity extends AppCompatActivity {
    private static final int STATUS_SIGNIN = 1;
    private static final int STATUS_SIGNUP = 2;

    @BindString(R.string.error_field_required) String STR_ERROR_FIELD_REQUIRED;

    @BindView(R.id.btn_sign_in) Button btnSignIn;
    @BindView(R.id.btn_sign_up) Button btnSignUp;
    @BindView(R.id.email_view_area) View emailView;
    @BindView(R.id.edit_user_name) AutoCompleteTextView editUserName;
    @BindView(R.id.edit_password) EditText editPassword;
    @BindView(R.id.edit_email) EditText editEmail;
    @BindView(R.id.progress_signin) ProgressBar progressBar;

    private int status = STATUS_SIGNIN;
    private List<LoginUserRecord> recordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        setSupportActionBar((Toolbar) ButterKnife.findById(this, R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recordList = DatabaseUtil.findAllLoginUserRecords(10);
        List<String> loginIdRecords = new ArrayList<>();
        for(LoginUserRecord lur : recordList)
            loginIdRecords.add(lur.getUserName());
        editUserName.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, loginIdRecords));
    }

    @OnClick(R.id.btn_sign_up)
    void onBtnSignUpClick(View v){
        switch (status){
            case STATUS_SIGNIN:
                status = STATUS_SIGNUP;
                emailView.setVisibility(View.VISIBLE);
                btnSignIn.setText(R.string.action_sign_up);
                btnSignUp.setText(R.string.action_sign_in);
                break;
            case STATUS_SIGNUP:
                status = STATUS_SIGNIN;
                emailView.setVisibility(View.GONE);
                btnSignIn.setText(R.string.action_sign_in);
                btnSignUp.setText(R.string.action_sign_up);
                break;
        }
    }

    @OnClick(R.id.btn_sign_in)
    void onBtnSignInClick(){
        performAction();
    }

    private void performAction(){
        String userName = editUserName.getText().toString();
        String password = editPassword.getText().toString();
        String email = editEmail.getText().toString();

        if(userName.isEmpty()){
            editUserName.setError(STR_ERROR_FIELD_REQUIRED);
            return;
        }else if(password.isEmpty()){
            editPassword.setError(STR_ERROR_FIELD_REQUIRED);
            return;
        }else if(password.length() < 6 || password.length() > 20){
            editPassword.setError(getString(R.string.error_illegal_password_length));
            return;
        }
        if(status == STATUS_SIGNUP){
            if(email.isEmpty()) {
                editEmail.setError(STR_ERROR_FIELD_REQUIRED);
                return;
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editEmail.setError(getString(R.string.error_illegal_email));
                return;
            }
        }
        switch (status){
            case STATUS_SIGNIN:
                signIn(userName, password);
                break;
            case STATUS_SIGNUP:
                signUp(userName, password, email);
                break;
        }
    }

    private void signIn(final String userName, String password){
        LoginRequest request = new LoginRequest(userName, password);
        onLoginStart();
        HttpApi.startRequest(new HttpApiJsonRequest(this, HttpApi.getApiUrl(HttpApi.AccountApi.LOGIN), Request.Method.PUT, null,
                request, new HttpApiJsonListener<LoginResult>(LoginResult.class) {
                    @Override
                    public void onResponse(LoginResult data) {
                        GlobalInfo.token = data.getToken();
                        GlobalInfo.user = data.getUser();
                        GlobalInfo.user.setUserName(userName);
                        Toast.makeText(LoginActivity.this, data.getMessage(), Toast.LENGTH_SHORT).show();
                        onLoginFinished();
                        onLoginSuccess();
                    }

            @Override
            public boolean onErrorResponse(int statusCode, Result errorInfo) {
                onLoginFinished();
                switch (errorInfo.getResultCode()){
                    case PublicResultCode.USER_NOT_EXIST:
                        editUserName.setError(errorInfo.getMessage());
                        return true;
                    case PublicResultCode.INCORRECT_PASSWORD:
                        editPassword.setError(errorInfo.getMessage());
                        return true;
                }
                return super.onErrorResponse(statusCode, errorInfo);
            }

            @Override
            public boolean onDataCorrupted(Throwable e) {
                onLoginFinished();
                return super.onDataCorrupted(e);
            }

            @Override
            public boolean onNetworkError(Throwable e) {
                onLoginFinished();
                return super.onNetworkError(e);
            }
        }));
    }

    private void signUp(final String userName, String password, String email){
        RegisterRequest request = new RegisterRequest(userName, password, email);
        onLoginStart();
        HttpApi.startRequest(new HttpApiJsonRequest(this, HttpApi.getApiUrl(HttpApi.AccountApi.REGISTER), Request.Method.POST, null,
                request, new HttpApiJsonListener<LoginResult>(LoginResult.class) {
            @Override
            public void onResponse(LoginResult data) {
                GlobalInfo.token = data.getToken();
                GlobalInfo.user = data.getUser();
                GlobalInfo.user.setUserName(userName);
                Toast.makeText(LoginActivity.this, data.getMessage(), Toast.LENGTH_SHORT).show();
                onLoginFinished();
                onLoginSuccess();
            }

            @Override
            public boolean onErrorResponse(int statusCode, Result errorInfo) {
                onLoginFinished();
                switch (errorInfo.getResultCode()){
                    case PublicResultCode.USER_NAME_USED:
                        editUserName.setError(errorInfo.getMessage());
                        return true;
                    case PublicResultCode.ILLEGAL_PASSWORD_LENGTH:
                    case PublicResultCode.ILLEGAL_PASSWORD_FORMAT:
                        editPassword.setError(errorInfo.getMessage());
                        return true;
                    case PublicResultCode.ILLEGAL_EMAIL:
                        editEmail.setError(errorInfo.getMessage());
                        return true;
                }
                return super.onErrorResponse(statusCode, errorInfo);
            }

            @Override
            public boolean onDataCorrupted(Throwable e) {
                onLoginFinished();
                return super.onDataCorrupted(e);
            }

            @Override
            public boolean onNetworkError(Throwable e) {
                onLoginFinished();
                return super.onNetworkError(e);
            }
        }));
    }

    private void onLoginStart(){
        btnSignIn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void onLoginFinished(){
        btnSignIn.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }

    private void onLoginSuccess(){
        DatabaseUtil.updateLoginRecord(GlobalInfo.user, GlobalInfo.token);
        Intent intent = new Intent(Application.ACTION_LOGIN);
        intent.addCategory(getPackageName());
        sendBroadcast(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
