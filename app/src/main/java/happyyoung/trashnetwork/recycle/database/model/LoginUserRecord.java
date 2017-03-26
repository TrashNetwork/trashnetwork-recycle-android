package happyyoung.trashnetwork.recycle.database.model;

import android.graphics.Bitmap;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-03-25
 */

@Table(name = "LoginUserRecord")
public class LoginUserRecord extends Model {
    @Column(name = "UserId", notNull = true, index = true, unique = true)
    private long userId;

    @Column(name = "UserName", notNull = true, index = true, unique = true)
    private String userName;

    @Column(name = "Token", length = 40)
    private String token;

    @Column(name = "LastLoginTime", notNull = true)
    private Date lastLoginTime;

    public LoginUserRecord(){
        super();
    }

    public LoginUserRecord(long userId, String userName, String token, Date lastLoginTime) {
        super();
        this.userId = userId;
        this.userName = userName;
        this.token = token;
        this.lastLoginTime = lastLoginTime;
    }

    public LoginUserRecord(long userId, String userName, String token) {
        this(userId, userName, token, new Date());
    }

    public long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}
