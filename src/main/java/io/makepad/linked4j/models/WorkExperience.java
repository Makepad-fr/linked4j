/* (C)2021 */
package io.makepad.linked4j.models;

import java.util.ArrayList;
import java.util.List;

public class WorkExperience {
    public final Company company;
    public final List<Role> roles;
    public final String totalDuration;
    public final String location;

    /**
     * Function creates a new WorkExperience object
     *
     * @param company The company related to the work experience
     * @param totalDuration The total duration of this experience
     * @param location The location of the work experience
     */
    public WorkExperience(Company company, String totalDuration, String location) {
        this.company = company;
        this.roles = new ArrayList<Role>();
        this.totalDuration = totalDuration;
        this.location = location;
    }

    /**
     * Function creates a new WorkExperience object
     *
     * @param company The company related to the work experience
     * @param totalDuration The total duration of the work experience
     */
    public WorkExperience(Company company, String totalDuration) {
        this(company, totalDuration, "");
    }
}
