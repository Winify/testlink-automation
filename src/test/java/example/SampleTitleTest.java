package example;

import utils.TestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.example.GooglePage;

public class SampleTitleTest extends TestBase {

    private static Logger log;

    private static String TESTLINK_PLAN = "testPlan";
    private static String TESTLINK_BUILD = "buildName";

    @BeforeClass
    public void setUp() throws Exception {
        log = LoggerFactory.getLogger(String.valueOf(this).getClass());
        PROPERTIES = loadProperties();

        log.info("SETUP - Open Browser");
        initializeDriver();
    }

    @AfterClass
    public void tearDownSuite() {
        driver.quit();
    }

    @AfterMethod
    public void tearDown(ITestResult result, ITestContext context) {
        String testCaseName = context.getCurrentXmlTest().getParameter("testCaseName");
        evaluatingResultsAndUpdateToTestLink(testCaseName, TESTLINK_PLAN, TESTLINK_BUILD, result);
    }

    @Test
    public void testGoogleTitle(ITestContext context) {
        context.getCurrentXmlTest().addParameter("testCaseName", "Google Title");

        log.info("TEST - Navigate to Google");
        GooglePage googlePage = GooglePage.get(driver);

        log.info("TEST - Assert elements");
        Assert.assertTrue(googlePage.searchBar.isDisplayed());
        Assert.assertTrue(googlePage.searchButton.isDisplayed());
    }

    @Test
    public void testGoogleTitleWrong(ITestContext context) {
        context.getCurrentXmlTest().addParameter("testCaseName", "Google Title Wrong");

        log.info("TEST - Navigate to Google");
        GooglePage googlePage = GooglePage.get(driver);

        log.info("TEST - Assert not equals Title");
        Assert.assertNotEquals(driver.getTitle(), "Not Google", "Assert title");
        Assert.assertNotEquals(googlePage.searchBar.isDisplayed(), true);
        Assert.assertNotEquals(googlePage.searchButton.isDisplayed(), true);
    }

    @Test
    public void testGoogleSearch(ITestContext context) {
        context.getCurrentXmlTest().addParameter("testCaseName", "Google Search");

        log.info("TEST - Navigate to Google");
        GooglePage googlePage = GooglePage.get(driver);

        log.info("TEST - Search");
        googlePage.searchFor("Selenium");

        log.info("TEST - Assert search");
        Assert.assertTrue(driver.getTitle().contains("Selenium"));
    }
}
