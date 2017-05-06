package happyyoung.trashnetwork.recycle.model;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-05-05
 */
public class CreditRank {
    private String userName;
    private Integer credit;

    public CreditRank(String userName, Integer credit) {
        this.userName = userName;
        this.credit = credit;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }
}
