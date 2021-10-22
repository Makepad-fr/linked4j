/* (C)2021 */
package io.makepad.linked4j.user;

abstract class UserExperienceSelectors {

    public static String
            experienceItemPath = "//li[contains(@class,'pv-entity__position-group-role-item')]",
            experienceGroupPath =
                    "//section[@id='experience-section']//li[contains(@class,'pv-entity__position-group-pager')]",
            moreExperienceButton =
                    "//section[@id='experience-section']//div[contains(@class,'pv-experience-section__see-more')]//li-icon[@type='chevron-down-icon']/parent::button";
}
