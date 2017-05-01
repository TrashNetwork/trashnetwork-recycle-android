package happyyoung.trashnetwork.recycle.ui.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;

import butterknife.BindView;
import butterknife.ButterKnife;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.net.http.HttpApi;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonListener;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonRequest;
import happyyoung.trashnetwork.recycle.net.model.request.PostFeedbackRequest;
import happyyoung.trashnetwork.recycle.net.model.result.Result;
import happyyoung.trashnetwork.recycle.util.GlobalInfo;

public class NewFeedbackActivity extends AppCompatActivity {
    private static final int MENU_ID_ACTION_DONE = 0x666666;

    @BindView(R.id.edit_title) EditText editTitle;
    @BindView(R.id.edit_content) EditText editContent;
    @BindView(R.id.check_anonymous) CheckBox checkAnonymous;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_feedback);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        token = GlobalInfo.token;
        if(GlobalInfo.user == null){
            checkAnonymous.setChecked(true);
            checkAnonymous.setEnabled(false);
        }

        checkAnonymous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    token = null;
                else
                    token = GlobalInfo.token;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(0, MENU_ID_ACTION_DONE, 0, R.string.action_done);
        item.setIcon(R.drawable.ic_done_white);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    private void postFeedback(){
        String title = editTitle.getText().toString();
        if(title.isEmpty()) {
            editTitle.setError(getString(R.string.error_field_required));
            return;
        }
        String content = editContent.getText().toString();
        if(content.isEmpty()) {
            editContent.setError(getString(R.string.error_field_required));
            return;
        }
        PostFeedbackRequest request = new PostFeedbackRequest(title, content);
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage(getString(R.string.alert_waiting));
        pd.show();
        HttpApi.startRequest(new HttpApiJsonRequest(this, HttpApi.getApiUrl(HttpApi.FeedbackApi.POST_FEEDBACK), Request.Method.POST,
                token, request, new HttpApiJsonListener<Result>(Result.class) {
            @Override
            public void onDataResponse(Result data) {
                pd.dismiss();
                Toast.makeText(NewFeedbackActivity.this, data.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onErrorResponse() {
                pd.dismiss();
            }
        }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                return true;
            case MENU_ID_ACTION_DONE:
                postFeedback();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
