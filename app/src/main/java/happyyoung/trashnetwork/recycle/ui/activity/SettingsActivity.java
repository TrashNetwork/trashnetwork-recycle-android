package happyyoung.trashnetwork.recycle.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.ui.widget.PreferenceCard;
import happyyoung.trashnetwork.recycle.util.GlobalInfo;

public class SettingsActivity extends AppCompatActivity {
    @BindDimen(R.dimen.activity_vertical_margin) int DIMEN_VERTICAL_MARGIN;

    @BindView(R.id.settings_container) ViewGroup container;
    private PreferenceCard accountCard;
    private PreferenceCard otherCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.bottomMargin = DIMEN_VERTICAL_MARGIN;

        if(GlobalInfo.user != null){
            accountCard = new PreferenceCard(this)
                    .addGroup(getString(R.string.account))
                    .addItem(R.drawable.ic_exit, getString(R.string.action_logout), null, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            logout();
                        }
                    });
            accountCard.getView().setLayoutParams(params);
            container.addView(accountCard.getView());
        }

        otherCard = new PreferenceCard(this)
                    .addGroup(getString(R.string.other))
                    .addItem(R.drawable.ic_info_outline_48dp, getString(R.string.action_about), null, null);
        container.addView(otherCard.getView());
    }

    private void logout(){
        accountCard.getView().setVisibility(View.GONE);
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
