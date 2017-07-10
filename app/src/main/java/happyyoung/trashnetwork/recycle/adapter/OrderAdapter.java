package happyyoung.trashnetwork.recycle.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.model.Order;
import happyyoung.trashnetwork.recycle.util.DateTimeUtil;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-07-09
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;
    private OnItemClickListener listener;

    public OrderAdapter(List<Order> orderList, @Nullable OnItemClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OrderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false));
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, final int position) {
        final Order order = orderList.get(position);
        Context context = holder.itemView.getContext();
        holder.txtCommodityTitle.setText(order.getTitle());
        holder.txtTotalCredit.setText(String.format(context.getString(R.string.credit_total_format), order.getCredit(),
                order.getQuantity(), order.getCredit() * order.getQuantity()));
        holder.txtSubmitTime.setText(DateTimeUtil.convertTimestamp(context, order.getSubmitTime(), true, true, false));
        holder.txtOrderStatus.setText(order.getStatusLiteral(context));
        int color = 0;
        switch (order.getStatus()){
            case Order.ORDER_STATUS_IN_PROGRESS:
                color = ContextCompat.getColor(context, R.color.blue_500);
                break;
            case Order.ORDER_STATUS_DELIVERING:
                color = ContextCompat.getColor(context, R.color.teal_500);
                break;
            case Order.ORDER_STATUS_CANCELLED:
                color = ContextCompat.getColor(context, R.color.blue_grey_500);
                break;
            case Order.ORDER_STATUS_FINISHED:
                color = ContextCompat.getColor(context, R.color.green_500);
                break;
        }
        holder.txtOrderStatus.setTextColor(color);
        holder.iconDelivery.setColorFilter(color);
        holder.orderCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onItemClick(order, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public interface OnItemClickListener{
        void onItemClick(Order order, int position);
    }

    class OrderViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.order_card)
        View orderCard;
        @BindView(R.id.txt_commodity_title)
        TextView txtCommodityTitle;
        @BindView(R.id.txt_submit_time)
        TextView txtSubmitTime;
        @BindView(R.id.txt_total_credit)
        TextView txtTotalCredit;
        @BindView(R.id.icon_delivery)
        ImageView iconDelivery;
        @BindView(R.id.txt_order_status)
        TextView txtOrderStatus;

        public OrderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
