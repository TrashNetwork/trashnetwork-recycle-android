package happyyoung.trashnetwork.recycle.ui.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.adapter.CommodityPreviewAdapter;
import happyyoung.trashnetwork.recycle.model.CommodityPreview;
import happyyoung.trashnetwork.recycle.net.PublicResultCode;
import happyyoung.trashnetwork.recycle.net.http.HttpApi;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonListener;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonRequest;
import happyyoung.trashnetwork.recycle.net.model.result.CommodityPreviewListResult;
import happyyoung.trashnetwork.recycle.net.model.result.Result;
import happyyoung.trashnetwork.recycle.ui.activity.CreditMallDetailActivity;
import happyyoung.trashnetwork.recycle.util.DateTimeUtil;
import hugo.weaving.DebugLog;

public class CreditMallFragment extends Fragment implements CommodityPreviewAdapter.OnItemClickListener{
    private int MAX_REQUEST_COMMODITY = 15;

    private View rootView;
    @BindView(R.id.commodity_list)
    SuperRecyclerView commodityListView;
    @BindView(R.id.commodity_search_list)
    SuperRecyclerView commoditySearchListView;
    @BindView(R.id.txt_no_commodity)
    TextView txtNoCommodity;

    private MenuItem searchItem;
    private SearchView searchView;

    private CommodityPreviewAdapter adapter;
    private List<CommodityPreview> commodityList = new ArrayList<>();
    private Calendar endTime = Calendar.getInstance();
    private CommodityPreviewAdapter searchAdapter;
    private List<CommodityPreview> commoditySearchList = new ArrayList<>();
    private Calendar endTimeSearch = Calendar.getInstance();
    private String keyword;

    public CreditMallFragment() {
        // Required empty public constructor
    }

    public static CreditMallFragment newInstance(Context context) {
        CreditMallFragment fragment = new CreditMallFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_credit_mall, container, false);
        ButterKnife.bind(this, rootView);

        commodityListView.setLayoutManager(new LinearLayoutManager(getContext()));
        commodityListView.getRecyclerView().setNestedScrollingEnabled(false);
        commodityListView.getSwipeToRefresh().setColorSchemeResources(R.color.colorAccent);
        commodityListView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCommodity(true, null, endTime, commodityListView, adapter, commodityList);
            }
        });
        commodityListView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                refreshCommodity(false, null, endTime, commodityListView, adapter, commodityList);
            }
        }, -1);
        adapter = new CommodityPreviewAdapter(commodityList, this);
        commodityListView.setAdapter(adapter);

        commoditySearchListView.setLayoutManager(new LinearLayoutManager(getContext()));
        commoditySearchListView.getRecyclerView().setNestedScrollingEnabled(false);
        commoditySearchListView.getSwipeToRefresh().setColorSchemeResources(R.color.colorAccent);
        commoditySearchListView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCommodity(true, keyword, endTimeSearch, commoditySearchListView, searchAdapter, commoditySearchList);
            }
        });
        commoditySearchListView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                refreshCommodity(false, keyword, endTimeSearch, commoditySearchListView, searchAdapter, commoditySearchList);
            }
        }, -1);
        searchAdapter = new CommodityPreviewAdapter(commoditySearchList, this);
        commoditySearchListView.setAdapter(searchAdapter);

        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden){
            txtNoCommodity.setVisibility(View.GONE);
            commoditySearchListView.setVisibility(View.GONE);
            commodityListView.setVisibility(View.VISIBLE);
            searchItem.setVisible(true);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if(TextUtils.isEmpty(query)) {
                        searchView.clearFocus();
                        return false;
                    }
                    keyword = query;
                    commodityListView.setVisibility(View.GONE);
                    commoditySearchList.clear();
                    searchAdapter.notifyDataSetChanged();
                    commoditySearchListView.setVisibility(View.VISIBLE);
                    refreshCommodity(true, query, endTimeSearch, commoditySearchListView, searchAdapter, commoditySearchList);
                    searchView.clearFocus();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    onSearchHidden();
                    return false;
                }
            });

            refreshCommodity(true, null, endTime, commodityListView, adapter, commodityList);
        }else{
            commodityList.clear();
            commoditySearchList.clear();
            if(rootView != null){
                adapter.notifyDataSetChanged();
                searchAdapter.notifyDataSetChanged();
            }
            if(searchItem != null){
                searchView.onActionViewCollapsed();
                searchItem.setVisible(false);
                searchView.setOnQueryTextListener(null);
                searchView.setOnCloseListener(null);
            }
        }
        super.onHiddenChanged(hidden);
    }

    private void onSearchHidden(){
        searchView.onActionViewCollapsed();
        commoditySearchList.clear();
        searchAdapter.notifyDataSetChanged();
        commoditySearchListView.setVisibility(View.GONE);
        commodityListView.setVisibility(View.VISIBLE);
        if(!commodityList.isEmpty()){
            txtNoCommodity.setVisibility(View.GONE);
        }else{
            txtNoCommodity.setVisibility(View.VISIBLE);
        }
    }

    public boolean onBackPressed(){
        if(!searchView.isIconified()){
            onSearchHidden();
            return true;
        }
        return false;
    }

    private void refreshCommodity(final boolean refresh, String keyword, final Calendar tempEndTime, final SuperRecyclerView listView, final CommodityPreviewAdapter adapter, final List<CommodityPreview> tempCommodityList){
        if(refresh) {
            tempEndTime.setTimeInMillis(System.currentTimeMillis());
            listView.setRefreshing(true);
        }
        String url = "";
        if(TextUtils.isEmpty(keyword)){
            url = HttpApi.getApiUrl(HttpApi.CreditMallApi.COMMODITY_LIST, DateTimeUtil.getUnixTimestampStr(tempEndTime.getTime()), "" + MAX_REQUEST_COMMODITY);
        }else{
            try {
                url = HttpApi.getApiUrl(HttpApi.CreditMallApi.COMMODITY_LIST_BY_KEYWORD, URLEncoder.encode(keyword, "utf-8"), DateTimeUtil.getUnixTimestampStr(tempEndTime.getTime()), "" + MAX_REQUEST_COMMODITY);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        HttpApi.startRequest(new HttpApiJsonRequest(getContext(), url, Request.Method.GET, null, null,
                new HttpApiJsonListener<CommodityPreviewListResult>(CommodityPreviewListResult.class) {
                    @Override
                    public void onResponse() {
                        listView.setRefreshing(false);
                        listView.hideMoreProgress();
                    }

                    @Override
                    public void onDataResponse(CommodityPreviewListResult data) {
                        txtNoCommodity.setVisibility(View.GONE);
                        listView.getRecyclerView().setVisibility(View.VISIBLE);
                        if(refresh) {
                            tempCommodityList.clear();
                            adapter.notifyDataSetChanged();
                        }
                        for(CommodityPreview cp : data.getCommodityList()){
                            tempCommodityList.add(cp);
                            tempEndTime.setTimeInMillis(cp.getAddedTime().getTime() - 1000);
                            adapter.notifyItemInserted(tempCommodityList.size() - 1);
                        }
                        if(data.getCommodityList().size() < MAX_REQUEST_COMMODITY)
                            listView.setNumberBeforeMoreIsCalled(-1);
                        else
                            listView.setNumberBeforeMoreIsCalled(1);
                    }

                    @Override
                    public void onErrorResponse() {
                        if(refresh && tempCommodityList.isEmpty()){
                            txtNoCommodity.setVisibility(View.VISIBLE);
                            listView.getRecyclerView().setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public boolean onErrorDataResponse(int statusCode, Result errorInfo) {
                        if(errorInfo.getResultCode() == PublicResultCode.COMMODITY_NOT_FOUND){
                            if(!refresh)
                                listView.setNumberBeforeMoreIsCalled(-1);
                            return true;
                        }
                        return super.onErrorDataResponse(statusCode, errorInfo);
                    }
                }));
    }

    public void setSearchView(MenuItem searchItem, SearchView searchView){
        this.searchItem = searchItem;
        this.searchView = searchView;
    }

    @Override
    public void onItemClick(CommodityPreview cp, int position) {
        Intent intent = new Intent(getContext(), CreditMallDetailActivity.class);
        intent.putExtra(CreditMallDetailActivity.BUNDLE_KEY_COMMODITY_ID, cp.getCommodityId());
        startActivity(intent);
    }
}
