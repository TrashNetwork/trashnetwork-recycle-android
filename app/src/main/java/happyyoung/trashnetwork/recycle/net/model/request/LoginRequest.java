package happyyoung.trashnetwork.recycle.net.model.request;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-02-20
 */
public class LoginRequest {
    private String userName;
    private String password;

    public LoginRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
