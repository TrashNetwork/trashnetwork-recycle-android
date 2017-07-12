package happyyoung.trashnetwork.recycle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.model.RecyclePoint;
import happyyoung.trashnetwork.recycle.model.RecycleRecord;
import happyyoung.trashnetwork.recycle.util.DateTimeUtil;
import happyyoung.trashnetwork.recycle.util.GlobalInfo;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-04-30
 */
public class RecycleRecordAdapter extends RecyclerView.Adapter<RecycleRecordAdapter.RecycleRecordViewHolder> {
    private List<RecycleRecord> recordList;
    private Context context;

    public RecycleRecordAdapter(Context context, List<RecycleRecord> recordList){
        this.context = context;
        this.recordList = recordList;
    }

    @Override
    public RecycleRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecycleRecordViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recycle_record, parent, false));
    }

    @Override
    public void onBindViewHolder(RecycleRecordViewHolder holder, int position) {
        RecycleRecord r = recordList.get(position);
        RecyclePoint rp = GlobalInfo.findRecyclePointById(r.getRecyclePointId());
        if(rp == null)
            return;

        holder.txtRecycleTime.setText(DateTimeUtil.convertTimestamp(context, r.getRecycleTime(), false, true, false));
        holder.txtRecycleDate.setText(DateTimeUtil.convertTimestamp(context, r.getRecycleTime(), true, false, false));
        holder.txtRecyclePointName.setText(rp.getRecyclePointName(context));
        holder.txtRecyclePointDesc.setText(rp.getDescription());
        if(r.getBottleNum() != null)
            holder.txtBottleNum.setText(String.valueOf(r.getBottleNum()));
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    class RecycleRecordViewHolder extends RecyclerView.ViewHolder{
        private View itemView;
        @BindView(R.id.txt_recycle_point_name)
        TextView txtRecyclePointName;
        @BindView(R.id.txt_recycle_point_desc)
        TextView txtRecyclePointDesc;
        @BindView(R.id.txt_bottle_num)
        TextView txtBottleNum;
        @BindView(R.id.txt_recycle_record_date)
        TextView txtRecycleDate;
        @BindView(R.id.txt_recycle_record_time)
        TextView txtRecycleTime;

        public RecycleRecordViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
