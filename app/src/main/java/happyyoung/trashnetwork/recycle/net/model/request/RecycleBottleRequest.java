package happyyoung.trashnetwork.recycle.net.model.request;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-03-28
 */
public class RecycleBottleRequest {
    private long trashId;
    private int quantity;
    private double longitude;
    private double latitude;

    public RecycleBottleRequest(long trashId, int quantity, double longitude, double latitude) {
        this.trashId = trashId;
        this.quantity = quantity;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public long getTrashId() {
        return trashId;
    }

    public void setTrashId(long trashId) {
        this.trashId = trashId;
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
