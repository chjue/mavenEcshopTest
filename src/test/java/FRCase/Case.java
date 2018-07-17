package FRCase;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import utils.Log;
import utils.Utils;

public class Case {
	WebDriver driver;
	
	  @BeforeClass
	  public void beforeMethod() {
		  
		  driver=Utils.openBrowser("Chrome");
		  
		 /* 
	      	System.setProperty("webdriver.chrome.driver", "E:\\SoftwareInstall\\chromedriver.exe");
	    	driver=new ChromeDriver();
	    	Log.info("浏览器打开了");*/

	  }

	  @AfterClass
	  public void afterMethod() {
		  driver.quit();
		  Log.info("浏览器关闭了");
	  }

}
