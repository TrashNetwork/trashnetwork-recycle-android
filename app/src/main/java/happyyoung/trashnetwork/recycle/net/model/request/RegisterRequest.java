package happyyoung.trashnetwork.recycle.net.model.request;

import happyyoung.trashnetwork.recycle.net.model.request.LoginRequest;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-03-25
 */
public class RegisterRequest extends LoginRequest {
    private String email;
    private char accountType;

    public RegisterRequest(String userName, String password, String email, char accountType) {
        super(userName, password);
        this.email = email;
        this.accountType = accountType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public char getAccountType() {
        return accountType;
    }

    public void setAccountType(char accountType) {
        this.accountType = accountType;
    }
}
