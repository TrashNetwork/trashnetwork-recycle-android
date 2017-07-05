package happyyoung.trashnetwork.recycle.net.model.result;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-03-28
 */
public class RecycleResult extends Result {
    private Integer credit;
    private Integer redPacketCredit;

    public RecycleResult(int resultCode, String message, int credit, int redPacketCredit) {
        super(resultCode, message);
        this.credit = credit;
        this.redPacketCredit = redPacketCredit;
    }

    public Integer getCredit() {
        return credit;
    }

    public Integer getRedPacketCredit() {
        return redPacketCredit;
    }
}
