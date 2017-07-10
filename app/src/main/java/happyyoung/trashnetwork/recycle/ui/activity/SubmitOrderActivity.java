package happyyoung.trashnetwork.recycle.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import happyyoung.trashnetwork.recycle.Application;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.model.Commodity;
import happyyoung.trashnetwork.recycle.model.DeliveryAddress;
import happyyoung.trashnetwork.recycle.net.PublicResultCode;
import happyyoung.trashnetwork.recycle.net.http.HttpApi;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonListener;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonRequest;
import happyyoung.trashnetwork.recycle.net.model.request.SubmitOrderRequest;
import happyyoung.trashnetwork.recycle.net.model.result.DeliveryAddressListResult;
import happyyoung.trashnetwork.recycle.net.model.result.Result;
import happyyoung.trashnetwork.recycle.util.GlobalInfo;
import happyyoung.trashnetwork.recycle.util.GsonUtil;
import happyyoung.trashnetwork.recycle.util.HttpUtil;

public class SubmitOrderActivity extends AppCompatActivity {
    public static final String BUNDLE_KEY_COMMODITY = "Commodity";
    private Commodity commodity;
    private SubmitOrderRequest submitOrderRequest;

    @BindView(R.id.txt_commodity_title)
    TextView txtCommodityTitle;

    @BindView(R.id.edit_quantity)
    EditText editQuantity;
    @BindView(R.id.txt_user_credit)
    TextView txtUserCredit;
    @BindView(R.id.txt_commodity_credit)
    TextView txtCommodityCredit;
    @BindView(R.id.txt_hint_maximum_quantity)
    TextView txtHintMaxQuantity;
    @BindView(R.id.txt_commodity_stock)
    TextView txtCommodityStock;
    @BindView(R.id.txt_total_credit)
    TextView txtTotalCredit;

    @BindView(R.id.address_card)
    View addressCardView;
    @BindView(R.id.txt_no_address)
    TextView txtNoAddress;
    @BindView(R.id.address_view)
    View addressView;

    @BindView(R.id.edit_remark)
    EditText editRemark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_order);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        commodity = GsonUtil.getGson().fromJson(getIntent().getStringExtra(BUNDLE_KEY_COMMODITY), Commodity.class);
        submitOrderRequest = new SubmitOrderRequest(commodity.getCommodityId());

        //Bill
        txtCommodityTitle.setText(commodity.getTitle());
        txtUserCredit.setText(String.format(getString(R.string.commodity_credit_format), GlobalInfo.user.getCredit()));
        txtCommodityCredit.setText(String.format(getString(R.string.commodity_credit_format), commodity.getCredit()));
        txtCommodityStock.setText(Integer.toString(commodity.getStock()));
        txtHintMaxQuantity.setText(String.format(getString(R.string.note_maximum_format), commodity.getQuantityLimit()));

        if(Commodity.COMMODITY_TYPE_VIRTUAL.equals(commodity.getType()))
            addressCardView.setVisibility(View.GONE);
        editQuantity.setText("1");
        getDefaultDeliveryAddr();
    }

    @OnClick({R.id.btn_minus, R.id.btn_plus})
    void onBtnQuantityChangeClick(View v){
        int quantity;
        if(TextUtils.isEmpty(editQuantity.getText()) || !TextUtils.isDigitsOnly(editQuantity.getText()))
            quantity = 0;
        else
            quantity = Integer.valueOf(editQuantity.getText().toString());
        switch (v.getId()){
            case R.id.btn_minus:
                quantity--;
                break;
            case R.id.btn_plus:
                quantity++;
        }
        if(quantity <= 0)
            quantity = 1;
        else if(quantity > commodity.getQuantityLimit())
            quantity = commodity.getQuantityLimit();
        editQuantity.setText(Integer.toString(quantity));
    }

    @OnTextChanged(value = R.id.edit_quantity, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void onTextQuantityChanged(CharSequence s, int start, int before, int count){
        if(TextUtils.isEmpty(s) || !TextUtils.isDigitsOnly(s))
            return;
        int quantity = Integer.valueOf(editQuantity.getText().toString());
        int total = commodity.getCredit() * quantity;
        txtTotalCredit.setText(String.format(getString(R.string.commodity_credit_format), total));
    }

    private void showDeliveryAddress(@Nullable DeliveryAddress addr){
        if(addr == null){
            txtNoAddress.setVisibility(View.VISIBLE);
            addressView.setVisibility(View.GONE);
            return;
        }
        txtNoAddress.setVisibility(View.GONE);
        addressView.setVisibility(View.VISIBLE);
        TextView txtName = ButterKnife.findById(addressView, R.id.txt_name);
        txtName.setText(addr.getName());
        TextView txtPhoneNumber = ButterKnife.findById(addressView, R.id.txt_phone_number);
        txtPhoneNumber.setText(addr.getPhoneNumber());
        TextView txtAddress = ButterKnife.findById(addressView, R.id.txt_address);
        txtAddress.setText(addr.getAddress());
    }

    private void getDefaultDeliveryAddr(){
        String url = HttpApi.getApiUrl(HttpApi.AccountApi.DELIVERY_ADDRESS);
        HttpApi.startRequest(new HttpApiJsonRequest(this, url, Request.Method.GET, GlobalInfo.token, null,
                new HttpApiJsonListener<DeliveryAddressListResult>(DeliveryAddressListResult.class) {
                    @Override
                    public void onDataResponse(DeliveryAddressListResult data) {
                        for(DeliveryAddress da : data.getAddressList()){
                            if(da.isDefault()){
                                submitOrderRequest.setDeliveryAddress(da);
                                showDeliveryAddress(da);
                                return;
                            }
                        }
                        submitOrderRequest.setDeliveryAddress(data.getAddressList().get(0));
                        showDeliveryAddress(data.getAddressList().get(0));
                    }

                    @Override
                    public void onErrorResponse() {
                        submitOrderRequest.setDeliveryAddress(null);
                        showDeliveryAddress(null);
                    }

                    @Override
                    public boolean onErrorDataResponse(int statusCode, Result errorInfo) {
                        if(errorInfo.getResultCode() == PublicResultCode.DELIVER_ADDRESS_NOT_FOUND)
                            return true;
                        return super.onErrorDataResponse(statusCode, errorInfo);
                    }
                }));
    }

    @OnClick(R.id.address_card)
    void onAddressCardClick(View v){
        Intent intent = new Intent(this, DeliverAddressActivity.class);
        intent.putExtra(DeliverAddressActivity.BUNDLE_KEY_REQUEST_CODE, DeliverAddressActivity.REQUEST_CODE_CHOOSE_ADDRESS);
        startActivityForResult(intent, DeliverAddressActivity.REQUEST_CODE_CHOOSE_ADDRESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == DeliverAddressActivity.REQUEST_CODE_CHOOSE_ADDRESS && resultCode == DeliverAddressActivity.RESULT_CODE_CHOOSE_ADDRESS){
            DeliveryAddress da = (DeliveryAddress) data.getSerializableExtra(DeliverAddressActivity.BUNDLE_RETURN_KEY_ADDRESS);
            submitOrderRequest.setDeliveryAddress(da);
            showDeliveryAddress(da);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.btn_submit)
    void onBtnSubmitClick(View v){
        if(TextUtils.isEmpty(editQuantity.getText()) || !TextUtils.isDigitsOnly(editQuantity.getText())) {
            editQuantity.setError(getString(R.string.error_illegal_quantity));
            return;
        }
        int quantity = Integer.valueOf(editQuantity.getText().toString());
        if(quantity > commodity.getQuantityLimit()){
            editQuantity.setError(getString(R.string.error_quantity_exceed_limit));
            return;
        }
        if(!Commodity.COMMODITY_TYPE_VIRTUAL.equals(commodity.getType()) && submitOrderRequest.getDeliveryAddress() == null){
            Toast.makeText(this, getString(R.string.error_no_delivery_address), Toast.LENGTH_SHORT).show();
            return;
        }
        submitOrderRequest.setQuantity(quantity);
        if(TextUtils.isEmpty(editRemark.getText()))
            submitOrderRequest.setRemark(null);
        else
            submitOrderRequest.setRemark(editRemark.getText().toString());

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage(getString(R.string.alert_waiting));
        pd.show();

        HttpApi.startRequest(new HttpApiJsonRequest(this, HttpApi.getApiUrl(HttpApi.CreditMallApi.SUBMIT_ORDER), Request.Method.POST, GlobalInfo.token,
                submitOrderRequest, new HttpApiJsonListener<Result>(Result.class) {
            @Override
            public void onResponse() {
                pd.dismiss();
            }

            @Override
            public void onDataResponse(Result data) {
                Toast.makeText(SubmitOrderActivity.this, data.getMessage(), Toast.LENGTH_SHORT).show();
                HttpUtil.updateUserInfo(getApplicationContext());
                finish();
            }

            @Override
            public boolean onErrorDataResponse(int statusCode, Result errorInfo) {
                if(errorInfo.getResultCode() != 401){
                    AlertDialog ad = new AlertDialog.Builder(SubmitOrderActivity.this)
                            .setTitle(R.string.error)
                            .setMessage(errorInfo.getMessage())
                            .setPositiveButton(R.string.action_ok, null)
                            .create();
                    ad.show();
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
}
