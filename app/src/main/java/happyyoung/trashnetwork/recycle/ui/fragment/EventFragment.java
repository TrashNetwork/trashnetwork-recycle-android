package happyyoung.trashnetwork.recycle.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import happyyoung.trashnetwork.recycle.Application;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.adapter.EventAdapter;
import happyyoung.trashnetwork.recycle.model.Event;
import happyyoung.trashnetwork.recycle.net.PublicResultCode;
import happyyoung.trashnetwork.recycle.net.http.HttpApi;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonListener;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonRequest;
import happyyoung.trashnetwork.recycle.net.model.result.EventListResult;
import happyyoung.trashnetwork.recycle.net.model.result.Result;
import happyyoung.trashnetwork.recycle.ui.activity.WebActivity;
import happyyoung.trashnetwork.recycle.util.DateTimeUtil;

public class EventFragment extends Fragment {
    private static final int EVENT_REQUEST_LIMIT = 10;

    private View rootView;
    @BindView(R.id.txt_no_event)
    TextView txtNoEvent;
    @BindView(R.id.event_list)
    SuperRecyclerView eventListView;

    private List<Event> eventList = new ArrayList<>();
    private EventAdapter adapter;
    private Calendar endTime;

    public EventFragment() {
        // Required empty public constructor
    }

    public static EventFragment newInstance(Context context) {
        EventFragment fragment = new EventFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_event, container, false);
        ButterKnife.bind(this, rootView);
        endTime = Calendar.getInstance();

        eventListView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventListView.getRecyclerView().setNestedScrollingEnabled(false);
        eventListView.getSwipeToRefresh().setColorSchemeResources(R.color.colorAccent);
        eventListView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshEvent(true);
            }
        });

        eventListView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                refreshEvent(false);
            }
        }, -1);

        adapter = new EventAdapter(eventList, new EventAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Event event, int pos) {
                if(!TextUtils.isEmpty(event.getUrl())){
                    Intent intent = new Intent(getContext(), WebActivity.class);
                    intent.putExtra(WebActivity.BUNDLE_KEY_URL, Application.WEBPAGE_BASE_URL + event.getUrl());
                    startActivity(intent);
                }
            }
        });

        eventListView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden)
            refreshEvent(true);
        super.onHiddenChanged(hidden);
    }

    private void refreshEvent(final boolean refresh){
        if(refresh){
            eventListView.setRefreshing(true);
            endTime = Calendar.getInstance();
        }

        String url = HttpApi.getApiUrl(HttpApi.EventApi.EVENTS, DateTimeUtil.getUnixTimestampStr(endTime.getTime()), "" + EVENT_REQUEST_LIMIT);
        HttpApi.startRequest(new HttpApiJsonRequest(getContext(), url, Request.Method.GET, null, null, new HttpApiJsonListener<EventListResult>(EventListResult.class) {
            @Override
            public void onDataResponse(EventListResult data) {
                showContentView(true, refresh);
                if(refresh){
                    eventList.clear();
                    adapter.notifyDataSetChanged();
                }
                for(Event e : data.getEventList()){
                    eventList.add(e);
                    endTime.setTimeInMillis(e.getReleaseTime().getTime() - 1000);
                    adapter.notifyItemInserted(eventList.size() - 1);
                }
                if(data.getEventList().size() < EVENT_REQUEST_LIMIT)
                    eventListView.setNumberBeforeMoreIsCalled(-1);
                else
                    eventListView.setNumberBeforeMoreIsCalled(1);
            }

            @Override
            public void onErrorResponse() {
                showContentView(false, refresh);
            }

            @Override
            public boolean onErrorDataResponse(int statusCode, Result errorInfo) {
                if(errorInfo.getResultCode() == PublicResultCode.EVENT_NOT_FOUND){
                    if(!refresh)
                        eventListView.setNumberBeforeMoreIsCalled(-1);
                    else
                        return true;
                }
                return super.onErrorDataResponse(statusCode, errorInfo);
            }
        }));
    }

    private void showContentView(boolean hasContent, boolean refresh){
        eventListView.setRefreshing(false);
        eventListView.hideMoreProgress();
        if(refresh && !hasContent){
            eventListView.getRecyclerView().setVisibility(View.INVISIBLE);
            txtNoEvent.setVisibility(View.VISIBLE);
        }else if(refresh && hasContent){
            eventListView.getRecyclerView().setVisibility(View.VISIBLE);
            txtNoEvent.setVisibility(View.GONE);
        }
    }
}
