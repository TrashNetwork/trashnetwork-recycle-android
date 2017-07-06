package happyyoung.trashnetwork.recycle.model;

import java.util.Date;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-03-22
 */
public class CreditRecord {
    private String itemDescription;
    private Integer credit;
    private Date recordTime;

    public CreditRecord(String itemDescription, Integer credit, Date recordTime) {
        this.itemDescription = itemDescription;
        this.credit = credit;
        this.recordTime = recordTime;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public Integer getCredit() {
        return credit;
    }

    public Date getRecordTime() {
        return recordTime;
    }
}
