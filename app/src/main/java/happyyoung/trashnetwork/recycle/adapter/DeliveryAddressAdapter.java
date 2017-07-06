package happyyoung.trashnetwork.recycle.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.model.DeliveryAddress;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-07-05
 */
public class DeliveryAddressAdapter extends RecyclerView.Adapter<DeliveryAddressAdapter.DeliveryAddressViewHolder> {
    private List<DeliveryAddress> addressList;
    private OnItemClickListener listener;

    public DeliveryAddressAdapter(List<DeliveryAddress> addressList, @Nullable OnItemClickListener listener) {
        this.addressList = addressList;
        this.listener = listener;
    }

    @Override
    public DeliveryAddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DeliveryAddressViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delivery_address, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(DeliveryAddressViewHolder holder, final int position) {
        final DeliveryAddress address = addressList.get(position);
        holder.txtName.setText(address.getName());
        holder.txtPhoneNumber.setText(address.getPhoneNumber());
        holder.txtAddress.setText(address.getAddress());
        holder.checkDefault.setOnCheckedChangeListener(null);
        holder.checkDefault.setChecked(address.isDefault());
        holder.checkDefault.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(listener != null)
                    listener.onRadioBtnDefaultCheckedChanged(isChecked, address, position);
            }
        });
        holder.addressCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onItemClick(address, position);
            }
        });
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onBtnEditClick(address, position);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onBtnDeleteClick(address, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public interface OnItemClickListener{
        void onItemClick(DeliveryAddress address, int position);
        void onBtnEditClick(DeliveryAddress address, int position);
        void onBtnDeleteClick(DeliveryAddress address, int position);
        void onRadioBtnDefaultCheckedChanged(boolean checked, DeliveryAddress address, int position);
    }

    class DeliveryAddressViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.address_card)
        CardView addressCard;
        @BindView(R.id.txt_name)
        TextView txtName;
        @BindView(R.id.txt_phone_number)
        TextView txtPhoneNumber;
        @BindView(R.id.txt_address)
        TextView txtAddress;
        @BindView(R.id.check_default_address)
        RadioButton checkDefault;
        @BindView(R.id.btn_edit)
        ImageButton btnEdit;
        @BindView(R.id.btn_delete)
        ImageButton btnDelete;

        public DeliveryAddressViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
