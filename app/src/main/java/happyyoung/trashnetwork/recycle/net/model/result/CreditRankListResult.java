package happyyoung.trashnetwork.recycle.net.model.result;

import java.util.Date;
import java.util.List;

import happyyoung.trashnetwork.recycle.model.CreditRank;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-05-05
 */
public class CreditRankListResult extends Result {
    private Date updateTime;
    private Integer rank;
    private Integer credit;
    private List<CreditRank> rankList;

    public CreditRankListResult(int resultCode, String message, Date updateTime, Integer rank, Integer credit, List<CreditRank> rankList) {
        super(resultCode, message);
        this.updateTime = updateTime;
        this.rank = rank;
        this.credit = credit;
        this.rankList = rankList;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public List<CreditRank> getRankList() {
        return rankList;
    }

    public void setRankList(List<CreditRank> rankList) {
        this.rankList = rankList;
    }
}
