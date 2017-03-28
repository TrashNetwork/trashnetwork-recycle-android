package happyyoung.trashnetwork.recycle.ui.activity;

import android.Manifest;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import happyyoung.trashnetwork.recycle.Application;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.service.LocationService;
import happyyoung.trashnetwork.recycle.ui.fragment.FeedbackFragment;
import happyyoung.trashnetwork.recycle.ui.fragment.MapFragment;
import happyyoung.trashnetwork.recycle.util.GlobalInfo;
import happyyoung.trashnetwork.recycle.util.HttpUtil;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String BUNDLE_KEY_UPDATE_USER_INFO = "UpdateUserInfo";
    public static final String QRCODE_ACTION_RECYCLE_BOTTLE = "Recycle bottle";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_main) NavigationView navView;
    private View mNavHeaderView;

    private FragmentManager mFragmentManager;
    private MapFragment mapFragment;
    private FeedbackFragment feedbackFragment;

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
        mFragmentManager.beginTransaction()
                .add(R.id.main_container, mapFragment)
                .add(R.id.main_container, feedbackFragment)
                .commit();
        onNavigationItemSelected(navView.getMenu().getItem(0));

        Application.checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        Application.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        Application.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
        if(getIntent().getBooleanExtra(BUNDLE_KEY_UPDATE_USER_INFO, false))
            HttpUtil.updateUserInfo(this);
    }

    private void exitApp(){
        stopService(new Intent(this, LocationService.class));
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
            navView.getMenu().findItem(R.id.nav_credit_record).setVisible(false);
        }else{
            userPortrait.setShapeColor(Application.generateColorFromStr(GlobalInfo.user.getUserName()));
            userPortrait.setLetter(GlobalInfo.user.getUserName());
            txtCredit.setText(View.VISIBLE);
            txtCredit.setText(String.format(getString(R.string.credit_format), GlobalInfo.user.getCredit()));
            txtPhoneNumber.setText(GlobalInfo.user.getUserName());
            userPortrait.setOnClickListener(null);
            navView.getMenu().findItem(R.id.nav_credit_record).setVisible(true);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(intentResult == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        String qrCodeData = intentResult.getContents();
        if(qrCodeData == null || qrCodeData.isEmpty())
            return;
        try {
            JsonElement parsedData = new JsonParser().parse(qrCodeData);
            if(!parsedData.getAsJsonObject().has("action"))
                return;
            switch (parsedData.getAsJsonObject().get("action").getAsString()){
                case QRCODE_ACTION_RECYCLE_BOTTLE:
                    if(!parsedData.getAsJsonObject().has("trash_id"))
                        return;
                    if(GlobalInfo.user == null){
                        startActivity(new Intent(this, LoginActivity.class));
                        Toast.makeText(this, R.string.alert_login_first, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    recycleBottle(parsedData.getAsJsonObject().get("trash_id").getAsLong());
                    return;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void recycleBottle(final long trashId){
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
                        HttpUtil.recycleBottle(MainActivity.this, trashId, seekQuantity.getProgress() + 1);
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
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
        ft.hide(mapFragment);
        ft.hide(feedbackFragment);
    }

    private void scanQRCode(){
        new IntentIntegrator(this)
                .setOrientationLocked(false)
                .setCaptureActivity(ScanQRCodeActivity.class)
                .setBarcodeImageEnabled(false)
                .setBeepEnabled(false)
                .initiateScan();
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
