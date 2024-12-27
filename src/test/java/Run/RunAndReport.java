package Run;

import com.vimalselvam.cucumber.listener.Reporter;
import configProject.FileReaderManager;
import cucumber.api.CucumberOptions;
import io.cucumber.junit.Cucumber;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.AfterClass;

import java.io.File;

@RunWith(Cucumber.class)

@CucumberOptions(
        features = "src/test/resources/Features/View.feature",
        glue = {"StepDefinitions"},
        monochrome = true,
        plugin = {"com.cucumber.listener.ExtentCucumberFormatter:target/cucumber-reports/report.html"},
        tags = "@View "
)
public class RunAndReport {
//        @BeforeClass
//        public static void beforExtentReport() {
//                ExtentReports extentReports = new ExtentReports();
//                extentReports.setGherkinDialect("pt");
//        }

        @AfterClass
        public static void writeExtentReport() {
//                File root = new File(System.getProperty("user.dir"));
                String file_config = FileReaderManager.getInstance().getConfigReader().getReportConfigPath();
                Reporter.loadXMLConfig(file_config);
                Reporter.setSystemInfo("User Name", System.getProperty("user.name"));
                Reporter.setSystemInfo("Time Zone", System.getProperty("user.timezone"));
                Reporter.setSystemInfo("Machine", 	"Windows 10" + "64 Bit");
                Reporter.setSystemInfo("Selenium", "4.24.0");
                Reporter.setSystemInfo("Maven", "4.2.2");
                Reporter.setSystemInfo("Java Version", "JDK-22");
                Reporter.setTestRunnerOutput("Sample test runner output message");
        }
}

