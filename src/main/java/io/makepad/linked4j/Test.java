package io.makepad.linked4j;

import io.makepad.linked4j.bot.Bot;
import io.makepad.linked4j.structs.LinkedIn;

import java.io.File;

public class Test {
    public static void main(String[] args) throws Exception {
    final String COOKIE_FILE_PATH = "cookie.data";
    File f = new File(COOKIE_FILE_PATH);
        LinkedIn l;
        if(f.exists() && !f.isDirectory()){
            System.out.println("login with cookies");
            l = new LinkedIn(COOKIE_FILE_PATH);
        }else{
            System.out.println("login with username");
            l = new LinkedIn(System.getenv("LINKEDIN_USERNAME"),System.getenv("LINKEDIN_PASSWORD"));
            l.bot.saveCookies(COOKIE_FILE_PATH);
        }
    }
}
