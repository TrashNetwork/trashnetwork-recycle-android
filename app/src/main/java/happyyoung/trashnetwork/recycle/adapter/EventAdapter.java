package happyyoung.trashnetwork.recycle.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.model.Event;
import happyyoung.trashnetwork.recycle.util.DateTimeUtil;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-07-03
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> eventList;
    private OnItemClickListener listener;

    public EventAdapter(List<Event> eventList, OnItemClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EventViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false));
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, final int position) {
        final Event event = eventList.get(position);
        holder.txtTitle.setText(event.getTitle());
        holder.txtReleaseTime.setText(DateTimeUtil.convertTimestamp(holder.itemView.getContext(), event.getReleaseTime(), true, true, false));
        if(event.getEventImage() != null){
            holder.imgEventImage.setVisibility(View.VISIBLE);
            holder.imgEventImage.setImageBitmap(event.getEventImage());
        }else{
            holder.imgEventImage.setVisibility(View.GONE);
        }
        if(TextUtils.isEmpty(event.getDigest())){
            holder.txtDigest.setVisibility(View.GONE);
        }else{
            holder.txtDigest.setVisibility(View.VISIBLE);
            holder.txtDigest.setText(event.getDigest());
        }
        if(listener != null){
            holder.eventCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(event, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public interface OnItemClickListener{
        void onItemClick(Event event, int pos);
    }

    class EventViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.event_card)
        View eventCardView;
        @BindView(R.id.txt_event_title)
        TextView txtTitle;
        @BindView(R.id.txt_event_time)
        TextView txtReleaseTime;
        @BindView(R.id.img_event)
        ImageView imgEventImage;
        @BindView(R.id.txt_event_digest)
        TextView txtDigest;

        public EventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
