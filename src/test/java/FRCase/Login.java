package FRCase;

import org.testng.annotations.Test;

import utils.Log;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

import static org.testng.Assert.assertTrue;

import java.io.IOException;

import org.openqa.selenium.By;
import org.testng.annotations.AfterMethod;

public class Login extends Case{
  
  @Test(dataProvider = "dp")
  public void testA(String n, String s) throws InterruptedException {
	  driver.get("http://www.baidu.com");
	  Log.info("首页打开了");
	  Log.info("现在执行"+n+"号用例");
	  driver.findElement(By.id("kw")).sendKeys(s);
	  driver.findElement(By.id("su")).click();
	  Thread.sleep(3000);
	  
	  try {
	  assertTrue(!driver.getPageSource().contains(s));
	  }catch(Exception e) {
	  String ss=utils.Utils.takeScreenshot(s+"的截图");
	  Log.info(e.getMessage());
	  Log.info("获取截图地址是这个："+ss);
	  }
	  Thread.sleep(2000);
	
  }
  
  @DataProvider(name="dp")
  public Object[][] dp() throws IOException {
    return utils.ReadExcel.getTestData("C:\\Test\\", "tests.xls", "用力数据");
  }
  
}
