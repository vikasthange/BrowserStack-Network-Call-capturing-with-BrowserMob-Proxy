import java.net.URL;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.browserstack.local.Local;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.proxy.CaptureType;

public class Test {

	public static void main(String[] args) throws Exception {
		// start the proxy
		BrowserMobProxy proxy = new BrowserMobProxyServer();
		proxy.start(1234);
		System.out.println("Started proxy server at: " + proxy.getPort());

//	    For Local Test Execution
//	    org.openqa.selenium.Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
//	    System.setProperty("webdriver.gecko.driver", "/Applications/geckodriver");
//	    FirefoxOptions fo = new FirefoxOptions();
//	    fo.setCapability(CapabilityType.PROXY, seleniumProxy);
//	    WebDriver driver = new FirefoxDriver(fo);

		// BS Execution
		String username = "your BS Username";
		String accessKey = "Your BS API Key";

		Local l = new Local();
		Map<String, String> options = new HashMap<String, String>();
		options.put("key", accessKey);

		options.put("v", "true");
		options.put("force", "true");
		options.put("onlyAutomate", "true");

		options.put("forcelocal", "true");
		options.put("forceproxy", "true");

		options.put("localProxyHost", "localhost");
		options.put("localProxyPort", "1234");
		options.put("localIdentifier", "Test1");

		l.start(options);

		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("browser", "Chrome");
		capabilities.setCapability("browserstack.local", "true");
		capabilities.setCapability("browserstack.debug", "true");
		capabilities.setCapability("name", "testing BrowserMob & BrowserStack");
		capabilities.setCapability("browserstack.networkLogs", "true");
		capabilities.setCapability("browserstack.acceptInsecureCerts", "true");
		capabilities.setCapability("browserstack.local", "true");
		capabilities.setCapability("browserstack.localIdentifier", "Test1");
		WebDriver driver = new RemoteWebDriver(
				new URL("http://" + username + ":" + accessKey + "@hub-cloud.browserstack.com/wd/hub"), capabilities);

		// BS Execution

		proxy.enableHarCaptureTypes(EnumSet.of(CaptureType.REQUEST_HEADERS, CaptureType.REQUEST_CONTENT,
				CaptureType.RESPONSE_HEADERS, CaptureType.RESPONSE_CONTENT));
		proxy.newHar("weather.com");

		driver.get("http://weather.com");

		Thread.sleep(5000);

		System.out.println("getting har");

		List<HarEntry> entries = proxy.getHar().getLog().getEntries();
		for (HarEntry harEntry : entries) {
			System.out.println(harEntry.getRequest().getUrl());

		}
		driver.quit();
		proxy.stop();
	}
}
