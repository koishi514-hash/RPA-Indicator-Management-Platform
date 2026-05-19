package com.rbac.common.utils;

import java.util.UUID;

/**
 * ID生成工具类
 */
public class IdUtils {

    /**
     * 生成UUID（去掉横线）
     *
     * @return UUID
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成UUID（保留横线）
     *
     * @return UUID
     */
    public static String uuidWithHyphen() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成订单号（时间戳 + 随机数）
     *
     * @return 订单号
     */
    public static String generateOrderNo() {
        return System.currentTimeMillis() + String.format("%06d", (int) (Math.random() * 1000000));
    }

    /**
     * 生成交易流水号
     *
     * @return 交易流水号
     */
    public static String generateTransactionNo() {
        return "TXN" + System.currentTimeMillis() + String.format("%04d", (int) (Math.random() * 10000));
    }

    /**
     * 生成请求ID
     *
     * @return 请求ID
     */
    public static String generateRequestId() {
        return "REQ" + System.currentTimeMillis() + String.format("%04d", (int) (Math.random() * 10000));
    }
}
