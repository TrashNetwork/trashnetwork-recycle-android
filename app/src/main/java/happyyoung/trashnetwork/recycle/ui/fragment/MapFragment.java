package happyyoung.trashnetwork.recycle.ui.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.route.WalkStep;
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
import happyyoung.trashnetwork.recycle.util.DateTimeUtil;
import happyyoung.trashnetwork.recycle.util.GlobalInfo;
import happyyoung.trashnetwork.recycle.util.ImageUtil;

public class MapFragment extends Fragment {
    private static final String BUNDLE_KEY_MARKER_TYPE = "MarkerType";
    private static final String BUNDLE_KEY_RECYCLE_POINT_ID = "RecyclePointID";
    private static final int MARKER_TYPE_USER = 1;
    private static final int MARKER_TYPE_RECYCLE_POINT = 2;

    private View rootView;
    @BindView(R.id.amap_view)
    MapView mapView;
    @BindView(R.id.user_location_area)
    View userLocationView;
    @BindView(R.id.txt_user_location)
    TextView txtUserLocation;
    @BindView(R.id.txt_user_update_time)
    TextView txtUserUpdateTime;
    @BindView(R.id.recycle_point_view_area)
    View recyclePointView;
    @BindView(R.id.txt_recycle_point_name)
    TextView txtRecyclePointName;
    @BindView(R.id.txt_recycle_point_desc)
    TextView txtRecyclePointDesc;
    @BindView(R.id.recycle_point_extra_info_view)
    View recyclePointExtraInfoView;
    @BindView(R.id.btn_recycle)
    ImageButton btnRecycle;
    @BindView(R.id.btn_refresh)
    FloatingActionButton btnRefresh;
    @BindView(R.id.txt_bottle_num)
    TextView txtBottleNum;
    @BindView(R.id.recycle_point_walk_view)
    View recyclePointWalkView;
    @BindView(R.id.txt_distance)
    TextView txtWalkDistance;
    @BindView(R.id.txt_time_elapse)
    TextView txtWalkTime;

    private boolean mapCenterFlag = false;
    private long currentShowRecyclePointId = -1;
    private AMap amap;
    private Marker userMarker;
    private List<Marker> recyclePointMarkers = new ArrayList<>();
    private LocationReceiver locationReceiver;
    private UserInfoReceiver userInfoReceiver;
    private Marker walkStartPointMarker;
    private RouteSearch routeSearch;
    private Polyline walkPathLine;

    private Bitmap icTrash;
    private Bitmap icRedPacket;
    private Bitmap icLocation;

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
        if (rootView != null)
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
                if (bundle == null)
                    return false;
                switch (bundle.getInt(BUNDLE_KEY_MARKER_TYPE)) {
                    case MARKER_TYPE_USER:
                        showUserLocation(true);
                        break;
                    case MARKER_TYPE_RECYCLE_POINT:
                        showRecyclePointInfo(bundle.getLong(BUNDLE_KEY_RECYCLE_POINT_ID));
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
        routeSearch = new RouteSearch(getContext());

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

        icLocation = ImageUtil.getBitmapFromDrawable(getContext(), R.drawable.ic_location_red);
        icTrash = ImageUtil.getBitmapFromDrawable(getContext(), R.drawable.ic_delete_green_32dp);
        Bitmap tempIcRedPacket = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_redpacket);
        double ratio = (double)tempIcRedPacket.getHeight() / tempIcRedPacket.getWidth();
        icRedPacket = Bitmap.createScaledBitmap(tempIcRedPacket, getResources().getDimensionPixelSize(R.dimen.small_icon_size),
                (int) (getResources().getDimensionPixelSize(R.dimen.small_icon_size) * ratio), false);
        tempIcRedPacket.recycle();

        getAllRecyclePoint();
        return rootView;
    }

    @OnClick(R.id.user_location_area)
    void onUserLocationViewClick(View v) {
        amap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition(new LatLng(GlobalInfo.currentLocation.getLatitude(),
                        GlobalInfo.currentLocation.getLongitude()), 18, 0, 0)));
    }

    @OnClick(R.id.recycle_point_view_area)
    void onRecyclePointViewClick(View v) {
        RecyclePoint t = GlobalInfo.findRecyclePointById(currentShowRecyclePointId);
        if (t == null)
            return;
        amap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition(new LatLng(t.getLatitude(), t.getLongitude()), 18, 0, 0)));
    }

    @OnClick(R.id.btn_refresh)
    void onBtnRefreshClick(View v) {
        getAllRecyclePoint();
    }

    @OnClick(R.id.btn_recycle)
    void onBtnRecycleClick(View v) {
        final RecyclePointCleanRequest req = new RecyclePointCleanRequest(currentShowRecyclePointId);
        HttpApi.startRequest(new HttpApiJsonRequest(getActivity(), HttpApi.getApiUrl(HttpApi.RecycleRecordApi.NEW_RECORD), Request.Method.POST, GlobalInfo.token, req,
                new HttpApiJsonListener<Result>(Result.class) {
                    @Override
                    public void onDataResponse(Result data) {
                        Toast.makeText(getContext(), data.getMessage(), Toast.LENGTH_SHORT).show();
                        RecyclePoint rp = GlobalInfo.findRecyclePointById(req.getRecyclePointId());
                        if (rp == null)
                            return;
                        if (rp.isBottleRecycle())
                            rp.setBottleNum(0);
                        if (currentShowRecyclePointId == req.getRecyclePointId())
                            showRecyclePointInfo(req.getRecyclePointId());
                    }
                }));
    }

    private void addRecyclePointMarker() {
        MarkerOptions recyclePointMarkerOpt = new MarkerOptions()
                .draggable(false)
                .alpha(0.9f);

        for (RecyclePoint rp : GlobalInfo.recyclePointList) {
            if (!rp.isBottleRecycle())
                continue;
            recyclePointMarkerOpt.position(new LatLng(rp.getLatitude(), rp.getLongitude()));
            if (rp.isRedPacketPoint()) {
                recyclePointMarkerOpt.icon(BitmapDescriptorFactory.fromBitmap(icRedPacket));
            } else {
                recyclePointMarkerOpt.icon(BitmapDescriptorFactory.fromBitmap(icTrash));
            }
            Marker recyclePointMarker = amap.addMarker(recyclePointMarkerOpt);
            Bundle bundle = new Bundle();
            bundle.putInt(BUNDLE_KEY_MARKER_TYPE, MARKER_TYPE_RECYCLE_POINT);
            bundle.putLong(BUNDLE_KEY_RECYCLE_POINT_ID, rp.getRecyclePointId());
            recyclePointMarker.setObject(bundle);
            recyclePointMarkers.add(recyclePointMarker);
            if (rp.getRecyclePointId() == currentShowRecyclePointId && recyclePointView.getVisibility() == View.VISIBLE)
                showRecyclePointInfo(rp.getRecyclePointId());
        }
    }

    private void updateUserLocation() {
        LatLng pos = new LatLng(GlobalInfo.currentLocation.getLatitude(), GlobalInfo.currentLocation.getLongitude());
        if (userMarker == null) {
            MarkerOptions userMarkerOptions = new MarkerOptions()
                    .alpha(0.9f)
                    .icon(BitmapDescriptorFactory.fromBitmap(icLocation))
                    .position(pos);
            userMarker = amap.addMarker(userMarkerOptions);
            Bundle bundle = new Bundle();
            bundle.putInt(BUNDLE_KEY_MARKER_TYPE, MARKER_TYPE_USER);
            userMarker.setObject(bundle);
        } else {
            userMarker.setPosition(pos);
        }
        if (!mapCenterFlag) {
            amap.moveCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition(pos, 18, 0, 0)
            ));
            mapCenterFlag = true;
        }
        if (recyclePointView.getVisibility() == View.VISIBLE)
            return;
        showUserLocation(false);
    }

    private void showUserLocation(boolean fromUser) {
        if (GlobalInfo.currentLocation == null)
            return;
        recyclePointView.setVisibility(View.GONE);
        if (userLocationView.getVisibility() != View.VISIBLE || !fromUser) {
            userLocationView.setVisibility(View.VISIBLE);
            txtUserUpdateTime.setText(DateTimeUtil.convertTimestamp(getContext(), GlobalInfo.currentLocation.getUpdateTime(), true, true, true));
            if (GlobalInfo.currentLocation.getAddress() != null && !GlobalInfo.currentLocation.getAddress().isEmpty())
                txtUserLocation.setText(GlobalInfo.currentLocation.getAddress());
            else
                txtUserLocation.setText(R.string.unknown_location);
        }
    }

    private void showRecyclePointInfo(long recyclePointId) {
        RecyclePoint rp = GlobalInfo.findRecyclePointById(recyclePointId);
        if (rp == null)
            return;
        currentShowRecyclePointId = recyclePointId;
        userLocationView.setVisibility(View.GONE);
        recyclePointView.setVisibility(View.VISIBLE);
        txtRecyclePointName.setText(rp.getRecyclePointName(getContext()));
        txtRecyclePointDesc.setText(rp.getDescription());
        recyclePointWalkView.setVisibility(View.VISIBLE);
        searchWalkPath(rp);
        if (GlobalInfo.user != null && GlobalInfo.user.getUserId().equals(rp.getOwnerId())) {
            recyclePointExtraInfoView.setVisibility(View.VISIBLE);
            if (rp.getBottleNum() != null)
                txtBottleNum.setText(String.valueOf(rp.getBottleNum()));
            btnRecycle.setVisibility(View.VISIBLE);
        } else {
            recyclePointExtraInfoView.setVisibility(View.GONE);
            btnRecycle.setVisibility(View.GONE);
        }
    }

    private void getAllRecyclePoint() {
        btnRefresh.setEnabled(false);
        HttpApi.startRequest(new HttpApiJsonRequest(getActivity(), HttpApi.getApiUrl(HttpApi.RecyclePointApi.ALL_POINTS), Request.Method.GET,
                GlobalInfo.token, null, new HttpApiJsonListener<RecyclePointListResult>(RecyclePointListResult.class) {
            @Override
            public void onResponse() {
                btnRefresh.setEnabled(true);
            }

            @Override
            public void onDataResponse(RecyclePointListResult data) {
                for (Marker m : recyclePointMarkers)
                    m.remove();
                recyclePointMarkers.clear();
                GlobalInfo.recyclePointList = data.getRecyclePointList();
                addRecyclePointMarker();
            }
        }));
    }

    private void searchWalkPath(final RecyclePoint rp) {
        if (GlobalInfo.currentLocation == null)
            return;
        final LatLng startPos = new LatLng(GlobalInfo.currentLocation.getLatitude(), GlobalInfo.currentLocation.getLongitude());
        routeSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
            }

            @Override
            public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
            }

            @Override
            @SuppressWarnings("deprecation")
            public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int rCode) {
                if (rCode != 1000) {
                    Log.e("AMap route search", "route search failed, response code: " + rCode);
                    return;
                }
                if (walkStartPointMarker == null) {
                    MarkerOptions opt = new MarkerOptions()
                            .draggable(false)
                            .icon(BitmapDescriptorFactory.fromBitmap(ImageUtil.getBitmapFromDrawable(getContext(), R.drawable.ic_source_start_orange_40dp)))
                            .position(startPos);
                    walkStartPointMarker = amap.addMarker(opt);
                } else {
                    walkStartPointMarker.setPosition(startPos);
                }
                WalkPath path = walkRouteResult.getPaths().get(0);
                List<LatLng> polyLinePoints = new ArrayList<>();
                polyLinePoints.add(startPos);
                for (WalkStep ws : path.getSteps()) {
                    for (LatLonPoint llp : ws.getPolyline()) {
                        polyLinePoints.add(new LatLng(llp.getLatitude(), llp.getLongitude()));
                    }
                }
                polyLinePoints.add(new LatLng(rp.getLatitude(), rp.getLongitude()));
                if (walkPathLine == null) {
                    PolylineOptions opt = new PolylineOptions()
                            .transparency(0.9f)
                            .width(15)
                            .addAll(polyLinePoints)
                            .color(getContext().getResources().getColor(R.color.teal_500));
                    walkPathLine = amap.addPolyline(opt);
                } else {
                    walkPathLine.setPoints(polyLinePoints);
                }
                if (rp.getRecyclePointId() != currentShowRecyclePointId || recyclePointView.getVisibility() != View.VISIBLE)
                    return;
                float distance = path.getDistance();
                int minutes = (int) (path.getDuration() / 60);
                int second = (int) (path.getDuration() - minutes * 60);
                recyclePointWalkView.setVisibility(View.VISIBLE);
                txtWalkDistance.setText(String.format(getString(R.string.distance_meter_format), distance));
                txtWalkTime.setText(String.format(getString(R.string.time_elapse_min_sec_format), minutes, second));
            }
        });
        LatLonPoint startLlp = new LatLonPoint(startPos.latitude, startPos.longitude);
        LatLonPoint endLlp = new LatLonPoint(rp.getLatitude(), rp.getLongitude());
        RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(new RouteSearch.FromAndTo(startLlp, endLlp));
        routeSearch.calculateWalkRouteAsyn(query);
    }

    @Override
    public void onDestroy() {
        icLocation.recycle();
        icRedPacket.recycle();
        icTrash.recycle();
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
