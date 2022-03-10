/* (C)2021 */
package io.makepad.linked4j.user;

abstract class UserProfileSelectors {
    public static final String
            fullName = "//div[contains(@class,'pv-text-details__left-panel')][1]/div[1]/h1",
            currentJob = "//div[contains(@class,'pv-text-details__left-panel')][1]/div[2]",
            location = "//div[contains(@class,'pv-text-details__left-panel')][2]/span[1]",
            contactInfoButton = "//div[contains(@class,'pv-text-details__left-panel')][2]/span[2]",
            info = "//section[contains(@class,'pv-about-section')]/div",
            infoTextToDelete = "//section[contains(@class,'pv-about-section')]/div/span",
            premiumBadge = "//li-icon[@type='linkedin-bug']",
            influencerBadge = "//li-icon[@type='linkedin-influencer-color-icon']";
}
