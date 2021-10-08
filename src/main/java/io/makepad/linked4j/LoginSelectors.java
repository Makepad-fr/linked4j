/* (C)2021 */
package io.makepad.linked4j;

import org.openqa.selenium.By;

abstract class LoginSelectors {
    public static By username = By.xpath("//*[@id=\"session_key\"]"),
            password = By.xpath("//*[@id=\"session_password\"]"),
            loginButton = By.xpath("/html/body/main/section[1]/div[2]/form/button");
}
