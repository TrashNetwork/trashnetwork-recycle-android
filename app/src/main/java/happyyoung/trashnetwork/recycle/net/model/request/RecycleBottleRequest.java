package happyyoung.trashnetwork.recycle.net.model.request;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-03-28
 */
public class RecycleBottleRequest {
    private long recyclePointId;
    private int quantity;
    private double longitude;
    private double latitude;

    public RecycleBottleRequest(long recyclePointId, int quantity, double longitude, double latitude) {
        this.recyclePointId = recyclePointId;
        this.quantity = quantity;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public long getRecyclePointId() {
        return recyclePointId;
    }

    public void setRecyclePointId(long recyclePointId) {
        this.recyclePointId = recyclePointId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
