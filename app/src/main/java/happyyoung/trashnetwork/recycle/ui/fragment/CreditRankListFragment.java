package happyyoung.trashnetwork.recycle.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import happyyoung.trashnetwork.recycle.Application;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.adapter.CreditRankAdapter;
import happyyoung.trashnetwork.recycle.model.CreditRank;
import happyyoung.trashnetwork.recycle.net.http.HttpApi;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonListener;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonRequest;
import happyyoung.trashnetwork.recycle.net.model.result.CreditRankListResult;
import happyyoung.trashnetwork.recycle.util.DateTimeUtil;
import happyyoung.trashnetwork.recycle.util.GlobalInfo;

public class CreditRankListFragment extends Fragment {
    public static final int RANK_TYPE_DAY = 1;
    public static final int RANK_TYPE_WEEK = 2;
    private int rankType = RANK_TYPE_DAY;

    private View rootView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.txt_no_rank)
    TextView txtNoRank;
    @BindView(R.id.txt_update_time)
    TextView txtUpdateTime;
    @BindView(R.id.txt_credit_delta)
    TextView txtUserCredit;
    @BindView(R.id.txt_user_rank)
    TextView txtUserRank;
    @BindView(R.id.user_rank_view)
    View userRankView;
    @BindView(R.id.credit_rank_list)
    RecyclerView creditRankListView;
    private CreditRankAdapter adapter;
    private List<CreditRank> rankList = new ArrayList<>();

    private UserStatusReceiver userStatusReceiver;

    public CreditRankListFragment() {
        // Required empty public constructor
    }

    public static CreditRankListFragment newInstance(Context context, int rankType) {
        CreditRankListFragment fragment = new CreditRankListFragment();
        fragment.rankType = rankType;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_credit_rank_list, container, false);
        ButterKnife.bind(this, rootView);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRank();
            }
        });
        adapter = new CreditRankAdapter(getContext(), rankList);
        creditRankListView.setNestedScrollingEnabled(false);
        creditRankListView.setLayoutManager(new LinearLayoutManager(getContext()));
        creditRankListView.setAdapter(adapter);

        userStatusReceiver = new UserStatusReceiver();
        IntentFilter filter = new IntentFilter(Application.ACTION_LOGIN);
        filter.addCategory(getContext().getPackageName());
        getContext().registerReceiver(userStatusReceiver, filter);
        filter = new IntentFilter(Application.ACTION_LOGOUT);
        filter.addCategory(getContext().getPackageName());
        getContext().registerReceiver(userStatusReceiver, filter);
        filter = new IntentFilter(Application.ACTION_USER_UPDATE);
        filter.addCategory(getContext().getPackageName());
        getContext().registerReceiver(userStatusReceiver, filter);

        updateRank();
        return rootView;
    }

    @SuppressWarnings("deprecation")
    private void updateRank(){
        String url = HttpApi.getApiUrl(HttpApi.CreditRankApi.DAILY_RANK);
        if(rankType == RANK_TYPE_WEEK)
            url = HttpApi.getApiUrl(HttpApi.CreditRankApi.WEEKLY_RANK);
        refreshLayout.setRefreshing(true);
        HttpApi.startRequest(new HttpApiJsonRequest(getActivity(), url, Request.Method.GET, GlobalInfo.token, null,
                new HttpApiJsonListener<CreditRankListResult>(CreditRankListResult.class) {
                    @Override
                    public void onResponse() {
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onDataResponse(CreditRankListResult data) {
                        txtUpdateTime.setText(String.format(getString(R.string.update_time_format),
                                DateTimeUtil.convertTimestamp(getContext(), data.getUpdateTime(), true, true, false)));
                        if(data.getRankList().isEmpty()) {
                            showContent(true);
                            return;
                        }
                        else
                            showContent(false);
                        if(data.getRank() == null)
                            userRankView.setVisibility(View.GONE);
                        else{
                            userRankView.setVisibility(View.VISIBLE);
                            if(data.getRank() < 0) {
                                txtUserRank.setText(getString(R.string.tip_you_not_in_rank));
                                txtUserCredit.setVisibility(View.INVISIBLE);
                            }
                            else{
                                txtUserCredit.setVisibility(View.VISIBLE);
                                String text = String.format(getString(R.string.tip_your_rank_format), data.getRank());
                                txtUserRank.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
                                String creditText = String.valueOf(data.getCredit());
                                if(data.getCredit() >= 0)
                                    creditText = "+" + creditText;
                                txtUserCredit.setText(creditText);
                            }
                        }
                        rankList.clear();
                        rankList.addAll(data.getRankList());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onErrorResponse() {
                        showContent(true);
                    }
                }));
    }

    private void showContent(boolean noRank){
        if(noRank){
            userRankView.setVisibility(View.GONE);
            txtNoRank.setVisibility(View.VISIBLE);
            creditRankListView.setVisibility(View.INVISIBLE);
        }else{
            userRankView.setVisibility(View.VISIBLE);
            txtNoRank.setVisibility(View.GONE);
            creditRankListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(userStatusReceiver);
        super.onDestroy();
    }

    private class UserStatusReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            updateRank();
        }
    }
}
