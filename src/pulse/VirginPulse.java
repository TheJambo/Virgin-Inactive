package pulse;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.Writer;
import java.io.Reader;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

public class VirginPulse {
	static String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
	static Properties appProps = new Properties();

	public static void main(String[] args) throws Exception {
		loadProperties();

		String LastRunDate = appProps.getProperty("LastRunDate");

		System.out.println(
				"Todays Date:" + DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH).format(LocalDateTime.now()));

		System.out.println("Last Run Date: " + LastRunDate);

		if (new SimpleDateFormat("yyyy-MM-dd").parse(LastRunDate).before(new Date())) {

			WebDriver driver = new ChromeDriver();
			driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

			try {

				doMyFitnessPal(driver);
				login(driver);
				closeTrophies(driver);
				closePopups(driver);
				doCards(driver);
				doHabits(driver);
				doWhil(driver);
				doSlack(driver);
				String NewLastRunDate = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
						.format(LocalDateTime.now());
				System.out.println("Ran successfully. Setting last run date to: " + NewLastRunDate);
				appProps.setProperty("LastRunDate", NewLastRunDate);

			}

			catch (ElementNotInteractableException e) {
				System.out.println(e.toString());
			}

			finally {
				driver.quit();

			}
		}

		else {
			System.out.println("Already ran today.");
		}
	}

	public static void doSlack(WebDriver driver) throws Exception {
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS); // MFP can be slow.
		System.out.println("Getting results");
		driver.get("https://app.member.virginpulse.com/#/statement");
		String TodaysPoints = driver
				.findElement(By.xpath(
						"(.//*[normalize-space(text()) and normalize-space(.)='Total Points'])[1]/following::span[1]"))
				.getText();
		String YesterdaysPoints = driver
				.findElement(By.xpath(
						"//*[@id=\"statement-points-tab-content\"]/div[1]/div[2]/div[2]/div/div[1]/div[2]/span[2]"))
				.getText();
		String TotalPoints = driver.findElement(By.id("progress-bar-menu-points-total-value")).getText();
		driver.get("https://app.member.virginpulse.com/#/rewards/earn");
		String DaysLeft = driver.findElement(By.xpath(
				"(.//*[normalize-space(text()) and normalize-space(.)='Program Reminder'])[1]/following::span[1]"))
				.getText();
		int DaysLeftNumber = Integer.valueOf(DaysLeft.substring(0, 2));
		System.out.println(DaysLeftNumber);
		String message = "{\"username\":\"Virgin Inactive Report\","
				+ " \"icon_url\": \"https://james.am/VirginInactive.png\",\n" + "    \"attachments\": [\n {\n "
				+ "\"fallback\": \"Virgin inactive has added " + TodaysPoints + " points today.\",\n  " // This will
																										// show up in
																										// the
																										// notification
				+ "\"color\": \"#2eb886\",\n "
//		  		+ "\"author_name\": \"Points Report\",\n "
//		  		+ "\"author_link\": \"http://flickr.com/bobby/\",\n "
//		  		+ "\"author_icon\": \"http://flickr.com/icons/bobby.jpg\",\n "
//		  		+ "\"title\": \"Slack API Documentation\",\n "
//		  		+ "\"title_link\": \"https://api.slack.com/\",\n  "
				+ "\"text\": \"Virgin Inactive has successfully run.\",\n  " + "\"fields\": [\n " + "{\n "
				+ "\"title\": \"Points added so far today:\",\n  " + "\"value\": \"" + TodaysPoints + "\",\n  "
				+ "\"short\": false\n " + "}, " + "{\n " + "\"title\": \"Points gained yesterday:\",\n  "
				+ "\"value\": \"" + YesterdaysPoints + "\",\n  " + "\"short\": false\n " + "}, " + "{\n "
				+ "\"title\": \"Points this Quarter:\",\n  " + "\"value\": \"" + TotalPoints
				+ " out of 20,000 points. (" + Integer.valueOf(TotalPoints) / 200 + "%) \",\n  " + "\"short\": false\n "
				+ "}, " + "{\n " + "\"title\": \"Days remaining:\",\n  " + "\"value\": \"" + DaysLeft + " ("
				+ Math.round(DaysLeftNumber / 0.9D) + "%)" + "\",\n  " + "\"short\": false\n " + "} "
				+ "            ],\n  "
//		  		+ "\"image_url\": \"http://my-website.com/path/to/image.jpg\",\n  "
//		  		+ "\"thumb_url\": \"http://example.com/path/to/thumb.png\",\n "
				+ "\"footer\": \"Virgin Inactive\",\n   "
				+ "\"footer_icon\": \"https://james.am/VirginInactive.png\",\n   "
//		  		+ "\"ts\": 123456789\n        "
				+ "}\n    ]\n}";

		String SlackHookURL = appProps.getProperty("SlackHookURL");
		@SuppressWarnings("unused")
		HttpResponse<String> response = Unirest.post(SlackHookURL).header("content-type", "application/json")
				.header("cache-control", "no-cache").body(message).asString();

		System.out.println("Sending Report:" + message);
		System.out.println("Report sent. Time to leave.");

	}

	public static void doMyFitnessPal(WebDriver driver) throws Exception {
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS); // MFP can be slow.
		driver.get("https://www.myfitnesspal.com/account/login");
		Thread.sleep(3000);
		clickIfPresent(By.xpath("/html/body/div[8]/div[1]/div/div[2]/div[2]/a[1]"), driver);
		driver.findElement(By.id("username")).click();
		driver.findElement(By.id("username")).clear();
		driver.findElement(By.id("username")).sendKeys(appProps.getProperty("MyFitnessPalEmail"));
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys(appProps.getProperty("MyFitnessPalPassword"));
		driver.findElement(By.id("password")).sendKeys(Keys.RETURN);
		driver.get("https://www.myfitnesspal.com/food/diary");
		int calories = Integer.valueOf(driver
				.findElement(
						By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Totals'])[1]/following::td[1]"))
				.getText());
		if (calories == 0) {
			System.out.println("No calorie input found. Adding fake calories.");
			driver.findElement(By.linkText("Add Food")).click();
			driver.findElement(By.id("search")).click();
			driver.findElement(By.id("search")).clear();
			driver.findElement(By.id("search")).sendKeys("sugar");
			driver.findElement(By.xpath(
					"(.//*[normalize-space(text()) and normalize-space(.)='Quick add calories'])[1]/following::input[4]"))
					.click();
			driver.findElement(
					By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Sugar'])[1]/following::p[1]"))
					.click();
			driver.findElement(By.xpath(
					"(.//*[normalize-space(text()) and normalize-space(.)='To which meal?'])[2]/following::input[1]"))
					.click();
			Thread.sleep(500);
			driver.get("https://www.myfitnesspal.com/food/diary");
			calories = Integer.valueOf(driver
					.findElement(By.xpath(
							"(.//*[normalize-space(text()) and normalize-space(.)='Totals'])[1]/following::td[1]"))
					.getText());
			System.out.println("Added " + calories + " calories.");
		} else
			System.out.println("Found " + calories + " calories already. Skipping adding fake calories.");
	}

	private static void doHabits(WebDriver driver) throws Exception {
		List<String> habits = Arrays.asList("713", "642", "13", "44", "687", "684", "42", "43", "691", "685", "690",
				"688", "9", "689", "692", "686", "4", "678");

		System.out.println("Opening Habits Menu");
		driver.get("https://app.member.virginpulse.com/#/healthyhabits");

		System.out.println("Accepting habits...");
		driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS); // We're using the same page the entire
																				// loop, no need to wait.
		for (int i = 0; i < habits.size(); i++) {
			System.out.println("Accepting habit " + habits.get(i) + "...");
			clickIfPresent(By.id("tracker-" + habits.get(i) + "-track-yes"), driver);
		}
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS); // Back to slow loads :(
	}

	private static void doCards(WebDriver driver) throws Exception {
		System.out.println("Accept first card");
		clickIfPresent(By.xpath("//*[@id=\"daily-tips-slider\"]/div[1]/quizzes/div/div[3]/div[5]/div/button[1]"),
				driver);

		System.out.println("Accept second card");
		clickIfPresent(By.xpath("//*[@id=\"daily-tips-slider\"]/div[1]/quizzes/div/div[3]/div[5]/div/button[1]"),
				driver);

		Thread.sleep(3000);
	}

	private static void login(WebDriver driver) throws Exception {
		System.out.println("Attempting to log in.");
		driver.get(
				"https://iam.virginpulse.com/auth/realms/virginpulse/protocol/openid-connect/auth?client_id=genesis-ui&redirect_uri=https%3A%2F%2Fapp.member.virginpulse.com%2F%3Fredirect_fragment%3D%252Fhome&state=9c3aedca-d8f1-4846-b583-6498ff108e15&nonce=d61e2ef3-65bb-4928-b165-35f9014fb6f6&response_mode=fragment&response_type=code&scope=openid");
		Thread.sleep(1000);
		driver.findElement(By.id("kc-form-wrapper")).click(); // Focus the username field
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

			Reader PropReader = Files.newBufferedReader(PropertyFile);
			appProps.load(PropReader);

			PropReader.close();
			System.setProperty("webdriver.chrome.driver", appProps.getProperty("ChromeDriverLocation"));

		} catch (IOException Ex) {
			System.out.println(
					"Unable to find configuration file \"application.properties\". The file has been created for you.");
			createProperties();
		}

	}

	private static void createProperties() {
		Properties AppProps = new Properties();
		AppProps.setProperty("EmailLogin", "username@domain.tld");
		AppProps.setProperty("Password", "ThisIsMySecurePassword");
		AppProps.setProperty("ChromeDriverLocation", "C:\\chromedriver.exe");
		AppProps.setProperty("MyFitnessPalEmail", "username@domain.tld");
		AppProps.setProperty("MyFitnessPalPassword", "ThisIsMySecureFitbitPassword");
		AppProps.setProperty("MindfulnessVideo", "0");
		AppProps.setProperty("SlackHookURL",
				"https://hooks.slack.com/services/xxxxxxxxx/xxxxxxxxx/xxxxxxxxxxxxxxxxxxxxxxxx");
		AppProps.setProperty("LastRunDate", "2018-01-01");

		Path PropertyFile = Paths.get("application.properties");

		try {
			Writer PropWriter = Files.newBufferedWriter(PropertyFile);
			AppProps.store(PropWriter, "Application Properties");
			PropWriter.close();
		} catch (IOException Ex) { // No Properties file - Create one!
			System.out.println("Could not create file.");
		}
	}

	private static void clickIfPresent(By by, WebDriver driver) throws Exception {
		List<WebElement> Elements = driver.findElements(by);
		for (WebElement Element : Elements) {
			if (Element.isEnabled()) {
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", Element);
			} else {
				System.out.println("Nothing to click here...");
			}
			Thread.sleep(2500);
		}
	}

	private static void doWhil(WebDriver driver) throws Exception { // This section assumes that mindfulness is the last
																	// course you've opened.
		String lastVideo = appProps.getProperty("MindfulnessVideo");
		System.out.println("Last video played was #" + lastVideo);
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS); // Whil is so slow, I think the actual
																			// medidation is meant to be during loads.
		System.out.println("Logging into Whil.");
		driver.get(
				"https://connect.whil.com/virginpulsesso/redirect?destination=series&seriesUrl=https%3A%2F%2Fconnect.whil.com%2Fcms%2Fprograms%2Ffreemium%2Fseries%2Fthrive-mindfulness-101");
		System.out.println("Opening Mindfulness course.");
		driver.get("https://connect.whil.com/goaltags/freemium-mindfulness-101?w");

		if (!driver.findElements(By.id("triggerCloseCurtain")).isEmpty()) { // If we're mid-course, keep going.
			System.out.println("Resuming Mindfulness course");
			clickIfPresent(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div/div[2]/div"), driver);
		} else { // If we're not mid course, do dis.
			System.out.println("Starting Mindfulness course");
			switch (lastVideo) {
			case "0":
				playWhil("introduction-basics-with-kelly", 240000, driver); // 240s for both the intro and first one.
				appProps.setProperty("MindfulnessVideo", "2");
				break;
			case "1":
				playWhil("focus-your-attention", 120000, driver); // We shouldn't ever have to do this one.
				appProps.setProperty("MindfulnessVideo", "2");
				break;
			case "2":
				playWhil("set-intention", 320000, driver);
				appProps.setProperty("MindfulnessVideo", "3");
				break;
			case "3":
				playWhil("mindfulness-of-breath", 320000, driver);
				appProps.setProperty("MindfulnessVideo", "4");
				break;
			case "4":
				playWhil("sense-the-body", 320000, driver);
				appProps.setProperty("MindfulnessVideo", "5");
				break;
			case "5":
				playWhil("recognize-and-release-thoughts", 320000, driver);
				appProps.setProperty("MindfulnessVideo", "6");
				break;
			case "6":
				playWhil("welcome-emotions", 320000, driver);
				appProps.setProperty("MindfulnessVideo", "7");
				break;
			case "7":
				playWhil("relax-the-nervous-system", 320000, driver);
				appProps.setProperty("MindfulnessVideo", "8"); // No break here, finish it up so we complete it once a
																// week.
			case "8":
				playWhil("takeaways-basics-with-mark", 70, driver);
				appProps.setProperty("MindfulnessVideo", "0");
				break;
			}

			System.out.println("Playing video");
			driver.findElement(By.xpath("//*[@id=\"playerContainer\"]/div[2]/button")).click();
			Thread.sleep(310000); // play for 5 minutes
			System.out.println("Video Played. Time to leave.");
		}
	}

	private static void playWhil(String video, int time, WebDriver driver) throws Exception {
		driver.get("https://connect.whil.com/goaltags/freemium-mindfulness-101/sessions/" + video);
		System.out.println("Opening https://connect.whil.com/goaltags/freemium-mindfulness-101/sessions/" + video);
		System.out.println("Playing video");
		clickIfPresent(By.xpath("//*[@id=\"playerContainer\"]/div[2]/button"), driver);
		Thread.sleep(time);
	}

}