/* (C)2021 */
package io.makepad.linked4j;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import io.makepad.linked4j.user.UserProfile;
import io.makepad.socialwalker.commons.models.AbstractBot;
import io.makepad.socialwalker.commons.models.Configuration;
import io.makepad.socialwalker.commons.models.exceptions.CookieFileNotFoundException;
import java.net.MalformedURLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Linked4J extends AbstractBot {

    public UserProfile userProfile;
    private static final Logger logger = LogManager.getLogger(Linked4J.class);

    public Linked4J(Configuration config) throws MalformedURLException {
        super(config, "https://linkedin.com");
    }

    /**
     * Function handles the login action with username and password
     *
     * @param username The username of the user to log in
     * @param password The password of the user to log in
     */
    @Override
    public void login(String username, String password) {
        if (super.config.useCookies && super.config.isCookiesExists()) {
            try {
                this.login();
                return;
            } catch (CookieFileNotFoundException e) {
                logger.error("This should never happen");
                e.printStackTrace();
            }
        }
        this.wait.until(presenceOfElementLocated(LoginSelectors.username));
        this.driver.findElement(LoginSelectors.username).sendKeys(username);

        this.wait.until(presenceOfElementLocated(LoginSelectors.password));
        this.driver.findElement(LoginSelectors.password).sendKeys(password);

        this.wait.until(presenceOfElementLocated(LoginSelectors.loginButton));
        this.driver.findElement(LoginSelectors.loginButton).click();

        if (super.config.useCookies) {
            super.saveCookies();
        }
    }

    /**
     * Function handles login action with cookies
     *
     * @throws CookieFileNotFoundException When cookie file does not exists
     */
    @Override
    public void login() throws CookieFileNotFoundException {
        if (config.useCookies && config.isCookiesExists()) {
            super.loadCookies();
            return;
        }
        throw new CookieFileNotFoundException(
                String.format("cookie file %s not found", super.config.cookiesPath));
    }

    /**
     * Function creates initialise the userProfile attribute
     *
     * @param username The username of the user
     */
    public void setUserProfile(String username) {
        this.userProfile = new UserProfile(super.driver, username, super.wait);
    }
}
