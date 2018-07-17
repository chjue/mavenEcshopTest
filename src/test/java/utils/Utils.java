package utils;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.fail;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;

import utils.Constants;

public class Utils {
	public static WebDriver driver;

	// 启动浏览器
	public static WebDriver openBrowser(String browser) 
	{
	   try {
	         if (browser.equalsIgnoreCase("firefox")) {
	               FirefoxProfile profile = new FirefoxProfile();
	               //设置不要禁用混合内容
	               profile.setPreference("security.mixed_content.block_active_content", false);
	               profile.setPreference("security.mixed_content.block_display_content", true);
	              //设置自动下载 1.不显示下载管理器,2.指定自动下载的文件类型
	              profile.setPreference("browser.download.manager.showWhenStarting", false);
	              profile.setPreference("browser.helperApps.neverAsk.saveToDisk","application/octet-stream,application/vnd.ms-excel,text/csv,application/zip");
	              //3.默认下载文件夹，0是桌面、1是默认系统用户的”下载“，2是自定义文件夹
	              profile.setPreference("browser.download.folderList", 2);
	              //4.设置自定义文件夹
	              profile.setPreference("browser.download.dir", "D:\\downloads");
	              //启动Firefox
	             driver = new FirefoxDriver(profile);
	            }
	         else if (browser.equalsIgnoreCase("ie")){
					DesiredCapabilities ieCapabilities=DesiredCapabilities.internetExplorer();
					ieCapabilities.setCapability("nativeEvents", true); 
					ieCapabilities.setCapability("unexpectedAlertBehaviour", "accept");
					ieCapabilities.setCapability("ignoreProtectedModeSettings", true);
					ieCapabilities.setCapability("disable-popup-blocking", true);
					ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
					ieCapabilities.setCapability("requireWindowFocus", false);
					ieCapabilities.setCapability("enablePersistentHover", false);
					System.setProperty("webdriver.ie.driver", Constants.IE_DRIVER);
					//启动IE
					driver = new InternetExplorerDriver(ieCapabilities);
													            }
	                else if (browser.equalsIgnoreCase("chrome")) {
							//delete warning --ignore-certificate-errors
							DesiredCapabilities capabilities = DesiredCapabilities.chrome();
							capabilities.setCapability("chrome.switches", Arrays.asList("--incognito"));
							ChromeOptions options = new ChromeOptions();
							options.addArguments("--test-type");
							options.addArguments("enable-automation");
							options.addArguments("--disable-infobars");
							capabilities.setCapability(ChromeOptions.CAPABILITY, options);
							System.setProperty("webdriver.chrome.driver", Constants.CHROME_DRIVER);
							//启动Chrome
							driver = new ChromeDriver(capabilities);
	                        }
	        
               else{
	             Log.error("Invalid browser type:"+browser);
	              }
	         
			         Log.info("Browser is started,Type is "+browser);
			         driver.manage().window().maximize();
			         driver.manage().timeouts().implicitlyWait(Constants.IMPLICITLY_WAIT, TimeUnit.SECONDS);
			         return driver;
	         } catch (Exception e) 
	         {   Log.error("Unable to Open Browser.");
	             Log.error(e.getMessage());
	             fail(e.getMessage());
	             return null;
	             }
	          }
        //	}





	//2、元素断言失败时截图
	public static String takeScreenshot(String sTestCaseName) {
		DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd-hh_mm_ss");
		Date date = new Date();
		File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(file, new File(Constants.SCREENSHOT  + "/" + sTestCaseName + " # " + dateformat.format(date) + ".png"));
		} catch (Exception e) {
			Log.error("Issue in Taking Screenshot");
		}
		String result="截图地址是:"+Constants.SCREENSHOT + sTestCaseName + "/" + sTestCaseName + " # " + dateformat.format(date) + ".png";
		return result;
	}

	//执行JS
	public static Object executeJS(String js,Object... arg1) 
	{
	try {
	  Log.info("Execute JS:"+js);
	  Log.info("JS arg1:"+arg1);
	  return ((JavascriptExecutor) driver).executeScript(js,arg1); 
	 } catch (Exception e) 
	{ e.printStackTrace();
	  Log.error(e.getMessage());
	  return null; }
	}

	//等待固定时间

	public static void sleep(long ms){
	try {
	Thread.sleep(ms);
	} catch (InterruptedException e) 
	{ e.printStackTrace();
	 Log.error("The sleep thread is interrupted.");
	 e.printStackTrace();
	 fail();
	 }
	}

	//2、等待网页加载完毕，一直等待，无终止条件

	public static void waitForPageLoad() {
		String js = "return document.readyState;";
		while (!"complete".equals((String) executeJS(js))) {
			Log.info("Page is loading……");
			sleep(1000);
		}
		Log.info("The page is loaded.");
	}

	//3、等待网页加载完毕，30 秒作为最长等待时间

	public static void waitForPageLoad30() {
		String js = "return document.readyState;";
		boolean flag = false;
		// 最多等待30秒
		for (int i = 1; i <= 30; i++) {
			sleep(1000);
			if ("complete".equals((String) executeJS(js))) {
				flag = true;
				Log.info("Page is loaded in " + i + " seconds.");
				break;
			}
		}
		if (!flag) {
			fail();
			Log.warn("Page is not loaded in 30 seconds.");
		}
	}

//	4、自定义显式等待，等待网页标题包含预期标题

	public static void explicitWaitTitle(final String title){
	WebDriverWait wait = new WebDriverWait(driver,Constants.EXPLICIT_WAIT);
	try{ wait.until(new ExpectedCondition<Boolean>() {
	@Override
	public Boolean apply(WebDriver d) {
	return d.getTitle().toLowerCase().contains(title.toLowerCase());
	}});
	}catch(TimeoutException te) {
	System.out.println(title);
	System.out.println(driver.getTitle());
	throw new IllegalStateException("当前不是预期页面，当前页面title是：" + driver.getTitle());
	}}

	//封装获取页面元素状态（是否可见、是否可用）的方法

	public static boolean getElementStatus(WebElement element)
	{ if(!element.isDisplayed()){
	  Log.error("The element is not displayed:"+element.toString());
	  takeScreenshot("Utils-getElementStatus");} 
	 else if (!element.isEnabled()){
	  Log.error("The element is disabled:"+element.toString());
	  takeScreenshot("Utils-getElementStatus");}
	 return element.isDisplayed()&&element.isEnabled();
	}



	//封装选择下拉框的选项方法：
	public static void selectDropDown(WebElement element, String flag, String data) {
		if (getElementStatus(element)) {
			Select select = new Select(element);
			Log.info("Select option in dropdown list :" + flag);
			if (flag.equalsIgnoreCase("byvalue")) {
				select.selectByValue(data);
			} else if (flag.equalsIgnoreCase("byindex")) {
				select.selectByIndex(Integer.parseInt(data));
			} else {
				select.selectByVisibleText(data);
			}
			Log.info(data + " is selectedin dropdown list:" + element.toString());
		} else {
			Log.error("dropdown list is disabled or not displayed.");
			fail("dropdown list is disabled or not displayed.");
		}
	}

//	测试用例中调用该方法：
//  Utils.selectDropDown(brand,"byvalue","9");
//	Utils.selectDropDown(brand,"byindex","3");
//	Utils.selectDropDown(brand,"bytext",“联想");


	
//	页面元素点击可以直接用click方法。为了方便调试，最好要先判断该元素在页面上是否存在。步骤如下：–1、判断该页面元素是否存在和可用–2、调用click方法–3、如果网页有更新，

	public static void click(WebElement element){
	try {
	 if(getElementStatus(element)){//1、判断该页面元素是否存在和可用
	  element.click();//2、点击元素
	  Log.info(element.toString()+" is clicked.");
	 }
	} catch (Exception e) {
	  e.printStackTrace();
	  Log.error("Method [elementClick] "+e.getMessage());
	  fail(e.getMessage());
	 }
	}



	//点击页面元素并且等待网页加载的clickAndWait方法如下：

	public static void clickAndWait(WebElement element){
	 click(element);
	 waitForPageLoad();//等待网页加载
	}

	//页面输入通常使用sendKeys方法
	public static void inputValue(WebElement element,String value){ 
	 try {
	    if(getElementStatus(element)){
	   //1、判断该页面元素是否存在和可用
	    if(value!=null)
	      { element.clear();
	        element.sendKeys(value);
	        Log.info("Test data: "+value + " is input to element:"+element.toString());}
	        else{Log.error("Test data: "+value + " is null.");}}
	  } catch (Exception e) 
	   { Log.error("Method [inputValue] "+e.getMessage());
	     fail(e.getMessage());}
	}

	// Frame切换--切换到默认网页
	/*
	 * 切换到默认网页
	 */
	public static void switchToDefault() {
		driver.switchTo().defaultContent();
		Log.info("Switched to default content");
	}

	// 使用ID或Name属性值来切换到新的Frame
	public static void switchFrame(String idOrName){
	 try {
	  switchToDefault();
	  driver.switchTo().frame(idOrName);
	  Log.info("Switched to frame, idOrName:"+idOrName);
	}catch(NoSuchFrameException e){
	  Log.error("No Frame to switch.");
	  fail(e.getMessage());
	}
	}

	// 通过index定位切换Frame

	public static void switchFrame(int index){
	try{
	  switchToDefault();
	  driver.switchTo().frame(index);
	  Log.info("Switched to frame, index:"+index);
	}catch(NoSuchFrameException e){
	  Log.error("No Frame to switch.");
	  fail(e.getMessage());
	}
	}

	// 通过其他方式定位切换Frame
	public static void switchFrame(WebElement frameElement){
	try{
	  if (getElementStatus(frameElement)){
	    switchToDefault();
	    driver.switchTo().frame(frameElement);
	    Log.info("Switched to frame.");}
	  }catch(NoSuchFrameException e){
	    Log.error("No Frame to switch.");
	    fail(e.getMessage());}
	}

	// 通过网页内容定位切换Frame
	public static void switchFrameByPageSource(String pageSource) {
		switchToDefault();
		List<WebElement> frames = driver.findElements(By.tagName("frame"));
		if (frames.size() > 0) {
			boolean flag = false;
			// 未找到目标Frame
			for (int i = 0; i < frames.size(); i++) {
				// 遍历所有Frame
				switchFrame(i); // 切换到编号为i的
				// 如果网页内容包含指定信息
				if (driver.getPageSource().contains(pageSource))
					flag = true; // 找到目标Frame
				break; // 就退出循环
			}
			if (flag) {
				Log.info("Switched to frame contains:" + pageSource);
			} else {
				Log.error("No frame contains:" + pageSource);
			}
		} else {
			Log.error("No frame to switch");
			fail("No frame to switch");
		}
	}

	// 通过名称切换到新窗口
	public static void switchWindow(String nameOrHdl){
	try{
	  driver.switchTo().window(nameOrHdl);
	}catch(NoSuchWindowException e){
	  e.printStackTrace();
	  Log.error("The window cannot be found："+nameOrHdl);
	  fail(e.getMessage());
	 }
	}

	// 切换到另一个窗口(适合一共叧有2个窗口的情况)
	public static void switchWindow() {
		String originalWinHandle = driver.getWindowHandle();
		Set<String> allWindows = driver.getWindowHandles();
		if (allWindows.size() == 2) {
			Iterator<String> it = allWindows.iterator();
			while (it.hasNext()) {
				String currentWindow = it.next();
				if (!currentWindow.equals(originalWinHandle))
					driver.switchTo().window(currentWindow);
			}
			Log.info("Switched to new window.");
		} else {
			Log.error("There are not two windows.");
			fail("There are not two windows.");
		}
	}

	// 多个窗口的情况下，可以判断窗口标题或URL或网页源码是否满足条件，如果满足,通过窗口句柄切换到新窗口
	// 参数type设计为一个整数：1代表标题，2代表URL，3代表网页源码
	public static void switchWindow(int type,String value)

	{
		String originalWinHandle = driver.getWindowHandle();
		Set<String> allWindows =driver.getWindowHandles();
		Iterator<String> it = allWindows.iterator();
		if (allWindows.size() > 1) {
			boolean flag = false;// 未切换到目标窗口
			while (it.hasNext()) {
				// 遍历时不是当前窗口，就切换
				String currentWindow = it.next();
				if (!currentWindow.equals(originalWinHandle))
					driver.switchTo().window

					(currentWindow);
				String currentValue;
				switch (type) {
				case 1:
					currentValue = driver.getTitle();
					Log.info("switch Window By Title Value.");
					break;
				case 2:
					currentValue = driver.getCurrentUrl();
					Log.info("switch Window By URL Value.");
					break;
				default:
					currentValue = driver.getPageSource();
					Log.info("switch Window By PageSource.");
					break;
				}
				// 如果标题或URL或网页源码包含指定的信息，则到达指定窗口
				if (currentValue.contains(value)) {
					flag = true;// 找到目标窗口，停止切换
					break; // 退出循环
				}
			}
			if (flag) {
				Log.info("Switched to new window.");
				fail("Switched to new window.");
			} else {
				Log.error("Can not find new window contains:" + value);
				fail("Can not find new window contains:" + value);
			}
		} else {
			Log.error("There are not more then one windows.");
			fail("There are not more then one windows.");
		}
	}

	// 断言页面元素出现--判断指定的页面元素是否出现
	public static boolean isElementPresent(By by) {
	try {
	  driver.findElement(by);
	  Log.info("Matching elements are found."); 
	  return true;
	} catch (NoSuchElementException e) {
	  Log.error("No matching elements are found.");
	  return false;
	 }
	}

	// 判断弹出框是否出现
	public static boolean isAlertPresent() {
	 try {
	  driver.switchTo().alert();
	  Log.info("The alert dialog can be found.");
	  return true;
	 } catch (NoAlertPresentException e) { 
	   Log.error("The alert dialog cannot be found.");
	   return false;
	  }
	}

	// 获得弹出框中内容，并且关闭弹出框，默认点击“确定”
	public static boolean acceptNextAlert= true;
	public static String closeAlertAndGetItsText() {
	try {  Alert alert= driver.switchTo().alert();
	       String alertText= alert.getText();
	       if (acceptNextAlert) {
	          Log.info("Accept the dialog");
	          alert.accept();
	       } else {Log.info("Dismiss the dialog");
	           alert.dismiss();
	        }
	       Log.info("The text in the dialog is:"+alertText);
	      return alertText;
	   } finally {
	     acceptNextAlert= true;
	   }
	}

	// 页面断言文本等于预期值
	public static void assertText(WebElement element,String expText){
	  if(getElementStatus(element)){
	  if(expText!=null){
	  try{assertEquals(element.getText(),expText);
	}catch(AssertionError e){
	  e.printStackTrace();Log.error(e.getMessage());
	  fail(e.getMessage());
	 }
	}else{
	  fail("Test data: "+expText+ " is null.");
	  Log.error("Test data: "+expText+ " is null.");}
	 }
	}

	// 封装断言属性值等于预期值的方法
	public static void assertAttribute(WebElement element,String attributeName,String expValue){
	  if(getElementStatus(element)){
	  if (expValue!=null){
	try{
	  assertEquals(element.getAttribute(attributeName),expValue);
	}catch(AssertionError e){
	  e.printStackTrace();
	  Log.error(e.getMessage());
	  }
	} else {
	  Log.error("Expected value :["+expValue+"] is null.");
	  fail("Expected value :["+expValue+"] is null.");
	    }
	  }
	 }
	

	// 断言复选框或单选按钮当前被选中
	public static void assertChecked(WebElement element){
	  if(getElementStatus(element)){
	   try{
	     assertTrue(element.isSelected());
	    }catch(AssertionError e){
	        e.printStackTrace();
	        Log.error("The checkbox or radiobuttonis not checked.");
	        fail(e.getMessage());
	    }
	  }
	}

	// 断言复选框或单选按钮当前未被选中
	public static void assertNotChecked(WebElement element){
	if(getElementStatus(element)){
	try{
	assertFalse(element.isSelected());
	}catch(AssertionError e){
	e.printStackTrace();
	Log.error("The checkbox or radiobuttonis checked.");
	fail(e.getMessage());
	}
	}
	}

	// 断言下拉框或列表框的当前选项等于预期文本
	public static void assertSelectedOption(WebElement element,String expOption)
	{ if(getElementStatus(element))
	 { try{String actOption= new Select(element).getFirstSelectedOption().getText();
	  assertEquals(actOption,expOption);
	 }catch(AssertionError e)
	 {  e.printStackTrace();
	    Log.error(e.getMessage());fail(e.getMessage());
	 }
	}
	}

	// 断言下拉框或列表框的所有选项中包含一些预期文本
	public static void assertOptionsContains(WebElement element,String... expOptions){
	if(getElementStatus(element)){
	for (String expOption:expOptions){
	boolean flag = false;
	for(WebElement option:new Select(element).getOptions()){
	String actOption= option.getText();
	if (actOption.contains(expOption)){
	flag = true;
	break;
	}
	}try{
	assertTrue(flag);
	}catch(AssertionError e){
	e.printStackTrace();
	Log.error("All options does not contains expected option ["+expOption+"]");
	fail(e.getMessage());
	}
	}
	}
	}

	// 断言下拉框或列表框的当前选项可以多选
	public static void assertMultiple(WebElement element){
	if(getElementStatus(element)){
	try{
	assertTrue(new Select(element).isMultiple());
	}catch(AssertionError e){
	e.printStackTrace();
	Log.error("The dropdown list is not multiple");
	fail(e.getMessage());
	}
	}
	}

	// 断言下拉框或列表框的当前选项丌可以多选，只能单选
	public static void assertNotMultiple(WebElement element){
	if(getElementStatus(element)){
	try{
	assertFalse(new Select(element).isMultiple());
	}catch(AssertionError e){
	e.printStackTrace();
	Log.error("The dropdown list is multiple");
	fail(e.getMessage());
	}
	}
	}

	// 断言下拉框或列表框的多个当前选项中包含预期文本
	public static void assertSelectedContains(WebElement element,String... expOptions)
	{ if(getElementStatus(element))
	  {  for(String expOption:expOptions)    
	       { boolean flag = false;
	       for (WebElement option:new Select (element).getAllSelectedOptions())
	          { String actOption= option.getText();
	            if (actOption.contains(expOption))
	          { flag = true;//找到了
	            break;}}
	try{
	  assertTrue(flag);
	}catch(AssertionError e){
	  e.printStackTrace();
	  Log.error("Selectedoptions does not contains expected   option:"+expOption);
	  fail("Selected options does not contains :"+expOption);
	   }
	  }
	 }
	}



	//断言下拉框或列表框的所有选项个数正确

	public static void assertOptionsCount(WebElement element,int expCount){
	if(getElementStatus(element)){
	int actCount= new Select(element).getOptions().size();
	try{
	assertEquals(actCount,expCount);
	} catch(AssertionError e){
	  e.printStackTrace();
	  Log.error(e.getMessage());
	  fail(e.getMessage());
	  }
	 }
	}

	// 断言下拉框或列表框的当前选项个数正确

	public static void assertSelectedOptionsCount(WebElement element, int expCount) {
		if (getElementStatus(element)) {
			int actCount = new Select(element).getAllSelectedOptions().size();
			try {
				assertEquals(actCount, expCount);
			} catch (AssertionError e) {
				e.printStackTrace();
				Log.error(e.getMessage());
				fail(e.getMessage());
			}
		}
	}

}
