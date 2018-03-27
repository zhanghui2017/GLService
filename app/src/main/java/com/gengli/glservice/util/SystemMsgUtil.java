package com.gengli.glservice.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 获取android设备信息，工具类
 *
 * @author psx
 * @category tools
 */


public class SystemMsgUtil {
    private static String TAG = "SystemMsg";

    /**
     * 版本号
     */
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    /**
     * 获取app包信息
     */
    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pi;
    }

    /**
     * 版本名
     */
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    /**
     * 获取IMEI
     * 需要权限:android.permission.READ_PHONE_STATE,水货手机IMEI可能为无效信息。
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imEI = telephonyManager.getDeviceId();
        Log.i(TAG, "--the IMEI of this System is ：" + imEI);
        return imEI;
    }

    /**
     * 获取系统硬件设备信息,取其长度前10位进行拼接，在前面加上35,Build.CPU_ABI.length()%10 +
     */
    public static String getUniqueID() {
        String myUniqueId = "35" +
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 digits
        return myUniqueId;
    }

    /**
     * 获取android ID
     * Id 可变，一般不单独使用。（有时为null，恢复出厂设置或者root都可使其改变）
     */
    public static String getAndroidId(Context context) {
        String myAndroidId = Settings.System.getString(context.getContentResolver(), Secure.ANDROID_ID);
        Log.i(TAG, "--the Android Id of this System is ：" + myAndroidId);
        return myAndroidId;
    }
//	/**
//	 * 获取WLAN MAC 地址
//	 * 需要权限android.permission.ACCESS_WIFI_STATE ，无则返回null
//	 * 不必打开wlan即可获取，地址可被伪造。
//	 * */
//	public static String getWlanMAC(Context context){
//		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//		String MAC = wifiManager.getConnectionInfo().getMacAddress();
//		Log.i(TAG, "--the WLAN MAC of this System is ：" + MAC);
//		return MAC;
//	}
//	/**
//	 * 获取蓝牙MAC地址
//	 * 需要权限：android.permission.BLUETOOTH
//	 * */
//	public static String getBluetoothMac(){
//		BluetoothAdapter bluetoothAdapter = null;
//		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//		String mac = bluetoothAdapter.getAddress();
//		Log.i(TAG, "--the WLAN MAC of this System is ：" + mac);
//		return mac;
//	}

    /**
     * 获取SerialNumber Android2.3以上支持。通用型
     */
    public static String getSingleKey(Context context) {
        String serial = Build.SERIAL;
        return MD5Utils.toMd5(serial);
    }


    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
}
