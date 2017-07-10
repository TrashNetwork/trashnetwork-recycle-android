package happyyoung.trashnetwork.recycle.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.model.Order;
import happyyoung.trashnetwork.recycle.ui.widget.PreferenceCard;
import happyyoung.trashnetwork.recycle.util.DateTimeUtil;

public class OrderDetailActivity extends AppCompatActivity {
    public static final String BUNDLE_KEY_ORDER = "Order";
    private Order order;

    @BindDimen(R.dimen.activity_vertical_margin)
    int DIMEN_VERTICAL_MARGIN;

    @BindView(R.id.order_detail_container)
    ViewGroup containerView;

    private PreferenceCard orderCard;
    private PreferenceCard deliveryAddrCard;
    private PreferenceCard deliveryCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        order = (Order) getIntent().getSerializableExtra(BUNDLE_KEY_ORDER);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.bottomMargin = DIMEN_VERTICAL_MARGIN;

        orderCard = new PreferenceCard(this)
                .addGroup(getString(R.string.order))
                .addItem(null, getString(R.string.order_id), order.getOrderId(), true, null)
                .addItem(null, getString(R.string.submit_time), DateTimeUtil.convertTimestamp(OrderDetailActivity.this, order.getSubmitTime(), true, true, false), null)
                .addItem(null, getString(R.string.commodity), order.getTitle(), true, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(order.getCommodityId() != null){
                            Intent intent = new Intent(OrderDetailActivity.this, CreditMallDetailActivity.class);
                            intent.putExtra(CreditMallDetailActivity.BUNDLE_KEY_COMMODITY_ID, order.getCommodityId());
                            startActivity(intent);
                        }
                    }
                })
                .addItem(null, getString(R.string.total_credit),
                        String.format(getString(R.string.credit_total_format),
                                order.getCredit(),
                                order.getQuantity(),
                                order.getCredit() * order.getQuantity()),
                        null)
                .addItem(null, getString(R.string.status), order.getStatusLiteral(OrderDetailActivity.this), null);
        if(!TextUtils.isEmpty(order.getRemark())){
            orderCard.addItem(null, getString(R.string.remark), order.getRemark(), true, null);
        }

        containerView.addView(orderCard.getView());
        if(order.getDeliveryAddress() != null){
            orderCard.getView().setLayoutParams(params);
            deliveryAddrCard = new PreferenceCard(this)
                    .addGroup(getString(R.string.setting_delivery_address))
                    .addItem(null, getString(R.string.name), order.getDeliveryAddress().getName(), null)
                    .addItem(null, getString(R.string.phone_number), order.getDeliveryAddress().getPhoneNumber(), null)
                    .addItem(null, getString(R.string.address), order.getDeliveryAddress().getAddress(), null);
            containerView.addView(deliveryAddrCard.getView());
        }

        if(order.getDelivery() != null){
            orderCard.getView().setLayoutParams(params);
            deliveryAddrCard.getView().setLayoutParams(params);
            deliveryCard = new PreferenceCard(this)
                    .addGroup(getString(R.string.delivery_info))
                    .addItem(null, getString(R.string.delivery_company), order.getDelivery().getCompany(), null)
                    .addItem(null, getString(R.string.waybill_id), order.getDelivery().getWaybillId(), true, null);
            containerView.addView(deliveryCard.getView());
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
