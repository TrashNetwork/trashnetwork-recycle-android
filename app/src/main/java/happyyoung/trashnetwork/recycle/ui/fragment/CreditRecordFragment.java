package happyyoung.trashnetwork.recycle.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import happyyoung.trashnetwork.recycle.ui.widget.DateSelector;

public class CreditRecordFragment extends Fragment {
    private View rootView;
    private DateSelector dateSelector;
    @BindView(R.id.txt_no_record) TextView txtNoRecord;
    @BindView(R.id.credit_record_list) SuperRecyclerView creditRecordListView;

    private List<CreditRecord> recordList = new ArrayList<>();
    private CreditRecordAdapter adapter;
    private Calendar endTime;
    private Calendar startTime;

    public CreditRecordFragment() {
        // Required empty public constructor
    }

    public static CreditRecordFragment newInstance(Context context) {
        CreditRecordFragment fragment = new CreditRecordFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_credit_record, container, false);
        ButterKnife.bind(this, rootView);

        startTime = Calendar.getInstance();
        endTime = Calendar.getInstance();
        dateSelector = new DateSelector(rootView, endTime, new DateSelector.OnDateChangedListener() {
            @Override
            public void onDateChanged(Calendar newDate) {
                endTime = newDate;
                refreshCreditRecord(true);
            }
        });

        creditRecordListView.setLayoutManager(new LinearLayoutManager(getContext()));
        creditRecordListView.getRecyclerView().setNestedScrollingEnabled(false);
        creditRecordListView.getSwipeToRefresh().setColorSchemeResources(R.color.colorAccent);
        creditRecordListView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                refreshCreditRecord(false);
            }
        }, 0);
        creditRecordListView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCreditRecord(true);
            }
        });

        adapter = new CreditRecordAdapter(getContext(), recordList);
        creditRecordListView.setAdapter(adapter);
        refreshCreditRecord(true);

        return rootView;
    }

    private void updateTime(){
        endTime.set(Calendar.HOUR_OF_DAY, 23);
        endTime.set(Calendar.MINUTE, 59);
        endTime.set(Calendar.SECOND, 59);
        startTime.set(endTime.get(Calendar.YEAR), endTime.get(Calendar.MONTH), endTime.get(Calendar.DATE),
                0, 0, 0);
    }

    private void refreshCreditRecord(final boolean refresh){
        //TODO
        if(refresh)
            updateTime();
    }
}
