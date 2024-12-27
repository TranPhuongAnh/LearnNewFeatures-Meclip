package Run;

import com.vimalselvam.cucumber.listener.Reporter;
import configProject.FileReaderManager;
import cucumber.api.cli.Main;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import org.junit.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class UseCucumberFromMain {
    public static void main(String[] args) throws Throwable {
        Main.main(new String[]{
                "--glue", "StepDefinitions",
//                "--plugin", "html:target/cucumber-reports/cucumber-pretty.html",
                "--plugin", "json:target/cucumber-reports/CucumberTestReport.json",
//                "--plugin", "junit:target/cucumber-reports/cucumber-report.xml",
                "classpath:Features/View.feature"
        });

    }

}