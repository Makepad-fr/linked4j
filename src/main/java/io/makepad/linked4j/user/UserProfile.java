/* (C)2021 */
package io.makepad.linked4j.user;

import io.makepad.linked4j.models.Company;
import io.makepad.linked4j.models.Education;
import io.makepad.linked4j.models.Role;
import io.makepad.linked4j.models.School;
import io.makepad.linked4j.models.WorkExperience;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public String getShortDescription() {
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

    /**
     * Function returns the work experiences of the user profile
     *
     * @return List of work experiences on the user profile
     */
    public List<WorkExperience> getUserExperiences() {
        List<WorkExperience> experiences = new ArrayList<WorkExperience>();
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

            try {
                this.wait.until(ExpectedConditions.presenceOfElementLocated(roleContainerSelector));
                String expandRolesPath =
                        String.format(
                                "%s//div[contains(@class,'pv-entity__paging')]//li-icon[@type='chevron-down-icon']/parent::button",
                                experienceGroupPath);
                expandAll(By.xpath(expandRolesPath));
                List<WebElement> roleElements = this.driver.findElements(roleContainerSelector);
                experiences.add(
                        this.getExperienceWithMultipleRoles(
                                experienceGroupLinkPath,
                                roleElements,
                                roleContainerPath,
                                companyInternalURL));

            } catch (TimeoutException | NoSuchElementException e) {
                experiences.add(
                        this.getExperienceWithSingleRole(
                                experienceGroupPath, experienceGroupLinkPath, companyInternalURL));
            }
        }
        return experiences;
    }

    /**
     * Function return a WorkExperience with multiple position
     *
     * @param experienceGroupPath The XPath selector string for the experience group
     * @param roleElements List of WebElements for each role
     * @param roleContainerPath XPath selector for role container
     * @param companyURL Company's LinkedIn url related to this experience
     * @return The WorkExperience object with multiple roles
     */
    private WorkExperience getExperienceWithMultipleRoles(
            String experienceGroupPath,
            List<WebElement> roleElements,
            String roleContainerPath,
            String companyURL) {
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
        WorkExperience experience =
                new WorkExperience(
                        new Company(experienceTitleElement.getText(), companyURL),
                        experienceSubTitleElement.getText());
        for (int j = 0; j < roleElements.size(); j++) {
            Role r;
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
            String roleLocation = "";
            if (roleInfoElement.size() > 2) roleLocation = roleInfoElement.get(2).getText();
            r =
                    new Role(
                            roleNameElement.getText(),
                            roleInfoElement.get(1).getText(),
                            roleInfoElement.get(0).getText(),
                            roleLocation,
                            this.getExperienceDescription(roleDescriptionPath));
            experience.roles.add(r);
        }
        return experience;
    }

    /**
     * Function return work experience with single role
     *
     * @param experienceGroupPath XPath selector for experience group
     * @param experienceGroupLinkPath XPath selector for experience group link
     * @param companyURL The LinkedIn URL of the company page
     * @return WorkExperience with single role
     */
    private WorkExperience getExperienceWithSingleRole(
            String experienceGroupPath, String experienceGroupLinkPath, String companyURL) {
        String experienceSummaryPath =
                String.format(
                        "%s//div[contains(@class,'pv-entity__summary-info')]",
                        experienceGroupLinkPath);
        String roleNamePath = String.format("%s//h3", experienceSummaryPath),
                companyNamePath = String.format("%s//p[2]", experienceSummaryPath),
                timeIntervalPath =
                        String.format(
                                "%s//h4[contains(@class,'pv-entity__date-range')]/span[2]",
                                experienceSummaryPath),
                locationPath =
                        String.format(
                                "%s//h4[contains(@class,'pv-entity__location')]/span[2]",
                                experienceSummaryPath),
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
        String roleDescription = this.getExperienceDescription(descriptionPath);
        String location = "";
        location = getTextOfWebElementIfExists(locationPath);

        WorkExperience exp =
                new WorkExperience(
                        new Company(companyNameElement.getText(), companyURL),
                        durationElement.getText());
        exp.roles.add(
                new Role(
                        roleNameElement.getText(),
                        durationElement.getText(),
                        timeIntervalElement.getText(),
                        location,
                        roleDescription));
        return exp;
    }

    /**
     * Function returns the description of the role or experience
     *
     * @param descriptionPath The XPath selector for the description
     * @return Description string
     */
    private String getExperienceDescription(String descriptionPath) {
        try {
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

    /**
     * Function returns the list of educations items on the user profile
     *
     * @return The list of education of the user.
     */
    public List<Education> getEducation() {
        this.goProfilePage();
        List<Education> educations = new ArrayList<>();
        try {
            this.wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath(UserEducationSelectors.educationSectionPath)));
            this.expandAll(By.xpath(UserEducationSelectors.seeMoreButtonPath));
            List<WebElement> educationListItems =
                    this.driver.findElements(
                            By.xpath(UserEducationSelectors.educationListItemPath));
            for (int i = 0; i < educationListItems.size(); i++) {
                String
                        educationListItemPath =
                                String.format(
                                        "(%s)[%d]",
                                        UserEducationSelectors.educationListItemPath, i + 1),
                        schoolURLPath = String.format("%s//a", educationListItemPath),
                        schoolNamePath =
                                String.format(
                                        "%s%s",
                                        educationListItemPath,
                                        UserEducationSelectors.educationSchoolNamePath),
                        degreeNamePath =
                                String.format(
                                        "%s%s",
                                        educationListItemPath,
                                        UserEducationSelectors.degreeNamePath),
                        fieldNamePath =
                                String.format(
                                        "%s%s",
                                        educationListItemPath,
                                        UserEducationSelectors.educationFieldNamePath),
                        datePath =
                                String.format(
                                        "%s%s",
                                        educationListItemPath,
                                        UserEducationSelectors.educationDatePath),
                        descriptionPath =
                                String.format(
                                        "%s%s",
                                        educationListItemPath,
                                        UserEducationSelectors.educationDescriptionPath),
                        activitiesAndSocietiesPath =
                                String.format(
                                        "%s%s",
                                        educationListItemPath,
                                        UserEducationSelectors.educationActivitiesAndSocieties);

                WebElement schoolURLElement = this.driver.findElement(By.xpath(schoolURLPath)),
                        schoolNameElement = this.driver.findElement(By.xpath(schoolNamePath)),
                        dateElement = this.driver.findElement(By.xpath(datePath));
                School school =
                        new School(
                                schoolNameElement.getText(), schoolURLElement.getAttribute("href"));
                // logger.debug(String.format("Field name %s", fieldNameElement.getText()));
                String degree = getTextOfWebElementIfExists(degreeNamePath),
                        field = getTextOfWebElementIfExists(fieldNamePath),
                        description = getTextOfWebElementIfExists(descriptionPath),
                        activitiesAndSocieties =
                                getTextOfWebElementIfExists(activitiesAndSocietiesPath);

                try {
                    String dateText = dateElement.getText();
                    String[] parsedDate = dateText.split("[-–]");
                    SimpleDateFormat df = new SimpleDateFormat("yyyy");
                    educations.add(
                            new Education(
                                    school,
                                    field,
                                    degree,
                                    description,
                                    activitiesAndSocieties,
                                    df.parse(parsedDate[0]),
                                    df.parse(parsedDate[1])));
                } catch (IndexOutOfBoundsException | ParseException e) {
                    System.err.printf("Can not parse date %s", dateElement.getText());
                    // TODO: Do something when we cannot parse the date element
                }
            }
        } catch (TimeoutException | NoSuchElementException e) {
            logger.warn("This user has no education on the profile");
        }
        return educations;
    }

    /**
     * Function returns the text content of the web element
     *
     * @param xpathSelector The XPath selector of the web element
     * @return The text of the web element
     */
    private String getTextOfWebElementIfExists(String xpathSelector) {
        try {
            return this.driver.findElement(By.xpath(xpathSelector)).getText();
        } catch (TimeoutException | NoSuchElementException e) {
            return "";
        }
    }
    // TODO(#23): Get usser current company (right side of the profile)
    // TODO(#24): Get user latest formation (right side of the profile)
    // TODO(#22): Get user interests (pages and groups)
    // TODO(#21): Get user contact details
    // TODO(#20): Get user profile image
    // TODO(#19): Get user profile cover
    // TODO(#18): Get user's common relations
    // TODO(#11): Get user's relations
    // TODO(#25): Get user's skills and recommondations
    // TODO(#26): Get user's formation (education)
    // TODO(#27): Get uers's recommondations
    // TODO(#28): Get user's certifications
    // TODO(#29): Get user's non profit experience
}
