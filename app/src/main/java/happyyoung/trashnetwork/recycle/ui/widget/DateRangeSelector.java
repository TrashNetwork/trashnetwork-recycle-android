package happyyoung.trashnetwork.recycle.ui.widget;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.util.DateTimeUtil;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-07-08
 */
public class DateRangeSelector {
    @BindView(R.id.edit_start_date)
    EditText editStartDate;
    @BindView(R.id.edit_end_date)
    EditText editEndDate;

    private Context context;
    private DatePickerDialog datePickerDialog;
    private Calendar startDate;
    private Calendar endDate;
    private Calendar selectDate;
    private EditText selectDateText;
    private DateRangeSelector.OnDateChangedListener listener;

    public DateRangeSelector(Activity activity, @Nullable Calendar startDate, @Nullable Calendar endDate, @Nullable OnDateChangedListener listener){
        this(activity.findViewById(android.R.id.content), activity, startDate, endDate, listener);
    }

    public DateRangeSelector(View rootView, @Nullable Calendar startDate, @Nullable Calendar endDate, @Nullable OnDateChangedListener listener){
        this(rootView, rootView.getContext(), startDate, endDate, listener);
    }

    private DateRangeSelector(View rootView, Context context, @Nullable Calendar outStartDate, @Nullable Calendar outEndDate, @Nullable OnDateChangedListener listener){
        this.context = context;
        ButterKnife.bind(this, rootView);
        if(outStartDate != null) {
            editStartDate.setText(DateTimeUtil.convertTimestamp(context, outStartDate.getTime(), true, false));
            startDate = Calendar.getInstance();
            startDate.setTime(outStartDate.getTime());
        }else {
            editStartDate.setText("");
        }
        if(outEndDate != null) {
            editEndDate.setText(DateTimeUtil.convertTimestamp(context, outEndDate.getTime(), true, false));
            endDate = Calendar.getInstance();
            endDate.setTime(outEndDate.getTime());
        }else {
            editEndDate.setText("");
        }
        this.listener = listener;
        Calendar tempDate = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectDate.set(year, month, dayOfMonth);
                selectDateText.setText(DateTimeUtil.convertTimestamp(DateRangeSelector.this.context, selectDate.getTime(), true, false));
                if(DateRangeSelector.this.listener != null)
                    DateRangeSelector.this.listener.onDateChanged(DateRangeSelector.this.startDate, DateRangeSelector.this.endDate);
            }
        }, tempDate.get(Calendar.YEAR), tempDate.get(Calendar.MONTH), tempDate.get(Calendar.DAY_OF_MONTH));
    }

    @OnClick({R.id.edit_start_date, R.id.edit_end_date})
    void onDateChangeClick(View v){
        switch (v.getId()){
            case R.id.edit_start_date:
                if(startDate == null)
                    startDate = Calendar.getInstance();
                datePickerDialog.updateDate(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
                selectDate = startDate;
                selectDateText = editStartDate;
                datePickerDialog.show();
                break;
            case R.id.edit_end_date:
                if(endDate == null)
                    endDate = Calendar.getInstance();
                datePickerDialog.updateDate(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));
                selectDate = endDate;
                selectDateText = editEndDate;
                datePickerDialog.show();
                break;
        }
    }

    public interface OnDateChangedListener{
        void onDateChanged(Calendar newStartDate, Calendar newEndDate);
    }

    public void setEnable(boolean enable){
        editEndDate.setEnabled(enable);
        editStartDate.setEnabled(enable);
    }
}
