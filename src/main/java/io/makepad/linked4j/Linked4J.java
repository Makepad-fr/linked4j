/* (C)2021 */
package io.makepad.linked4j;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Browser.NewContextOptions;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.makepad.linked4j.user.UserProfile;
import io.makepad.linked4j.utils.PageHelpers;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Linked4J {

    public UserProfile userProfile;
    private static final Logger logger = LogManager.getLogger(Linked4J.class);
    private final Browser browser;
    private final BrowserContext context;
    private final Page page;
    private final String authenticatedContextPath;
    private final PageHelpers helpers;
    /**
     * Creates a new instance of Linked4J object
     *
     * @param browserType The type of the browser to use
     * @param contextOptions Context options to pass to Playwright
     * @param launchOptions Launch options to pass to Playwright
     * @param authenticatedContextPath If given, it'll save and load the authenticated context
     */
    public Linked4J(
            BrowserType browserType,
            NewContextOptions contextOptions,
            LaunchOptions launchOptions,
            String authenticatedContextPath)
            throws IllegalArgumentException {
        this.authenticatedContextPath = authenticatedContextPath;
        Playwright playwright = Playwright.create();
        com.microsoft.playwright.BrowserType bt;
        switch (browserType) {
            case chrome -> bt = playwright.chromium();
            case firefox -> bt = playwright.firefox();
            case webkit -> bt = playwright.webkit();
            default -> throw new IllegalArgumentException();
        }
        logger.debug("Creating browser");
        this.browser = launchOptions == null ? bt.launch() : bt.launch(launchOptions);
        logger.info("Browser created");
        if (authenticatedContextPath != null
                && Files.exists(Paths.get(this.authenticatedContextPath))) {
            logger.debug("Loading authenticated context");
            this.context = this.loadAuthenticatedContext();
            logger.info("Authenticated context loaded");
        } else {
            logger.debug("Creating new context");
            this.context =
                    (contextOptions == null
                            ? this.browser.newContext()
                            : this.browser.newContext(contextOptions));
            logger.info("New context created");
        }
        logger.debug("Creating new page");
        this.page = this.context.newPage();
        logger.info("New page created");
        logger.debug("Navigating to the LinkedIn url");
        this.page.navigate("https://linkedin.com");
        logger.info("Navigated to the LinkedIn");
        this.helpers = new PageHelpers(this.page);
    }

    /**
     * Creates a new instance of Linked4J object
     *
     * @param browserType Type type of the browser to run
     * @param authenticatedContextPath if given, it'll save and load the authenticated context
     */
    public Linked4J(BrowserType browserType, String authenticatedContextPath) {
        this(browserType, null, null, authenticatedContextPath);
    }

    private void saveAuthenticatedContext() {
        context.storageState(
                new BrowserContext.StorageStateOptions()
                        .setPath(Paths.get(this.authenticatedContextPath)));
    }

    private BrowserContext loadAuthenticatedContext() {
        return browser.newContext(
                new Browser.NewContextOptions()
                        .setStorageStatePath(Paths.get(this.authenticatedContextPath)));
    }

    /**
     * Function handles the login action with username and password
     *
     * @param username The username of the user to log in
     * @param password The password of the user to log in
     */
    public void login(String username, String password) {
        if (this.authenticatedContextPath != null
                && Files.exists(Paths.get(this.authenticatedContextPath))) {
            return;
        }
        this.page.fill(LoginSelectors.username, username);
        this.page.fill(LoginSelectors.password, password);
        this.page.click(LoginSelectors.loginButton);
        if (this.authenticatedContextPath != null) {
            this.saveAuthenticatedContext();
        }
    }

    /**
     * Function creates initialise the userProfile attribute
     *
     * @param username The username of the user
     */
    public void setUserProfile(String username) {
        this.userProfile = new UserProfile(this.page, username, this.helpers);
    }

    public void close() {
        this.page.close();
        this.context.close();
        this.browser.close();
    }
}
