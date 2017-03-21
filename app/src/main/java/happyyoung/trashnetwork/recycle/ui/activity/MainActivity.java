package happyyoung.trashnetwork.recycle.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;

import butterknife.BindView;
import butterknife.ButterKnife;
import happyyoung.trashnetwork.recycle.Application;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.service.LocationService;
import happyyoung.trashnetwork.recycle.ui.fragment.MapFragment;
import happyyoung.trashnetwork.recycle.util.GlobalInfo;
import happyyoung.trashnetwork.recycle.ui.activity.ScanQRCodeActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_main) NavigationView navView;
    private View mNavHeaderView;

    private FragmentManager mFragmentManager;
    private MapFragment mapFragment;

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
        mFragmentManager.beginTransaction()
                .add(R.id.main_container, mapFragment)
                .commit();
        onNavigationItemSelected(navView.getMenu().getItem(0));

        Application.checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        Application.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        Application.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        startService(new Intent(this, LocationService.class));
        updateUserInfo();
    }

    private void exitApp(){
        stopService(new Intent(this, LocationService.class));
        GlobalInfo.logout(this);
        finish();
    }

    private void updateUserInfo(){
        ImageView userPortrait = ButterKnife.findById(mNavHeaderView, R.id.nav_header_portrait);
        TextView txtPhoneNumber = ButterKnife.findById(mNavHeaderView, R.id.txt_nav_header_phone_number);
        TextView txtCredit = ButterKnife.findById(mNavHeaderView, R.id.txt_nav_header_credit);
        if(GlobalInfo.user == null){
            txtCredit.setVisibility(View.GONE);
            txtPhoneNumber.setText(R.string.alert_click_avatar_login);
            userPortrait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            });
        }else{
            txtCredit.setText(View.VISIBLE);
            txtCredit.setText(getString(R.string.credit) + ':' + GlobalInfo.user.getCredit());
            txtPhoneNumber.setText(GlobalInfo.user.getPhoneNumber());
            userPortrait.setOnClickListener(null);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            exitApp();
        }
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
    }

    private void scanQRCode(){
        new IntentIntegrator(this)
                .setOrientationLocked(false)
                .setCaptureActivity(ScanQRCodeActivity.class)
                .initiateScan();
    }
}
