package com.core.server.wxtools;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class GetRealIp {
    public static String getRealIp() throws SocketException {
        String localip = null;
        String netip = null;
        Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;
        boolean finded = false;

        while(netInterfaces.hasMoreElements() && !finded) {
            NetworkInterface ni = (NetworkInterface)netInterfaces.nextElement();
            Enumeration address = ni.getInetAddresses();

            while(address.hasMoreElements()) {
                ip = (InetAddress)address.nextElement();
                if(!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
                    netip = ip.getHostAddress();
                    finded = true;
                    break;
                }

                if(ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
                    localip = ip.getHostAddress();
                }
            }
        }

        return netip != null && !"".equals(netip)?netip:localip;
    }
}
