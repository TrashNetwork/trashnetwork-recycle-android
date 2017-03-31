package happyyoung.trashnetwork.recycle.net.model.result;

import java.util.List;

import happyyoung.trashnetwork.recycle.model.Feedback;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-03-24
 */
public class FeedbackListResult extends Result {
    private List<Feedback> feedbackList;

    public FeedbackListResult(int resultCode, String message, List<Feedback> feedbackList) {
        super(resultCode, message);
        this.feedbackList = feedbackList;
    }

    public List<Feedback> getFeedbackList() {
        return feedbackList;
    }

    public void setFeedbackList(List<Feedback> feedbackList) {
        this.feedbackList = feedbackList;
    }
}
