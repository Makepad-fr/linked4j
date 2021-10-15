/* (C)2021 */
package io.makepad.linked4j.user;

abstract class UserEducationSelectors {
    public static String educationSectionPath = "//section[contains(@id,'education-section')]",
            educationElementsContainerPath =
                    String.format(
                            "%s/ul[contains(@class,'pv-profile-section__section-info')]",
                            educationSectionPath),
            seeMoreButtonPath =
                    String.format(
                            "%s//div[contains(@class,'pv-profile-section__actions-inline')]//li-icon[@type='chevron-down-icon']/parent::button",
                            educationSectionPath),
            educationListItemPath =
                    String.format(
                            "%s//li[contains(@class,'pv-education-entity')]",
                            educationElementsContainerPath),
            educationSchoolNamePath = "//h3[contains(@class,'pv-entity__school-name')]",
            entitySecondaryTitlePath = "//p[contains(@class,'pv-entity__secondary-title')]",
            educationFieldNamePath = "//p[contains(@class,'pv-entity__fos')]/span[2]",
            degreeNamePath = "//p[contains(@class,'pv-entity__degree-name')]/span[2]",
            educationDatePath = "//p[contains(@class,'pv-entity__dates')]//span[2]",
            educationDescriptionPath = "//p[contains(@class,'pv-entity__description')]/span[2]",
            educationActivitiesAndSocieties =
                    String.format(
                            "%s//span[contains(@class,'activities-societies')]",
                            entitySecondaryTitlePath);
}
