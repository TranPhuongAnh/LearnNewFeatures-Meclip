package StepDefinitions;

import configProject.CommonMethod;
import configProject.ConfigFileReader;
import configProject.ExcelHelpers;
import configProject.WebDriverManager;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class OpenNewTab extends WebDriverManager{
    WebDriver driver = null;
    ConfigFileReader config = new ConfigFileReader();
    CommonMethod common = new CommonMethod(getDriver());
    ExcelHelpers excel = new ExcelHelpers();
    FileWriter writer = null;

    //Login Screen
    private By InputPhone = By.xpath("//input[@id='username']");
    private By InputPass = By.xpath("//input[@id='password']");
    private By Button_Submit = By.xpath("//button[@type='submit']");

    //Home page
    private By User = By.xpath("//div[@class='avatar']");
    private By Button_Logout = By.xpath("//a[@href='http://meclip.vn/logout']");
    private By Button_Login = By.xpath("//a[@href='http://meclip.vn/login']");
    private By Banner = By.xpath("//section[@class='home__slide yc-section']");

    //Video Screen
    private By Video = By.xpath("//section/div[1]/div[1]/div[1]");
    private By Video_Check = By.xpath("//*[@id='video-player']/div[2]/div[4]/video");
    private By View = By.xpath("//div[@class='video__view-counter left']");
    private By Progress_Bar = By.xpath("//*[@id='video-player']/div[2]/div[12]/div[5]/div[1]/div");
    private By Error = By.xpath("//div[@class='jw-error-msg jw-info-overlay jw-reset']");

    // Export data
    private List<DataExport> dataExportList = new ArrayList<>();

    public String open_web_brower(){
        Steps.getInstance().ImplicitlyWait_Config();
        String owb = null;
        // Mở trình duyệt
        try {
            driver = getDriver();
            driver.navigate().to(config.getApplicationUrl() + "login");
            common.waitForPageLoaded();
            System.out.println(driver.getCurrentUrl());
            if (driver.getCurrentUrl().equals("http://meclip.vn/")){
                logout_website();
            }
            owb = "Mở trình duyệt thành công";
            System.out.println(owb);
        } catch (WebDriverException e){
            owb = e.getRawMessage();
            e.printStackTrace();
        }
        return owb;
    }

    public String login_website(String sheetName, int rowNum) throws Exception {
        //Nhập thông tin từ file excel
        excel.setExcelFile(config.getDataPath(), sheetName);
        String phone = excel.getCell("phone", rowNum);
        String pass = excel.getCell("password", rowNum);
        System.out.println("Phone: " + phone + "\nPass: " + pass);
        fill_data(InputPhone, phone);
        fill_data(InputPass, pass);

        // Click button và login page
        common.clickElement(Button_Submit);
        common.waitForPageLoaded();

        return phone;
    }

    public void fill_data(By element, String text) {
        driver = getDriver();
        try {
            Thread.sleep(1000); // Đợi 1 giây (1000 milliseconds)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        common.sendKeyElement(element, text);
    }

    public void click_on_the_progress_bar(){
        WebElement progress_bar = driver.findElement(Progress_Bar);

        // Tạo action để di chuột
        Actions actions = new Actions(driver);

        // Lấy vị trí của thanh tiến trình
        int x = progress_bar.getLocation().getX();
        int y = progress_bar.getLocation().getY();
        int width = progress_bar.getSize().getWidth();

        // Tính toán điểm click tại vị trí đầu tiên của thanh tiến trình (với left = 0%)
        int xOffset = x; // Lúc này là vị trí x của thanh tiến trình, tức là left = 0%

        // Di chuột vào vị trí đầu thanh tiến trình
        actions.moveToElement(progress_bar, xOffset, 0).click().perform();

        try {
            Thread.sleep(1000);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void open_new_tabs(List<String> urls, Set<String> tabs){
        // Mở tối đa 10 tab và truyền những url đầu tiên
        for (int i = 0 ; i < 10 && i < urls.size() ; i++){
            String mess_error = null;

            if(i == 0){
                // Lưu window handle của tab
                tabs.add(driver.getWindowHandle());

                driver.get(urls.get(i));

            } else {
                // Mở tab tiếp theo
                driver.switchTo().newWindow(WindowType.TAB);
                // Lưu window handle của tab
                tabs.add(driver.getWindowHandle());

                driver.get(urls.get(i));

                // Nhấn video play lại từ đầu
                click_on_the_progress_bar();
            }
        }
    }

    public List<DataExport> check_view_all_urls(String phone, List<String> urls) throws Exception {
        Set<String> tabs = driver.getWindowHandles();
        config.getImplicitlyWait();

        // Mở tối đa 10 tab và truyền những url đầu tiên
        open_new_tabs(urls, tabs);

        int tabs_size = tabs.size();
        int a = 0;
        if(tabs_size <= 10){
            a = 0;
        } else {
            a = 10;
        }

        // Sau khi mở 10 tab, Chuyển đổi qua lại giữa các tab và truyền URL vào từng tab
        for (int i = a; i < urls.size(); i++) {

            // Chuyển đến tab mà bạn muốn cập nhật
            if (a == 10){
                driver.switchTo().window((String) tabs.toArray()[i % 10]);
            } else {
                driver.switchTo().window((String) tabs.toArray()[i]);
            }

            int view_1 = 0;
            int view_2 = 0;
            String url = null;
            String mess_error = null;
            String reload = null;
            boolean want_continue = false;
            boolean want_break = false;

            if (a == 10){
                url = urls.get(i - 10);
                System.out.println("Tab " + (i % 10 + 1) + ": " + url);
            } else {
                url = urls.get(i);
                System.out.println("Tab " + (i + 1) + ": " + url);
            }

            // Kiểm tra các trường hợp ngoại lệ
            if (common.checkDisplay(View) == false){    //Kiểm tra không tìm được link
                mess_error = "Website lỗi, không mở được video";
                dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                System.out.println(mess_error);
                continue;
            } else if (common.checkDisplay(Error) == true){     // Kiểm tra không play dc video
                mess_error = "Video bị lỗi, không play được video";
                dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                System.out.println(mess_error);
                continue;
            } else if (!driver.getCurrentUrl().equals(url)){  // Kiểm tra video bị next bài
                String mess = "Video bị nhảy sang video khác";
                System.out.println(mess);
                reload = reload_page(url);

                if (reload.contains("ERR_INTERNET_DISCONNECTED")){
                    mess_error = "Kết nối Internet đang bị ngắt";
                    dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                    System.out.println(mess_error);
                    break;
                } else if (common.checkDisplay(View) == false){    //Kiểm tra web lỗi, không tìm được view
                    mess_error = mess + "\n" + "Website lỗi, không mở được video";
                    dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                    System.out.println(mess_error);
                    continue;
                } else if (common.checkDisplay(Error) == true){     // Kiểm tra không play dc video
                    mess_error = mess + "\n" + "Video bị lỗi, không play được video";
                    dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                    System.out.println(mess_error);
                    continue;
                }

                mess_error = mess;
            }

            // Kiểm tra video bị pause và play lại video
            WebElement eVideo = driver.findElement(Video_Check);
            String src_video = eVideo.getAttribute("src");
            System.out.println("Video src: " + src_video);
            if (src_video.equals("")){
                mess_error = "Video bị pause";
                common.clickElement(Video_Check);
                if (src_video.equals("")){
                    mess_error = "Click video lần 1: Video vẫn bị pause";
                    common.clickElement(Video_Check);
                    if (src_video.equals("")){
                        mess_error = "Click video lần 2: Video vẫn bị pause -> out";
                        want_continue = true;
                    }
                }
                dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                System.out.println(mess_error);
            }
            if (want_continue == true){
                continue;
            }

            // lấy giá trị view_1 của url cũ
            view_1 = get_view_count();
            System.out.println(view_1);

            if ((a == 10 && i % 10 == 0) || (a == 0 && i == 0)){
                long time = config.getViewSeconds() * 1000;
                Thread.sleep(time);
                System.out.println("Chờ video chạy: " + time);
            }

            for (int j = 1 ; j < 3 ; j++){
                Boolean want_con = false;
                reload = reload_page(url);
                if (reload.contains("ERR_INTERNET_DISCONNECTED")){
                    mess_error = "Kết nối Internet đang bị ngắt";
                    dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                    System.out.println(mess_error);
                    want_break = true;
                    break;
                } else if (common.checkDisplay(View) == false){    //Kiểm tra web lỗi, không tìm được view
                    mess_error = "Reload page lần " + j + ": Website lỗi, không mở được video";
                    dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                    System.out.println(mess_error);
                    want_con = true;
                    want_continue = want_con;
                    continue;
                }

                int view = get_view_count();
                if (view > view_1){
                    System.out.println("Reload page " + j + " lần");
                    break;
                }
            }
            if (want_break == true){
                break;
            } else if(want_continue == true){
                continue;
            }

            // Lấy giá trị view video lần 2
            view_2 = get_view_count();
            if (view_2 > view_1){
                System.out.println(view_2);
                dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
            } else {
                reload = reload_page(url);   // Lần 3
                System.out.println("Reload page 3 lần");
                if (reload.contains("ERR_INTERNET_DISCONNECTED")) {
                    mess_error = "Kết nối Internet đang bị ngắt";
                    dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                    System.out.println(mess_error);
                    break;
                } else if (common.checkDisplay(View) == false){    //Kiểm tra web lỗi, không tìm được view
                    mess_error = "Reload page lần 3: Website lỗi, không mở được video";
                    dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                    System.out.println(mess_error);
                    continue;
                }

                // Lấy giá trị view video lần 2
                view_2 = get_view_count();
                System.out.println(view_2);
                // So sánh giá trị
                if (view_2 <= view_1) {
                    mess_error = "So sánh số lượng view không chính xác";
                    dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                    System.out.println(mess_error);
                    continue;
                }
                dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
            }

            // Mở URL mới trong tab đó
            if (a == 10){
                driver.get(urls.get(i));

                // Nhấn video play lại từ đầu
                click_on_the_progress_bar();

                System.out.println(" Url mới: " + driver.getCurrentUrl());
            } else {
                System.out.println("Không có url mới");
            }
        }

        return dataExportList;
    }

    public String reload_page(String url){
        String reload = null;
        try {
            driver.navigate().to(url);
            common.waitForPageLoaded();
            reload = "Reload page thành công";
        } catch (WebDriverException e) {
            reload = e.getRawMessage();
            e.printStackTrace();
        }
        return reload;
    }

    public int get_view_count(){
        WebElement view = driver.findElement(View);
        String string = view.getText();
        String sub = null;
        String sub_1 = null;
        for(int i = 0; i < string.length() ; i++){
            char s_i = string.charAt(i);
            if(s_i == ' '){
                sub_1 = string.substring(0, i);
                break;
            }
        }
        sub = sub_1;
        for(int j = 0 ; j < sub_1.length() ; j++){
            char s_j = string.charAt(j);
            if(s_j == '.'){
                sub = sub_1.substring(0, j) + sub_1.substring( j+1, sub_1.length());
            }
        }

        int viewNumber = Integer.parseInt(String.valueOf(sub));
        return viewNumber;
    }

    public void logout_website(){
        common.clickElement(User);
        common.clickElement(Button_Logout);
        common.waitForPageLoaded();

        if (common.checkDisplay(Button_Login) == false){
            common.clickElement(User);
            common.clickElement(Button_Logout);
            common.waitForPageLoaded();
            System.out.println("Logout failed, try again");
        }
        common.clickElement(Button_Login);
        common.waitForPageLoaded();
        System.out.println("Logout successful");
    }

    @Given("Runner tool main")
    public void runner_tool_main() throws Exception {
        String sh_1 = "Account";
        String sh_2 = "Url";
        int sum_row_1 = excel.getSumRow(config.getDataPath(), sh_1);
        List<String> urls = excel.readUrlExcel(config.getDataPath(), sh_2);

        // Tạo file data
        Workbook workbook = new XSSFWorkbook();
        Sheet sh_write = excel.ExcelFileCreate(workbook);

        // Tạo file trong thiết bị
        File dir = new File("target/ouput");
        dir.mkdirs();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");
        String time = LocalDateTime.now().format(formatter);
        String nameFile = "ToolReport_" + time + ".xlsx";
        FileOutputStream fileExcelWrite = new FileOutputStream("target/ouput/" + nameFile);

        for (int i = 1 ; i <= sum_row_1 ; i++){
            String mess_error = null;

            String internet = open_web_brower();
            if (internet.contains("ERR_INTERNET_DISCONNECTED")){
                mess_error = "Kết nối Internet đang bị ngắt";
                dataExportList.add(new DataExport("", "", 0, 0, mess_error));
                System.out.println(mess_error);
                break;
            }

            String phone = login_website(sh_1, i);
            if (common.checkDisplay(Banner) == false){
                mess_error = "Tài khoản không chính xác || Login không thành công";
                dataExportList.add(new DataExport(phone, "", 0, 0, mess_error));
                System.out.println(mess_error);
                continue;
            }

            dataExportList = check_view_all_urls(phone, urls);
        }

        // Ghi data
        excel.setData_Class(sh_write, dataExportList);
        workbook.write(fileExcelWrite);
        workbook.close();
        fileExcelWrite.close();
    }

    @And("Close browser and all tabs")
    public void close_browser_and_all_tabs() throws InterruptedException {
        Steps.getInstance().CloseDriver();
        System.out.println("Đóng trình duyệt");
    }
}
