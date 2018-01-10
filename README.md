## Az automatizált tesztek alapprojektje.

##### IntelliJ használat esetén célszerű az alábbi sablonokat beállítani:
##### Settings(Ctrl + Alt + S) -> Live Templates -> Add(Alt + Insert)

### TestLink kapcsolatot kihasználó sablonok

##### Teszt osztály TestLink kapcsolattal
* Abbrevation: testclasslink  <br/>
Leírás: Váz egy TestNG osztályhoz, amihez tartozik TestLink-en belül Test Plan és Test Build.
* Paraméterek:
    * CLASSNAME: A tesztosztály neve
    * TESTLINK_BUILD: A TestLink Build neve
    * TESTLINK_PLAN: A TestLink Test plan neve, itt a testng.xml-ben a test neve

```java
public class $CLASSNAME$ extends TestBase{
    private static Logger log;

    private static String TESTLINK_PLAN;
    private static String TESTLINK_BUILD = "$TESTLINK_BUILD$";

    @BeforeClass
    public void setUp() throws Exception {

        log = LoggerFactory.getLogger($CLASSNAME$.class);
        initializeDriver();
        //initializeDatabase("probono");
    }

    @AfterClass
    public void tearDownSuite() throws Exception {
        log.info("TEARDOWN - Adatbázis kapcsolat lezárása");
        if (con != null) con.close();

        log.info("TEARDOWN - Böngésző ablak bezárása");
        if (driver != null) driver.quit();
    }

    @AfterMethod
    public void tearDown(ITestResult result, ITestContext context) {
        String testCaseName = context.getCurrentXmlTest().getParameter("testCaseName");
        TESTLINK_PLAN = context.getCurrentXmlTest().getName();

        evaluatingResultsAndUpdateToTestLink(testCaseName, TESTLINK_PLAN, TESTLINK_BUILD, result);
    }

    $END$
}
```

##### Teszt eset TestLink kapcsolattal
* Abbrevation: testmethodlink  <br/>
Leírás: Váz egy TestNG tesztesethez, amihez tartozik TestLink teszteset.
* Paraméterek:
    * TEST_NAME: A teszteset neve
    * TESTLINK_TC_ID: A TestLink teszteset látható id-ja (például "blended-24")

```java
@Test(enabled = true)
public void $TEST_NAME$Test(ITestContext context) {
    context.getCurrentXmlTest().addParameter("testCaseName", "$TESTLINK_TC_ID$");

    $END$
}
```

### TestLink kapcsolat nélküli sablonok
##### Teszt osztály
* Abbrevation: testclass <br/>
Leírás: Váz egy TestNG osztályhoz.
* Paraméterek:
    * CLASSNAME: A tesztosztály neve

```java
public class $CLASSNAME$ extends TestBase{
    private static Logger log;

    @BeforeClass
    public void setUp() throws Exception {

        log = LoggerFactory.getLogger($CLASSNAME$.class);
        initializeDriver();
        //initializeDatabase("probono");
    }

    @AfterClass
    public void tearDownSuite() throws Exception {
        log.info("TEARDOWN - Adatbázis kapcsolat lezárása");
        if (con != null) con.close();

        log.info("TEARDOWN - Böngésző ablak bezárása");
        if (driver != null) driver.quit();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        evaluatingResults(result);
    }

    $END$
}
```

##### Teszt eset
* Abbrevation: testmethodlink <br/>
Leírás: Váz egy TestNG tesztesethez.
* Paraméterek:
    * TEST_NAME: A teszteset neve

```java
@Test(enabled = true)
public void $TEST_NAME$Test() {
    $END$
}
```

### Page Object Model sablonok
##### Page object model osztály
* Abbrevation: pageclass <br/>
Leírás: Váz egy POM osztályhoz.
* Paraméterek:
    * PAGENAME: Az oldal neve
    * URL: Az oldal címe

```java
public class $PAGENAME$ extends AbstractPage{
    
    public static String URL = $URL$;
    
    public static void assertPage(WebDriver driver){
        Assert.assertEquals(driver.getCurrentUrl(), URL);
    }
}
```

##### Selenium POM Webelement
* Abbrevation: webelement <br/>
Leírás: Váz egy Selenium által használt POM webelement-hez.
* Paraméterek:
    * ELEMENT: A webelement neve
    * LOCATOR: By statikus osztály által jelölt azonosító

```java
public static WebElement $ELEMENT$(WebDriver driver){
    By by = $LOCATOR$;
    Assert.assertTrue(isElementPresent(driver, by), "Elem létezésének ellenőrzése.");

    element = driver.findElement(by);
    return element;
}
```   
