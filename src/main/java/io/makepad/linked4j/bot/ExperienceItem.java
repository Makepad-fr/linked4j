package io.makepad.linked4j.bot;

import java.util.Date;

public class ExperienceItem {
    private String companyName;
    private String role;
    private int duration;
    private String description;
    private Date starts;
    private Date ends;
    private String companyPageURL;



    public ExperienceItem(String companyName, String role, String description, int duration, Date starts, Date ends, String companyPageURL){
        this.description = description;
        this.duration = duration;
        this.role = role;
        this.companyName = companyName;
        this.starts = starts;
        this.ends = ends;
        this.companyPageURL = companyPageURL;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public Date getStarts() {
        return starts;
    }

    public void setStarts(Date starts) {
        this.starts = starts;
    }

    public Date getEnds() {
        return ends;
    }

    public void setEnds(Date ends) {
        this.ends = ends;
    }

    public String getCompanyPageURL() {
        return companyPageURL;
    }

    public void setCompanyPageURL(String companyPageURL) {
        this.companyPageURL = companyPageURL;
    }
}
