package happyyoung.trashnetwork.recycle.model;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-07-03
 */
public class Event {
    private String title;
    private Date releaseTime;
    private Bitmap eventImage;
    private String digest;
    private String url;

    public Event(String title, Date releaseTime, Bitmap eventImage, String digest, String url) {
        this.title = title;
        this.releaseTime = releaseTime;
        this.eventImage = eventImage;
        this.digest = digest;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public Bitmap getEventImage() {
        return eventImage;
    }

    public String getDigest() {
        return digest;
    }

    public String getUrl() {
        return url;
    }
}
