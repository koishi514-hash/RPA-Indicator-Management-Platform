package com.rbac.core.spider;

/**
 * 采集阶段从页面直接抓到的字符串（尽量不做转换）
 */
public class CollectedInvoiceRow {
    private String sign;
    private String state;
    private String invoiceTime;
    private String jshjText;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getInvoiceTime() {
        return invoiceTime;
    }

    public void setInvoiceTime(String invoiceTime) {
        this.invoiceTime = invoiceTime;
    }

    public String getJshjText() {
        return jshjText;
    }

    public void setJshjText(String jshjText) {
        this.jshjText = jshjText;
    }
}

