package pages.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import static org.testng.Assert.assertEquals;

public class GooglePage {

    @FindBy(name = "q")
    public WebElement searchBar;

    @FindBy(name = "btnG")
    public WebElement searchButton;

    private GooglePage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public static GooglePage get(WebDriver driver) {
        String URL = "https://www.google.com";
        driver.get(URL);
        assertPage(driver);

        return new GooglePage(driver);
    }

    private static void assertPage(WebDriver driver) {
        assertEquals(driver.getTitle(), "Google");
    }

    public void searchFor(String searchKey) {

        searchBar.clear();
        searchBar.sendKeys(searchKey);

        searchButton.click();
    }
}