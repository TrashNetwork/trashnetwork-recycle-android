package happyyoung.trashnetwork.recycle.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.akashandroid90.imageletter.MaterialLetterIcon;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.nekocode.badge.BadgeDrawable;
import happyyoung.trashnetwork.recycle.Application;
import happyyoung.trashnetwork.recycle.R;
import happyyoung.trashnetwork.recycle.model.CommodityPreview;
import happyyoung.trashnetwork.recycle.util.StringUtil;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-07-07
 */
public class CommodityPreviewAdapter extends RecyclerView.Adapter<CommodityPreviewAdapter.CommodityPreviewViewHolder> {
    private List<CommodityPreview> commodityPreviewList;
    private OnItemClickListener listener;

    public CommodityPreviewAdapter(List<CommodityPreview> commodityPreviewList, @Nullable OnItemClickListener listener) {
        this.commodityPreviewList = commodityPreviewList;
        this.listener = listener;
    }

    @Override
    public CommodityPreviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommodityPreviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_commodity_preview, parent, false));
    }

    private static float dp2px(Context context, int dp) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    @Override
    public void onBindViewHolder(CommodityPreviewViewHolder holder, final int position) {
        Context context = holder.itemView.getContext();
        final CommodityPreview cp = commodityPreviewList.get(position);
        if(cp.getThumbnail() != null){
            holder.imgPrev.setImageBitmap(cp.getThumbnail());
        }else{
            holder.imgPrev.setShapeColor(Application.generateColorFromStr(cp.getTitle()));
            holder.imgPrev.setLetter(StringUtil.getDigestLetters(cp.getTitle(), 3));
        }
        holder.txtTitle.setText(cp.getTitle());
        holder.txtCredit.setText(String.format(context.getString(R.string.commodity_credit_format), cp.getCredit()));
        if(System.currentTimeMillis() - cp.getAddedTime().getTime() <= 48 * 3600 * 1000) {     //48 hours
            BadgeDrawable newDrawable =
                    new BadgeDrawable.Builder()
                            .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                            .badgeColor(Color.RED)
                            .padding(dp2px(context, 4), dp2px(context, 4), dp2px(context, 4),
                                     dp2px(context, 4), dp2px(context, 4))
                            .text1("NEW")
                            .build();
            holder.imgNewBadge.setImageDrawable(newDrawable);
            holder.imgNewBadge.setVisibility(View.VISIBLE);
        }else{
            holder.imgNewBadge.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onItemClick(cp, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return commodityPreviewList.size();
    }

    public interface OnItemClickListener{
        void onItemClick(CommodityPreview cp, int position);
    }

    class CommodityPreviewViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.img_commodity_prev)
        MaterialLetterIcon imgPrev;
        @BindView(R.id.txt_commodity_title)
        TextView txtTitle;
        @BindView(R.id.txt_commodity_credit)
        TextView txtCredit;
        @BindView(R.id.img_new_badge)
        ImageView imgNewBadge;

        public CommodityPreviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
