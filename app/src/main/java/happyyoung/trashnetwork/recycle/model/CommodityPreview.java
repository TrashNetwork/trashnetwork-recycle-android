package happyyoung.trashnetwork.recycle.model;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-07-07
 */
public class CommodityPreview {
    private Long commodityId;
    private String title;
    private Integer credit;
    private Date addedTime;
    private Bitmap thumbnail;

    public CommodityPreview(Long commodityId, String title, Integer credit, Date addedTime, Bitmap thumbnail) {
        this.commodityId = commodityId;
        this.title = title;
        this.credit = credit;
        this.addedTime = addedTime;
        this.thumbnail = thumbnail;
    }

    public Long getCommodityId() {
        return commodityId;
    }

    public String getTitle() {
        return title;
    }

    public Integer getCredit() {
        return credit;
    }

    public Date getAddedTime() {
        return addedTime;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }
}
