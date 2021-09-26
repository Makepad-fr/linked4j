package io.makepad.linked4j.structs;

import io.makepad.linked4j.bot.Bot;

public class LinkedIn {
    public Bot bot;
    public LinkedIn(String username, String password) throws Exception {
        this.bot = new Bot();
        this.bot.login(username,password);
    }
    public LinkedIn(String filePath) {
        this.bot = new Bot();
        this.bot.loadCookies(filePath);
    }
}
