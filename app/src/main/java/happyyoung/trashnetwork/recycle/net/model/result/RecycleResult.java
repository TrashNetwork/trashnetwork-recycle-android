package happyyoung.trashnetwork.recycle.net.model.result;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-03-28
 */
public class RecycleResult extends Result {
    private int credit;

    public RecycleResult(int resultCode, String message, int credit) {
        super(resultCode, message);
        this.credit = credit;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }
}
