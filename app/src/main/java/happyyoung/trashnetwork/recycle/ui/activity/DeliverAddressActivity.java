package happyyoung.trashnetwork.recycle.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.adapter.DeliveryAddressAdapter;
import happyyoung.trashnetwork.recycle.model.DeliveryAddress;
import happyyoung.trashnetwork.recycle.net.PublicResultCode;
import happyyoung.trashnetwork.recycle.net.http.HttpApi;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonListener;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonRequest;
import happyyoung.trashnetwork.recycle.net.model.request.NewDeliveryAddressRequest;
import happyyoung.trashnetwork.recycle.net.model.result.DeliveryAddressListResult;
import happyyoung.trashnetwork.recycle.net.model.result.Result;
import happyyoung.trashnetwork.recycle.util.GlobalInfo;

public class DeliverAddressActivity extends AppCompatActivity {
    public static String BUNDLE_KEY_REQUEST_CODE = "RequestCode";
    public static String BUNDLE_RETURN_KEY_ADDRESS = "ReturnAddress";
    public static int REQUEST_CODE_CHOOSE_ADDRESS = 0x233;
    public static int RESULT_CODE_CHOOSE_ADDRESS = 0x666;

    @BindView(R.id.btn_add_address)
    FloatingActionButton btnAddAddress;
    @BindView(R.id.txt_no_address)
    TextView txtNoAddress;
    @BindView(R.id.address_list)
    SuperRecyclerView addressListView;

    private List<DeliveryAddress> addressList = new ArrayList<>();
    private DeliveryAddressAdapter adapter;
    private int editPos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getIntExtra(BUNDLE_KEY_REQUEST_CODE, -1) == REQUEST_CODE_CHOOSE_ADDRESS)
            setTitle(R.string.action_choose_address);
        setContentView(R.layout.activity_deliver_address);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addressListView.setLayoutManager(new LinearLayoutManager(this));
        addressListView.getRecyclerView().setNestedScrollingEnabled(false);
        addressListView.getSwipeToRefresh().setColorSchemeResources(R.color.colorAccent);
        addressListView.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0 && btnAddAddress.isShown())
                    btnAddAddress.hide();
                else if(dy < 0 && !btnAddAddress.isShown())
                    btnAddAddress.show();
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        addressListView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAddress();
            }
        });

        adapter = new DeliveryAddressAdapter(addressList, new DeliveryAddressAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DeliveryAddress address, int position) {
                if(getIntent().getIntExtra(BUNDLE_KEY_REQUEST_CODE, -1) == REQUEST_CODE_CHOOSE_ADDRESS){
                    Intent intent = new Intent();
                    intent.putExtra(BUNDLE_RETURN_KEY_ADDRESS, address);
                    setResult(RESULT_CODE_CHOOSE_ADDRESS, intent);
                    finish();
                }
            }

            @Override
            public void onBtnEditClick(DeliveryAddress address, int position) {
                editPos = position;
                Intent intent = new Intent(DeliverAddressActivity.this, EditDeliveryAddressActivity.class);
                intent.putExtra(EditDeliveryAddressActivity.BUNDLE_KEY_ADDRESS, address);
                startActivityForResult(intent, EditDeliveryAddressActivity.REQUEST_CODE_ADD);
            }

            @Override
            public void onBtnDeleteClick(final DeliveryAddress address, final int position) {
                AlertDialog ad = new AlertDialog.Builder(DeliverAddressActivity.this)
                        .setTitle(R.string.action_confirm)
                        .setMessage(R.string.alert_delete)
                        .setNegativeButton(R.string.action_cancel, null)
                        .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addressList.remove(address);
                                adapter.notifyItemRemoved(position);
                                if(addressList.isEmpty()) {
                                    txtNoAddress.setVisibility(View.VISIBLE);
                                }else if(addressList.size() == 1){
                                    addressList.get(0).setDefault(true);
                                    adapter.notifyItemChanged(0);
                                }
                                saveNewAddress();
                            }
                        })
                        .create();
                ad.show();
            }

            @Override
            public void onRadioBtnDefaultCheckedChanged(boolean checked, DeliveryAddress address, int position) {
                if(!checked)
                    return;
                for(int i = 0; i < addressList.size(); i++){
                    DeliveryAddress tempAddr = addressList.get(i);
                    if(tempAddr.isDefault()){
                        tempAddr.setDefault(false);
                        adapter.notifyItemChanged(i);
                    }
                }
                address.setDefault(true);
                adapter.notifyItemChanged(position);
                saveNewAddress();
            }
        });

        addressListView.setAdapter(adapter);
        refreshAddress();
    }

    private void refreshAddress(){
        addressListView.setRefreshing(true);
        HttpApi.startRequest(new HttpApiJsonRequest(this, HttpApi.getApiUrl(HttpApi.AccountApi.DELIVERY_ADDRESS), Request.Method.GET, GlobalInfo.token,
                null, new HttpApiJsonListener<DeliveryAddressListResult>(DeliveryAddressListResult.class) {
            @Override
            public void onResponse() {
                addressListView.setRefreshing(false);
            }

            @Override
            public void onDataResponse(DeliveryAddressListResult data) {
                txtNoAddress.setVisibility(View.GONE);
                addressList.clear();
                for(DeliveryAddress da : data.getAddressList())
                    addressList.add(da);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onErrorResponse() {
                if(addressList.isEmpty())
                    txtNoAddress.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean onErrorDataResponse(int statusCode, Result errorInfo) {
                if(errorInfo.getResultCode() == PublicResultCode.DELIVER_ADDRESS_NOT_FOUND)
                    return true;
                return super.onErrorDataResponse(statusCode, errorInfo);
            }
        }));
    }

    @OnClick(R.id.btn_add_address)
    void onBtnAddAddress(View v){
        editPos = -1;
        startActivityForResult(new Intent(this, EditDeliveryAddressActivity.class), EditDeliveryAddressActivity.REQUEST_CODE_ADD);
    }

    private void saveNewAddress(){
        for(DeliveryAddress da : addressList){
            if(da.isDefault())
                continue;
            da.setDefault(null);
        }
        NewDeliveryAddressRequest req = new NewDeliveryAddressRequest(addressList);
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.alert_waiting));
        HttpApi.startRequest(new HttpApiJsonRequest(this, HttpApi.getApiUrl(HttpApi.AccountApi.NEW_DELIVERY_ADDRESS), Request.Method.PUT, GlobalInfo.token,
                req, new HttpApiJsonListener<Result>(Result.class) {
            @Override
            public void onResponse() {
                pd.dismiss();
            }

            @Override
            public void onDataResponse(Result data) {}
        }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == EditDeliveryAddressActivity.REQUEST_CODE_ADD &&
                resultCode == EditDeliveryAddressActivity.DONE_RESULT_CODE){
            DeliveryAddress dr = (DeliveryAddress) data.getSerializableExtra(EditDeliveryAddressActivity.BUNDLE_RETURN_KEY_ADDRESS);
            if(editPos < 0){
                if(addressList.isEmpty())
                    dr.setDefault(true);
                addressList.add(dr);
                adapter.notifyItemInserted(addressList.size() - 1);
            }else{
                if(addressList.size() == 1)
                    dr.setDefault(true);
                addressList.set(editPos, dr);
                adapter.notifyItemChanged(editPos);
            }
            txtNoAddress.setVisibility(View.GONE);
            saveNewAddress();
        }
        super.onActivityResult(requestCode, resultCode, data);
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
