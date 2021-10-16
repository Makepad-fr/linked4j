/* (C)2021 */
package io.makepad.linked4j.user;

import org.openqa.selenium.By;

abstract class UserProfileSelectors {
    public static By
            fullName =
                    By.xpath("//div[contains(@class,'pv-text-details__left-panel')][1]/div[1]/h1"),
            currentJob =
                    By.xpath("//div[contains(@class,'pv-text-details__left-panel')][1]/div[2]"),
            location = By.xpath("//div[contains(@class,'pv-text-details__left-panel')][2]/span[1]"),
            contactInfoButton =
                    By.xpath("//div[contains(@class,'pv-text-details__left-panel')][2]/span[2]"),
            info = By.xpath("//section[contains(@class,'pv-about-section')]/div"),
            infoTextToDelete = By.xpath("//section[contains(@class,'pv-about-section')]/div/span"),
            premiumBadge = By.xpath("//li-icon[@type='linkedin-bug']"),
            influencerBadge = By.xpath("//li-icon[@type='linkedin-influencer-color-icon']");
}
