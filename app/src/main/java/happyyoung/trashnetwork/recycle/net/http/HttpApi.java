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
        public static final String LOGOUT = "recycle/account/logout";
    }
}
