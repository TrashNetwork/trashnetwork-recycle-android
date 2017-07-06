package happyyoung.trashnetwork.recycle.net.model.request;

import java.util.List;

import happyyoung.trashnetwork.recycle.model.DeliveryAddress;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-07-05
 */
public class NewDeliveryAddressRequest {
    private List<DeliveryAddress> newAddrList;

    public NewDeliveryAddressRequest(List<DeliveryAddress> newAddrList) {
        this.newAddrList = newAddrList;
    }

    public List<DeliveryAddress> getNewAddrList() {
        return newAddrList;
    }

    public void setNewAddrList(List<DeliveryAddress> newAddrList) {
        this.newAddrList = newAddrList;
    }
}
