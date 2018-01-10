package utils;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import testlink.api.java.client.TestLinkAPIClient;
import testlink.api.java.client.TestLinkAPIException;
import testlink.api.java.client.TestLinkAPIResults;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class TestBase {

    protected static Properties PROPERTIES;
    private static final long TIMEOUT_SECONDS = 10;

    protected WebDriver driver;
    private Logger log = LoggerFactory.getLogger(TestBase.class);
    private int lastExecutionID;

    protected void initializeDriver() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    protected void evaluatingResultsAndUpdateToTestLink(String testCaseName, String testPlan, String testBuild, ITestResult result) {
        log.info("TEARDOWN - Evaluating results.");
        try {
            if (result.getStatus() == ITestResult.FAILURE) {

                String errorMessage = getErrorMessageFromThrowable(result.getThrowable());
                String defectedMethodName = result.getTestClass().getName() + "_" + result.getMethod().getMethodName();

                log.error("ERROR - While running:" + defectedMethodName);
                log.error("ERROR - Reason: \n" + errorMessage);

                log.error("ERROR - Taking snapshot.");
                takeSnapshot(driver, defectedMethodName + "_error");

                log.error("ERROR - Uploading results.");
                updateTestLinkResult(testCaseName, testPlan, testBuild,
                        "ERROR \n" + errorMessage,
                        TestLinkAPIResults.TEST_FAILED);

                log.error("ERROR - Uploading snapshot.");
                uploadAttachmentToFailedExecution(testCaseName,
                        "ERROR - Reason: \n" + errorMessage,
                        defectedMethodName);

            } else {
                log.info("SUCCESS - " + result.getTestClass().getName() + " - " + result.getMethod().getMethodName());
                log.info("INFO - Uploading results.");
                updateTestLinkResult(testCaseName, testPlan, testBuild, "SUCCESS - " + result.getMethod().getMethodName(), TestLinkAPIResults.TEST_PASSED);
            }

        } catch (Exception e) {
            log.error("ERROR - Error while running AfterMethod", e);
        }
    }

    protected Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        String propFileName = "test.properties";

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

        if (inputStream != null) {
            properties.load(inputStream);
        } else {
            throw new FileNotFoundException(propFileName + " Not Found!");
        }

        return properties;
    }

    private void takeSnapshot(WebDriver driver, String pictureName) {
        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(srcFile, new File("log/" + pictureName + ".png"), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTestLinkResult(String testCaseName, String testPlanName, String testBuildName, String message, String testResult) throws TestLinkAPIException {
        TestLinkAPIClient testLinkAPIClient = new TestLinkAPIClient(PROPERTIES.getProperty("developerKey"),
                PROPERTIES.getProperty("serverURL"));

        testLinkAPIClient.reportTestCaseResult(PROPERTIES.getProperty("testProjectName"), testPlanName,
                testCaseName, testBuildName, message, testResult);

        lastExecutionID = Integer.parseInt((String) testLinkAPIClient.getLastExecutionResult(PROPERTIES.getProperty("testProjectName"), testPlanName, testCaseName).getData(0).get("id"));
    }

    private void uploadAttachmentToFailedExecution(String testCaseName, String message, String fileName) throws IOException {
        TestLinkAPI testLinkAPI = new TestLinkAPI(new URL(PROPERTIES.getProperty("serverURL")), PROPERTIES.getProperty("developerKey"));

        String pictureName = fileName + "_error.png";
        File attachmentFile = new File("log/" + pictureName);

        byte[] byteArray = FileUtils.readFileToByteArray(attachmentFile);
        String fileContent = new String(Base64.encodeBase64(byteArray));

        testLinkAPI.uploadExecutionAttachment(
                lastExecutionID,
                testCaseName,
                message,
                pictureName,
                "image/png",
                fileContent);
    }

    private String getErrorMessageFromThrowable(Throwable throwable) {
        String[] splittedMessage = throwable.toString().split("\n");
        String errorMessage;

        if (splittedMessage.length > 1) {
            errorMessage = splittedMessage[0] + "\n" + splittedMessage[1];
        } else {
            errorMessage = Arrays.toString(splittedMessage);
        }
        return errorMessage;
    }
}
