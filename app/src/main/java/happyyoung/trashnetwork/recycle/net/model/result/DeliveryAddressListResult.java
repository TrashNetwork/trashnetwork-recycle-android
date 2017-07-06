package happyyoung.trashnetwork.recycle.net.model.result;

import java.util.List;

import happyyoung.trashnetwork.recycle.model.DeliveryAddress;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-07-05
 */
public class DeliveryAddressListResult extends Result {
    private List<DeliveryAddress> addressList;

    public DeliveryAddressListResult(int resultCode, String message, List<DeliveryAddress> addressList) {
        super(resultCode, message);
        this.addressList = addressList;
    }

    public List<DeliveryAddress> getAddressList() {
        return addressList;
    }
}
