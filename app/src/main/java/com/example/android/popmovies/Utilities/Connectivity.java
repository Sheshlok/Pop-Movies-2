/*
 *  Copyright (C) 2016 Sheshlok Samal
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package com.example.android.popmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Created by sheshloksamal on 8/11/15.
 * A utility class for checking device's network connectivity & speed.
 * Writing code by hand for practice. Original author is listed below.
 * @author emil http://stackoverflow.com/users/220710/emil
 * @link   https://gist.github.com/emil2k/5130324
 */



public class Connectivity {

    /**
     * Get the network info
     */

    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo();
    }

    /**
     * Check if there is any connectivity
     */
    public static boolean isConnected(Context context) {
        NetworkInfo networkInfo = Connectivity.getNetworkInfo(context);
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check if there is any connectivity to Wifi network
     */

    public static boolean isConnectedWifi(Context context) {
        NetworkInfo networkInfo = Connectivity.getNetworkInfo(context);
        return (networkInfo != null && networkInfo.isConnected() &&
                networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * Check if there is any connectivity to mobile network
     */

    public static boolean isConnectedMobile(Context context) {
        NetworkInfo networkInfo = Connectivity.getNetworkInfo(context);
        return (networkInfo != null && networkInfo.isConnected() &&
                networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * Check if there is Fast Connectivity
     */
    public static boolean isConnectedFast(Context context) {
        NetworkInfo networkInfo = Connectivity.getNetworkInfo(context);
        return (networkInfo != null && networkInfo.isConnected() &&
                Connectivity.isConnectionFast(networkInfo.getType(), networkInfo.getSubtype()));
    }

    /**
     * Check if the connection is fast
     * @param type    Type of connection, i.e. Wifi/Mobile etc.
     * @param subType subType of connection, for e.g. in case of Mobile, 3G/GPRS/4G etc.
     */
    public static boolean isConnectionFast(int type, int subType) {
        if(type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE){
            switch(subType) {
                /* Slow Mobile networks, i.e. speed less than 100 kbps*/
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~50-100 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS: //2.5G
                    return false; // ~35 - 171 kbps (100 kbps)
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return false; // ~25 kbps

                /* Fast Mobile Networks, i.e. speed at least 100 kbps */
                case TelephonyManager.NETWORK_TYPE_UMTS: //3G
                    return true; // ~400-7000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return true; // ~5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA: //3.5G
                    return true; // ~700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return true; // ~1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return true; // ~10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_LTE: //4G
                    return true; // ~10+ Mbps

                //Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    return false;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }
}
