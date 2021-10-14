/* (C)2021 */
package io.makepad.linked4j.user;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class UserProfile {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String username;
    private static final Logger logger = LogManager.getLogger(UserProfile.class);

    public UserProfile(WebDriver driver, String username, WebDriverWait wait) {
        this.driver = driver;
        this.username = username;
        this.wait = wait;
    }

    /** Function navigates to the profile page of the user */
    private void goProfilePage() {
        String url = String.format("https://www.linkedin.com/in/%s/", this.username);
        logger.info(String.format("Current URL %s\n", this.driver.getCurrentUrl()));
        if (this.driver.getCurrentUrl().equals(url)) {
            return;
        }
        this.driver.get(url);
    }

    /**
     * Function returns the full name of the user
     *
     * @return The full name of the user
     */
    public String getFullName() {
        this.goProfilePage();
        return this.driver.findElement(UserProfileSelectors.fullName).getText();
    }

    /**
     * Function return the user info for the current user profile
     *
     * @return The info of the current user
     */
    public String getInfo() {
        this.goProfilePage();
        return this.driver
                .findElement(UserProfileSelectors.info)
                .getText()
                .replaceAll(
                        this.driver.findElement(UserProfileSelectors.infoTextToDelete).getText(),
                        "");
    }

    /**
     * Function returns the current location of the current user
     *
     * @return the location of the current user
     */
    public String getCurrentLocation() {
        this.goProfilePage();
        return this.driver.findElement(UserProfileSelectors.location).getText();
    }

    /**
     * Function returns the current job of the current user
     *
     * @return The current job of the current user
     */
    public String getCurrentJob() {
        this.goProfilePage();
        return this.driver.findElement(UserProfileSelectors.currentJob).getText();
    }
}
