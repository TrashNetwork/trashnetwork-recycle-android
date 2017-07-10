package happyyoung.trashnetwork.recycle.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-07-08
 */
public class BitmapSlider extends TextSliderView {
    private Bitmap bitmap;
    private OnSliderClickListener listener;

    public BitmapSlider(Context context) {
        super(context);
    }

    public BitmapSlider image(Bitmap bitmap){
        this.bitmap = bitmap;
        return this;
    }

    @Override
    public BaseSliderView setOnSliderClickListener(OnSliderClickListener l) {
        return super.setOnSliderClickListener(l);
    }

    @Override
    protected void bindEventAndShow(View v, ImageView targetImageView) {
        if(bitmap == null){
            super.bindEventAndShow(v, targetImageView);
            return;
        }
        v.findViewById(com.daimajia.slider.library.R.id.loading_bar).setVisibility(View.GONE);
        v.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(listener != null) {
                    listener.onSliderClick(BitmapSlider.this);
                }
            }
        });
        if(targetImageView != null) {
            if(getScaleType() == ScaleType.Fit){
                targetImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }else if(getScaleType() == ScaleType.FitCenterCrop){
                targetImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }else if(getScaleType() == ScaleType.CenterCrop){
                targetImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }else if(getScaleType() == ScaleType.CenterInside){
                targetImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
            targetImageView.setImageBitmap(bitmap);
        }
    }
}
