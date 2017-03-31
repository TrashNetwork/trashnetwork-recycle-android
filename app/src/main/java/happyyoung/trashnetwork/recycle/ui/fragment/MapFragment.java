package happyyoung.trashnetwork.recycle.ui.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.android.volley.Request;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import happyyoung.trashnetwork.recycle.Application;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.model.Trash;
import happyyoung.trashnetwork.recycle.net.http.HttpApi;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonListener;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonRequest;
import happyyoung.trashnetwork.recycle.net.model.result.TrashListResult;
import happyyoung.trashnetwork.recycle.util.GlobalInfo;
import happyyoung.trashnetwork.recycle.util.ImageUtil;

public class MapFragment extends Fragment {
    private static final String BUNDLE_KEY_MARKER_TYPE = "MarkerType";
    private static final String BUNDLE_KEY_TRASH_ID = "TrashID";
    private static final int MARKER_TYPE_USER = 1;
    private static final int MARKER_TYPE_TRASH = 2;

    private View rootView;
    @BindView(R.id.amap_view) MapView mapView;
    @BindView(R.id.user_location_area) View userLocationView;
    @BindView(R.id.txt_user_location) TextView txtUserLocation;
    @BindView(R.id.trash_view_area) View trashView;
    @BindView(R.id.txt_trash_name) TextView txtTrashName;
    @BindView(R.id.txt_trash_desc) TextView txtTrashDesc;

    private boolean mapCenterFlag = false;
    private long currentShowTrashId = -1;
    private AMap amap;
    private Marker userMarker;
    private LocationReceiver locationReceiver;

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
                    case MARKER_TYPE_TRASH:
                        showTrashInfo(bundle.getLong(BUNDLE_KEY_TRASH_ID));
                        break;
                }
                return true;
            }
        });

        getAllTrashInfo();

        locationReceiver = new LocationReceiver();
        IntentFilter filter = new IntentFilter(Application.ACTION_LOCATION);
        filter.addCategory(getContext().getPackageName());
        getContext().registerReceiver(locationReceiver, filter);

        return rootView;
    }

    @OnClick(R.id.user_location_area)
    void onUserLocationViewClick(View v){
        amap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition(new LatLng(GlobalInfo.currentLocation.getLatitude(),
                        GlobalInfo.currentLocation.getLongitude()), 18, 0, 0)));
    }

    @OnClick(R.id.trash_view_area)
    void onTrashViewClick(View v){
        Trash t = GlobalInfo.findTrashById(currentShowTrashId);
        if(t == null)
            return;
        amap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition(new LatLng(t.getLatitude(), t.getLongitude()), 18, 0, 0)));
    }

    private void addTrashMarker(){
        MarkerOptions trashMarkerOpts = new MarkerOptions()
                .draggable(false)
                .icon(BitmapDescriptorFactory.fromBitmap(
                        ImageUtil.getBitmapFromDrawable(getContext(), R.drawable.ic_delete_green_32dp)));
        for(Trash t : GlobalInfo.trashList){
            if(!t.isBottleRecycle())
                continue;
            trashMarkerOpts.position(new LatLng(t.getLatitude(), t.getLongitude()));
            Marker trashMarker = amap.addMarker(trashMarkerOpts);
            Bundle bundle = new Bundle();
            bundle.putInt(BUNDLE_KEY_MARKER_TYPE, MARKER_TYPE_TRASH);
            bundle.putLong(BUNDLE_KEY_TRASH_ID, t.getTrashId());
            trashMarker.setObject(bundle);
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
        if(trashView.getVisibility() == View.VISIBLE)
            return;
        showUserLocation(false);
    }

    private void showUserLocation(boolean fromUser){
        if(GlobalInfo.currentLocation == null)
            return;
        trashView.setVisibility(View.GONE);
        if(userLocationView.getVisibility() != View.VISIBLE || !fromUser){
            userLocationView.setVisibility(View.VISIBLE);
            if(GlobalInfo.currentLocation.getAddress() != null && !GlobalInfo.currentLocation.getAddress().isEmpty())
                txtUserLocation.setText(GlobalInfo.currentLocation.getAddress());
            else
                txtUserLocation.setText(R.string.unknown_location);
        }
    }

    private void showTrashInfo(long trashId){
        Trash t = GlobalInfo.findTrashById(trashId);
        if(t == null)
            return;
        currentShowTrashId = trashId;
        userLocationView.setVisibility(View.GONE);
        trashView.setVisibility(View.VISIBLE);
        txtTrashName.setText(t.getTrashName(getContext()));
        txtTrashDesc.setText(t.getDescription());
    }

    private void getAllTrashInfo(){
        HttpApi.startRequest(new HttpApiJsonRequest(getActivity(), HttpApi.getApiUrl(HttpApi.PublicAPI.ALL_TRASHES), Request.Method.GET,
                null, null, new HttpApiJsonListener<TrashListResult>(TrashListResult.class) {
            @Override
            public void onResponse(TrashListResult data) {
                GlobalInfo.trashList = data.getTrashList();
                addTrashMarker();
            }
        }));
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        getContext().unregisterReceiver(locationReceiver);
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
}
