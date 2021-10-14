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
        String url =
                String.format(
                        "https://www.linkedin.com/in/%s/",
                        URLEncoder.encode(this.username, StandardCharsets.UTF_8));
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
        try {
            return this.driver
                    .findElement(UserProfileSelectors.info)
                    .getText()
                    .replaceAll(
                            this.driver
                                    .findElement(UserProfileSelectors.infoTextToDelete)
                                    .getText(),
                            "");
        } catch (NoSuchElementException e) {
            return "";
        }
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

    /** Function expands all experiences on the user profile */
    private void expandAll(By selector) {
        try {
            WebElement moreExperiencesButton =
                    this.wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
            moreExperiencesButton.click();
            expandAll(selector);
        } catch (TimeoutException ignored) {
        }
    }

    public String getUserExperiences() {
        this.goProfilePage();
        // Check if more experience button is available
        // If it's available, click on it to show all experiences
        this.expandAll(UserExperienceSelectors.moreExperienceButton);
        // Once we have all experience groups
        List<WebElement> experienceWE =
                this.driver.findElements(UserExperienceSelectors.experienceGroup);
        for (int i = 0; i < experienceWE.size(); i++) {
            String
                    roleContainerPath =
                            String.format(
                                    "%s[%d]//div[@class='pv-entity__role-container']",
                                    UserExperienceSelectors.experienceGroupPath, i + 1),
                    experienceGroupPath =
                            String.format(
                                    "%s[%d]", UserExperienceSelectors.experienceGroupPath, i + 1),
                    experienceGroupLinkPath =
                            String.format(
                                    "%s[%d]//a",
                                    UserExperienceSelectors.experienceGroupPath, i + 1);
            By experienceGroupSelector = By.xpath(experienceGroupLinkPath),
                    roleContainerSelector = By.xpath(roleContainerPath);
            String companyInternalURL =
                    this.driver.findElement(experienceGroupSelector).getAttribute("href");
            logger.info(String.format("Internal company URL %s", companyInternalURL));

            try {
                this.wait.until(ExpectedConditions.presenceOfElementLocated(roleContainerSelector));
                String expandRolesPath =
                        String.format(
                                "%s//div[contains(@class,'pv-entity__paging')]//li-icon[@type='chevron-down-icon']/parent::button",
                                experienceGroupPath);
                expandAll(By.xpath(expandRolesPath));
                List<WebElement> roleElements = this.driver.findElements(roleContainerSelector);
                logger.debug(String.format("Number of roles %d", roleElements.size()));

                logger.debug(String.format("Expand all button path %s", expandRolesPath));
                this.getExperienceWithMultipleRoles(
                        experienceGroupLinkPath, roleElements, roleContainerPath);
            } catch (TimeoutException | NoSuchElementException e) {
                logger.debug("No roles for this experience");
                this.getExperienceWithSingleRole(experienceGroupPath, experienceGroupLinkPath);
            }
            // TODO: Check if we can get attachments if any
        }
        // create Experience item for all experiences
        return null;
    }

    // TODO: Change return type with correct class
    // TODO: Complete JavaDoc comments
    private void getExperienceWithMultipleRoles(
            String experienceGroupPath, List<WebElement> roleElements, String roleContainerPath) {
        String experienceGroupTitleSelector =
                String.format(
                        "%s//div[contains(@class,'pv-entity__company-summary-info')]/h3/span[2]",
                        experienceGroupPath);
        WebElement experienceTitleElement =
                this.driver.findElement(By.xpath(experienceGroupTitleSelector));
        String experienceGroupSubTitleSelector =
                String.format(
                        "%s//div[contains(@class,'pv-entity__company-summary-info')]/h4/span[2]",
                        experienceGroupPath);
        WebElement experienceSubTitleElement =
                this.driver.findElement(By.xpath(experienceGroupSubTitleSelector));
        logger.debug(
                String.format(
                        "Total duration in this company %s", experienceSubTitleElement.getText()));
        logger.info(
                String.format(
                        "Company name with multiple roles %s", experienceTitleElement.getText()));

        for (int j = 0; j < roleElements.size(); j++) {
            String rolePath = String.format("(%s)[%d]", roleContainerPath, j + 1);
            String
                    roleNamePath =
                            String.format(
                                    "%s//div[contains(@class,'pv-entity__summary-info-v2')]//h3//span[2]",
                                    rolePath),
                    roleInfoPath =
                            String.format(
                                    "%s//div[contains(@class,'pv-entity__summary-info-v2')]//h4//span[2]",
                                    rolePath),
                    roleDescriptionPath =
                            String.format(
                                    "%s//div[contains(@class,'pv-entity__extra-details')]/div[contains(@class,'inline-show-more-text')]",
                                    rolePath);
            WebElement roleNameElement = this.driver.findElement(By.xpath(roleNamePath));
            List<WebElement> roleInfoElement = this.driver.findElements(By.xpath(roleInfoPath));
            logger.debug(String.format("Role title %s", roleNameElement.getText()));
            logger.debug(String.format("Role time interval %s", roleInfoElement.get(0).getText()));
            logger.debug(String.format("Role duration %s", roleInfoElement.get(1).getText()));
            if (roleInfoElement.size() > 2)
                logger.debug(String.format("Role location %s", roleInfoElement.get(2).getText()));
            String roleDescription = this.getExperienceDescription(roleDescriptionPath);
            logger.debug(String.format("Role description %s", roleDescription));
        }
    }

    // TODO: Change return type with correct class
    // TODO: Complete JavaDoc comments
    private void getExperienceWithSingleRole(
            String experienceGroupPath, String experienceGroupLinkPath) {
        logger.debug(String.format("Experience group link path %s", experienceGroupLinkPath));
        logger.debug(String.format("Experience group path %s", experienceGroupPath));
        String experienceSummaryPath =
                String.format(
                        "%s//div[contains(@class,'pv-entity__summary-info')]",
                        experienceGroupLinkPath);
        String roleNamePath = String.format("%s//h3", experienceSummaryPath),
                companyNamePath = String.format("%s//p[2]", experienceSummaryPath),
                timeIntervalPath = String.format("%s//h4[1]/span[2]", experienceSummaryPath),
                durationPath = String.format("%s//h4[2]/span[2]", experienceSummaryPath),
                descriptionPath =
                        String.format(
                                "%s//div[contains(@class,'pv-entity__extra-details')]/div[contains(@class,"
                                    + " 'inline-show-more-text')]",
                                experienceGroupPath);
        WebElement roleNameElement = this.driver.findElement(By.xpath(roleNamePath)),
                companyNameElement = this.driver.findElement(By.xpath(companyNamePath)),
                timeIntervalElement = this.driver.findElement(By.xpath(timeIntervalPath)),
                durationElement = this.driver.findElement(By.xpath(durationPath));
        logger.info(String.format("Role name %s", roleNameElement.getText()));
        logger.info(String.format("Company name %s", companyNameElement.getText()));
        logger.info(String.format("Time interval %s", timeIntervalElement.getText()));
        logger.info(String.format("Duration %s", durationElement.getText()));
        String roleDescription = this.getExperienceDescription(descriptionPath);
        logger.debug(String.format("Role description %s", roleDescription));
    }

    /**
     * Function returns the description of the role or experience
     *
     * @param descriptionPath The XPath selector for the description
     * @return Description string
     */
    private String getExperienceDescription(String descriptionPath) {
        try {
            logger.debug(String.format("Role description path %s", descriptionPath));
            WebElement descriptionElement = this.driver.findElement(By.xpath(descriptionPath));
            String roleDescription = descriptionElement.getText();
            try {
                String showMoreButtonPath =
                        String.format(
                                "%s/span[contains(@class,'inline-show-more-text__link-container-collapsed')]",
                                descriptionPath);
                WebElement showMoreButtonText =
                        this.driver.findElement(By.xpath(showMoreButtonPath));
                String textToIgnore = showMoreButtonText.getText();
                roleDescription =
                        roleDescription.replaceFirst(
                                "(?s)" + textToIgnore + "(?!.*?" + textToIgnore + ")", "");
            } catch (TimeoutException | NoSuchElementException ignore) {
            }
            return roleDescription;
        } catch (NoSuchElementException ignore) {
            return "";
        }
    }
    // TODO: Get user interests (pages and groups)
    // TODO: Get user contact details
    // TODO: Get user profile image
    // TODO: Get user profile cover
    // TODO: Get user's common relations
    // TODO: Get user's relations
    // TODO: Get user's skills and recommondations
    // TODO: Get user's formation (education)
    // TODO: Get uers's recommondations
    // TODO: Get user's certifications
    // TODO: Get user's non profit experience
}
