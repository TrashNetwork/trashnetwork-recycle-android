package happyyoung.trashnetwork.recycle.net.model.request;

import happyyoung.trashnetwork.recycle.model.DeliveryAddress;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-07-09
 */
public class SubmitOrderRequest {
    private long commodityId;
    private int quantity = 1;
    private String remark;
    private DeliveryAddress deliveryAddress;

    public SubmitOrderRequest(long commodityId) {
        this.commodityId = commodityId;
    }

    public long getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(long commodityId) {
        this.commodityId = commodityId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
