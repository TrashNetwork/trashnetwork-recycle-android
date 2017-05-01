package happyyoung.trashnetwork.recycle.net.model.result;

import java.util.List;

import happyyoung.trashnetwork.recycle.model.RecycleRecord;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-04-30
 */
public class RecycleRecordListResult extends Result {
    private List<RecycleRecord> recycleRecordList;

    public RecycleRecordListResult(int resultCode, String message, List<RecycleRecord> recycleRecordList) {
        super(resultCode, message);
        this.recycleRecordList = recycleRecordList;
    }

    public List<RecycleRecord> getRecycleRecordList() {
        return recycleRecordList;
    }
}
