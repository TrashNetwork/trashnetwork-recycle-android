package happyyoung.trashnetwork.recycle.model;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-02-19
 */
public class User {
    public static final char USER_TYPE_NORMAL_USER = 'N';
    public static final char USER_TYPE_GARBAGE_COLLECTOR = 'C';

    private Long userId;
    private String userName;
    private Character accountType;
    private Integer credit;

    public User(Long userId, String userName, Character accountType, Integer credit) {
        this.userId = userId;
        this.userName = userName;
        this.accountType = accountType;
        this.credit = credit;
    }

    public User(Long userId, String userName, Character accountType) {
        this(userId, userName, accountType, 0);
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

    public Character getAccountType() {
        return accountType;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof User){
            return userId.equals(((User) obj).userId);
        }
        return false;
    }
}
