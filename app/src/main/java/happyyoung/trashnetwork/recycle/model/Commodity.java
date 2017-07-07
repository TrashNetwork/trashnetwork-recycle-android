package happyyoung.trashnetwork.recycle.model;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.List;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-07-07
 */
public class Commodity extends CommodityPreview {
    private String description;
    private Integer stock;
    private List<Bitmap> commodityImages;

    public Commodity(Long commodityId, String title, Integer credit, Date addedTime, Bitmap thumbnail, String description, Integer stock, List<Bitmap> commodityImages) {
        super(commodityId, title, credit, addedTime, thumbnail);
        this.description = description;
        this.stock = stock;
        this.commodityImages = commodityImages;
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
}
