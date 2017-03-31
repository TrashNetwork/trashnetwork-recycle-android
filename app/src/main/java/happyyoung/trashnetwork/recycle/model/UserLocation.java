package happyyoung.trashnetwork.recycle.model;

import java.util.Date;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-03-07
 */
public class UserLocation {
    private Double longitude;
    private Double latitude;
    private String address;
    private Date updateTime;

    public UserLocation(Double longitude, Double latitude, Date updateTime, String address) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.updateTime = updateTime;
        this.address = address;
    }

    public UserLocation(Double longitude, Double latitude, String address) {
        this(longitude, latitude, new Date(), address);
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
