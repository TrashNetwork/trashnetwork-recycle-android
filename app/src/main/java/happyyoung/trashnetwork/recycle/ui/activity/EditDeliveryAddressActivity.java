package happyyoung.trashnetwork.recycle.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.model.DeliveryAddress;

public class EditDeliveryAddressActivity extends AppCompatActivity {
    public static final String BUNDLE_KEY_ADDRESS = "OriginAddress";
    public static final String BUNDLE_RETURN_KEY_ADDRESS = "NewAddress";
    public static final int REQUEST_CODE_ADD = 0x233;
    public static final int DONE_RESULT_CODE = 0x2333;
    private static final int MENU_ID_ACTION_DONE = 0x666666;

    @BindView(R.id.edit_name)
    EditText editName;
    @BindView(R.id.edit_phone_number)
    EditText editPhoneNumber;
    @BindView(R.id.edit_address)
    EditText editAddr;

    private DeliveryAddress address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delivery_address);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        address = (DeliveryAddress) getIntent().getSerializableExtra(BUNDLE_KEY_ADDRESS);
        if(address != null){
            setTitle(R.string.action_edit_address);
            editName.setText(address.getName());
            editPhoneNumber.setText(address.getPhoneNumber());
            editAddr.setText(address.getAddress());
        }else{
            setTitle(R.string.action_new_address);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(0, MENU_ID_ACTION_DONE, 0, R.string.action_done);
        item.setIcon(R.drawable.ic_done_white);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case MENU_ID_ACTION_DONE:
                finishEdit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void finishEdit(){
        address = new DeliveryAddress(editName.getText().toString(), editPhoneNumber.getText().toString(),
                                      editAddr.getText().toString(), false);
        if(TextUtils.isEmpty(address.getName())){
            editName.setError(getString(R.string.error_field_required));
            return;
        }else if(TextUtils.isEmpty(address.getPhoneNumber())){
            editPhoneNumber.setError(getString(R.string.error_field_required));
            return;
        }else if(!TextUtils.isDigitsOnly(address.getPhoneNumber())){
            editPhoneNumber.setError(getString(R.string.error_illegal_phone_number));
            return;
        }else if(TextUtils.isEmpty(address.getAddress())){
            editAddr.setError(getString(R.string.error_field_required));
            return;
        }
        Intent data = new Intent();
        data.putExtra(BUNDLE_RETURN_KEY_ADDRESS, address);
        setResult(DONE_RESULT_CODE, data);
        finish();
    }
}
