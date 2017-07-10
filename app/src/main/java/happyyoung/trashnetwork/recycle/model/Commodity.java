package happyyoung.trashnetwork.recycle.model;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-07-07
 */
public class Commodity extends CommodityPreview implements Serializable {
    public static final String COMMODITY_TYPE_PHYSICAL = "P";
    public static final String COMMODITY_TYPE_VIRTUAL = "V";

    @Expose
    private String description;
    @Expose
    private Integer stock;
    private List<Bitmap> commodityImages;
    @Expose
    private String type = "P";
    @Expose
    private Integer quantityLimit = 5;

    public Commodity(Long commodityId, String title, Integer credit, Date addedTime, Bitmap thumbnail, String description, Integer stock, List<Bitmap> commodityImages, Integer quantityLimit, String type) {
        super(commodityId, title, credit, addedTime, thumbnail);
        this.description = description;
        this.stock = stock;
        this.commodityImages = commodityImages;
        this.quantityLimit = quantityLimit;
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public Integer getStock() {
        return stock;
    }

    public List<Bitmap> getCommodityImages() {
        return commodityImages;
    }

    public Integer getQuantityLimit() {
        return quantityLimit;
    }

    public String getType() {
        return type;
    }
}
