package happyyoung.trashnetwork.recycle.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
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
    @BindView(R.id.email_view_area) View emailView;
    @BindView(R.id.edit_user_name) EditText editUserName;
    @BindView(R.id.edit_password) EditText editPassword;
    @BindView(R.id.edit_email) EditText editEmail;
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
        }else if(email.isEmpty() && status == STATUS_SIGNUP){
            editEmail.setError(STR_ERROR_FIELD_REQUIRED);
            return;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmail.setError(getString(R.string.error_illegal_email));
            return;
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

    private void signIn(String userName, String password){

    }

    private void signUp(String userName, String password, String email){

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
