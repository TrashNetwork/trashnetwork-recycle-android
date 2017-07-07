package happyyoung.trashnetwork.recycle.net.model.result;

import java.util.List;

import happyyoung.trashnetwork.recycle.model.CommodityPreview;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-07-07
 */
public class CommodityPreviewListResult extends Result {
    private List<CommodityPreview> commodityList;

    public CommodityPreviewListResult(int resultCode, String message, List<CommodityPreview> commodityList) {
        super(resultCode, message);
        this.commodityList = commodityList;
    }

    public List<CommodityPreview> getCommodityList() {
        return commodityList;
    }
}
