package configProject;

import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.apache.commons.io.FileUtils;
import org.jboss.aerogear.security.otp.Totp;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;

public class CommonMethod extends WebDriverManager {
    static WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;

    public CommonMethod(WebDriver driver) {
        CommonMethod.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        js = (JavascriptExecutor) driver;
    }


    /**
     * ---- CHECK GIAO DIỆN TƯƠNG TÁC VỚI PHẦN TỬ -----
     */
    //Check hiển thị phần tử
    public boolean checkDisplay(By element) {
        WebElement locator;
        try {
            locator = driver.findElement(element);
            return locator.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    //Check cho phép thao tác với phần tử
    public boolean checkEnable(By element) {
        WebElement locator;
        try {
            locator = driver.findElement(element);
            return locator.isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // scroll tới phần tử
    public void scrollToElement(By element) {
        driver = getDriver();
        // chuyển định dạng của phần tử
        WebElement element_name = driver.findElement(element);
        // Scroll tới phần tử
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element_name);
    }


    /**
     * --- NHÓM THAO TÁC VỚI CHUỘT ---
     */
    // Nhấn đúp chuột vào phần tử
    public void shouldDoubleClickElement(By element) {
        WebElement dblClick = driver.findElement(element);
        Actions actions = new Actions(driver);
        actions.doubleClick(dblClick).perform();
    }

    // Nhấn chuột phải vào phần tử
    public void shouldContextClick(By element) {
        WebElement dblClick = driver.findElement(element);
        Actions actions = new Actions(driver);
        actions.contextClick(dblClick).perform();
    }


    /**
     * --- Hàm hover chuột
     */
    //Hover vào 1 đối tượng
    public void hover(By e){
        WebElement hover_e = driver.findElement(e);
        Actions actions = new Actions(driver);
        actions.moveToElement(hover_e).perform();
    }

    //Hover đối tượng A và click đối tượng B
    public void hoverAndClick(By A, By B){
        WebElement hover_A = driver.findElement(A);
        WebElement click_B = driver.findElement(B);
        Actions actions = new Actions(driver);
        actions.moveToElement(hover_A).click(click_B).build().perform();
//        actions.moveToElement(hover_A).build().perform(); //Hai dòng 97-98 tác dụng tương tự dòng 96
//        click_B.click();
    }
    //Hover menu và action vào submenu (trường hợp hover vào A mà không kịp bắt B)
    public void hoverMenu(By Menu, By Submenu){
        WebElement menu = driver.findElement(Menu);
        WebElement submenu = driver.findElement(Submenu);
        Actions actions = new Actions(driver);
        actions.moveToElement(menu).perform();
        actions.moveToElement(submenu);
        actions.click();
        actions.perform();
    }

    /**
     * --- NHÓM THAO TÁC VỚI TEXT
     */
    // Get text từ lable
    public String getText(By element) {
        String text = driver.findElement(element).getText();
        return text;
    }

    // Get text từ input
    public String getValue(By element) {
        String text = driver.findElement(element).getAttribute("value");
        return text;
    }


    /**
     * --- NHÓM THAO TÁC CLICK
     */
    // Thực hiện click phần tử khi phần tử hiển thị trên DOM
    public void clickElement(By element) {
        driver.findElement(element).click();
    }

    // Thực hiện click phần tử khi phần tử không hiển thị trên DOM với JS
    public void clickElementHidden(By element) {
        WebElement element1 = driver.findElement(element);
        js.executeScript("arguments[0].click();", element1);
    }


    /**
     * --- NHÓM THAO TÁC LẤY THÔNG TIN URL
     */
    // Thực hiện lấy URL
    public String getURL() {
        String IdUserChrome = driver.getCurrentUrl();
        return IdUserChrome;
    }


    /**
     * --- NHÓM THAO TÁC LẤY TÊN MIỀN ---
     */
    // Thực hiện lấy tên miền
    public String getDomainName(By element) {
        return js.executeScript("return document.domain;").toString();
    }

    // Thực hiện GET URL khi phần tử không hiển thị trên DOM với JS
    public String getURLHidden() {
        String IdUserChrome = js.executeScript("return document.URL;").toString();
        return IdUserChrome;
    }


    /**
     * --- NHÓM THAO TÁC DỮ LIỆU TRONG TRƯỜNG ---
     */
    // Hàm dùng chung điền dữ liệu vào element
    public void sendKeyElement(By element, String text) {
        driver.findElement(element).sendKeys(text);
    }


    // Hàm dùng chung điền dữ liệu vào element bị ẩn
    public void sendKeyElementHidden(By element, String text) {
        js.executeScript(element + ".setAttribute('value','" + text + "');");
    }

    // Hàm dùng chung xóa dữ liệu trong element
    public void clearTextElement(By element) {
        driver.findElement(element).clear();
    }

    //Hàm dùng chung chọn giá trị đầu tiên trong selectbox tĩnh
    public void selectStaticValue(By element) {
        WebElement element1 = driver.findElement(element);
        Select select = new Select(element1);
        select.selectByIndex(0);
    }

    // Hàm dùng chung chọn giá trị "option" trong combobox động
    public void selectDynamicValue(By element, By option) {
        WebElement element1 = driver.findElement(element);
        element1.click();
        WebElement option3 = driver.findElement(option);
        option3.click();
    }


    /**
     * --- HÀM DÙNG CHUNG ĐIỀN MÃ XÁC THỰC GOOGLE AUTHEN---
     */
    public void googleAuthenticator(By element, String code) {
        try {
            Totp totp = new Totp(code);
            String otpCode = totp.now();
            commonMethod.sendKeyElement(element, otpCode);
        } catch (Exception e) {
            e.getMessage();
        }
    }
//        try {
//            // Lấy thời gian hiện tại theo múi giờ UTC
//
//            // Lấy mã OTP theo thời gian Việt Nam
//            Top top = new Top(encode(code.getBytes()));
//            String otpCode = top.now();
//            commonMethod.sendKeyElement(element, otpCode);
//        } catch (Exception e) {
//            e.getMessage();
//        }
//    }
//
//    public String encode(byte[] bytes) {
//        return new Base32().encodeToString(bytes);
//    }

    /**
     * Hàm lấy chuỗi * trong input Password theo chuỗi ký tự đã nhập
     */
    public String Password(String p, String kytu){
        int a = p.length();
        String string = "";
        if (a < 2){
            string = kytu;
        } else {
            for (int i = 0; i < a ; i++){
                string += kytu;
            }
        }
        return string;
    }

    /**
     * ---- HÀM DÙNG CHUNG THAO TÁC VỚI ALERT ----
     */
    // Alert: Thực hiện nhấn Đồng ý
    public void alertAccept() {
        Alert alert = driver.switchTo().alert();
        alert.accept();
    }

    // Alert: Thực hiện nhấn Từ chối
    public void alertDismiss() {
        Alert alert = driver.switchTo().alert();
        alert.dismiss();
    }

    //Messsage (flash/toast): Lấy nội dung message
    public String messageText(By e){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(e));
        String textMess = message.getText();

        return textMess;
    }


    /**
     * THAO TÁC VỚI CHECKBOX
     */
    // Kiểm tra trạng thái checked của checkbox
    public String checkCheckboxStatus(By element) {
        String status;
        boolean b = true;
        if (driver.findElement(element).isSelected())
            status = "Checked";
        else status = "Uncheck";
        return status;
    }

    // Thực hiện checked checkbox
    public void clickCheckbox(By element) {
        driver.findElement(element).click();
    }

    // Thực hiện checked checkbox khi ẩn trên DOM
    public void checkedCheckboxHidden(By element) {
        js.executeScript("document.getElementById('id_of_element').checked=true;");
    }

    // Thực hiện uncheck checkbox khi ẩn trên DOM
    public void unCheckedCheckboxHidden(By element) {
        js.executeScript("document.getElementById('id_of_element').checked=false;");
    }

    // Thực hiện uncheck checkbox


    /**
     * THAO TÁC VỚI RADIO BUTTON
     */
    // Check radiobutton is checked
    public void checkCheckedRadioButton(By element) {
        driver.findElement(element).isSelected();
    }

    // Check radiobutton is Enable
    public void checkEnableRadioButton(By element) {
        driver.findElement(element).isEnabled();
    }

    // Check radiobutton is Disable
    public void checkDisableRadioButton(By element) {
        driver.findElement(element).isDisplayed();
    }

    // Click on radio button
    public void clickRadioButton(By element) {
        driver.findElement(element).click();
    }


    /**
     * ---- NHÓM THAO TÁC BÀN PHÍM ----
     */
    // Hàm chung thực hiện nhấn phím Enter
    public void keyPressEnter() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

    public void keyPressBacKSpace() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_BACK_SPACE);
        robot.keyRelease(KeyEvent.VK_BACK_SPACE);
    }

    public void keyPressTab() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_TAB);
    }

    public void keyPressCancel() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_CANCEL);
        robot.keyRelease(KeyEvent.VK_CANCEL);
    }

    public void keyPressClear() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_CLEAR);
        robot.keyRelease(KeyEvent.VK_CLEAR);
    }

    public void keyPressShift() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_SHIFT);
        robot.keyRelease(KeyEvent.VK_SHIFT);
    }

    public void keyPressControl() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    public void keyPressAlt() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyRelease(KeyEvent.VK_ALT);
    }

    public void keyPressPause() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_PAUSE);
        robot.keyRelease(KeyEvent.VK_PAUSE);
    }

    public void keyPressCapsLock() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_CAPS_LOCK);
        robot.keyRelease(KeyEvent.VK_CAPS_LOCK);
    }

    public void keyPressEscape() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ESCAPE);
        robot.keyRelease(KeyEvent.VK_ESCAPE);
    }

    public void keyPressSpace() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_SPACE);
        robot.keyRelease(KeyEvent.VK_SPACE);
    }

    public void keyPressPageUp() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_PAGE_UP);
        robot.keyRelease(KeyEvent.VK_PAGE_UP);
    }

    public void keyPressPageDown() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_PAGE_DOWN);
        robot.keyRelease(KeyEvent.VK_PAGE_DOWN);
    }

    public void keyPressEnd() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_END);
        robot.keyRelease(KeyEvent.VK_END);
    }

    public void keyPressHome() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_HOME);
        robot.keyRelease(KeyEvent.VK_HOME);
    }

    public void keyPressLeft() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_LEFT);
        robot.keyRelease(KeyEvent.VK_LEFT);
    }

    public void keyPressUp() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_UP);
        robot.keyRelease(KeyEvent.VK_UP);
    }

    public void keyPressRight() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_RIGHT);
        robot.keyRelease(KeyEvent.VK_RIGHT);
    }

    public void keyPressDown() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_DOWN);
        robot.keyRelease(KeyEvent.VK_DOWN);
    }

    public void keyPressComma() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_COMMA);
        robot.keyRelease(KeyEvent.VK_COMMA);
    }

    public void keyPressMinus() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_MINUS);
        robot.keyRelease(KeyEvent.VK_MINUS);
    }

    public void keyPressPeriod() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_PERIOD);
        robot.keyRelease(KeyEvent.VK_PERIOD);
    }

    public void keyPressSlash() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_SLASH);
        robot.keyRelease(KeyEvent.VK_SLASH);
    }

    public void keyPress0() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_0);
        robot.keyRelease(KeyEvent.VK_0);
    }

    public void keyPress1() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_1);
        robot.keyRelease(KeyEvent.VK_1);
    }

    public void keyPress2() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_2);
        robot.keyRelease(KeyEvent.VK_2);
    }

    public void keyPress3() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_3);
        robot.keyRelease(KeyEvent.VK_3);
    }

    public void keyPress4() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_4);
        robot.keyRelease(KeyEvent.VK_4);
    }

    public void keyPress5() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_5);
        robot.keyRelease(KeyEvent.VK_5);
    }

    public void keyPress6() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_6);
        robot.keyRelease(KeyEvent.VK_6);
    }

    public void keyPress7() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_7);
        robot.keyRelease(KeyEvent.VK_7);
    }

    public void keyPress8() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_8);
        robot.keyRelease(KeyEvent.VK_8);
    }

    public void keyPress9() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_9);
        robot.keyRelease(KeyEvent.VK_9);
    }

    // Thao tác nhấn phím ;
    public void keyPressSemicolon() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_SEMICOLON);
        robot.keyRelease(KeyEvent.VK_SEMICOLON);
    }

    // Thao tác nhấn phím =
    public void keyPressEqualsA() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_A);
        robot.keyRelease(KeyEvent.VK_A);
    }

    public void keyPressB() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_B);
        robot.keyRelease(KeyEvent.VK_B);
    }

    public void keyPressC() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_C);
        robot.keyRelease(KeyEvent.VK_C);
    }

    public void keyPressD() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_D);
        robot.keyRelease(KeyEvent.VK_D);
    }

    public void keyPressE() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_E);
        robot.keyRelease(KeyEvent.VK_E);
    }

    public void keyPressF() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_F);
        robot.keyRelease(KeyEvent.VK_F);
    }

    public void keyPressG() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_G);
        robot.keyRelease(KeyEvent.VK_G);
    }

    public void keyPressH() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_H);
        robot.keyRelease(KeyEvent.VK_H);
    }

    public void keyPressI() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_I);
        robot.keyRelease(KeyEvent.VK_I);
    }

    public void keyPressJ() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_J);
        robot.keyRelease(KeyEvent.VK_J);
    }

    public void keyPressK() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_K);
        robot.keyRelease(KeyEvent.VK_K);
    }

    public void keyPressL() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_L);
        robot.keyRelease(KeyEvent.VK_L);
    }

    public void keyPressM() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_M);
        robot.keyRelease(KeyEvent.VK_M);
    }

    public void keyPressN() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_N);
        robot.keyRelease(KeyEvent.VK_N);
    }

    public void keyPressP() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_P);
        robot.keyRelease(KeyEvent.VK_P);
    }

    public void keyPressQ() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_Q);
        robot.keyRelease(KeyEvent.VK_Q);
    }

    public void keyPressR() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_R);
        robot.keyRelease(KeyEvent.VK_R);
    }

    public void keyPressS() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_S);
        robot.keyRelease(KeyEvent.VK_S);
    }

    public void keyPressT() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_T);
        robot.keyRelease(KeyEvent.VK_T);
    }

    public void keyPressU() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_U);
        robot.keyRelease(KeyEvent.VK_U);
    }

    public void keyPressV() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
    }

    public void keyPressW() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_W);
        robot.keyRelease(KeyEvent.VK_W);
    }

    public void keyPressX() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_X);
        robot.keyRelease(KeyEvent.VK_X);
    }

    public void keyPressY() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_Y);
        robot.keyRelease(KeyEvent.VK_Y);
    }

    public void keyPressZ() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_Z);
        robot.keyRelease(KeyEvent.VK_Z);
    }

    // Thao tác nhấn phím [
    public void keyPressOpenBracKet() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_OPEN_BRACKET);
        robot.keyRelease(KeyEvent.VK_OPEN_BRACKET);
    }

    // Thao tác nhấn phím \
    public void keyPressBackSlash() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_BACK_SLASH);
        robot.keyRelease(KeyEvent.VK_BACK_SLASH);
    }

    // Thao tác nhấn phím ]
    public void keyPressCloseBracket() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_CLOSE_BRACKET);
        robot.keyRelease(KeyEvent.VK_CLOSE_BRACKET);
    }

    // Thao tác nhấn phím phụ phím 0
    public void keyPressNumPad0() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_NUMPAD0);
        robot.keyRelease(KeyEvent.VK_NUMPAD0);
    }

    // Thao tác nhấn phím phụ phím 1
    public void keyPressNumPad1() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_NUMPAD1);
        robot.keyRelease(KeyEvent.VK_NUMPAD1);
    }

    // Thao tác nhấn phím phụ phím 2
    public void keyPressNumPad2() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_NUMPAD2);
        robot.keyRelease(KeyEvent.VK_NUMPAD2);
    }

    // Thao tác nhấn phím phụ phím 3
    public void keyPressNumPad3() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_NUMPAD3);
        robot.keyRelease(KeyEvent.VK_NUMPAD3);
    }

    // Thao tác nhấn phím phụ phím 4
    public void keyPressNumPad4() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_NUMPAD4);
        robot.keyRelease(KeyEvent.VK_NUMPAD4);
    }

    // Thao tác nhấn phím phụ phím 5
    public void keyPressNumPad5() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_NUMPAD5);
        robot.keyRelease(KeyEvent.VK_NUMPAD5);
    }

    // Thao tác nhấn phím phụ phím 6
    public void keyPressNumPad6() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_NUMPAD6);
        robot.keyRelease(KeyEvent.VK_NUMPAD6);
    }

    // Thao tác nhấn phím phụ phím 7
    public void keyPressNumPad7() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_NUMPAD7);
        robot.keyRelease(KeyEvent.VK_NUMPAD7);
    }

    // Thao tác nhấn phím phụ phím 8
    public void keyPressNumPad8() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_NUMPAD8);
        robot.keyRelease(KeyEvent.VK_NUMPAD8);
    }

    // Thao tác nhấn phím phụ phím 9
    public void keyPressNumPad9() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_NUMPAD9);
        robot.keyRelease(KeyEvent.VK_NUMPAD9);
    }

    // Thao tác nhấn phím phụ phím Multiply
    public void keyPressMultiply() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_MULTIPLY);
        robot.keyRelease(KeyEvent.VK_MULTIPLY);
    }

    // Thao tác nhấn phím phụ phím Cộng
    public void keyPressAdd() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ADD);
        robot.keyRelease(KeyEvent.VK_ADD);
    }

    // Thao tác nhấn phím Insert
    public void keyPressInsert() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_INSERT);
        robot.keyRelease(KeyEvent.VK_INSERT);
    } // Thao tác nhấn phím phụ phím 0


    // Thao tác nhấn phím ký tự &
    public void keyPressAmpersand() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_AMPERSAND);
        robot.keyRelease(KeyEvent.VK_AMPERSAND);
    }

    // Thao tác nhấn phím ký tự *
    public void keyPressAsterisk() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ASTERISK);
        robot.keyRelease(KeyEvent.VK_ASTERISK);
    }

    // Thao tác nhấn phím ký tự "
    public void keyPressQuotedbl() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_QUOTEDBL);
        robot.keyRelease(KeyEvent.VK_QUOTEDBL);
    }

    // Thao tác nhấn phím ký tự <
    public void keyPressLess() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_LESS);
        robot.keyRelease(KeyEvent.VK_LESS);
    }


    // Thao tác nhấn phím ký tự >
    public void keyPressGreater() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_GREATER);
        robot.keyRelease(KeyEvent.VK_GREATER);
    }


    // Thao tác nhấn phím ký tự {
    public void keyPressBraceleft() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_BRACELEFT);
        robot.keyRelease(KeyEvent.VK_BRACELEFT);
    }


    // Thao tác nhấn phím ký tự }
    public void keyPressBraceright() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_BRACERIGHT);
        robot.keyRelease(KeyEvent.VK_BRACERIGHT);
    }


    // Thao tác nhấn phím ký tự @
    public void keyPressAt() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_AT);
        robot.keyRelease(KeyEvent.VK_AT);
    }

    // Thao tác nhấn phím ký tự :
    public void keyPressColon() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_COLON);
        robot.keyRelease(KeyEvent.VK_COLON);
    }

    // Thao tác nhấn phím ký tự ^
    public void keyPressCircumflex() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_CIRCUMFLEX);
        robot.keyRelease(KeyEvent.VK_CIRCUMFLEX);
    }

    // Thao tác nhấn phím ký tự $
    public void keyPressDollar() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_DOLLAR);
        robot.keyRelease(KeyEvent.VK_DOLLAR);
    }

    // Thao tác nhấn phím ký tự (
    public void keyPressLeftParenthesis() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_LEFT_PARENTHESIS);
        robot.keyRelease(KeyEvent.VK_LEFT_PARENTHESIS);
    }


    // Thao tác nhấn phím ký tự )
    public void keyPressRightParenthesis() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_RIGHT_PARENTHESIS);
        robot.keyRelease(KeyEvent.VK_RIGHT_PARENTHESIS);
    }

    // Thao tác nhấn phím ký tự #
    public void keyPressSign() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_NUMBER_SIGN);
        robot.keyRelease(KeyEvent.VK_NUMBER_SIGN);
    }

    // Thao tác nhấn phím ký tự +
    public void keyPressPlus() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_PLUS);
        robot.keyRelease(KeyEvent.VK_PLUS);
    }

    // Thao tác nhấn phím ký tự _
    public void keyPressUnderScore() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_UNDERSCORE);
        robot.keyRelease(KeyEvent.VK_UNDERSCORE);
    }

    // Thao tác nhấn phím ký tự !
    public void keyPressExclamationMark() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_EXCLAMATION_MARK);
        robot.keyRelease(KeyEvent.VK_EXCLAMATION_MARK);
    }

    // Thao tác nhấn Cut !
    public void keyPressCut() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_CUT);
        robot.keyRelease(KeyEvent.VK_CUT);
    }

    // Thao tác nhấn Copy
    public void keyPressCopy() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_COPY);
        robot.keyRelease(KeyEvent.VK_COPY);
    }

    // Thao tác nhấn Paste
    public void keyPressPaste() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_PASTE);
        robot.keyRelease(KeyEvent.VK_PASTE);
    }


    // Hàm chung Kiểm tra tìm kiếm
//   public void checkSearchResult(String value) {
////Xác định số dòng của table sau khi search
//      List<WebElement> row = driver.findElements(dasboardElementPage.results);
//      int rowTotal = row.size(); //Lấy ra số dòng
//      System.out.println("Số dòng tìm thấy: " + rowTotal);
////Duyệt từng dòng
//      for (int i = 1; i <= rowTotal; i++) {
//         WebElement elementCheck = driver.findElement(By.xpath("//*[@class='text-sm leading-4 font-bold " +
//                 "text-VNDG-mainDblue'][" + i + "]"));
//         JavascriptExecutor js = (JavascriptExecutor) driver;
//         js.executeScript("arguments[0].scrollIntoView(true);", elementCheck);
//         System.out.print(value + " - ");
//         System.out.println(elementCheck.getText());
//         Assert.assertTrue(elementCheck.getText().toUpperCase().contains(value.toUpperCase()), "Dòng số " + i + " không chứa giá trị tìm kiếm.");
//      }
//   }

//    public List<UserAccount> checkInformation() throws InterruptedException {
//        Thread.sleep(3000);
//        String IdUserChrome = driver.getCurrentUrl(); // Get URL hiện tại
//        String[] parts = IdUserChrome.split("/"); // Cắt chuỗi khi xuất hiện kí tự /
//        String part6 = parts[5]; // Get chuỗi là LoanID
//        List<UserAccount> lstUserAccounts = dbDataReader.getUsers(part6);// Gọi thông tin từ DB theo ID user
//        return lstUserAccounts;
//    }
//
//    public List<ImportOrder> getOrderInformation() throws InterruptedException {
//        Thread.sleep(3000);
////        String IdUserChrome = driver.getCurrentUrl(); // Get URL hiện tại
////        String[] parts = IdUserChrome.split("/"); // Cắt chuỗi khi xuất hiện kí tự /
////        String part6 = parts[5]; // Get chuỗi là LoanID
//        List<ImportOrder> lstImportOrder = dbDataReader.orderInformation();// Gọi thông tin từ DB theo ID user
//        return lstImportOrder;
//    }

    // Hàm dùng chung kiểm tra danh sách thông tin


    /**
     * Hàm đợi trang load xong rồi thao tác
     */
    public void waitForPageLoaded() {
        ExpectedCondition<Boolean> expectation = new
                ExpectedCondition<Boolean>() {
                    public Boolean apply(WebDriver driver) {
                        return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");
                    }
                };
        try {
            Thread.sleep(1000);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3000));
            wait.until(expectation);
        } catch (Throwable error) {
            Assert.fail("Timeout waiting for Page Load Request to complete.");
        }
    }

    /**
     * Hàm chụp ảnh màn hình TakesScreenShot của selenium
     * Chỉ có thể chụp toàn bộ giao diện driver
     */
    public void takeSnapShot(String fileWithPath){
        //Chuyển đổi đối tượng trình điều khiển web sang TakeScreenshot
        TakesScreenshot screenshot = ((TakesScreenshot)driver);

        //Gọi phương thức getScreenshotAs để tạo file ảnh
        File SrcFile=screenshot.getScreenshotAs(OutputType.FILE);

        // Di chuyển file ảnh tới đích mới
        File DestFile = new File(fileWithPath);

        // Sao chép tập tin tại đích
        try {
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Hàm chụp ảnh màn hình ScreenShot của Ashot
     * 1. Chụp toàn bộ giao diện driver
     * 2. Chụp từng element cụ thể
     * 3. Chụp hình ảnh đầy đủ của 1 trang web lớn hơn kích thước màn hình driver với class ShootingStrategy
     */
    // Chụp toàn bộ giao diện driver
    public void ashotDriver(String fileWithPath) throws IOException {
        Screenshot screenshot = new AShot().takeScreenshot(driver);

        ImageIO.write(screenshot.getImage(), "png", new File(fileWithPath));
    }

    // Chụp từng element cụ thể
    public void ashotElement(By image_X, String fileWithPath) throws IOException {
        WebElement element = driver.findElement(image_X);
        Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver, element);

        ImageIO.write(screenshot.getImage(), "png", new File(fileWithPath));
    }

    // Chụp hình ảnh đầy đủ của 1 trang web lớn hơn kích thước màn hình driver
    public void ashotShootingStrategy(String fileWithPath) throws IOException {
        Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver);

        ImageIO.write(screenshot.getImage(), "png", new File(fileWithPath));
    }

    // So sánh 2 hình ảnh
    public boolean ashotCompare(By image_A, String file_image_expect) throws IOException {
        // Tìm element A và chụp ảnh màn hình
        WebElement A = driver.findElement(image_A);
        Screenshot scrShotA = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver, A);

        // Đọc hình ảnh để so sánh

        BufferedImage expect_image = ImageIO.read(new File(file_image_expect));
        BufferedImage actual_image = scrShotA.getImage();

        // Tạo đối tượng ImageDiffer và gọi phương thức makeDiff()
        ImageDiffer imageDiffer = new ImageDiffer();
        ImageDiff diff = imageDiffer.makeDiff(actual_image, expect_image);

        return diff.hasDiff();
    }


    /**
     * Áp dụng API để lấy thông tin hoặc thực hiện các request
     */

    /**
     * POST request: Thực hiện gửi request để lấy accessToken
     * @param api
     * @param body
     * @return
     */
    public JsonNode getToken(String api, Object body){
        JsonNode resp = null;
        try {
            resp = Unirest.post(api).header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(body).asJson().getBody();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return resp;
    }

    /**
     * Lấy 1 object của 1 JsonNode (response api dạng JsonNode)
     * @param json
     * @param key
     * @return
     */
    public JSONObject getJsonKey_Obj(JsonNode json, String key){
        JSONObject obj = null;
        try {
            // 1. Tạo ra một JSONParser
            JSONParser jParser = new JSONParser();
            // 2. Parser chuỗi JSON về một JSONObject
            JSONObject bodyObj = (JSONObject) jParser.parse(String.valueOf(json));
            // 3. Lấy các giá trị trong jsonObject thông qua các Key
            // key là kiểu dữ liệu nào thì lấy kiểu dữ liệu đó
            obj = (JSONObject) bodyObj.get(key);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return obj;
    }

    /**
     * Lấy 1 object của 1 JsonObject (response api dạng JsonObject)
     * @param json
     * @param key
     * @return
     */
    public JSONObject getObjectKey_Obj(JSONObject json, String key){
        JSONObject obj = null;
        try {
            // 1. Tạo ra một JSONParser
            JSONParser jParser = new JSONParser();
            // 2. Parser chuỗi JSON về một JSONObject
            JSONObject bodyObj = (JSONObject) jParser.parse(String.valueOf(json));
            // 3. Lấy các giá trị trong jsonObject thông qua các Key
            // key là kiểu dữ liệu nào thì lấy kiểu dữ liệu đó
            obj = (JSONObject) bodyObj.get(key);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return obj;
    }

    /**
     * Lấy 1 String value theo key của 1 Object
     * Dành cho trường hợp key nằm trong 1 object của response api, không thể lấy giá trị trực tiếp từ response
     * @param json
     * @param key
     * @return
     */
    public String getJsonKey_String(JSONObject json, String key){
        String s = null;
        try {
            // 1. Tạo ra một JSONParser
            JSONParser jParser = new JSONParser();
            // 2. Parser chuỗi JSON về một JSONObject
            JSONObject bodyObj = (JSONObject) jParser.parse(String.valueOf(json));
            // 3. Lấy các giá trị trong jsonObject thông qua các Key
            // key là kiểu dữ liệu nào thì lấy kiểu dữ liệu đó
            s = (String) bodyObj.get(key);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return s;
    }

    /**
     * Lấy 1 Integer/Long value theo key của 1 Object
     * Dành cho trường hợp key nằm trong 1 object của response api, không thể lấy giá trị trực tiếp từ response
     * @param json
     * @param key
     * @return
     */
    public long getJsonKey_long(JSONObject json, String key){
        long a = 0;
        try {
            // 1. Tạo ra một JSONParser
            JSONParser jParser = new JSONParser();
            // 2. Parser chuỗi JSON về một JSONObject
            JSONObject bodyObj = (JSONObject) jParser.parse(String.valueOf(json));
            // 3. Lấy các giá trị trong jsonObject thông qua các Key
            // key là kiểu dữ liệu nào thì lấy kiểu dữ liệu đó
            a = (Long) bodyObj.get(key);
        } catch (ParseException e){
            e.printStackTrace();
        }

        return a;
    }

    /**
     * Lấy 1 Boolean value theo key của 1 Object
     * Dành cho trường hợp key nằm trong 1 object của response api, không thể lấy giá trị trực tiếp từ response
     * @param json
     * @param key
     * @return
     */
    public boolean getJsonKey_boolean(JSONObject json, String key){
        boolean b = true;
        try {
            // 1. Tạo ra một JSONParser
            JSONParser jParser = new JSONParser();
            // 2. Parser chuỗi JSON về một JSONObject
            JSONObject bodyObj = (JSONObject) jParser.parse(String.valueOf(json));
            // 3. Lấy các giá trị trong jsonObject thông qua các Key
            // key là kiểu dữ liệu nào thì lấy kiểu dữ liệu đó
            b = Boolean.parseBoolean((String) bodyObj.get(key));
        } catch (ParseException e){
            e.printStackTrace();
        }

        return b;
    }

    /**
     * GET request: Thực hiện gửi request để lấy kết quả trả về là response api
     * @param api
     * @param token
     * @return
     */
    public JsonNode getApi(String api, String token){
        JsonNode resp = null;
        try {
            resp = Unirest.get(api).header("Authorization", "Bearer " + token)
                    .asJson().getBody();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return resp;
    }

    /**
     * File thông tin cố định.
     * Post request: Thực hiện gửi request api theo path file payload .json
     *
     * @param api
     * @param fileName
     */
    public void postApi(String api, String token, String fileName) {
        JtwigTemplate temp = JtwigTemplate.classpathTemplate(fileName);
        try {
            Unirest.post(api).header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .body(temp)
                    .asJson();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * File thông tin cố định.
     * PUT request: Thực hiện gửi request api sửa theo path file payload .json.
     * Thêm thông tin bản ghi cần sửa tại param api, thường sẽ là id bản ghi.
     * @param api
     * @param token
     * @param fileName
     * @param paramName
     * @param paramValue
     */
    public void putApi(String api, String token, String fileName, String paramName, String paramValue){
        JtwigTemplate temp = JtwigTemplate.classpathTemplate(fileName);
        try {
            Unirest.put(api).routeParam(paramName, paramValue)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .body(temp)
                    .asJson();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * File thông tin linh động (đặt biến data).
     * Post request: Thực hiện gửi request api với data payload "model", theo path file payload .json
     * @param api
     * @param token
     * @param fileName
     * @param model
     */
    public void postApiModel(String api, String token, String fileName, JtwigModel model){
        JtwigTemplate temp = JtwigTemplate.classpathTemplate(fileName);
        try {
            Unirest.post(api).header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .body(temp.render(model))
                    .asJson();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * File thông tin linh động (đặt biến data).
     * PUT request: Thực hiện gửi request api sửa với data payload "model", theo path file payload .json.
     * Thêm thông tin bản ghi cần sửa tại param api, thường sẽ là id bản ghi.
     * @param api
     * @param token
     * @param fileName
     * @param paramName
     * @param paramValue
     * @param model
     */
    public void putApiModel(String api, String token, String fileName, String paramName, String paramValue, JtwigModel model){
        JtwigTemplate temp = JtwigTemplate.classpathTemplate(fileName);
        try {
            Unirest.put(api).routeParam(paramName, paramValue)
                    .header("accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .body(temp.render(model))
                    .asJson();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Delete request: Thực hiện xoá data.
     * Thêm thông tin bản ghi cần xoá tại param api, thường sẽ là id bản ghi
     * @param api
     * @param token
     * @param paramName
     * @param paramValue
     */
    public void deleteApi(String api, String token, String paramName, String paramValue){
        try {
            Unirest.delete(api).header("Authorization", "Bearer " + token)
                    .routeParam(paramName, paramValue)
                    .asJson();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}