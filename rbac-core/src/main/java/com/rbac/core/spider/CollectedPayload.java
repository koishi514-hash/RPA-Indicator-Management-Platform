package com.rbac.core.spider;

import java.util.ArrayList;
import java.util.List;

public class CollectedPayload {
    private String enterpriseName;
    private String taxNo;
    private String uscCode;
    private String appDate;
    private List<CollectedInvoiceRow> invoices = new ArrayList<>();

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getTaxNo() {
        return taxNo;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }

    public String getUscCode() {
        return uscCode;
    }

    public void setUscCode(String uscCode) {
        this.uscCode = uscCode;
    }

    public String getAppDate() {
        return appDate;
    }

    public void setAppDate(String appDate) {
        this.appDate = appDate;
    }

    public List<CollectedInvoiceRow> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<CollectedInvoiceRow> invoices) {
        this.invoices = invoices;
    }
}

