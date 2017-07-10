package happyyoung.trashnetwork.recycle.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import happyyoung.trashnetwork.recycle.BuildConfig;
import happyyoung.trashnetwork.recycle.R;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {
    @BindView(R.id.about_container)
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String[] openSourceLib = new String[]{
                "google/gson",
                "google/volley",
                "hdodenhof/CircleImageView",
                "pardom/ActiveAndroid",
                "rengwuxian/MaterialEditText",
                "medyo/android-about-page",
                "akashandroid90/ImageLetterIcon",
                "Malinskiy/SuperRecyclerView",
                "bingoogolapple/BGAQRCode-Android",
                "Justson/AgentWeb",
                "nekocode/Badge",
                "daimajia/AndroidImageSlider",
        };

        AboutPage aboutPage = new AboutPage(this)
                .setImage(R.mipmap.ic_launcher)
                .setDescription(getString(R.string.app_name))
                .addItem(new Element(String.format(getString(R.string.app_version_format), BuildConfig.VERSION_NAME), R.drawable.ic_info_outline_32dp))
                .addEmail("GGGZ-1101-28@Live.cn", "GGGZ-1101-28@Live.cn")
                .addGitHub("TrashNetwork", "https://github.com/TrashNetwork")
                .addGroup(getString(R.string.open_source_license));
        for(String s : openSourceLib){
            aboutPage.addGitHub(s, s);
        }
        container.addView(aboutPage.create());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
