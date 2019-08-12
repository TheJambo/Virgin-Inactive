package pulse;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.Writer;
import java.io.Reader;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

public class VirginPulse {
	static String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
	static Properties appProps = new Properties();


	public static void main(String[] args)  throws Exception {
		
		 loadProperties();		
				
		WebDriver driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

		try {
			
			doFitbit(driver);
			login(driver);
			closeTrophies(driver);
			closePopups(driver);
			doCards(driver);
			doHabits(driver);
			doWhil(driver);
			
		}

		catch (ElementNotInteractableException e) {
			System.out.println(e.toString());
		}

		finally {
			driver.quit();
		}
	}
	
	private static void doFitbit(WebDriver driver) throws Exception {

		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS); //Loading times are OK for fitbit.
		System.out.println("Opening Fitbit");
		driver.get("https://www.fitbit.com/uk/login");
		
		driver.findElement(By.xpath("//*[@id=\"ember644\"]")).sendKeys(appProps.getProperty("FitbitEmail"));
		driver.findElement(By.xpath("//*[@id=\"ember645\"]")).sendKeys(appProps.getProperty("FitbitPassword"));
		clickIfPresent(By.id("ember685"), driver);	
		System.out.println("Logged In!");
		driver.get("https://www.fitbit.com/foods/log");
		
		Thread.sleep(1000);
		driver.findElement(By.xpath("//*[@id=\"foodselectinput\"]")).sendKeys("Sugar");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//*[@id=\"foodselectinput\"]")).sendKeys(Keys.RETURN);
		Thread.sleep(1000);
		driver.findElement(By.xpath("//*[@id=\"quantityselectinput\"]")).sendKeys(Keys.RETURN); //1tbsp
		Thread.sleep(1000);
		driver.findElement(By.xpath("//*[@id=\"foodAutoCompButton\"]")).click(); //Confirm your sugar
		Thread.sleep(1000);
		System.out.println("Calories logged!");
		driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS); //We're using the same page the entire loop, no need to wait.
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS); //Back to slow loads :(
	}
	
	private static void doHabits(WebDriver driver) throws Exception {
		List<String> habits = Arrays.asList("713", "642", "13", "44", "687", "684", "42", "43", "691", "685", "690", "688", "9", "689","692","686","4", "678" );

		System.out.println("Opening Habits Menu");
		driver.get("https://app.member.virginpulse.com/#/healthyhabits");
		
		System.out.println("Accepting habits...");
		driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS); //We're using the same page the entire loop, no need to wait.
		for (int i = 0; i < habits.size(); i++) {
			System.out.println("Accepting habit " + habits.get(i) + "...");
			clickIfPresent(By.id("tracker-" + habits.get(i)  + "-track-yes"), driver);
		}
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS); //Back to slow loads :(
	}
	
	private static void doCards(WebDriver driver) throws Exception {
		System.out.println("Accept first card");
		clickIfPresent(By.xpath("//*[@id=\"daily-tips-slider\"]/div[1]/quizzes/div/div[3]/div[5]/div/button[1]"), driver);
		
		System.out.println("Accept second card");
		clickIfPresent(By.xpath("//*[@id=\"daily-tips-slider\"]/div[1]/quizzes/div/div[3]/div[5]/div/button[1]"), driver);
		
		Thread.sleep(3000);
	}
	
	private static void login(WebDriver driver) throws Exception {
		System.out.println("Attempting to log in.");
		driver.get(
				"https://iam.virginpulse.com/auth/realms/virginpulse/protocol/openid-connect/auth?client_id=genesis-ui&redirect_uri=https%3A%2F%2Fapp.member.virginpulse.com%2F%3Fredirect_fragment%3D%252Fhome&state=9c3aedca-d8f1-4846-b583-6498ff108e15&nonce=d61e2ef3-65bb-4928-b165-35f9014fb6f6&response_mode=fragment&response_type=code&scope=openid");
		Thread.sleep(1000);
		driver.findElement(By.id("kc-form-wrapper")).click(); //Focus the username field
		driver.findElement(By.id("username")).sendKeys(appProps.getProperty("EmailLogin"));
		driver.findElement(By.id("password")).sendKeys(appProps.getProperty("Password"));
		clickIfPresent(By.id("kc-login"), driver);
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
	
	private static void loadProperties() throws IOException {
		try {
		Path PropertyFile = Paths.get("application.properties");

        Reader PropReader = 
                Files.newBufferedReader(PropertyFile);
        appProps.load(PropReader);

        PropReader.close();
		System.setProperty("webdriver.chrome.driver", appProps.getProperty("ChromeDriverLocation"));

		}
		catch(IOException Ex)
		{
	        System.out.println("Unable to find configuration file \"application.properties\". The file has been created for you.");
			createProperties();
		}
		
       
	}
	
private static void createProperties() {
	Properties AppProps = new Properties();
    AppProps.setProperty("EmailLogin", "username@domain.tld");
    AppProps.setProperty("Password", "ThisIsMySecurePassword");
    AppProps.setProperty("ChromeDriverLocation", "C:\\chromedriver.exe");
    AppProps.setProperty("FitbitEmail", "username@domain.tld");
    AppProps.setProperty("FitbitPassword", "ThisIsMySecureFitbitPassword");

    Path PropertyFile = Paths.get("application.properties");
      
    try
    {
        Writer PropWriter = 
                Files.newBufferedWriter(PropertyFile);
        AppProps.store(PropWriter,
                "Application Properties");
        PropWriter.close();
    }
    catch(IOException Ex)
    { //No Properties file - Create one!
        System.out.println("Could not create file.");
    }
}

	private static void clickIfPresent(By by, WebDriver driver) throws Exception {
		List<WebElement> Elements = driver.findElements(by);
		for (WebElement Element : Elements) {
			if (Element.isEnabled()) {
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", Element);
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
		driver.get("https://connect.whil.com/goaltags/freemium-mindfulness-101/sessions/focus-your-attention");
		System.out.println("Resuming Mindfulness course");
		clickIfPresent(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div[2]/div"), driver);
		System.out.println("Playing video");
		driver.findElement(By.xpath("//*[@id=\"playerContainer\"]/div[2]/button")).click();		
		Thread.sleep(300010); //play for 5 minutes
		System.out.println("Video Played. Time to leave.");
	}
	


}