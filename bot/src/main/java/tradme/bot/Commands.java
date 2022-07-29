package tradme.bot;
import static java.util.stream.Collectors.toList;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import net.dv8tion.jda.api.events.message.guild.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Commands extends ListenerAdapter {
	
	ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private static final String PREF = "!";
	private long maxId = 0L;
	private boolean firstRun = true;
	private static boolean alreadyRunning = false;
	private static final String path = "https://www.trademe.co.nz/a/motors/cars/search";
	private static final String query = "?bof=NTk4ZOip&user_region=70&sort_order=motorslatestlistings&listing_type=private";
	private static final String trademeURL = path + query;
	private static boolean shouldRun = true;
	private static List<Long> queryTimes = new ArrayList<Long>();
	
	/*
	 * This method is called when a message is sent in a channel.
	 * It checks if the message starts with the PREF and if it does, it calls the appropriate method.
	 */
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		String[] args = event.getMessage().getContentRaw().split(" ");
		if (event.getAuthor().isBot() || !args[0].startsWith(PREF)) {return;}
		switch(args[0]) {
		case PREF + "run":
			if (alreadyRunning) {
				event.getMessage().reply("Program already running. Please stop the old program using `!stop` before starting it again.").queue();
				return;
			}
			firstRun = true;
			shouldRun = true;
			alreadyRunning = true;
			Runnable runnableScraper = new Runnable() {
				@Override
				public void run() {

					event.getMessage().reply("Initialising...").queue();
					ChromeOptions options = new ChromeOptions();
			        options.addArguments("--headless");
			        options.addArguments("--window-size=1400,800");
			        WebDriverManager.chromedriver().setup();
			        WebDriver driver = new ChromeDriver(options);

					while (shouldRun) {
						List<WebElement> elements= scrape(driver);;
						List<WebElement> elements3= elements.stream().limit(6).collect(toList());
						Collections.reverse(elements3);
						List<Car> cars = new ArrayList<>();
						for(WebElement w : elements3) {
							Car c = buildCar(w);
							cars.add(c);
						}
						for(Car car : cars) {
							if (car.ID > maxId) {
								maxId = car.ID;
								if (!firstRun) {
									event.getChannel().sendMessage(car.link).queue();
									event.getChannel().sendMessage(car.postInfo).queue();
								}
							}
						}
						if (firstRun) {
							event.getChannel().sendMessage("Initalised successfully.").queue();
						}
						firstRun = false;
					}
					alreadyRunning = false;
					event.getChannel().sendMessage("Stopped running.").queue();
				}
			};
			scheduler.schedule(runnableScraper, 0, TimeUnit.SECONDS);
			break;
		case PREF + "status":
			if (alreadyRunning) {
				event.getMessage().reply("Program is running.").queue(); 
			} else {
				event.getMessage().reply("Program is not running.").queue(); 
			}
			break;
		case PREF + "stop":
			if (shouldRun) {
				shouldRun = false;
				event.getMessage().reply("Instructing program to stop running. Please wait.").queue();
			} else {
				event.getMessage().reply("Program is not running.").queue();
			}
			break;
		case PREF + "help":
			event.getMessage().reply("Commands:\n"
					+ "`!help` - shows a list of commands\n"
					+ "`!run` - starts the program\n"
					+ "`!stop` - stops the program\n"
					+ "`!status` - says whether the program is running or not\n"
					+ "`!terminate` - turns off the bot").queue();
			break;
		case PREF + "time":
			double avg = queryTimes.stream().mapToDouble(a -> a).average().orElse(0.0) / 1000.0;
			event.getMessage().reply("Average query time of last " + queryTimes.size() + " queries was " + String.format("%.2f", avg) + " seconds.\n"
				+ "Max query time of last " + queryTimes.size() + " queries was " + String.format("%.2f", Collections.max(queryTimes).doubleValue()/1000.0) + " seconds.\n"
				+ "Min query time of last " + queryTimes.size() + " queries was " + String.format("%.2f", Collections.min(queryTimes).doubleValue()/1000.0) + " seconds.").queue();
			break;
		case PREF + "terminate":
			event.getMessage().reply("Terminating. Goodbye cruel world.").queue();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
			System.exit(0);
			break;
		default:
			if (args[0].startsWith(PREF)) {
				event.getMessage().reply("Invalid command. Use `!help` for a list of commands.").queue();
			}
			break;
		}
	}
    
	/*
	 *  This method is called when the bot is ready.
	 */
    private List<WebElement> scrape(WebDriver driver) {
    	long start = System.currentTimeMillis();
    	driver.get(trademeURL);
    	long duration = System.currentTimeMillis() - start;
    	queryTimes.add(duration);
    	if (queryTimes.size() > 20) {
    		queryTimes.remove(0);
    	}
    	return driver.findElements(By.className("tm-motors-search-card__link"));
    }
    
	/*
	 * This will build a car object from a web element from the scraping of the page
	 * 
	 */
	public Car buildCar(WebElement w) {
		// Get the link to the car
		String link = w.getAttribute("href");
		// Get the ID of the car
		long id = Long.parseLong(w.getAttribute("href").split("/")[9].substring(0,10));
		// Get the title of the car
		String title = w.findElement(By.className("tm-motors-search-card__title"))
				.getAttribute("aria-label");
		// Get the make and model of the car
		String Make_Model = title.substring(5,title.length()-1);
		// Get the year of the car
		int year = Integer.parseInt(title.substring(0, 4));
		// spilt the make and model into separate strings
		String make = Make_Model.split(" ")[0];
		String arr[] = Make_Model.split(" ",2);
		String model;
		if(arr.length<2) {
			model = arr[0];
		}else {
			model = arr[1];
		}
		// get the odometer of the car
		long odometer = 0;
		try {
			String o = w.findElement(By.className("tm-motors-search-card__body-odometer"))
					.getAttribute("aria-label");
			odometer = Integer.parseInt(o.substring(0,o.length()-1));
		} catch (Exception e) {}
		// get the price of the car
		String p = w.findElement(By.className("tm-motors-search-card__price")).getText();
		Number number = null;
		try {
			number = NumberFormat.getCurrencyInstance(Locale.US)
			        .parse(p);
		} catch (ParseException e) {e.printStackTrace();}
		// get the engine size of the car
		String engineDetails = w.findElement(By.className("tm-motors-search-card__engine-details"))
				.getAttribute("aria-label");
		// build the car object and return it
		Car car = new Car(id,year,make, model, odometer, number.intValue(),title,engineDetails);
		car.setLink(link);
		return car;
	}
}
