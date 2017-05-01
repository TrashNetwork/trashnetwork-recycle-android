package happyyoung.trashnetwork.recycle.model;

import android.content.Context;
import android.support.annotation.Nullable;

import happyyoung.trashnetwork.recycle.R;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-03-17
 */
public class RecyclePoint {
    private Long recyclePointId;
    private Long ownerId;
    private String description;
    private Double longitude;
    private Double latitude;
    private Integer bottleNum;
    private Boolean bottleRecycle = false;

    public RecyclePoint(Long recyclePointId, Long ownerId, String description, Double longitude, Double latitude, @Nullable Integer bottleNum) {
        this.recyclePointId = recyclePointId;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
        this.bottleNum = bottleNum;
        this.ownerId = ownerId;
        if(bottleNum != null)
            bottleRecycle = true;
    }

    public Long getRecyclePointId() {
        return recyclePointId;
    }

    public String getRecyclePointName(Context context){
        return String.format(context.getString(R.string.recycle_point_format), recyclePointId);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Boolean isBottleRecycle() {
        return bottleRecycle;
    }

    public Integer getBottleNum() {
        return bottleNum;
    }

    public void setBottleNum(Integer bottleNum) {
        this.bottleNum = bottleNum;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
