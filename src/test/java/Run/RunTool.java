package Run;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import org.junit.runner.RunWith;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


@RunWith(io.cucumber.junit.Cucumber.class)

@io.cucumber.junit.CucumberOptions(
        features = "src/test/resources/Features/View.feature",
        glue = {"StepDefinitions"},
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber-pretty.html",
                "json:target/cucumber-reports/CucumberTestReport.json",
                "timeline:target/test-output-thread/"
        },
        tags = "@View"
)
//
//public class RunTool {
//}

@Test
public class RunTool extends AbstractTestNGCucumberTests{

        @DataProvider(parallel = false)
        public Object[][] scenario() {
                return super.scenarios();
        }
}
