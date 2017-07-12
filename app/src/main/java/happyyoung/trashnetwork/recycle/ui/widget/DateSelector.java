package happyyoung.trashnetwork.recycle.ui.widget;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.util.DateTimeUtil;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-03-22
 */
public class DateSelector {
    @BindView(R.id.edit_date) EditText dateEdit;
    @BindView(R.id.btn_date_decrease) ImageButton btnDateDecrease;
    @BindView(R.id.btn_date_increase) ImageButton btnDateIncrease;

    private Context context;
    private DatePickerDialog datePickerDialog;
    private Calendar date;
    private OnDateChangedListener listener;

    public DateSelector(Activity activity, final Calendar date, @Nullable OnDateChangedListener listener){
        this(activity.findViewById(android.R.id.content), activity, date, listener);
    }

    public DateSelector(View rootView, final Calendar date, @Nullable OnDateChangedListener listener){
        this(rootView, rootView.getContext(), date, listener);
    }

    private DateSelector(View rootView, Context context, Calendar outDate, @Nullable OnDateChangedListener listener){
        this.context = context;
        ButterKnife.bind(this, rootView);
        dateEdit.setText(DateTimeUtil.convertTimestamp(context, outDate.getTime(), true, false));
        this.date = Calendar.getInstance();
        this.date.setTime(outDate.getTime());
        this.listener = listener;
        datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date.set(year, month, dayOfMonth);
                dateEdit.setText(DateTimeUtil.convertTimestamp(DateSelector.this.context, date.getTime(), true, false));
                if(DateSelector.this.listener != null)
                    DateSelector.this.listener.onDateChanged(date);
            }
        }, outDate.get(Calendar.YEAR), outDate.get(Calendar.MONTH), outDate.get(Calendar.DAY_OF_MONTH));
    }

    @OnClick({R.id.btn_date_decrease, R.id.btn_date_increase, R.id.edit_date})
    void onDateChangeClick(View v){
        switch (v.getId()){
            case R.id.btn_date_decrease:
                date.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH) - 1);
                dateEdit.setText(DateTimeUtil.convertTimestamp(context, date.getTime(), true, false));
                if(listener != null)
                    listener.onDateChanged(date);
                break;
            case R.id.btn_date_increase:
                date.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH) + 1);
                dateEdit.setText(DateTimeUtil.convertTimestamp(context, date.getTime(), true, false));
                if(listener != null)
                    listener.onDateChanged(date);
                break;
            case R.id.edit_date:
                datePickerDialog.updateDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;
        }
    }

    public interface OnDateChangedListener{
        void onDateChanged(Calendar newDate);
    }

    public void setEnable(boolean enable){
        dateEdit.setEnabled(enable);
        btnDateIncrease.setEnabled(enable);
        btnDateDecrease.setEnabled(enable);
    }
}
