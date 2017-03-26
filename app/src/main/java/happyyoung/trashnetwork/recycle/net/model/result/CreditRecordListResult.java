package happyyoung.trashnetwork.recycle.net.model.result;

import java.util.List;

import happyyoung.trashnetwork.recycle.model.CreditRecord;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-03-26
 */
public class CreditRecordListResult extends Result {
    List<CreditRecord> creditRecordList;

    public CreditRecordListResult(int resultCode, String message, List<CreditRecord> creditRecordList) {
        super(resultCode, message);
        this.creditRecordList = creditRecordList;
    }

    public List<CreditRecord> getCreditRecordList() {
        return creditRecordList;
    }

    public void setCreditRecordList(List<CreditRecord> creditRecordList) {
        this.creditRecordList = creditRecordList;
    }
}
