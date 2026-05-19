#!/usr/bin/env groovy

/**
 * 通用爬虫脚本模板
 * 用途：作为爬取不同网站的基础模板，可根据具体网站进行修改
 * 功能：使用 Playwright 爬取网页数据，提取结构化信息，存储到数据库
 * 
 * 变量说明：
 * - taskId: 任务ID
 * - enterpriseName: 企业名称
 * - baseUrl: 目标网站URL
 * - headless: 是否使用无头浏览器
 * - collectionMapper: 数据采集Mapper
 * - objectMapper: JSON序列化工具
 */

// 检查注入变量
if (taskId == null) throw new RuntimeException("未注入 taskId")
if (enterpriseName == null) throw new RuntimeException("未注入 enterpriseName")
if (baseUrl == null) throw new RuntimeException("未注入 baseUrl")

// 构建请求参数
def request = [:]
request.taskId = taskId
request.enterpriseName = enterpriseName
request.baseUrl = baseUrl
request.headless = headless != null ? headless : true
request.appDate = new Date().format("yyyy-MM-dd")

// 执行采集
import com.rbac.core.domain.entity.RpaDataCollection
import com.microsoft.playwright.*

// 创建采集对象
RpaDataCollection collection = new RpaDataCollection()
collection.setTaskId(taskId)
collection.setEnterpriseName(enterpriseName)
collection.setDataSource("spider_web")
collection.setCollectionTime(java.time.LocalDateTime.now())
collection.setStatus("collected")
collection.setDeleted(0)

Playwright playwright = null
Browser browser = null
Page page = null

try {
    // 初始化 Playwright
    playwright = Playwright.create()
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(request.headless))
    page = browser.newPage()
    
    // 导航到目标页面
    log.info("导航到: {}", request.baseUrl)
    page.navigate(request.baseUrl)
    
    // 等待页面加载完成
    page.waitForLoadState(Page.LoadState.NETWORKIDLE)
    log.info("页面加载完成")
    
    // ===== 这里开始根据具体网站修改 =====
    
    // 示例1: 填写表单并提交
    // page.fill("#username", "user")
    // page.fill("#password", "pass")
    // page.click("#login-button")
    // page.waitForLoadState(Page.LoadState.NETWORKIDLE)
    
    // 示例2: 点击元素
    // page.click("#some-button")
    // page.waitForLoadState(Page.LoadState.NETWORKIDLE)
    
    // 示例3: 提取数据
    // def title = page.title()
    // def content = page.content()
    // def elements = page.querySelectorAll(".data-item")
    
    // 提取网站标题和内容作为示例
    def websiteTitle = page.title()
    def websiteContent = page.content().take(5000) // 只取前5000字符，避免数据过大
    
    // 构建采集结果
    def collectedData = [
        websiteTitle: websiteTitle,
        websiteContent: websiteContent,
       采集时间: new Date().format("yyyy-MM-dd HH:mm:ss"),
        企业名称: enterpriseName,
        任务ID: taskId
    ]
    
    // ===== 这里结束根据具体网站修改 =====
    
    // 设置采集结果
    collection.setTaxNo(enterpriseName) // 这里可以根据实际情况设置
    collection.setRawData(objectMapper.writeValueAsString(collectedData))
    
    // 插入数据库
    collectionMapper.insert(collection)
    log.info("采集完成，存储ID: {}", collection.id)
    
    // 返回结果，供下一步使用
    return "{\"collectionId\": ${collection.id}, \"websiteTitle\": \"${websiteTitle.replace('"', '\\"')}\"}"
} catch (Exception e) {
    log.error("采集失败: {}", e.getMessage(), e)
    collection.setStatus("failed")
    collection.setErrorMessage(e.getMessage().take(2000))
    collection.setRawData(null)
    collectionMapper.insert(collection)
    throw e
} finally {
    // 清理资源
    if (page != null) {
        try {
            page.close()
        } catch (Exception e) {
            log.error("关闭页面失败: {}", e.getMessage())
        }
    }
    if (browser != null) {
        try {
            browser.close()
        } catch (Exception e) {
            log.error("关闭浏览器失败: {}", e.getMessage())
        }
    }
    if (playwright != null) {
        try {
            playwright.close()
        } catch (Exception e) {
            log.error("关闭Playwright失败: {}", e.getMessage())
        }
    }
}
