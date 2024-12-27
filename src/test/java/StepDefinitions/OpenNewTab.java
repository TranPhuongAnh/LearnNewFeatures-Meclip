package StepDefinitions;

import configProject.CommonMethod;
import configProject.ConfigFileReader;
import configProject.ExcelHelpers;
import configProject.WebDriverManager;
import org.openqa.selenium.*;

import java.awt.*;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

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
    private By Video_Check = By.xpath("//section/div[1]/div[1]/div[1]/div/div[2]/div[4]/video");
    private By View = By.xpath("//div[@class='video__view-counter left']");
    private By Pause = By.xpath("//*[@id='video-player']/div[2]/div[12]/div[5]/div[2]/div[1]");
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
//            if (driver.getCurrentUrl().equals("http://meclip.vn/")){
//                logout_website();
//            }
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

//        Assert.assertTrue(common.checkDisplay(Banner) == true, "Login không thành công");

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

    public void open_new_tab_with_url(String sheetName, String phone, List<String> urls) throws Exception {
        ArrayList<String> tabs = new ArrayList<>();

        // Mở 10 tab và truyền 10 url đầu tiên
        for (int i = 0 ; i < 10 && i < urls.size() ; i++){
            if(i == 0){
                driver.get(urls.get(i));
                // Lưu window handle của tab
                tabs.add(driver.getWindowHandle());
            } else {
                driver.switchTo().newWindow(WindowType.TAB);
                // Lưu window handle của tab
                tabs.add(driver.getWindowHandle());
                driver.get(urls.get(i));
            }
        }

        // Sau khi mở 10 tab, Chuyển đổi qua lại giữa các tab và truyền URL vào từng tab
        for (int i = 10; i < urls.size(); i++) {
            // Chuyển đến tab mà bạn muốn cập nhật
            driver.switchTo().window(tabs.get(i % 10)); // Chuyển sang tab tương ứng với i
            driver.get(urls.get(i)); // Mở URL trong tab đó
            System.out.println("Tab " + (i % 10 + 1) + ": " + driver.getCurrentUrl());
        }
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

    public void open_url_and_check_website(String sheetName, String phone, int view_1, int view_2) throws Exception {
        // Mở url và lấy giá trị tổng url
        excel.setExcelFile(config.getDataPath(), sheetName);
        int sum_row = excel.getSumRow(config.getDataPath(), sheetName);
        System.out.println(sum_row);

        for(int i = 1; i <= sum_row ; i++) {
            int rownum = i;
            String url = null;
            String mess_error = null;
            String reload = null;

            url = excel.getCell("url", rownum);
            if(url == ""){
                mess_error = "Không có url trong sheet";
                dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                System.out.println(mess_error);
                continue;
            }

            System.out.println(url);
            config.getImplicitlyWait();
            try {
                driver.navigate().to(url);
                common.waitForPageLoaded();
            } catch (WebDriverException e){
                e.getRawMessage();
                mess_error = "Kết nối Internet đang bị ngắt";
                dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                System.out.println(mess_error);
                break;
            }

            if ( driver.getCurrentUrl().equals("http://meclip.vn/404")){    //Kiểm tra không tìm được link
                mess_error = "Video lỗi 404";
                dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                System.out.println(mess_error);
                continue;
            } else if (driver.getTitle().equals("Server Error")){    //Kiểm tra lỗi server
                mess_error = "500 | Server Error";
                dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                System.out.println(mess_error);
                continue;
            } else if (common.checkDisplay(Error) == true){     // Kiểm tra không play dc video
                mess_error = "Video bị lỗi, không play được video";
                dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                System.out.println(mess_error);
                continue;
            } else if (driver.getTitle().equals("504 Gateway Time-out")){   // Kiểm tra lỗi time out
                mess_error = "504 Gateway Time-out";
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
                } else if ( driver.getCurrentUrl().equals("http://meclip.vn/404")){    //Kiểm tra không tìm được link
                    mess_error = mess + "\n" + "Video lỗi 404";
                    dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                    System.out.println(mess_error);
                    continue;
                } else if (driver.getTitle().equals("Server Error")){    //Kiểm tra lỗi server
                    mess_error = mess + "\n" + "500 | Server Error";
                    dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                    System.out.println(mess_error);
                    continue;
                } else if (common.checkDisplay(Error) == true){     // Kiểm tra không play dc video
                    mess_error = mess + "\n" + "Video bị lỗi, không play được video";
                    dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                    System.out.println(mess_error);
                    continue;
                } else if (driver.getTitle().equals("504 Gateway Time-out")) {  // Kiểm tra lỗi time out
                    mess_error = mess + "\n" + "504 Gateway Time-out";
                    dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                    System.out.println(mess_error);
                    continue;
                }
                mess_error = mess;
                dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
            }
        }
    }

    public boolean check_paused_video(){
        String mess_error = null;
        Boolean want_continue = false;

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
            System.out.println(mess_error);
        }

        return want_continue;
    }

    public List<DataExport> open_url_video_and_wait_run_video_time(String sheetName, int sec, String phone) throws Exception {
        // Mở url và lấy giá trị tổng url
        excel.setExcelFile(config.getDataPath(), sheetName);
        int sum_row = excel.getSumRow(config.getDataPath(), sheetName);
        System.out.println(sum_row);

        for(int i = 1; i <= sum_row ; i++){
            int rownum = i;
            String url = null;
            int view_1 = 0;
            int view_2 = 0;
            String mess_error = null;
            String reload = null;
            Boolean want_break = false;
            Boolean want_continue = false;

            url = excel.getCell("url", rownum);
            if(url == ""){
                mess_error = "Không có url trong sheet";
                dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                System.out.println(mess_error);
                continue;
            }

            System.out.println(url);
            config.getImplicitlyWait();
            try {
                driver.navigate().to(url);
                common.waitForPageLoaded();
            } catch (WebDriverException e){
                e.getRawMessage();
                mess_error = "Kết nối Internet đang bị ngắt";
                dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                System.out.println(mess_error);
                break;
            }

            if ( driver.getCurrentUrl().equals("http://meclip.vn/404")){    //Kiểm tra không tìm được link
                mess_error = "Video lỗi 404";
                dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                System.out.println(mess_error);
                continue;
            } else if (driver.getTitle().equals("Server Error")){    //Kiểm tra lỗi server
                mess_error = "500 | Server Error";
                dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                System.out.println(mess_error);
                continue;
            } else if (common.checkDisplay(Error) == true){     // Kiểm tra không play dc video
                mess_error = "Video bị lỗi, không play được video";
                dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                System.out.println(mess_error);
                continue;
            } else if (driver.getTitle().equals("504 Gateway Time-out")){   // Kiểm tra lỗi time out
                mess_error = "504 Gateway Time-out";
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
                } else if ( driver.getCurrentUrl().equals("http://meclip.vn/404")){    //Kiểm tra không tìm được link
                    mess_error = mess + "\n" + "Video lỗi 404";
                    dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                    System.out.println(mess_error);
                    continue;
                } else if (driver.getTitle().equals("Server Error")){    //Kiểm tra lỗi server
                    mess_error = mess + "\n" + "500 | Server Error";
                    dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                    System.out.println(mess_error);
                    continue;
                } else if (common.checkDisplay(Error) == true){     // Kiểm tra không play dc video
                    mess_error = mess + "\n" + "Video bị lỗi, không play được video";
                    dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                    System.out.println(mess_error);
                    continue;
                } else if (driver.getTitle().equals("504 Gateway Time-out")) {  // Kiểm tra lỗi time out
                    mess_error = mess + "\n" + "504 Gateway Time-out";
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

            // Lấy giá trị view video lần 1
            view_1 = get_view_count();
            System.out.println(view_1);

            // Đợi video chạy và lấy giá trị view
            int mini_sec = sec * 1000;
            Thread.sleep(mini_sec);
//            common.hoverAndClick(Video, Pause);

            for (int j = 1 ; j < 3 ; j++){
                Boolean want_con = false;
                reload = reload_page(url);
                if (reload.contains("ERR_INTERNET_DISCONNECTED")){
                    mess_error = "Kết nối Internet đang bị ngắt";
                    dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                    System.out.println(mess_error);
                    want_break = true;
                    break;
                } else if (driver.getCurrentUrl().equals("http://meclip.vn/404")){
                    mess_error = "Reload page lần " + j + ": Lỗi 404";
                    dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                    System.out.println(mess_error);
                    want_con = true;
                    want_continue = want_con;
                    continue;
                } else if (driver.getTitle().equals("Server Error")){
                    mess_error = "Reload page lần " + j + ": Lỗi 500 | Server Error";
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
                if (reload.contains("ERR_INTERNET_DISCONNECTED")){
                    mess_error = "Kết nối Internet đang bị ngắt";
                    dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                    System.out.println(mess_error);
                    break;
                } else if (driver.getCurrentUrl().equals("http://meclip.vn/404")){
                    mess_error = "Reload page lần 3 : Lỗi 404";
                    dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                    System.out.println(mess_error);
                    continue;
                } else if (driver.getTitle().equals("Server Error")){
                    mess_error = "Reload page lần 3: Lỗi 500 | Server Error";
                    dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                    System.out.println(mess_error);
                    continue;
                }

                // Lấy giá trị view video lần 2
                view_2 = get_view_count();
                System.out.println(view_2);
                // So sánh giá trị
                if (view_2 <= view_1){
                    mess_error = "So sánh số lượng view không chính xác";
                    dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
                    System.out.println(mess_error);
                    continue;
                }
                dataExportList.add(new DataExport(phone, url, view_1, view_2, mess_error));
            }

        }
        return dataExportList;
    }
}
