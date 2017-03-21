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

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
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
import happyyoung.trashnetwork.recycle.Application;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.model.UserLocation;
import happyyoung.trashnetwork.recycle.util.GlobalInfo;
import happyyoung.trashnetwork.recycle.util.GsonUtil;
import happyyoung.trashnetwork.recycle.util.ImageUtil;

public class MapFragment extends Fragment {
    private static final String LOG_TAG_GEO_CODER = "GeoCoder";

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

        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
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

        locationReceiver = new LocationReceiver();
        IntentFilter filter = new IntentFilter(Application.ACTION_LOCATION);
        filter.addCategory(getContext().getPackageName());
        getContext().registerReceiver(locationReceiver, filter);

        return rootView;
    }

    private void showUserLocation(){
        if(GlobalInfo.currentLocation == null || rootView == null)
            return;
        if(userMarker != null)
            userMarker.remove();
        LatLng pos = new LatLng(GlobalInfo.currentLocation.getLatitude(), GlobalInfo.currentLocation.getLongitude());
        userMarkerOptions.position(pos);
        userMarker = (Marker) baiduMap.addOverlay(userMarkerOptions);
        if(!mapCenterFlag){
            baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(pos));
            mapCenterFlag = true;
        }
        if(trashView.getVisibility() == View.VISIBLE)
            return;
        userLocationView.setVisibility(View.VISIBLE);
        userLocationGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(pos));
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
            showUserLocation();
        }
    }
}
