package happyyoung.trashnetwork.recycle.model;

import java.util.Date;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-03-22
 */
public class CreditRecord {
    private String goodDescription;
    private Integer quantity;
    private Integer credit;
    private Date recordTime;

    public CreditRecord(String goodDescription, Integer quantity, Integer credit, Date recordTime) {
        this.goodDescription = goodDescription;
        this.quantity = quantity;
        this.credit = credit;
        this.recordTime = recordTime;
    }

    public String getGoodDescription() {
        return goodDescription;
    }

    public void setGoodDescription(String goodDescription) {
        this.goodDescription = goodDescription;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    public Date getRecordTime() {
        return recordTime;
    }
}
