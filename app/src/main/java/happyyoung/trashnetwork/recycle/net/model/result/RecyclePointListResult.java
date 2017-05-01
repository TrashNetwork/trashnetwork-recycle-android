package happyyoung.trashnetwork.recycle.net.model.result;

import java.util.List;

import happyyoung.trashnetwork.recycle.model.RecyclePoint;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-03-29
 */
public class RecyclePointListResult extends Result {
    private List<RecyclePoint> recyclePointList;

    public RecyclePointListResult(int resultCode, String message, List<RecyclePoint> recyclePointList) {
        super(resultCode, message);
        this.recyclePointList = recyclePointList;
    }

    public List<RecyclePoint> getRecyclePointList() {
        return recyclePointList;
    }

    public void setRecyclePointList(List<RecyclePoint> recyclePointList) {
        this.recyclePointList = recyclePointList;
    }
}
