package happyyoung.trashnetwork.recycle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.akashandroid90.imageletter.MaterialLetterIcon;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import happyyoung.trashnetwork.recycle.Application;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.model.Feedback;
import happyyoung.trashnetwork.recycle.util.DateTimeUtil;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-03-15
 */
public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {
    private Context context;
    private List<Feedback> feedbackList;

    public FeedbackAdapter(Context context, List<Feedback> feedbackList) {
        this.context = context;
        this.feedbackList = feedbackList;
    }

    @Override
    public FeedbackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FeedbackViewHolder(LayoutInflater.from(context).inflate(R.layout.item_feedback, parent, false));
    }

    @Override
    public void onBindViewHolder(FeedbackViewHolder holder, int position) {
        Feedback fb = feedbackList.get(position);
        if(fb.getUserName() != null && !fb.getUserName().isEmpty()) {
            holder.feedbackPortrait.setShapeColor(Application.generateColorFromStr(fb.getUserName()));
            holder.txtFeedbackUsername.setText(fb.getUserName());
            holder.feedbackPortrait.setLettersNumber(2);
            holder.feedbackPortrait.setLetter(fb.getUserName());
        }else {
            holder.feedbackPortrait.setShapeColor(Application.getRandomColor());
            holder.txtFeedbackUsername.setText(context.getString(R.string.anonymous_user));
            holder.feedbackPortrait.setImageResource(R.drawable.ic_person_white_96dp);
        }
        holder.txtFeedbackTime.setText(DateTimeUtil.convertTimestamp(context, fb.getFeedbackTime(), true, true, false));
        holder.txtFeedbackTitle.setText(fb.getTitle());
        holder.txtFeedbackContent.setText(fb.getTextContent());
    }


    @Override
    public int getItemCount() {
        return feedbackList.size();
    }


    class FeedbackViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.feedback_portrait) MaterialLetterIcon feedbackPortrait;
        @BindView(R.id.txt_feedback_username) TextView txtFeedbackUsername;
        @BindView(R.id.txt_feedback_time) TextView txtFeedbackTime;
        @BindView(R.id.txt_feedback_title) TextView txtFeedbackTitle;
        @BindView(R.id.txt_feedback_content) TextView txtFeedbackContent;

        public FeedbackViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
