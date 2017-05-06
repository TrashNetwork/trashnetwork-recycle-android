package happyyoung.trashnetwork.recycle.adapter;

import android.content.Context;
import android.graphics.Color;
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
import happyyoung.trashnetwork.recycle.model.CreditRank;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-05-06
 */
public class CreditRankAdapter extends RecyclerView.Adapter<CreditRankAdapter.CreditRankViewHolder> {
    private Context context;
    private List<CreditRank> rankList;

    public CreditRankAdapter(Context context, List<CreditRank> rankList) {
        this.context = context;
        this.rankList = rankList;
    }

    @Override
    public CreditRankViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CreditRankViewHolder(LayoutInflater.from(context).inflate(R.layout.item_credit_rank, parent, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBindViewHolder(CreditRankViewHolder holder, int position) {
        CreditRank rankItem = rankList.get(position);
        int rank = position;
        for(int i = position - 1; i >= 0; i--){
            if(!rankList.get(i).getCredit().equals(rankItem.getCredit()))
                break;
            rank = i;
        }
        rank++;
        holder.txtRank.setText(String.valueOf(rank));
        switch (rank){
            case 1:
                holder.txtRank.setTextColor(context.getResources().getColor(R.color.gold));
                break;
            case 2:
                holder.txtRank.setTextColor(context.getResources().getColor(R.color.silver));
                break;
            case 3:
                holder.txtRank.setTextColor(context.getResources().getColor(R.color.copper));
                break;
            default:
                holder.txtRank.setTextColor(Color.BLACK);
        }
        holder.txtUserName.setText(rankItem.getUserName());
        holder.userPortrait.setLetter(rankItem.getUserName());
        holder.userPortrait.setShapeColor(Application.generateColorFromStr(rankItem.getUserName()));
        String creditStr = String.valueOf(rankItem.getCredit());
        if(rankItem.getCredit() >= 0)
            creditStr = "+" + creditStr;
        holder.txtCreditDelta.setText(creditStr);
    }

    @Override
    public int getItemCount() {
        return rankList.size();
    }

    class CreditRankViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        @BindView(R.id.txt_rank)
        TextView txtRank;
        @BindView(R.id.user_portrait)
        MaterialLetterIcon userPortrait;
        @BindView(R.id.txt_username)
        TextView txtUserName;
        @BindView(R.id.txt_credit_delta)
        TextView txtCreditDelta;

        public CreditRankViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
