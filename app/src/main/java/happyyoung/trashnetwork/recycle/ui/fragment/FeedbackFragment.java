package happyyoung.trashnetwork.recycle.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import butterknife.OnClick;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.adapter.FeedbackAdapter;
import happyyoung.trashnetwork.recycle.model.Feedback;
import happyyoung.trashnetwork.recycle.net.PublicResultCode;
import happyyoung.trashnetwork.recycle.net.http.HttpApi;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonListener;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonRequest;
import happyyoung.trashnetwork.recycle.net.model.result.FeedbackListResult;
import happyyoung.trashnetwork.recycle.net.model.result.Result;
import happyyoung.trashnetwork.recycle.ui.activity.NewFeedbackActivity;
import happyyoung.trashnetwork.recycle.ui.widget.DateSelector;
import happyyoung.trashnetwork.recycle.util.DateTimeUtil;
import happyyoung.trashnetwork.recycle.util.GlobalInfo;

public class FeedbackFragment extends Fragment {
    private static final int FEEDBACK_REQUEST_LIMIT = 20;

    private View rootView;
    @BindView(R.id.txt_no_feedback)
    TextView txtNoFeedback;
    @BindView(R.id.feedback_list)
    SuperRecyclerView feedbackListView;
    @BindView(R.id.btn_post_feedback)
    FloatingActionButton btnPostFeedback;
    private DateSelector dateSelector;

    private List<Feedback> feedbackList = new ArrayList<>();
    private FeedbackAdapter adapter;
    private Calendar endTime;
    private Calendar startTime;

    public FeedbackFragment() {
        // Required empty public constructor
    }

    public static FeedbackFragment newInstance(Context context) {
        FeedbackFragment fragment = new FeedbackFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_feedback, container, false);
        ButterKnife.bind(this, rootView);
        endTime = Calendar.getInstance();
        startTime = Calendar.getInstance();
        dateSelector = new DateSelector(rootView, endTime, new DateSelector.OnDateChangedListener() {
            @Override
            public void onDateChanged(Calendar newDate) {
                endTime = newDate;
                refreshFeedback(true, true);
            }
        });

        feedbackListView.setLayoutManager(new LinearLayoutManager(getContext()));
        feedbackListView.getRecyclerView().setNestedScrollingEnabled(false);
        feedbackListView.getSwipeToRefresh().setColorSchemeResources(R.color.colorAccent);
        feedbackListView.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && btnPostFeedback.isShown())
                    btnPostFeedback.hide();
                else if (dy < 0 && !btnPostFeedback.isShown())
                    btnPostFeedback.show();
            }
        });
        feedbackListView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFeedback(true, false);
            }
        });

        feedbackListView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                refreshFeedback(false, false);
            }
        }, -1);

        adapter = new FeedbackAdapter(getContext(), feedbackList);
        feedbackListView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            txtNoFeedback.setVisibility(View.GONE);
            refreshFeedback(true, false);
        }else{
            feedbackList.clear();
            if(rootView != null)
                adapter.notifyDataSetChanged();
        }
        super.onHiddenChanged(hidden);
    }

    @OnClick(R.id.btn_post_feedback)
    void onBtnPostFeedbackClick(View v) {
        startActivity(new Intent(getContext(), NewFeedbackActivity.class));
    }

    private void updateTime() {
        endTime.set(Calendar.HOUR_OF_DAY, 23);
        endTime.set(Calendar.MINUTE, 59);
        endTime.set(Calendar.SECOND, 59);
        startTime.set(endTime.get(Calendar.YEAR), endTime.get(Calendar.MONTH), endTime.get(Calendar.DATE),
                0, 0, 0);
    }

    private void refreshFeedback(final boolean refresh, final boolean dateChanged) {
        if (refresh) {
            updateTime();
            dateSelector.setEnable(false);
            feedbackListView.setRefreshing(true);
        }

        String url = HttpApi.getApiUrl(HttpApi.FeedbackApi.QUERY_FEEDBACK, DateTimeUtil.getUnixTimestampStr(startTime.getTime()),
                DateTimeUtil.getUnixTimestampStr(endTime.getTime()), "" + FEEDBACK_REQUEST_LIMIT);
        HttpApi.startRequest(new HttpApiJsonRequest(getActivity(), url, Request.Method.GET, GlobalInfo.token, null, new HttpApiJsonListener<FeedbackListResult>(FeedbackListResult.class) {
            @Override
            public void onDataResponse(FeedbackListResult data) {
                showContentView(true, refresh);
                if (refresh) {
                    feedbackList.clear();
                    adapter.notifyDataSetChanged();
                }
                for (Feedback fb : data.getFeedbackList()) {
                    feedbackList.add(fb);
                    endTime.setTimeInMillis(fb.getFeedbackTime().getTime() - 1000);
                    adapter.notifyItemInserted(feedbackList.size() - 1);
                }
                if (data.getFeedbackList().size() < FEEDBACK_REQUEST_LIMIT)
                    feedbackListView.setNumberBeforeMoreIsCalled(-1);
                else
                    feedbackListView.setNumberBeforeMoreIsCalled(1);
            }

            @Override
            public void onErrorResponse() {
                showContentView(!feedbackList.isEmpty() && !dateChanged, refresh);
            }

            @Override
            public boolean onErrorDataResponse(int statusCode, Result errorInfo) {
                if (errorInfo.getResultCode() == PublicResultCode.FEEDBACK_NOT_FOUND) {
                    if (!refresh)
                        feedbackListView.setNumberBeforeMoreIsCalled(-1);
                    else
                        return true;
                }
                return super.onErrorDataResponse(statusCode, errorInfo);
            }
        }));
    }

    private void showContentView(boolean hasContent, boolean refresh) {
        feedbackListView.setRefreshing(false);
        feedbackListView.hideMoreProgress();
        dateSelector.setEnable(true);
        if (refresh && !hasContent) {
            feedbackListView.getRecyclerView().setVisibility(View.INVISIBLE);
            txtNoFeedback.setVisibility(View.VISIBLE);
        } else if (refresh && hasContent) {
            feedbackListView.getRecyclerView().setVisibility(View.VISIBLE);
            txtNoFeedback.setVisibility(View.GONE);
        }
    }
}
