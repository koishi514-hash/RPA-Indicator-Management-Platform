package com.rbac.common.utils;

import com.rbac.common.annotation.Desensitize;
import org.apache.commons.lang3.StringUtils;

/**
 * 数据脱敏工具类
 */
public class DesensitizeUtils {

    /**
     * 脱敏字符
     */
    private static final String MASK_CHAR = "*";

    /**
     * 根据类型脱敏
     *
     * @param str  原始字符串
     * @param type 脱敏类型
     * @return 脱敏后的字符串
     */
    public static String desensitize(String str, Desensitize.DesensitizeType type) {
        if (StringUtils.isBlank(str)) {
            return str;
        }

        switch (type) {
            case NAME:
                return desensitizeName(str);
            case MOBILE:
                return desensitizeMobile(str);
            case ID_CARD:
                return desensitizeIdCard(str);
            case BANK_CARD:
                return desensitizeBankCard(str);
            case EMAIL:
                return desensitizeEmail(str);
            case ADDRESS:
                return desensitizeAddress(str);
            default:
                return str;
        }
    }

    /**
     * 自定义脱敏
     *
     * @param str   原始字符串
     * @param start 开始位置（包含）
     * @param end   结束位置（包含）
     * @return 脱敏后的字符串
     */
    public static String desensitize(String str, int start, int end) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        if (start < 0 || end >= str.length() || start > end) {
            return str;
        }
        return str.substring(0, start) + MASK_CHAR.repeat(end - start + 1) + str.substring(end + 1);
    }

    /**
     * 姓名脱敏（保留姓）
     */
    public static String desensitizeName(String name) {
        if (StringUtils.isBlank(name) || name.length() < 2) {
            return name;
        }
        return name.charAt(0) + MASK_CHAR.repeat(name.length() - 1);
    }

    /**
     * 手机号脱敏（保留前3位和后4位）
     */
    public static String desensitizeMobile(String mobile) {
        if (StringUtils.isBlank(mobile) || mobile.length() != 11) {
            return mobile;
        }
        return mobile.substring(0, 3) + MASK_CHAR.repeat(4) + mobile.substring(7);
    }

    /**
     * 身份证号脱敏（保留前6位和后4位）
     */
    public static String desensitizeIdCard(String idCard) {
        if (StringUtils.isBlank(idCard) || (idCard.length() != 15 && idCard.length() != 18)) {
            return idCard;
        }
        return idCard.substring(0, 6) + MASK_CHAR.repeat(idCard.length() - 10) + idCard.substring(idCard.length() - 4);
    }

    /**
     * 银行卡号脱敏（保留前6位和后4位）
     */
    public static String desensitizeBankCard(String bankCard) {
        if (StringUtils.isBlank(bankCard) || bankCard.length() < 10) {
            return bankCard;
        }
        return bankCard.substring(0, 6) + MASK_CHAR.repeat(bankCard.length() - 10) + bankCard.substring(bankCard.length() - 4);
    }

    /**
     * 邮箱脱敏（保留@前1位和@后全部）
     */
    public static String desensitizeEmail(String email) {
        if (StringUtils.isBlank(email) || !email.contains("@")) {
            return email;
        }
        int index = email.indexOf("@");
        if (index <= 1) {
            return email;
        }
        return email.charAt(0) + MASK_CHAR.repeat(index - 1) + email.substring(index);
    }

    /**
     * 地址脱敏（保留前6位）
     */
    public static String desensitizeAddress(String address) {
        if (StringUtils.isBlank(address) || address.length() < 8) {
            return address;
        }
        return address.substring(0, 6) + MASK_CHAR.repeat(address.length() - 6);
    }
}
