package happyyoung.trashnetwork.recycle.ui.activity;

import android.Manifest;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.akashandroid90.imageletter.MaterialLetterIcon;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import happyyoung.trashnetwork.recycle.Application;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.model.User;
import happyyoung.trashnetwork.recycle.service.LocationService;
import happyyoung.trashnetwork.recycle.ui.fragment.CreditMallFragment;
import happyyoung.trashnetwork.recycle.ui.fragment.CreditRankFragment;
import happyyoung.trashnetwork.recycle.ui.fragment.EventFragment;
import happyyoung.trashnetwork.recycle.ui.fragment.FeedbackFragment;
import happyyoung.trashnetwork.recycle.ui.fragment.MapFragment;
import happyyoung.trashnetwork.recycle.util.GlobalInfo;
import happyyoung.trashnetwork.recycle.util.HttpUtil;
import happyyoung.trashnetwork.recycle.util.StringUtil;
import hugo.weaving.DebugLog;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String QRCODE_ACTION_RECYCLE_BOTTLE = "Recycle bottle";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_main) NavigationView navView;
    private View mNavHeaderView;

    private FragmentManager mFragmentManager;
    private MapFragment mapFragment;
    private CreditRankFragment creditRankFragment;
    private FeedbackFragment feedbackFragment;
    private EventFragment eventFragment;
    private CreditMallFragment creditMallFragment;

    private boolean exitFlag = false;
    private UserInfoReceiver userInfoReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);
        mNavHeaderView = navView.getHeaderView(0);
        mFragmentManager = getSupportFragmentManager();
        mapFragment = MapFragment.newInstance(this);
        feedbackFragment = FeedbackFragment.newInstance(this);
        creditRankFragment = CreditRankFragment.newInstance(this);
        eventFragment = EventFragment.newInstance(this);
        creditMallFragment = CreditMallFragment.newInstance(this);
        mFragmentManager.beginTransaction()
                .add(R.id.main_container, mapFragment)
                .add(R.id.main_container, feedbackFragment)
                .add(R.id.main_container, creditRankFragment)
                .add(R.id.main_container, eventFragment)
                .add(R.id.main_container, creditMallFragment)
                .commit();
        onNavigationItemSelected(navView.getMenu().getItem(0));

        Application.checkPermission(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });
        startService(new Intent(this, LocationService.class));
        updateUserInfo();

        userInfoReceiver = new UserInfoReceiver();
        IntentFilter filter = new IntentFilter(Application.ACTION_LOGIN);
        filter.addCategory(getPackageName());
        registerReceiver(userInfoReceiver, filter);
        filter = new IntentFilter(Application.ACTION_LOGOUT);
        filter.addCategory(getPackageName());
        registerReceiver(userInfoReceiver, filter);
        filter = new IntentFilter(Application.ACTION_USER_UPDATE);
        filter.addCategory(getPackageName());
        registerReceiver(userInfoReceiver, filter);

        JPushInterface.setDebugMode(true);
        JPushInterface.init(getApplicationContext());
        JPushInterface.resumePush(getApplicationContext());
    }

    private void exitApp(){
        stopService(new Intent(this, LocationService.class));
        JPushInterface.stopPush(getApplicationContext());
        GlobalInfo.logout(this);
        finish();
    }

    private void updateUserInfo(){
        MaterialLetterIcon userPortrait = ButterKnife.findById(mNavHeaderView, R.id.nav_header_portrait);
        TextView txtPhoneNumber = ButterKnife.findById(mNavHeaderView, R.id.txt_nav_header_phone_number);
        TextView txtCredit = ButterKnife.findById(mNavHeaderView, R.id.txt_nav_header_credit);
        if(GlobalInfo.user == null){
            txtCredit.setVisibility(View.GONE);
            userPortrait.setShapeColor(getResources().getColor(R.color.colorAccent));
            userPortrait.setImageResource(R.drawable.ic_person_white_96dp);
            txtPhoneNumber.setText(R.string.alert_click_avatar_login);
            userPortrait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            });
            navView.getMenu().setGroupVisible(R.id.nav_group_user, false);
        }else{
            userPortrait.setShapeColor(Application.generateColorFromStr(GlobalInfo.user.getUserName()));
            userPortrait.setLetter(StringUtil.getDigestLetters(GlobalInfo.user.getUserName(), 2));
            txtCredit.setVisibility(View.VISIBLE);
            txtCredit.setText(String.format(getString(R.string.credit_format), GlobalInfo.user.getCredit()));
            txtPhoneNumber.setText(GlobalInfo.user.getUserName());
            userPortrait.setOnClickListener(null);
            navView.getMenu().setGroupVisible(R.id.nav_group_user, true);
            if(GlobalInfo.user.getAccountType() == User.USER_TYPE_GARBAGE_COLLECTOR)
                navView.getMenu().findItem(R.id.nav_recycle_record).setVisible(true);
            else
                navView.getMenu().findItem(R.id.nav_recycle_record).setVisible(false);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(creditMallFragment.isVisible()){
                if(creditMallFragment.onBackPressed())
                    return;
            }
            if(!exitFlag){
                Toast.makeText(this, R.string.alert_press_again_to_exit, Toast.LENGTH_SHORT).show();
                exitFlag = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exitFlag = false;
                    }
                }, 3000);
                return;
            }
            exitApp();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ScanQRCodeActivity.REQUEST_CODE_SCAN_QR_CODE && resultCode == ScanQRCodeActivity.RESULT_CDOE_SCAN_QR_CODE
                && data != null){
            String qrCodeData = data.getStringExtra(ScanQRCodeActivity.BUNDLE_QR_CODE_STR);
            if(qrCodeData == null || qrCodeData.isEmpty())
                return;
            try {
                JsonElement parsedData = new JsonParser().parse(qrCodeData);
                if(!parsedData.getAsJsonObject().has("action"))
                    return;
                switch (parsedData.getAsJsonObject().get("action").getAsString()){
                    case QRCODE_ACTION_RECYCLE_BOTTLE:
                        if(!parsedData.getAsJsonObject().has("recycle_point_id"))
                            return;
                        if(GlobalInfo.user == null){
                            startActivity(new Intent(this, LoginActivity.class));
                            Toast.makeText(this, R.string.alert_login_first, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        recycleBottle(parsedData.getAsJsonObject().get("recycle_point_id").getAsLong());
                        return;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void recycleBottle(final long recyclePointId){
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_recycle_bottle, null, false);
        final TextView txtQuantity = ButterKnife.findById(dialogView, R.id.txt_quantity);
        final SeekBar seekQuantity = ButterKnife.findById(dialogView, R.id.seekbar_quantity);
        seekQuantity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtQuantity.setText(Integer.toString(progress + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.action_recycle_bottle)
                .setView(dialogView)
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HttpUtil.recycleBottle(MainActivity.this, recyclePointId, seekQuantity.getProgress() + 1);
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        creditMallFragment.setSearchView(searchItem, searchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_main_scan:
                scanQRCode();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentTransaction ft;
        switch (item.getItemId()){
            case R.id.nav_map:
                setTitle(getString(R.string.action_map));
                ft = mFragmentManager.beginTransaction();
                hideAllFragment(ft);
                ft.show(mapFragment);
                ft.commit();
                break;
            case R.id.nav_credit_rank:
                setTitle(getString(R.string.action_credit_rank));
                ft = mFragmentManager.beginTransaction();
                hideAllFragment(ft);
                ft.show(creditRankFragment);
                ft.commit();
                break;
            case R.id.nav_event:
                setTitle(getString(R.string.action_event));
                ft = mFragmentManager.beginTransaction();
                hideAllFragment(ft);
                ft.show(eventFragment);
                ft.commit();
                break;
            case R.id.nav_credit_mall:
                setTitle(getString(R.string.action_credit_mall));
                ft = mFragmentManager.beginTransaction();
                hideAllFragment(ft);
                ft.show(creditMallFragment);
                ft.commit();
                break;
            case R.id.nav_order:
                startActivity(new Intent(this, OrderActivity.class));
                break;
            case R.id.nav_credit_record:
                startActivity(new Intent(this, CreditRecordActivity.class));
                break;
            case R.id.nav_feedback:
                setTitle(getString(R.string.action_feedback));
                ft = mFragmentManager.beginTransaction();
                hideAllFragment(ft);
                ft.show(feedbackFragment);
                ft.commit();
                break;
            case R.id.nav_recycle_record:
                startActivity(new Intent(this, RecycleRecordActivity.class));
                break;
            case R.id.nav_scan_qrcode:
                scanQRCode();
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_exit:
                exitApp();
                return true;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void hideAllFragment(FragmentTransaction ft){
        ft.hide(mapFragment)
          .hide(creditRankFragment)
          .hide(eventFragment)
          .hide(feedbackFragment)
          .hide(creditMallFragment);
    }

    private void scanQRCode(){
        startActivityForResult(new Intent(this, ScanQRCodeActivity.class), ScanQRCodeActivity.REQUEST_CODE_SCAN_QR_CODE);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(userInfoReceiver);
        super.onDestroy();
    }

    private class UserInfoReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUserInfo();
        }
    }
}
