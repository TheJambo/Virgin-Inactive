package pulse;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

public class VirginPulse {
	static String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
	static Properties appProps = new Properties();
	static String username;
	static String password;
	static String chromeLocation;

	

	public static void main(String[] args)  throws Exception { //pulse.jar email password chromelocation
		List<String> habits = Arrays.asList("713", "642", "13", "44", "687", "684", "42", "43", "691", "685", "690", "688", "9", "689","692","686","4" );
		 username = args[0];
		 password = args[1];
		 chromeLocation = args[2];
		System.setProperty("webdriver.chrome.driver", chromeLocation);
				
		WebDriver driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		login(driver);
		closeTrophies(driver);
		closePopups(driver);

		try {

			System.out.println("Accept first card");
			clickIfPresent(By.xpath("//*[@id=\"daily-tips-slider\"]/div[1]/quizzes/div/div[3]/div[5]/div/button[1]"), driver);
			
			System.out.println("Accept second card");
			clickIfPresent(By.xpath("//*[@id=\"daily-tips-slider\"]/div[1]/quizzes/div/div[3]/div[5]/div/button[1]"), driver);
			
			System.out.println("Opening Habits Menu");
			driver.get("https://app.member.virginpulse.com/#/healthyhabits");
			Thread.sleep(3000);
			
			System.out.println("Accepting habits...");
			driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS); //We're using the same page the entire loop, no need to wait.
			for (int i = 0; i < habits.size(); i++) {
				System.out.println("Accepting habit " + habits.get(i) + "...");
				clickIfPresent(By.id("tracker-" + habits.get(i)  + "-track-yes"), driver);
			}
			
			System.out.println("Logged In, and Trophies closed.");
			driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS); //Back to slow loads :(
			doWhil(driver);
		}

		catch (ElementNotInteractableException e) {
			System.out.println(e.toString());
		}

		finally {
			driver.quit();
		}
	}
	
	private static void login(WebDriver driver) throws Exception {
		System.out.println("Attempting to log in.");
		driver.get(
				"https://iam.virginpulse.com/auth/realms/virginpulse/protocol/openid-connect/auth?client_id=genesis-ui&redirect_uri=https%3A%2F%2Fapp.member.virginpulse.com%2F%3Fredirect_fragment%3D%252Fhome&state=9c3aedca-d8f1-4846-b583-6498ff108e15&nonce=d61e2ef3-65bb-4928-b165-35f9014fb6f6&response_mode=fragment&response_type=code&scope=openid");
		Thread.sleep(1000);
		driver.findElement(By.id("kc-form-wrapper")).click(); //Focus the username field
		driver.findElement(By.id("username")).sendKeys(username);
		driver.findElement(By.id("password")).sendKeys(password);
		driver.findElement(By.id("kc-login")).click();
		Thread.sleep(10000);
		System.out.println("No trophies to close.");
	}

	private static void closeTrophies(WebDriver driver) throws Exception {
		System.out.println("Attempting to accept trophies.");
		while (!driver.findElements(By.id("trophy-modal-close-btn")).isEmpty()) {
			driver.findElement(By.id("trophy-modal-close-btn")).click();
			System.out.println("Trophy closed.");
			Thread.sleep(100);
		}
		System.out.println("No trophies to close.");
	}

	private static void closePopups(WebDriver driver) throws Exception {
		System.out.println("Attempting to close popups.");
		while (!driver.findElements(By.id("triggerCloseCurtain")).isEmpty()) {
			driver.findElement(By.id("triggerCloseCurtain")).click();
			System.out.println("Popup closed.");
			Thread.sleep(1000);
			closeTrophies(driver);
		}
		System.out.println("No popups to close.");
	}

	private static void clickIfPresent(By by, WebDriver driver) throws Exception {
		List<WebElement> Elements = driver.findElements(by);
		for (WebElement Element : Elements) {
			if (Element.isEnabled()) {
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", Element);
				System.out.println("Accepted!");
			}
			else {
				System.out.println("Nothing to click here...");
			}
			Thread.sleep(2500);
		}
	}
	private static void doWhil(WebDriver driver) throws Exception { 
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS); //Whil is so slow, I think the actual medidation is meant to be during loads.
		System.out.println("Logging into Whil.");
		driver.get("https://connect.whil.com/virginpulsesso/redirect?destination=series&seriesUrl=https%3A%2F%2Fconnect.whil.com%2Fcms%2Fprograms%2Ffreemium%2Fseries%2Fthrive-mindfulness-101");
		System.out.println("Opening Mindfulness course.");
		driver.get("https://connect.whil.com/goaltags/freemium-mindfulness-101?w");
		
		if (!driver.findElements(By.id("triggerCloseCurtain")).isEmpty()) {
			System.out.println("Resuming Mindfulness course");
			clickIfPresent(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div[2]/div"), driver);
		}
		else {
			System.out.println("Starting Mindfulness course");
			driver.get("https://connect.whil.com/goaltags/freemium-mindfulness-101/sessions/introduction-basics-with-kelly");
		}
		
		System.out.println("Playing video");
		driver.findElement(By.xpath("//*[@id=\"playerContainer\"]/div[2]/button")).click();		
		Thread.sleep(300010); //play for 5 minutes
		System.out.println("Video Played. Time to leave.");
	}
	


}