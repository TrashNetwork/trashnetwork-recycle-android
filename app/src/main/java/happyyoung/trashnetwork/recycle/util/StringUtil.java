package happyyoung.trashnetwork.recycle.util;

/**
 * Created by shengyun-zhou <GGGZ-1101-28@Live.cn> on 2017-07-07
 */
public class StringUtil {
    public static String getDigestLetters(String str, int len){
        if(str.length() <= len)
            return str;
        String[] splitStr = str.split(" ");
        int[] avgLen = new int[splitStr.length];
        int c = 0;
        while (c < len){
            for(int i = 0; i < splitStr.length; i++){
                if(avgLen[i] < splitStr[i].length()) {
                    avgLen[i]++;
                    c++;
                    if(c >= len)
                        break;
                }
            }
        }
        String result = "";
        for(int i = 0; i < splitStr.length; i++) {
            if(avgLen[i] == 0)
                break;
            result += splitStr[i].substring(0, avgLen[i]);
        }
        return result;
    }
}
