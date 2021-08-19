package io.makepad.linked4j.bot;

public interface IBot {
    void login(String username, String password);
    void saveCookies(String filePath);
    void loadCookies(String filePath);
}
