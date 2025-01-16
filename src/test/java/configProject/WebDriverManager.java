package configProject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.time.Duration;

public class WebDriverManager {
//    private static final String CHROME_DRIVER = "webdriver.chrome.driver"; //Cấu hình biến trỏ đến file webDriver cứng, selenium v4 trở đi k cần webDriver cứng
    private static final WebDriverManager webDriverManager = new WebDriverManager();
    public static CommonMethod commonMethod;
    private static WebDriver driver = initializeWebDriver();
    private static EnvironmentType environmentType;
    private static DriverType driverType;

    // Phương thức để cấu hình và khởi tạo WebDriver
    public static WebDriver initializeWebDriver() {
        // Tạo một ChromeOptions đối tượng và loại bỏ chế độ headless
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-gpu");
        options.addArguments("--start-maximized");
        options.addArguments("--autoplay-policy=no-user-gesture-required"); // Tắt chính sách autoplay

        // Khởi tạo WebDriver với các cài đặt đã cấu hình
        return new ChromeDriver(options);
    }

    //Hàm sử dụng được tất cả hàm của class WebDriverManager
    public static WebDriverManager getInstance() {
        return webDriverManager;
    }

    //Hàm chờ theo thời gian trong file config
    public void ImplicitlyWait_Config() {
        long time = FileReaderManager.getInstance().getConfigReader().getImplicitlyWait();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(time));
    }

    //Hàm đặt biến cho giá trị cấu hình từ file config
    public WebDriverManager() {
        driverType = FileReaderManager.getInstance().getConfigReader().getBrowser();
        environmentType = FileReaderManager.getInstance().getConfigReader().getEnvironment();
    }

    // Hàm getDriver() khởi tạo diver
    protected WebDriver getDriver(){
        if (driver == null){
            driver = createDriver();
        }
        return driver;
    }

    // Hàm createDriver() xác định môi trường cho webDriver
    private  WebDriver createDriver(){
        switch (environmentType){
            case LOCAL:
                driver = createLocalDriver();
                break;
            case REMOTE:
                driver = createRemoteDriver();
                break;
        }
        return driver;
    }

    private WebDriver createRemoteDriver(){
        throw new RuntimeException("RemoteWebDriver chưa được triển khai");
    }

    //Hàm createLocalDriver() xác định webDriver đang dùng từ file config của môi trường LOCAL
    private WebDriver createLocalDriver(){
        switch (driverType){
            case CHROME:
                //Chỉ cấu hình khi selenium version < 4, với selenium v4 trở đi thì k cần bản cứng
//                System.setProperty(CHROME_DRIVER, System.getProperty("user.dir") + FileReaderManager.getInstance().getConfigReader().getDriverPath());
                driver = new ChromeDriver();
                break;
            case FIREFOX:
                driver = new FirefoxDriver();
                break;
            case INTERNETEXPLORER:
                driver = new InternetExplorerDriver();
                break;
            case EDGE:
                driver = new EdgeDriver();
                break;
            case SAFARI:
                driver = new SafariDriver();
                break;
        }
        driver.manage().window().maximize();

        ImplicitlyWait_Config();
        return driver;
    }

    // Hàm đóng tất cả tab của web browser
    public void CloseDriver() throws InterruptedException {
        try
        {
            driver.close();
            Thread.sleep(1000);
            driver.quit();
        }
        catch(Exception exp) {
            System.out.println(exp.getMessage());
//            System.out.println(exp.getCause());
//            exp.printStackTrace();
        }
    }
}
