package happyyoung.trashnetwork.recycle.model;

import android.graphics.Bitmap;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-02-19
 */
public class User {
    private Long userId;
    private String phoneNumber;
    private Integer credit;

    public User(Long userId, String phoneNumber) {
        this(userId, phoneNumber, 0);
    }

    public User(Long userId, String phoneNumber, Integer credit) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.credit = credit;
    }

    public Long getUserId() {
        return userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof User){
            return userId.equals(((User) obj).userId);
        }
        return false;
    }
}
