package admin.sgss.maplocation;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Admin on 4/10/2018.
 */

public class NetworkUtil {

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    // Whether the display should be refreshed.
    private static boolean state = false;





    public static int getConnectivityStatus(Context context) {
        if(context!=null) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {
                wifiConnected = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
                if (wifiConnected) {
                    return TYPE_WIFI;
                }
                mobileConnected = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
                if (mobileConnected) {
                    return TYPE_MOBILE;
                }
            } else {
                wifiConnected = false;
                mobileConnected = false;
            }
        }
        return TYPE_NOT_CONNECTED;

    }

    public static boolean getConnectivityStatusString(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        //  String status = null;
        if (conn == NetworkUtil.TYPE_WIFI) {
            state = true;
        } else if (conn == NetworkUtil.TYPE_MOBILE) {
            state = true;
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
            state = false;
        }
        return state;
    }
}