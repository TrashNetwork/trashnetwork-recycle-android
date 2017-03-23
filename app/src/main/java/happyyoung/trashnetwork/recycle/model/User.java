package happyyoung.trashnetwork.recycle.model;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-02-19
 */
public class User {
    private Long userId;
    private String userName;
    private Integer credit;

    public User(Long userId, String userName) {
        this(userId, userName, 0);
    }

    public User(Long userId, String userName, Integer credit) {
        this.userId = userId;
        this.userName = userName;
        this.credit = credit;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
