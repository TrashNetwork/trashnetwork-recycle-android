package happyyoung.trashnetwork.recycle.ui.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.daimajia.slider.library.SliderLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.model.Commodity;
import happyyoung.trashnetwork.recycle.net.http.HttpApi;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonListener;
import happyyoung.trashnetwork.recycle.net.http.HttpApiJsonRequest;
import happyyoung.trashnetwork.recycle.net.model.result.CommodityDetailResult;
import happyyoung.trashnetwork.recycle.ui.widget.BitmapSlider;

public class CreditMallDetailActivity extends AppCompatActivity {
    public static final String BUNDLE_KEY_COMMODITY_ID = "CommodityID";

    @BindView(R.id.commodity_view)
    View commodityView;
    @BindView(R.id.btn_exchange)
    Button btnExchange;
    @BindView(R.id.txt_commodity_title)
    TextView txtTitle;
    @BindView(R.id.txt_commodity_credit)
    TextView txtCredit;
    @BindView(R.id.txt_commodity_desc)
    TextView txtDesc;
    @BindView(R.id.img_slider_commodity)
    SliderLayout imgSlider;
    @BindView(R.id.txt_reload)
    TextView txtReload;
    @BindView(R.id.progress_loading)
    ProgressBar progressBar;

    private long commodityId;
    private Commodity commodity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_mall_detial);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        commodityId = getIntent().getLongExtra(BUNDLE_KEY_COMMODITY_ID, 0);
        loadCommodity();
    }

    private void loadCommodity(){
        progressBar.setVisibility(View.VISIBLE);
        txtReload.setVisibility(View.GONE);
        commodityView.setVisibility(View.GONE);
        btnExchange.setVisibility(View.GONE);

        HttpApi.startRequest(new HttpApiJsonRequest(this, HttpApi.getApiUrl(HttpApi.CreditMallApi.COMMODITY_DETAIL, "" + commodityId), Request.Method.GET, null,
                null, new HttpApiJsonListener<CommodityDetailResult>(CommodityDetailResult.class) {
            @Override
            public void onResponse() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onDataResponse(CommodityDetailResult data) {
                commodityView.setVisibility(View.VISIBLE);
                btnExchange.setVisibility(View.VISIBLE);
                commodity = data.getCommodity();
                showCommodity();
            }

            @Override
            public void onErrorResponse() {
                txtReload.setVisibility(View.VISIBLE);
            }
        }));
    }

    @OnClick(R.id.txt_reload)
    void onTxtReloadClick(View v){
        loadCommodity();
    }

    @SuppressWarnings("deprecation")
    private void showCommodity(){
        txtTitle.setText(commodity.getTitle());
        txtCredit.setText(String.format(getString(R.string.commodity_credit_format), commodity.getCredit()));
        if(commodity.getStock() <= 0){
            txtCredit.setTextColor(Color.RED);
            txtCredit.setText(R.string.out_of_stock);
            btnExchange.setEnabled(false);
        }else{
            txtCredit.setText(String.format(getString(R.string.stock_format), commodity.getStock()));
        }
        txtDesc.setText(Html.fromHtml(commodity.getDescription()));
        if(commodity.getCommodityImages() == null || commodity.getCommodityImages().isEmpty()){
            imgSlider.setVisibility(View.GONE);
        }else{
            for(Bitmap b : commodity.getCommodityImages()){
                BitmapSlider bitmapSlider = new BitmapSlider(this)
                        .image(b);
                imgSlider.addSlider(bitmapSlider);
            }
        }
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
