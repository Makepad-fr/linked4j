/* (C)2021 */
package io.makepad.example;

import io.makepad.linked4j.Linked4J;
import io.makepad.linked4j.models.WorkExperience;
import io.makepad.socialwalker.commons.models.Configuration;
import io.makepad.socialwalker.commons.models.exceptions.CookieFileNotFoundException;
import java.net.MalformedURLException;
import java.util.List;

public class Example {
    public static void main(String[] args)
            throws MalformedURLException, CookieFileNotFoundException {
        Configuration config = new Configuration(true, "linked4j_cookies.data");
        Linked4J l = new Linked4J(config);
        if (config.isCookiesExists()) {
            l.login();
        } else {
            l.login(System.getenv("LINKEDIN_USERNAME"), System.getenv("LINKEDIN_PASSWORD"));
        }
        l.setUserProfile("cindygallop");
        // l.setUserProfile("nursu-gür-43037a14b");
        System.out.printf("User full name %s\n", l.userProfile.getFullName());
        System.out.printf("User info %s\n", l.userProfile.getInfo());
        System.out.printf("User current job %s\n", l.userProfile.getCurrentJob());
        System.out.printf("User current location %s\n", l.userProfile.getCurrentLocation());
        List<WorkExperience> workExperience = l.userProfile.getUserExperiences();
        System.out.printf("Number of experiences %s\n", workExperience.size());
    }
}
