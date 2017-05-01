package happyyoung.trashnetwork.recycle.net.model.request;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-04-30
 */
public class RecyclePointCleanRequest {
    private long recyclePointId;

    public RecyclePointCleanRequest(long recyclePointId) {
        this.recyclePointId = recyclePointId;
    }

    public long getRecyclePointId() {
        return recyclePointId;
    }

    public void setRecyclePointId(long recyclePointId) {
        this.recyclePointId = recyclePointId;
    }
}
