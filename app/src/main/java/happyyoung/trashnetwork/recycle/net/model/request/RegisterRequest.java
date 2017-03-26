package happyyoung.trashnetwork.recycle.net.model.request;

import happyyoung.trashnetwork.recycle.net.model.request.LoginRequest;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-03-25
 */
public class RegisterRequest extends LoginRequest {
    private String email;

    public RegisterRequest(String userName, String password, String email) {
        super(userName, password);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
