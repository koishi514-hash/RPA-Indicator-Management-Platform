package com.rbac.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金额计算工具类（金融级精度）
 */
public class MoneyUtils {

    /**
     * 默认精度（小数点后2位）
     */
    private static final int DEFAULT_SCALE = 2;

    /**
     * 默认舍入模式（四舍五入）
     */
    private static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * 元转分
     *
     * @param yuan 元
     * @return 分
     */
    public static Long yuanToFen(BigDecimal yuan) {
        if (yuan == null) {
            return 0L;
        }
        return yuan.multiply(new BigDecimal("100")).longValue();
    }

    /**
     * 分转元
     *
     * @param fen 分
     * @return 元
     */
    public static BigDecimal fenToYuan(Long fen) {
        if (fen == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(fen).divide(new BigDecimal("100"), DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 加法
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 和
     */
    public static BigDecimal add(BigDecimal v1, BigDecimal v2) {
        if (v1 == null) {
            v1 = BigDecimal.ZERO;
        }
        if (v2 == null) {
            v2 = BigDecimal.ZERO;
        }
        return v1.add(v2).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 减法
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 差
     */
    public static BigDecimal subtract(BigDecimal v1, BigDecimal v2) {
        if (v1 == null) {
            v1 = BigDecimal.ZERO;
        }
        if (v2 == null) {
            v2 = BigDecimal.ZERO;
        }
        return v1.subtract(v2).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 乘法
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 积
     */
    public static BigDecimal multiply(BigDecimal v1, BigDecimal v2) {
        if (v1 == null) {
            v1 = BigDecimal.ZERO;
        }
        if (v2 == null) {
            v2 = BigDecimal.ZERO;
        }
        return v1.multiply(v2).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 除法
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 商
     */
    public static BigDecimal divide(BigDecimal v1, BigDecimal v2) {
        if (v1 == null) {
            v1 = BigDecimal.ZERO;
        }
        if (v2 == null || v2.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("除数不能为0");
        }
        return v1.divide(v2, DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 比较大小
     *
     * @param v1 数值1
     * @param v2 数值2
     * @return v1 > v2 返回1，v1 = v2 返回0，v1 < v2 返回-1
     */
    public static int compare(BigDecimal v1, BigDecimal v2) {
        if (v1 == null) {
            v1 = BigDecimal.ZERO;
        }
        if (v2 == null) {
            v2 = BigDecimal.ZERO;
        }
        return v1.compareTo(v2);
    }

    /**
     * 判断是否为正数
     *
     * @param value 数值
     * @return 是否为正数
     */
    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * 判断是否为负数
     *
     * @param value 数值
     * @return 是否为负数
     */
    public static boolean isNegative(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * 判断是否为零
     *
     * @param value 数值
     * @return 是否为零
     */
    public static boolean isZero(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * 格式化金额（保留2位小数）
     *
     * @param value 金额
     * @return 格式化后的金额
     */
    public static String format(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }
        return value.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE).toString();
    }
}
