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
        v.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(listener != null) {
                    listener.onSliderClick(BitmapSlider.this);
                }
            }
        });
        if(targetImageView != null) {
            switch (getScaleType().ordinal()){
                case 1:
                    targetImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                case 2:
                    targetImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    break;
                case 3:
                    targetImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    break;
            }
            targetImageView.setImageBitmap(bitmap);
        }
    }
}
