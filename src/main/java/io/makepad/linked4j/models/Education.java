/* (C)2021 */
package io.makepad.linked4j.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Education {
    public final School school;
    public final String fieldOfStudy, degreeName, description, activitiesAndSocieties;
    public Date start, end;

    /**
     * Function creates a new instance of the Education object
     *
     * @param school The school related to the education
     * @param fieldOfStudy The field of study
     * @param degreeName The name of the degree
     * @param description The description of the studies
     * @param activitiesAndSocieties Activities and societies related to the education
     * @param start The start date
     * @param end The end date
     */
    public Education(
            School school,
            String fieldOfStudy,
            String degreeName,
            String description,
            String activitiesAndSocieties,
            Date start,
            Date end) {
        this.school = school;
        this.fieldOfStudy = fieldOfStudy;
        this.degreeName = degreeName;
        this.description = description;
        this.activitiesAndSocieties = activitiesAndSocieties;
        this.start = start;
        this.end = end;
    }

    public Education(
            School school,
            String fieldOfStudy,
            String degreeName,
            String description,
            String activitiesAndSocieties,
            String startYear,
            String endYear)
            throws ParseException {
        this(
                school,
                fieldOfStudy,
                degreeName,
                description,
                activitiesAndSocieties,
                new SimpleDateFormat("yyyy").parse(startYear),
                new SimpleDateFormat("yyyy").parse(endYear));
    }

    public Education(
            School school,
            String fieldOfStudy,
            String degreeName,
            String description,
            String activitiesAndSocieties,
            String dateInterval)
            throws ParseException {
        this(
                school,
                fieldOfStudy,
                degreeName,
                description,
                activitiesAndSocieties,
                dateInterval.split("-")[0].trim(),
                dateInterval.split("-")[1].trim());
    }
}
