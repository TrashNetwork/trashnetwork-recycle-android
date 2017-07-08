package happyyoung.trashnetwork.recycle.net.model.result;

import happyyoung.trashnetwork.recycle.model.Commodity;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-07-08
 */
public class CommodityDetailResult extends Result {
    private Commodity commodity;

    public CommodityDetailResult(int resultCode, String message, Commodity commodity) {
        super(resultCode, message);
        this.commodity = commodity;
    }

    public Commodity getCommodity() {
        return commodity;
    }
}
