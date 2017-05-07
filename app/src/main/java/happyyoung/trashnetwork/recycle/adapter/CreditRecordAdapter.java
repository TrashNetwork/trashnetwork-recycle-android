package happyyoung.trashnetwork.recycle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.akashandroid90.imageletter.MaterialLetterIcon;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import happyyoung.trashnetwork.recycle.Application;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.model.CreditRecord;
import happyyoung.trashnetwork.recycle.util.DateTimeUtil;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-03-22
 */
public class CreditRecordAdapter extends RecyclerView.Adapter<CreditRecordAdapter.CreditRecordViewHolder> {
    private Context context;
    private List<CreditRecord> recordList;

    public CreditRecordAdapter(Context context, List<CreditRecord> recordList) {
        this.context = context;
        this.recordList = recordList;
    }

    @Override
    public CreditRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CreditRecordViewHolder(LayoutInflater.from(context).inflate(R.layout.item_credit_record, parent, false));
    }

    @Override
    public void onBindViewHolder(CreditRecordViewHolder holder, int position) {
        CreditRecord cr = recordList.get(position);
        holder.goodIcon.setShapeColor(Application.getRandomColor());
        holder.goodIcon.setLetter(cr.getGoodDescription());
        holder.txtGoodName.setText(cr.getGoodDescription());
        holder.txtGoodQuantity.setText(String.valueOf(cr.getQuantity()));
        holder.txtRecordTime.setText(DateTimeUtil.convertTimestamp(context, cr.getRecordTime(), false, true, false));
        String creditDelta = cr.getCredit().toString();
        if(cr.getCredit() > 0)
            creditDelta = "+" + cr.getCredit();
        holder.txtCreditDelta.setText(creditDelta);
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    class CreditRecordViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.icon_good_letters) MaterialLetterIcon goodIcon;
        @BindView(R.id.txt_good_name) TextView txtGoodName;
        @BindView(R.id.txt_good_quantity) TextView txtGoodQuantity;
        @BindView(R.id.txt_credit_delta) TextView txtCreditDelta;
        @BindView(R.id.txt_credit_record_time) TextView txtRecordTime;

        public CreditRecordViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
