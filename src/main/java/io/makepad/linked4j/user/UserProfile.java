/* (C)2021 */
package io.makepad.linked4j.user;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import io.makepad.linked4j.models.Company;
import io.makepad.linked4j.models.Education;
import io.makepad.linked4j.models.Role;
import io.makepad.linked4j.models.School;
import io.makepad.linked4j.models.WorkExperience;
import io.makepad.linked4j.utils.PageHelpers;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserProfile {
    private final Page page;
    private final String username;
    private static final Logger logger = LogManager.getLogger(UserProfile.class);
    private final PageHelpers helpers;

    public UserProfile(Page page, String username, PageHelpers helpers) {
        this.page = page;
        this.username = username;
        this.helpers = helpers;
    }

    /** Function navigates to the profile page of the user */
    private void goProfilePage() {
        String url =
                String.format(
                        "https://www.linkedin.com/in/%s/",
                        URLEncoder.encode(this.username, StandardCharsets.UTF_8));
        logger.info(String.format("Current URL %s\n", this.page.url()));
        if (this.page.url().equals(url)) {
            return;
        }
        this.page.navigate(url);
    }

    /**
     * Function returns the full name of the user
     *
     * @return The full name of the user
     */
    public String getFullName() {
        this.goProfilePage();
        return this.page.textContent(UserProfileSelectors.fullName);
    }

    /**
     * Function return the user info for the current user profile
     *
     * @return The info of the current user
     */
    public String getInfo() {
        this.goProfilePage();
        try {
            return this.page
                    .textContent(UserProfileSelectors.info)
                    .replaceAll(this.page.textContent(UserProfileSelectors.infoTextToDelete), "");
        } catch (TimeoutError e) {
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
        return this.page.textContent(UserProfileSelectors.location);
    }

    /**
     * Function returns the current job of the current user
     *
     * @return The current job of the current user
     */
    public String getShortDescription() {
        this.goProfilePage();
        return this.page.textContent(UserProfileSelectors.currentJob);
    }

    /** Function expands all experiences on the user profile */
    private void expandAll(String selector) {
        try {
            this.page.click(selector);
            expandAll(selector);
        } catch (TimeoutError ignored) {
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
        this.helpers.scrollUntilElementAppears(UserExperienceSelectors.moreExperienceButton);
        // Check if more experience button is available
        // If it's available, click on it to show all experiences
        this.expandAll(UserExperienceSelectors.moreExperienceButton);
        // Once we have all experience groups
        List<ElementHandle> experienceWE =
                this.page.querySelectorAll(UserExperienceSelectors.experienceGroupPath);
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
            String companyInternalURL = this.page.getAttribute(experienceGroupPath, "href");
            try {
                String expandRolesPath =
                        String.format(
                                "%s//div[contains(@class,'pv-entity__paging')]//li-icon[@type='chevron-down-icon']/parent::button",
                                experienceGroupPath);
                expandAll(expandRolesPath);
                List<ElementHandle> roleElements = this.page.querySelectorAll(roleContainerPath);
                experiences.add(
                        this.getExperienceWithMultipleRoles(
                                experienceGroupLinkPath,
                                roleElements,
                                roleContainerPath,
                                companyInternalURL));

            } catch (TimeoutError e) {
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
            List<ElementHandle> roleElements,
            String roleContainerPath,
            String companyURL) {
        String experienceGroupTitleSelector =
                String.format(
                        "%s//div[contains(@class,'pv-entity__company-summary-info')]/h3/span[2]",
                        experienceGroupPath);
        String experienceGroupSubTitleSelector =
                String.format(
                        "%s//div[contains(@class,'pv-entity__company-summary-info')]/h4/span[2]",
                        experienceGroupPath);

        WorkExperience experience =
                new WorkExperience(
                        new Company(
                                this.page.textContent(experienceGroupTitleSelector), companyURL),
                        this.page.textContent(experienceGroupSubTitleSelector));
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
            ElementHandle roleNameElement = this.page.querySelector(roleNamePath);
            List<ElementHandle> roleInfoElement = this.page.querySelectorAll(roleInfoPath);
            String roleLocation = "";
            if (roleInfoElement.size() > 2) roleLocation = roleInfoElement.get(2).textContent();
            r =
                    new Role(
                            roleNameElement.textContent(),
                            roleInfoElement.get(1).textContent(),
                            roleInfoElement.get(0).textContent(),
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
        ElementHandle roleNameElement = this.page.querySelector(roleNamePath),
                companyNameElement = this.page.querySelector(companyNamePath),
                timeIntervalElement = this.page.querySelector(timeIntervalPath),
                durationElement = this.page.querySelector(durationPath);
        String roleDescription = this.getExperienceDescription(descriptionPath);
        String location = this.helpers.textContent(locationPath);
        WorkExperience exp =
                new WorkExperience(
                        new Company(companyNameElement.textContent(), companyURL),
                        durationElement.textContent());
        exp.roles.add(
                new Role(
                        roleNameElement.textContent(),
                        durationElement.textContent(),
                        timeIntervalElement.textContent(),
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
            String roleDescription = this.page.textContent(descriptionPath);
            try {
                String showMoreButtonPath =
                        String.format(
                                "%s/span[contains(@class,'inline-show-more-text__link-container-collapsed')]",
                                descriptionPath);
                String textToIgnore = this.page.textContent(showMoreButtonPath);
                roleDescription =
                        roleDescription.replaceFirst(
                                "(?s)" + textToIgnore + "(?!.*?" + textToIgnore + ")", "");
            } catch (TimeoutError ignore) {
            }
            return roleDescription;
        } catch (TimeoutError ignore) {
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
            logger.debug("Scrolling the page until the educations container appears");
            this.helpers.scrollUntilElementAppears(UserEducationSelectors.educationSectionPath);
            logger.info("Education section appeared");
            logger.debug("Will expand all education stuff");
            this.expandAll(UserEducationSelectors.seeMoreButtonPath);
            logger.info("All education elements are expanded");
            logger.debug(
                    String.format(
                            "Education list item selector %s",
                            UserEducationSelectors.educationListItemPath));
            List<ElementHandle> educationListItems =
                    this.page.querySelectorAll(UserEducationSelectors.educationListItemPath);
            logger.debug(String.format("Number of education items %d", educationListItems.size()));

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

                ElementHandle schoolURLElement = this.page.querySelector(schoolURLPath),
                        schoolNameElement = this.page.querySelector(schoolNamePath),
                        dateElement = this.page.querySelector(datePath);
                School school =
                        new School(
                                schoolNameElement.textContent(),
                                schoolURLElement.getAttribute("href"));
                // logger.debug(String.format("Field name %s", fieldNameElement.getText()));
                String degree = this.helpers.textContent(degreeNamePath),
                        field = this.helpers.textContent(fieldNamePath),
                        description = this.helpers.textContent(descriptionPath),
                        activitiesAndSocieties =
                                this.helpers.textContent(activitiesAndSocietiesPath);

                try {
                    String dateText = dateElement.textContent();
                    logger.debug(String.format("Date text %s", dateText));
                    String[] parsedDate = dateText.split("[\\-–]");
                    logger.debug(
                            String.format("Parsed date contains %d elements", parsedDate.length));
                    logger.debug(
                            String.format(
                                    "Parsed date elements %s, %s",
                                    parsedDate[0].trim(), parsedDate[1].trim()));
                    SimpleDateFormat df = new SimpleDateFormat("yyyy");
                    educations.add(
                            new Education(
                                    school,
                                    field,
                                    degree,
                                    description,
                                    activitiesAndSocieties,
                                    df.parse(parsedDate[0].trim()),
                                    df.parse(parsedDate[1].trim())));
                } catch (IndexOutOfBoundsException | ParseException e) {
                    logger.error("Something went wrong while parsing date");
                    logger.error(e.getMessage());
                    // TODO: Do something when we cannot parse the date element
                }
            }
        } catch (TimeoutError e) {
            logger.warn(e.getMessage());
            logger.warn("This user has no education on the profile");
        }
        return educations;
    }

    /**
     * Function checks if the user has premium badge or not
     *
     * @return True if user has premium badge, false if not
     */
    public boolean hasPremium() {
        this.goProfilePage();
        try {
            this.page.waitForSelector(UserProfileSelectors.premiumBadge);
            return true;
        } catch (TimeoutError e) {
            return false;
        }
    }

    /**
     * Function checks if the user has the influencer badge
     *
     * @return True if the user has influencer badge or not
     */
    public boolean isInfluencer() {
        this.goProfilePage();
        try {
            this.page.waitForSelector(UserProfileSelectors.influencerBadge);
            return true;
        } catch (TimeoutError e) {
            return false;
        }
    }

    // TODO(#28): Get user's certifications
    // TODO(#29): Get user's non profit experience
    // TODO(#22): Get user interests (pages and groups)
    // TODO(#21): Get user contact details

    // TODO(#20): Get user profile image
    // TODO(#19): Get user profile cover
    // TODO(#18): Get user's common relations
    // TODO(#11): Get user's relations
    // TODO(#25): Get user's skills and recommondations
    // TODO(#27): Get uers's recommondations

    // TODO(#23): Get user current company (right side of the profile)
    // TODO(#24): Get user latest formation (right side of the profile)
}
