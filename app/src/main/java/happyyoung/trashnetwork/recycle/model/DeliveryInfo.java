package happyyoung.trashnetwork.recycle.model;

import java.io.Serializable;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-07-09
 */
public class DeliveryInfo implements Serializable {
    private String company;
    private String waybillId;

    public DeliveryInfo(String company, String waybillId) {
        this.company = company;
        this.waybillId = waybillId;
    }

    public String getCompany() {
        return company;
    }

    public String getWaybillId() {
        return waybillId;
    }
}
