package happyyoung.trashnetwork.recycle.model;

import java.util.Date;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-03-07
 */
public class UserLocation {
    private Double longitude;
    private Double latitude;
    private Date updateTime;

    public UserLocation(Double longitude, Double latitude, Date updateTime) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.updateTime = updateTime;
    }

    public UserLocation(Double longitude, Double latitude) {
        this(longitude, latitude, new Date());
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
