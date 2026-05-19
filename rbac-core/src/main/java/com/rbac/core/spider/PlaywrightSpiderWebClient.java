package com.rbac.core.spider;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.util.ArrayList;
import java.util.List;

/**
 * 按 `spider_web/参数位置和操作说明.md` 的页面步骤进行抓取：
 * 1) /enterprise-info：企业名称 -> taxNo/uscCode
 * 2) /application：填 taxNo/uscCode/appDate -> 校验 appDate-display
 * 3) /invoice-query：填 taxNo/uscCode -> 查询 -> 抓取每行 invoice-sign/state/time/jshj
 */
public class PlaywrightSpiderWebClient {

    public CollectedPayload collect(String baseUrl, boolean headless, String enterpriseName, String appDate) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
            Page page = browser.newPage();

            CollectedPayload payload = new CollectedPayload();
            payload.setEnterpriseName(enterpriseName);

            // 1) 企业信息查询页
            page.navigate(baseUrl + "/enterprise-info");
            page.waitForSelector("#enterprise-name-input");
            page.fill("#enterprise-name-input", enterpriseName);
            // 点击“查询”
            page.locator("button:has-text(\"查询\")").first().click();
            page.waitForSelector("#tax-no");
            String taxNo = safeText(page, "#tax-no");
            String uscCode = safeText(page, "#usc-code");
            payload.setTaxNo(taxNo);
            payload.setUscCode(uscCode);

            // 2) 业务申请页（拿 appDate）
            page.navigate(baseUrl + "/application");
            page.waitForSelector("#app-tax-no");
            page.fill("#app-tax-no", taxNo);
            page.fill("#app-usc-code", uscCode);
            page.fill("#app-date", appDate);
            // 必填项：申请类型，否则 HTML5 校验会阻止提交，success 区域不会出现
            page.selectOption("#app-type", "credit");
            // 提交
            page.locator("button[type=\"submit\"]:has-text(\"提交申请\")").click();
            page.waitForSelector("#app-date-display", new Page.WaitForSelectorOptions().setTimeout(60000));
            String confirmedAppDate = safeText(page, "#app-date-display");
            payload.setAppDate(confirmedAppDate);

            // 3) 发票查询页（抓取明细）
            page.navigate(baseUrl + "/invoice-query");
            page.waitForSelector("#invoice-tax-no");
            page.fill("#invoice-tax-no", taxNo);
            page.fill("#invoice-usc-code", uscCode);
            page.locator("button:has-text(\"查询\")").first().click();

            // 等待表格出现（tbody有行）
            page.waitForSelector("tbody tr");

            int rowCount = page.locator("tbody tr").count();
            List<CollectedInvoiceRow> rows = new ArrayList<>();
            for (int i = 0; i < rowCount; i++) {
                CollectedInvoiceRow row = new CollectedInvoiceRow();
                row.setSign(safeText(page, "#invoice-sign-" + i));
                row.setState(safeText(page, "#invoice-state-" + i));
                row.setInvoiceTime(safeText(page, "#invoice-time-" + i));
                row.setJshjText(safeText(page, "#invoice-jshj-" + i));
                rows.add(row);
            }
            payload.setInvoices(rows);

            browser.close();
            return payload;
        }
    }

    // 安全获取元素文本，避免异常
    private String safeText(Page page, String selector) {
        try {
            return page.locator(selector).first().innerText().trim();
        } catch (Exception e) {
            return "";
        }
    }
}

