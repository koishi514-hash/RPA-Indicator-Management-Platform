package com.rbac.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    FAILED(500, "操作失败"),

    /**
     * 参数错误
     */
    VALIDATE_FAILED(400, "参数校验失败"),

    /**
     * 未认证
     */
    UNAUTHORIZED(401, "未认证或认证已过期"),

    /**
     * 无权限
     */
    FORBIDDEN(403, "无权限访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 请求方法不支持
     */
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(408, "请求超时"),

    /**
     * 重复提交
     */
    REPEAT_SUBMIT(409, "请勿重复提交"),

    /**
     * 限流
     */
    RATE_LIMIT(429, "访问过于频繁，请稍后再试"),

    /**
     * 服务降级
     */
    DEGRADED(503, "服务繁忙，请稍后再试"),

    /**
     * 业务异常
     */
    BUSINESS_ERROR(600, "业务异常"),

    /**
     * 用户不存在
     */
    USER_NOT_EXIST(1001, "用户不存在"),

    /**
     * 用户已存在
     */
    USER_ALREADY_EXIST(1002, "用户已存在"),

    /**
     * 用户名或密码错误
     */
    USERNAME_OR_PASSWORD_ERROR(1003, "用户名或密码错误"),

    /**
     * 用户已被禁用
     */
    USER_DISABLED(1004, "用户已被禁用"),

    /**
     * 账户余额不足
     */
    INSUFFICIENT_BALANCE(2001, "账户余额不足"),

    /**
     * 交易金额异常
     */
    INVALID_AMOUNT(2002, "交易金额异常"),

    /**
     * 交易状态异常
     */
    INVALID_TRANSACTION_STATUS(2003, "交易状态异常"),

    /**
     * 幂等性校验失败
     */
    IDEMPOTENT_CHECK_FAILED(2004, "请勿重复操作"),

    /**
     * 签名验证失败
     */
    SIGNATURE_VERIFY_FAILED(3001, "签名验证失败"),

    /**
     * 时间戳过期
     */
    TIMESTAMP_EXPIRED(3002, "请求已过期"),

    /**
     * 加密失败
     */
    ENCRYPT_FAILED(3003, "加密失败"),

    /**
     * 解密失败
     */
    DECRYPT_FAILED(3004, "解密失败");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 消息
     */
    private final String message;
}
