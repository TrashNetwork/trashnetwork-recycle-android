package happyyoung.trashnetwork.recycle.net.model.result;

import java.util.List;

import happyyoung.trashnetwork.recycle.model.Order;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-07-09
 */
public class OrderListResult extends Result {
    private List<Order> orderList;

    public OrderListResult(int resultCode, String message, List<Order> orderList) {
        super(resultCode, message);
        this.orderList = orderList;
    }

    public List<Order> getOrderList() {
        return orderList;
    }
}
