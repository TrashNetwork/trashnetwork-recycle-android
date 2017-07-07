package happyyoung.trashnetwork.recycle.ui.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.adapter.CreditRecordAdapter;
import happyyoung.trashnetwork.recycle.model.CreditRecord;
import happyyoung.trashnetwork.recycle.net.PublicResultCode;
import happyyoung.trashnetwork.recycle.net.http.HttpApi;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonListener;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonRequest;
import happyyoung.trashnetwork.recycle.net.model.result.CreditRecordListResult;
import happyyoung.trashnetwork.recycle.net.model.result.Result;
import happyyoung.trashnetwork.recycle.ui.widget.DateSelector;
import happyyoung.trashnetwork.recycle.util.DateTimeUtil;
import happyyoung.trashnetwork.recycle.util.GlobalInfo;

public class CreditRecordActivity extends AppCompatActivity {
    private static final int RECORD_REQUEST_LIMIT = 20;

    private DateSelector dateSelector;
    @BindView(R.id.txt_no_record) TextView txtNoRecord;
    @BindView(R.id.credit_record_list) SuperRecyclerView creditRecordListView;

    private List<CreditRecord> recordList = new ArrayList<>();
    private CreditRecordAdapter adapter;
    private Calendar endTime;
    private Calendar startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_record);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startTime = Calendar.getInstance();
        endTime = Calendar.getInstance();
        dateSelector = new DateSelector(this, endTime, new DateSelector.OnDateChangedListener() {
            @Override
            public void onDateChanged(Calendar newDate) {
                endTime = newDate;
                refreshCreditRecord(true, true);
            }
        });

        creditRecordListView.setLayoutManager(new LinearLayoutManager(this));
        creditRecordListView.getRecyclerView().setNestedScrollingEnabled(false);
        creditRecordListView.getSwipeToRefresh().setColorSchemeResources(R.color.colorAccent);
        creditRecordListView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                refreshCreditRecord(false, false);
            }
        }, -1);
        creditRecordListView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCreditRecord(true, false);
            }
        });

        adapter = new CreditRecordAdapter(this, recordList);
        creditRecordListView.setAdapter(adapter);
        refreshCreditRecord(true, false);
    }

    private void updateTime(){
        endTime.set(Calendar.HOUR_OF_DAY, 23);
        endTime.set(Calendar.MINUTE, 59);
        endTime.set(Calendar.SECOND, 59);
        startTime.set(endTime.get(Calendar.YEAR), endTime.get(Calendar.MONTH), endTime.get(Calendar.DATE),
                0, 0, 0);
    }

    private void refreshCreditRecord(final boolean refresh, final boolean dateChanged){
        if(refresh) {
            updateTime();
            dateSelector.setEnable(false);
            creditRecordListView.setRefreshing(true);
        }
        String url = HttpApi.getApiUrl(HttpApi.CreditRecordApi.QUERY_RECORD, DateTimeUtil.getUnixTimestampStr(startTime.getTime()),
                DateTimeUtil.getUnixTimestampStr(endTime.getTime()), "" + RECORD_REQUEST_LIMIT);
        HttpApi.startRequest(new HttpApiJsonRequest(this, url, Request.Method.GET, GlobalInfo.token, null,
                new HttpApiJsonListener<CreditRecordListResult>(CreditRecordListResult.class) {
                    @Override
                    public void onDataResponse(CreditRecordListResult data) {
                        showContent(true, refresh);
                        if(refresh){
                            recordList.clear();
                            adapter.notifyDataSetChanged();
                        }
                        for(CreditRecord cr : data.getCreditRecordList()){
                            recordList.add(cr);
                            adapter.notifyItemInserted(recordList.size() - 1);
                        }
                        if(data.getCreditRecordList().size() < RECORD_REQUEST_LIMIT)
                            creditRecordListView.setNumberBeforeMoreIsCalled(-1);
                        else
                            creditRecordListView.setNumberBeforeMoreIsCalled(1);
                    }

                    @Override
                    public void onErrorResponse() {
                        showContent(!recordList.isEmpty() && !dateChanged, refresh);
                    }

                    @Override
                    public boolean onErrorDataResponse(int statusCode, Result errorInfo) {
                        if(errorInfo.getResultCode() == PublicResultCode.CREDIT_RECORD_NOT_FOUND){
                            if(!refresh)
                                creditRecordListView.setNumberBeforeMoreIsCalled(-1);
                            else
                                return true;
                        }
                        return super.onErrorDataResponse(statusCode, errorInfo);
                    }
                }));
    }

    private void showContent(boolean hasContent, boolean refresh){
        dateSelector.setEnable(true);
        creditRecordListView.setRefreshing(false);
        creditRecordListView.hideMoreProgress();

        if(refresh && !hasContent){
            txtNoRecord.setVisibility(View.VISIBLE);
            creditRecordListView.getRecyclerView().setVisibility(View.INVISIBLE);
        }else if(refresh){
            txtNoRecord.setVisibility(View.GONE);
            creditRecordListView.getRecyclerView().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
