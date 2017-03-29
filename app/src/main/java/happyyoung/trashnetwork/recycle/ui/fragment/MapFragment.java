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

import com.android.volley.Request;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import happyyoung.trashnetwork.recycle.Application;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.model.Trash;
import happyyoung.trashnetwork.recycle.net.http.HttpApi;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonListener;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonRequest;
import happyyoung.trashnetwork.recycle.net.model.result.Result;
import happyyoung.trashnetwork.recycle.net.model.result.TrashListResult;
import happyyoung.trashnetwork.recycle.util.GlobalInfo;
import happyyoung.trashnetwork.recycle.util.ImageUtil;

public class MapFragment extends Fragment {
    private static final String LOG_TAG_GEO_CODER = "GeoCoder";
    private static final String BUNDLE_KEY_MARKER_TYPE = "MarkerType";
    private static final String BUNDLE_KEY_TRASH_ID = "TrashID";
    private static final int MARKER_TYPE_USER = 1;
    private static final int MARKER_TYPE_TRASH = 2;

    private View rootView;
    @BindView(R.id.bmap_view) MapView mapView;
    @BindView(R.id.user_location_area) View userLocationView;
    @BindView(R.id.txt_user_location) TextView txtUserLocation;
    @BindView(R.id.trash_view_area) View trashView;
    @BindView(R.id.txt_trash_name) TextView txtTrashName;
    @BindView(R.id.txt_trash_desc) TextView txtTrashDesc;

    private BaiduMap baiduMap;
    private boolean mapCenterFlag = false;

    private MarkerOptions userMarkerOptions;
    private Marker userMarker;
    private GeoCoder userLocationGeoCoder;
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

        baiduMap = mapView.getMap();
        baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(18));
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                showUserLocation(true);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle bundle = marker.getExtraInfo();
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
        userMarkerOptions = new MarkerOptions()
                .draggable(false)
                .alpha((float) 0.9)
                .icon(BitmapDescriptorFactory.fromBitmap(ImageUtil.getBitmapFromDrawable(getContext(), R.drawable.ic_location_red)));
        userLocationGeoCoder = GeoCoder.newInstance();
        userLocationGeoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {}

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if(reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR){
                    showGeoCoderError(reverseGeoCodeResult);
                    txtUserLocation.setText(R.string.unknown_location);
                    return;
                }
                txtUserLocation.setText(reverseGeoCodeResult.getAddress());
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
        baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(
                new LatLng(GlobalInfo.currentLocation.getLatitude(),
                        GlobalInfo.currentLocation.getLongitude())
        ));
    }

    private void addTrashMarker(){
        MarkerOptions trashMarkerOpts = new MarkerOptions()
                .draggable(false)
                .alpha(0.9f)
                .icon(BitmapDescriptorFactory.fromBitmap(
                        ImageUtil.getBitmapFromDrawable(getContext(), R.drawable.ic_delete_green_32dp)));
        for(Trash t : GlobalInfo.trashList){
            if(!t.isBottleRecycle())
                continue;
            trashMarkerOpts.position(new LatLng(t.getLatitude(), t.getLongitude()));
            Marker trashMarker = (Marker) baiduMap.addOverlay(trashMarkerOpts);
            Bundle bundle = new Bundle();
            bundle.putInt(BUNDLE_KEY_MARKER_TYPE, MARKER_TYPE_TRASH);
            bundle.putLong(BUNDLE_KEY_TRASH_ID, t.getTrashId());
            trashMarker.setExtraInfo(bundle);
        }
    }

    private void updateUserLocation(){
        LatLng pos = new LatLng(GlobalInfo.currentLocation.getLatitude(), GlobalInfo.currentLocation.getLongitude());
        if(userMarker == null){
            userMarkerOptions.position(pos);
            userMarker = (Marker) baiduMap.addOverlay(userMarkerOptions);
            Bundle bundle = new Bundle();
            bundle.putInt(BUNDLE_KEY_MARKER_TYPE, MARKER_TYPE_USER);
            userMarker.setExtraInfo(bundle);
        }else{
            userMarker.setPosition(pos);
        }
        if(!mapCenterFlag){
            baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(pos));
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
            LatLng pos = new LatLng(GlobalInfo.currentLocation.getLatitude(), GlobalInfo.currentLocation.getLongitude());
            userLocationGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(pos));
        }
    }

    private void showTrashInfo(long trashId){
        Trash t = GlobalInfo.findTrashById(trashId);
        if(t == null)
            return;
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

    private void showGeoCoderError(ReverseGeoCodeResult reverseGeoCodeResult){
        if(reverseGeoCodeResult == null){
            Log.e(LOG_TAG_GEO_CODER, "Geo coder error");
        }else if(reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR){
            Log.e(LOG_TAG_GEO_CODER, "Geo coder error code: " + reverseGeoCodeResult.error);
        }
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        getContext().unregisterReceiver(locationReceiver);
        rootView = null;
        userLocationGeoCoder.destroy();
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

    private class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUserLocation();
        }
    }
}
