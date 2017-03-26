package happyyoung.trashnetwork.recycle.util;

import com.activeandroid.query.Select;

import java.util.Date;
import java.util.List;

import happyyoung.trashnetwork.recycle.database.model.LoginUserRecord;
import happyyoung.trashnetwork.recycle.model.User;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-03-25
 */
public class DatabaseUtil {
    public static void updateLoginRecord(User user, String token){
        LoginUserRecord loginRecord = findLoginUserRecord(user.getUserId());
        if(loginRecord == null){
            loginRecord = new LoginUserRecord(user.getUserId(), user.getUserName(), token);
            loginRecord.save();
        }else{
            loginRecord.setLastLoginTime(new Date());
            loginRecord.setToken(token);
            loginRecord.save();
        }
    }

    public static List<LoginUserRecord> findAllLoginUserRecords(int limit){
        return new Select().from(LoginUserRecord.class)
                .orderBy("LastLoginTime DESC").limit(limit).execute();
    }

    public static LoginUserRecord findLoginUserRecord(long userId){
        return new Select().from(LoginUserRecord.class).where("UserId=?", userId)
                .executeSingle();
    }
}
