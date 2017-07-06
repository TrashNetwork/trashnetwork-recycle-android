package happyyoung.trashnetwork.recycle.net.http;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-02-12
 */
public class HttpApi {
    public static String BASE_URL_V1;
    private static RequestQueue requestQueue = null;

    public static String getApiUrl(String... urlParam){
        String url = BASE_URL_V1;
        for(String s : urlParam){
            if(s != null && !s.isEmpty())
                url += '/' + s;
        }
        return url;
    }

    public static void startRequest(HttpApiRequest req){
        if(requestQueue == null)
            requestQueue = Volley.newRequestQueue(req.getContext());
        requestQueue.add(req);
    }

    public static class AccountApi{
        public static final String LOGIN = "recycle/account/login";
        public static final String REGISTER = "recycle/account/register";
        public static final String LOGOUT = "recycle/account/logout";
        public static final String CHECK_LOGIN = "recycle/account/check_login";
        public static final String SELF_INFO = "recycle/account/self";
        public static final String DELIVERY_ADDRESS = "recycle/account/delivery_address";
        public static final String NEW_DELIVERY_ADDRESS = "recycle/account/delivery_address/new";
    }

    public static class CreditRecordApi{
        public static final String QUERY_RECORD = "recycle/credit/record";
        public static final String POST_RECORD_BY_BOTTLE_RECYCLE = "recycle/credit/record/new/bottle_recycle";
    }

    public static class FeedbackApi{
        public static final String QUERY_FEEDBACK = "public/feedback/feedbacks";
        public static final String POST_FEEDBACK = "recycle/feedback/new_feedback";
    }

    public static class RecyclePointApi{
        public static final String ALL_POINTS = "recycle/recycle_point/all_points";
    }

    public static class RecycleRecordApi{
        public static final String NEW_RECORD = "recycle/recycle_record/new_record";
        public static final String QUERY_RECORD = "recycle/recycle_record";
    }

    public static class CreditRankApi{
        public static final String DAILY_RANK = "recycle/credit_rank/day";
        public static final String WEEKLY_RANK = "recycle/credit_rank/week";
    }

    public static class EventApi{
        public static final String EVENTS = "recycle/event";
    }
}
