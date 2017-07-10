package happyyoung.trashnetwork.recycle.ui.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.content.Context;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.content.res.Resources.Theme;

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
import happyyoung.trashnetwork.recycle.adapter.OrderAdapter;
import happyyoung.trashnetwork.recycle.model.Order;
import happyyoung.trashnetwork.recycle.net.PublicResultCode;
import happyyoung.trashnetwork.recycle.net.http.HttpApi;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonListener;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonRequest;
import happyyoung.trashnetwork.recycle.net.model.result.OrderListResult;
import happyyoung.trashnetwork.recycle.net.model.result.Result;
import happyyoung.trashnetwork.recycle.ui.widget.DateRangeSelector;
import happyyoung.trashnetwork.recycle.util.DateTimeUtil;
import happyyoung.trashnetwork.recycle.util.GlobalInfo;

public class OrderActivity extends AppCompatActivity {
    private static final int REQUEST_MAX_ORDER = 15;
    private static String STATUS_IN_PROGRESS = "in_progress";
    private static String STATUS_DELIVERING = "delivering";
    private static String STATUS_CANCELLED = "cancelled";
    private static String STATUS_FINISHED = "finished";

    @BindView(R.id.spinner_order)
    Spinner spinner;
    @BindView(R.id.order_list)
    SuperRecyclerView orderListView;
    @BindView(R.id.widget_date_range_selector)
    View dateRangeSelectorView;
    @BindView(R.id.txt_no_order)
    TextView txtNoOrder;

    private String orderStatus = STATUS_IN_PROGRESS;
    private List<Order> orderList = new ArrayList<>();
    private OrderAdapter adapter;
    private Calendar startTime;
    private Calendar endTime;
    private DateRangeSelector dateRangeSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        dateRangeSelectorView.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinner.setAdapter(new OrderStatusSpinnerAdapter(
                toolbar.getContext(),
                getResources().getStringArray(R.array.order_status)));

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txtNoOrder.setVisibility(View.GONE);
                endTime = Calendar.getInstance();
                if(position != 4) {
                    dateRangeSelectorView.setVisibility(View.GONE);
                }else{
                    startTime = null;
                    dateRangeSelectorView.setVisibility(View.VISIBLE);
                    dateRangeSelector = new DateRangeSelector(OrderActivity.this, null, endTime, new DateRangeSelector.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(Calendar newStartDate, Calendar newEndDate) {
                            startTime = newStartDate;
                            endTime = newEndDate;
                            refreshOrder(true, true);
                        }
                    });
                }
                switch (position){
                    case 0:
                        orderStatus = STATUS_IN_PROGRESS;
                        break;
                    case 1:
                        orderStatus = STATUS_DELIVERING;
                        break;
                    case 2:
                        orderStatus = STATUS_CANCELLED;
                        break;
                    case 3:
                        orderStatus = STATUS_FINISHED;
                        break;
                    case 4:
                        orderStatus = null;
                        break;
                }
                refreshOrder(true, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        orderListView.setLayoutManager(new LinearLayoutManager(this));
        orderListView.getRecyclerView().setNestedScrollingEnabled(false);
        orderListView.getSwipeToRefresh().setColorSchemeResources(R.color.colorAccent);
        orderListView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshOrder(true, false);
            }
        });
        orderListView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                refreshOrder(false, false);
            }
        }, -1);
        adapter = new OrderAdapter(orderList, new OrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Order order, int position) {
                Intent intent = new Intent(OrderActivity.this, OrderDetailActivity.class);
                intent.putExtra(OrderDetailActivity.BUNDLE_KEY_ORDER, order);
                startActivity(intent);
            }
        });
        orderListView.setAdapter(adapter);
        endTime = Calendar.getInstance();
        refreshOrder(true, true);
    }

    private void updateTime(){
        endTime.set(Calendar.HOUR_OF_DAY, 23);
        endTime.set(Calendar.MINUTE, 59);
        endTime.set(Calendar.SECOND, 59);
        if(startTime != null) {
            startTime.set(startTime.get(Calendar.YEAR), startTime.get(Calendar.MONTH), startTime.get(Calendar.DATE),
                    0, 0, 0);
        }
    }

    private void refreshOrder(final boolean refresh, final boolean dateChanged){
        if(refresh){
            orderListView.setRefreshing(true);
            updateTime();
        }
        if(dateRangeSelectorView.getVisibility() == View.VISIBLE)
            dateRangeSelector.setEnable(false);
        if(refresh && dateChanged){
            orderList.clear();
            adapter.notifyDataSetChanged();
        }
        String url;
        if(orderStatus == null) {
            url = HttpApi.getApiUrl(HttpApi.CreditMallApi.ORDERS, DateTimeUtil.getUnixTimestampStr(startTime), DateTimeUtil.getUnixTimestampStr(endTime), "" + REQUEST_MAX_ORDER);
        }else{
            url = HttpApi.getApiUrl(HttpApi.CreditMallApi.ORDERS_BY_STATUS, orderStatus, DateTimeUtil.getUnixTimestampStr(endTime), "" + REQUEST_MAX_ORDER);
        }
        HttpApi.startRequest(new HttpApiJsonRequest(this, url, Request.Method.GET, GlobalInfo.token, null, new HttpApiJsonListener<OrderListResult>(OrderListResult.class) {
            @Override
            public void onResponse() {
                orderListView.setRefreshing(false);
                orderListView.hideMoreProgress();
                if(dateRangeSelectorView.getVisibility() == View.VISIBLE)
                    dateRangeSelector.setEnable(true);
            }

            @Override
            public void onDataResponse(OrderListResult data) {
                txtNoOrder.setVisibility(View.GONE);
                if(refresh){
                    orderList.clear();
                    adapter.notifyDataSetChanged();
                }
                for(Order o : data.getOrderList()){
                    orderList.add(o);
                    adapter.notifyItemInserted(orderList.size() - 1);
                    endTime.setTimeInMillis(o.getSubmitTime().getTime() - 1000);
                }
                if(data.getOrderList().size() < REQUEST_MAX_ORDER)
                    orderListView.setNumberBeforeMoreIsCalled(-1);
                else
                    orderListView.setNumberBeforeMoreIsCalled(1);
            }

            @Override
            public void onErrorResponse() {
                if(refresh && orderList.isEmpty())
                    txtNoOrder.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean onErrorDataResponse(int statusCode, Result errorInfo) {
                if(errorInfo.getResultCode() == PublicResultCode.ORDER_NOT_FOUND){
                    if(!refresh)
                        orderListView.setNumberBeforeMoreIsCalled(-1);
                    return true;
                }
                return super.onErrorDataResponse(statusCode, errorInfo);
            }
        }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private static class OrderStatusSpinnerAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        public OrderStatusSpinnerAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return view;
        }

        @Override
        public Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }
    }
}
