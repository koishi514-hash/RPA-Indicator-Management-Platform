package com.rbac.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

@SpringBootApplication(scanBasePackages = "com.rbac")
@MapperScan("com.rbac.core.domain.mapper")
public class RbacSystemApplication {

    public static void main(String[] args) {

        // 获取本地IP地址
        String localIp = "localhost";
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            localIp = inetAddress.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 获取无线局域网IP地址
        String wifiIp = getWifiIpAddress();

        SpringApplication.run(RbacSystemApplication.class, args);
        System.out.println("\n==========================================");
        System.out.println("接口文档地址: http://localhost:8080/doc.html");
//        System.out.println("本地IP地址: " + localIp);
        System.out.println("无线局域网IP地址: " + wifiIp);
        System.out.println("==========================================\n");
    }

    /**
     * 获取无线局域网的IPv4地址
     */
    private static String getWifiIpAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                // 跳过回环接口和未启动的接口
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                // 检查是否是无线接口（名称包含wlan或wireless）
                String interfaceName = networkInterface.getName().toLowerCase();
                if (interfaceName.contains("wlan") || interfaceName.contains("wireless") || interfaceName.contains("wi-fi")) {
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        // 只返回IPv4地址
                        if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().contains(".")) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

}
