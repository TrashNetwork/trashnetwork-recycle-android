package happyyoung.trashnetwork.recycle.model;

import java.util.Date;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-04-30
 */
public class RecycleRecord {
    private Long recyclePointId;
    private Date recycleTime;
    private Integer bottleNum;

    public RecycleRecord(Long recyclePointId, Date recycleTime, Integer bottleNum) {
        this.recyclePointId = recyclePointId;
        this.recycleTime = recycleTime;
        this.bottleNum = bottleNum;
    }

    public Long getRecyclePointId() {
        return recyclePointId;
    }

    public Date getRecycleTime() {
        return recycleTime;
    }

    public Integer getBottleNum() {
        return bottleNum;
    }

    public void setBottleNum(Integer bottleNum) {
        this.bottleNum = bottleNum;
    }
}
