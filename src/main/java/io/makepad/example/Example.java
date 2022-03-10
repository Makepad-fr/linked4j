/* (C)2021 */
package io.makepad.example;

import com.microsoft.playwright.BrowserType.LaunchOptions;
import io.makepad.linked4j.BrowserType;
import io.makepad.linked4j.Linked4J;
import io.makepad.linked4j.models.Education;
import java.util.List;

public class Example {
    public static void main(String[] args) {
        Linked4J l =
                new Linked4J(
                        BrowserType.firefox,
                        null,
                        new LaunchOptions().setHeadless(false),
                        "linked4j.json");
        l.login(System.getenv("LINKEDIN_USERNAME"), System.getenv("LINKEDIN_PASSWORD"));
        l.setUserProfile("emrecubukcu");
        // l.setUserProfile("bülent-uraz-b42a80120");
        // l.setUserProfile("cindygallop");
        // l.setUserProfile("nursu-gür-43037a14b");
        System.out.printf("User full name %s\n", l.userProfile.getFullName());
        System.out.printf("User info %s\n", l.userProfile.getInfo());
        System.out.printf("User current job %s\n", l.userProfile.getShortDescription());
        System.out.printf("User current location %s\n", l.userProfile.getCurrentLocation());
        // List<WorkExperience> workExperience = l.userProfile.getUserExperiences();
        // System.out.printf("Number of experiences %s\n", workExperience.size());
        List<Education> educations = l.userProfile.getEducation();
        System.out.printf(
                "Number of educations objects on the user profile %d\n", educations.size());
        System.out.printf("User has premium badge ? %b\n", l.userProfile.hasPremium());
        System.out.printf("Is influencer ? %b", l.userProfile.isInfluencer());

        l.close();
    }
}
