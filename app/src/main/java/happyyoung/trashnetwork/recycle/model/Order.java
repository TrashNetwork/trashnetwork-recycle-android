package happyyoung.trashnetwork.recycle.model;

import android.content.Context;

import java.util.Date;

import happyyoung.trashnetwork.recycle.R;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-07-09
 */
public class Order {
    public static final String ORDER_STATUS_IN_PROGRESS = "P";
    public static final String ORDER_STATUS_DELIVERING = "D";
    public static final String ORDER_STATUS_FINISHED = "F";
    public static final String ORDER_STATUS_CANCELLED = "C";

    private String orderId;
    private Date submitTime;
    private Long commodityId;
    private String title;
    private Integer credit;
    private Integer quantity;
    private DeliveryAddress deliveryAddress;
    private DeliveryInfo delivery;
    private String remark;
    private String status;


    public Order(String orderId, Date submitTime, Long commodityId, String title, Integer credit, Integer quantity, DeliveryAddress deliveryAddress, DeliveryInfo delivery, String remark, String status) {
        this.orderId = orderId;
        this.submitTime = submitTime;
        this.commodityId = commodityId;
        this.title = title;
        this.credit = credit;
        this.quantity = quantity;
        this.deliveryAddress = deliveryAddress;
        this.delivery = delivery;
        this.remark = remark;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public Date getSubmitTime() {
        return submitTime;
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

    public Integer getQuantity() {
        return quantity;
    }

    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public DeliveryInfo getDelivery() {
        return delivery;
    }

    public String getRemark() {
        return remark;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusLiteral(Context context){
        switch (status){
            case ORDER_STATUS_IN_PROGRESS:
                return context.getResources().getStringArray(R.array.order_status)[0];
            case ORDER_STATUS_DELIVERING:
                return context.getResources().getStringArray(R.array.order_status)[1];
            case ORDER_STATUS_CANCELLED:
                return context.getResources().getStringArray(R.array.order_status)[2];
            case ORDER_STATUS_FINISHED:
                return context.getResources().getStringArray(R.array.order_status)[3];
        }
        return "";
    }
}
