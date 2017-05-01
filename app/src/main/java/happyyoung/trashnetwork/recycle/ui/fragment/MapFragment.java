package happyyoung.trashnetwork.recycle.ui.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.android.volley.Request;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import happyyoung.trashnetwork.recycle.Application;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.model.RecyclePoint;
import happyyoung.trashnetwork.recycle.net.http.HttpApi;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonListener;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonRequest;
import happyyoung.trashnetwork.recycle.net.model.request.RecyclePointCleanRequest;
import happyyoung.trashnetwork.recycle.net.model.result.RecyclePointListResult;
import happyyoung.trashnetwork.recycle.net.model.result.Result;
import happyyoung.trashnetwork.recycle.ui.activity.MainActivity;
import happyyoung.trashnetwork.recycle.util.DateTimeUtil;
import happyyoung.trashnetwork.recycle.util.GlobalInfo;
import happyyoung.trashnetwork.recycle.util.ImageUtil;

public class MapFragment extends Fragment {
    private static final String BUNDLE_KEY_MARKER_TYPE = "MarkerType";
    private static final String BUNDLE_KEY_RECYCLE_POINT_ID = "RecyclePointID";
    private static final int MARKER_TYPE_USER = 1;
    private static final int MARKER_TYPE_RECYCLE_POINT = 2;

    private View rootView;
    @BindView(R.id.amap_view) MapView mapView;
    @BindView(R.id.user_location_area) View userLocationView;
    @BindView(R.id.txt_user_location) TextView txtUserLocation;
    @BindView(R.id.txt_user_update_time) TextView txtUserUpdateTime;
    @BindView(R.id.recycle_point_view_area) View recyclePointView;
    @BindView(R.id.txt_recycle_point_name) TextView txtRecyclePointName;
    @BindView(R.id.txt_recycle_point_desc) TextView txtRecyclePointDesc;
    @BindView(R.id.recycle_point_info_view) View recyclePointInfoView;
    @BindView(R.id.btn_recycle) ImageButton btnRecycle;
    @BindView(R.id.btn_refresh) FloatingActionButton btnRefresh;
    @BindView(R.id.txt_bottle_num) TextView txtBottleNum;

    private boolean mapCenterFlag = false;
    private long currentShowRecyclePointId = -1;
    private AMap amap;
    private Marker userMarker;
    private List<Marker> recyclePointMarkers = new ArrayList<>();
    private LocationReceiver locationReceiver;
    private UserInfoReceiver userInfoReceiver;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(Context context) {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, rootView);

        mapView.onCreate(savedInstanceState);
        amap = mapView.getMap();
        amap.getUiSettings().setCompassEnabled(true);
        amap.getUiSettings().setScaleControlsEnabled(true);
        amap.getUiSettings().setZoomGesturesEnabled(true);
        amap.getUiSettings().setScrollGesturesEnabled(true);
        amap.getUiSettings().setRotateGesturesEnabled(true);
        amap.moveCamera(CameraUpdateFactory.zoomTo(18f));
        amap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                showUserLocation(true);
            }
        });
        amap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle bundle = (Bundle) marker.getObject();
                switch (bundle.getInt(BUNDLE_KEY_MARKER_TYPE)){
                    case MARKER_TYPE_USER:
                        showUserLocation(true);
                        break;
                    case MARKER_TYPE_RECYCLE_POINT:
                        showRecyclePointInfo(bundle.getLong(BUNDLE_KEY_RECYCLE_POINT_ID));
                        break;
                }
                return true;
            }
        });

        locationReceiver = new LocationReceiver();
        IntentFilter filter = new IntentFilter(Application.ACTION_LOCATION);
        filter.addCategory(getContext().getPackageName());
        getContext().registerReceiver(locationReceiver, filter);

        userInfoReceiver = new UserInfoReceiver();
        filter = new IntentFilter(Application.ACTION_LOGIN);
        filter.addCategory(getContext().getPackageName());
        getContext().registerReceiver(userInfoReceiver, filter);
        filter = new IntentFilter(Application.ACTION_LOGOUT);
        filter.addCategory(getContext().getPackageName());
        getContext().registerReceiver(userInfoReceiver, filter);
        filter = new IntentFilter(Application.ACTION_USER_UPDATE);
        filter.addCategory(getContext().getPackageName());
        getContext().registerReceiver(userInfoReceiver, filter);

        getAllRecyclePoint();
        return rootView;
    }

    @OnClick(R.id.user_location_area)
    void onUserLocationViewClick(View v){
        amap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition(new LatLng(GlobalInfo.currentLocation.getLatitude(),
                        GlobalInfo.currentLocation.getLongitude()), 18, 0, 0)));
    }

    @OnClick(R.id.recycle_point_view_area)
    void onRecyclePointViewClick(View v){
        RecyclePoint t = GlobalInfo.findRecyclePointById(currentShowRecyclePointId);
        if(t == null)
            return;
        amap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition(new LatLng(t.getLatitude(), t.getLongitude()), 18, 0, 0)));
    }

    @OnClick(R.id.btn_refresh)
    void onBtnRefreshClick(View v){
        getAllRecyclePoint();
    }

    @OnClick(R.id.btn_recycle)
    void onBtnRecycleClick(View v){
        final RecyclePointCleanRequest req = new RecyclePointCleanRequest(currentShowRecyclePointId);
        HttpApi.startRequest(new HttpApiJsonRequest(getActivity(), HttpApi.getApiUrl(HttpApi.RecycleRecordApi.NEW_RECORD), Request.Method.POST, GlobalInfo.token, req,
                new HttpApiJsonListener<Result>(Result.class) {
                    @Override
                    public void onDataResponse(Result data) {
                        Toast.makeText(getContext(), data.getMessage(), Toast.LENGTH_SHORT).show();
                        RecyclePoint rp = GlobalInfo.findRecyclePointById(req.getRecyclePointId());
                        if(rp == null)
                            return;
                        if(rp.isBottleRecycle())
                            rp.setBottleNum(0);
                        if(currentShowRecyclePointId == req.getRecyclePointId())
                            showRecyclePointInfo(req.getRecyclePointId());
                    }
                }));
    }

    private void addRecyclePointMarker(){
        MarkerOptions recyclePointMarkerOpt = new MarkerOptions()
                .draggable(false)
                .icon(BitmapDescriptorFactory.fromBitmap(
                        ImageUtil.getBitmapFromDrawable(getContext(), R.drawable.ic_delete_green_32dp)));
        for(RecyclePoint rp : GlobalInfo.recyclePointList){
            if(!rp.isBottleRecycle())
                continue;
            recyclePointMarkerOpt.position(new LatLng(rp.getLatitude(), rp.getLongitude()));
            Marker recyclePointMarker = amap.addMarker(recyclePointMarkerOpt);
            Bundle bundle = new Bundle();
            bundle.putInt(BUNDLE_KEY_MARKER_TYPE, MARKER_TYPE_RECYCLE_POINT);
            bundle.putLong(BUNDLE_KEY_RECYCLE_POINT_ID, rp.getRecyclePointId());
            recyclePointMarker.setObject(bundle);
            recyclePointMarkers.add(recyclePointMarker);
            if(rp.getRecyclePointId() == currentShowRecyclePointId && recyclePointView.getVisibility() == View.VISIBLE)
                showRecyclePointInfo(rp.getRecyclePointId());
        }
    }

    private void updateUserLocation(){
        LatLng pos = new LatLng(GlobalInfo.currentLocation.getLatitude(), GlobalInfo.currentLocation.getLongitude());
        if(userMarker == null){
            MarkerOptions userMarkerOptions = new MarkerOptions()
                    .alpha(0.9f)
                    .icon(BitmapDescriptorFactory.fromBitmap(
                            ImageUtil.getBitmapFromDrawable(getContext(), R.drawable.ic_location_red)))
                    .position(pos);
            userMarker = amap.addMarker(userMarkerOptions);
            Bundle bundle = new Bundle();
            bundle.putInt(BUNDLE_KEY_MARKER_TYPE, MARKER_TYPE_USER);
            userMarker.setObject(bundle);
        }else{
            userMarker.setPosition(pos);
        }
        if(!mapCenterFlag){
            amap.moveCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition(pos, 18, 0, 0)
            ));
            mapCenterFlag = true;
        }
        if(recyclePointView.getVisibility() == View.VISIBLE)
            return;
        showUserLocation(false);
    }

    private void showUserLocation(boolean fromUser){
        if(GlobalInfo.currentLocation == null)
            return;
        recyclePointView.setVisibility(View.GONE);
        if(userLocationView.getVisibility() != View.VISIBLE || !fromUser){
            userLocationView.setVisibility(View.VISIBLE);
            txtUserUpdateTime.setText(DateTimeUtil.convertTimestamp(getContext(), GlobalInfo.currentLocation.getUpdateTime(), true, true, true));
            if(GlobalInfo.currentLocation.getAddress() != null && !GlobalInfo.currentLocation.getAddress().isEmpty())
                txtUserLocation.setText(GlobalInfo.currentLocation.getAddress());
            else
                txtUserLocation.setText(R.string.unknown_location);
        }
    }

    private void showRecyclePointInfo(long recyclePointId){
        RecyclePoint rp = GlobalInfo.findRecyclePointById(recyclePointId);
        if(rp == null)
            return;
        currentShowRecyclePointId = recyclePointId;
        userLocationView.setVisibility(View.GONE);
        recyclePointView.setVisibility(View.VISIBLE);
        txtRecyclePointName.setText(rp.getRecyclePointName(getContext()));
        txtRecyclePointDesc.setText(rp.getDescription());
        if(GlobalInfo.user != null && GlobalInfo.user.getUserId().equals(rp.getOwnerId())){
            recyclePointInfoView.setVisibility(View.VISIBLE);
            if(rp.getBottleNum() != null)
                txtBottleNum.setText(String.valueOf(rp.getBottleNum()));
            btnRecycle.setVisibility(View.VISIBLE);
        }else{
            recyclePointInfoView.setVisibility(View.GONE);
            btnRecycle.setVisibility(View.GONE);
        }
    }

    private void getAllRecyclePoint(){
        btnRefresh.setEnabled(false);
        HttpApi.startRequest(new HttpApiJsonRequest(getActivity(), HttpApi.getApiUrl(HttpApi.RecyclePointApi.ALL_POINTS), Request.Method.GET,
                GlobalInfo.token, null, new HttpApiJsonListener<RecyclePointListResult>(RecyclePointListResult.class) {
            @Override
            public void onResponse() {
                btnRefresh.setEnabled(true);
            }

            @Override
            public void onDataResponse(RecyclePointListResult data) {
                for(Marker m : recyclePointMarkers)
                    m.remove();
                recyclePointMarkers.clear();
                GlobalInfo.recyclePointList = data.getRecyclePointList();
                addRecyclePointMarker();
            }
        }));
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        getContext().unregisterReceiver(locationReceiver);
        getContext().unregisterReceiver(userInfoReceiver);
        rootView = null;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUserLocation();
        }
    }

    private class UserInfoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            getAllRecyclePoint();
        }
    }
}
