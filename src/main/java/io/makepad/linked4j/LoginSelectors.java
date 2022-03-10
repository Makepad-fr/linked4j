/* (C)2021 */
package io.makepad.linked4j;

abstract class LoginSelectors {
    public static String username = "//*[@id=\"session_key\"]",
            password = "//*[@id=\"session_password\"]",
            loginButton = "//button[contains(@class,'sign-in-form__submit-button')]";
}
