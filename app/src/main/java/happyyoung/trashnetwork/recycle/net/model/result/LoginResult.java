package happyyoung.trashnetwork.recycle.net.model.result;

import happyyoung.trashnetwork.recycle.model.User;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-02-20
 */
public class LoginResult extends Result {
    private String token;
    private User user;

    public LoginResult(int resultCode, String message, String token) {
        super(resultCode, message);
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
