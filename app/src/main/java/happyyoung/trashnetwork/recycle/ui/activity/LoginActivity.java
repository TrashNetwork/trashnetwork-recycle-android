package happyyoung.trashnetwork.recycle.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import happyyoung.trashnetwork.recycle.R;

public class LoginActivity extends AppCompatActivity {
    private static final int STATUS_SIGNIN = 1;
    private static final int STATUS_SIGNUP = 2;

    @BindString(R.string.error_field_required) String STR_ERROR_FIELD_REQUIRED;

    @BindView(R.id.btn_sign_in) Button btnSignIn;
    @BindView(R.id.btn_sign_up) Button btnSignUp;
    @BindView(R.id.captcha_view_area) View captchaView;
    @BindView(R.id.edit_phone_number) EditText editPhoneNum;
    @BindView(R.id.edit_password) EditText editPassword;
    @BindView(R.id.edit_captcha) EditText editCaptcha;
    @BindView(R.id.progress_signin) ProgressBar progressBar;


    private int status = STATUS_SIGNIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        setSupportActionBar((Toolbar) ButterKnife.findById(this, R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.btn_sign_up)
    void onBtnSignUpClick(View v){
        switch (status){
            case STATUS_SIGNIN:
                status = STATUS_SIGNUP;
                captchaView.setVisibility(View.VISIBLE);
                btnSignIn.setText(R.string.action_sign_up);
                btnSignUp.setText(R.string.action_sign_in);
                break;
            case STATUS_SIGNUP:
                status = STATUS_SIGNIN;
                captchaView.setVisibility(View.GONE);
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
        String phoneNum = editPhoneNum.getText().toString();
        String password = editPassword.getText().toString();
        String captcha = editCaptcha.getText().toString();

        if(phoneNum.isEmpty()){
            editPhoneNum.setError(STR_ERROR_FIELD_REQUIRED);
            return;
        }else if(password.isEmpty()){
            editPassword.setError(STR_ERROR_FIELD_REQUIRED);
            return;
        }else if(password.length() < 6 || password.length() > 20){
            editPassword.setError(getString(R.string.error_illegal_password_length));
            return;
        }else if(captcha.isEmpty() && status == STATUS_SIGNUP){
            editCaptcha.setError(STR_ERROR_FIELD_REQUIRED);
            return;
        }
        switch (status){
            case STATUS_SIGNIN:
                signIn(phoneNum, password);
                break;
            case STATUS_SIGNUP:
                signUp(phoneNum, password, captcha);
                break;
        }
    }

    private void signIn(String phoneNumber, String password){

    }

    private void signUp(String phoneNumber, String password, String captcha){

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
