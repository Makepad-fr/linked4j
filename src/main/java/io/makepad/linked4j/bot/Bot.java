package io.makepad.linked4j.bot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import org.openqa.selenium.*;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

public class Bot extends AbstractBot implements IBot {

    public Bot(){
        super();
    }

    public void login(String username, String password)  {

        final By usernamePath = By.xpath("//*[@id=\"session_key\"]");
        super.wait.until(presenceOfElementLocated(usernamePath));
        super.driver.findElement(usernamePath).sendKeys(username);

        final By passwordPath = By.xpath("//*[@id=\"session_password\"]");
        super.wait.until(presenceOfElementLocated(passwordPath));
        super.driver.findElement(passwordPath).sendKeys(password);

        final By loginButtonPath = By.xpath("/html/body/main/section[1]/div[2]/form/button");
        super.wait.until(presenceOfElementLocated(loginButtonPath));
        super.driver.findElement(loginButtonPath).click();
    }
    /**
     * Save cookies to the given file path
     * @param filePath The file path to save the cookies
     */
    public void saveCookies(String filePath) {
        File file = new File(filePath);
        try
        {
            // Delete old file if exists
            file.delete();
            file.createNewFile();
            FileWriter fileWrite = new FileWriter(file);
            BufferedWriter Bwrite = new BufferedWriter(fileWrite);
            // loop for getting the cookie information
            System.out.println("Number of cookies " + driver.manage().getCookies().size());
            // loop for getting the cookie information
            for(Cookie ck : driver.manage().getCookies())
            {
                Bwrite.write((ck.getName()+";"+ck.getValue()+";"+ck.getDomain()+";"+ck.getPath()+";"+ck.getExpiry()+";"+ck.isSecure()));
                Bwrite.newLine();
            }
            Bwrite.close();
            fileWrite.close();

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Function loads the cookies from the given file path
     * @param filePath The path of the cookie file to load
     */
    public void loadCookies(String filePath) {
        try{

            File file = new File(filePath);
            FileReader fileReader = new FileReader(file);
            BufferedReader Buffreader = new BufferedReader(fileReader);
            String strline;

            while((strline=Buffreader.readLine())!=null){
                StringTokenizer token = new StringTokenizer(strline,";");
                while(token.hasMoreTokens()){
                    String name = token.nextToken();
                    String value = token.nextToken();
                    String domain = token.nextToken();
                    String path = token.nextToken();
                    Date expiry = null;

                    String val;
                    if(!(val=token.nextToken()).equals("null"))
                    {
                        expiry = (new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")).parse(val);
                    }
                    boolean isSecure = Boolean.parseBoolean(token.nextToken());
                    Cookie ck = new Cookie(name,value,domain,path,expiry,isSecure);
                    driver.manage().addCookie(ck); // This will add the stored cookie to your current session
                }
            }
            super.driver.navigate().refresh();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void getUserDetails(String username){
        super.driver.get(String.format("https://www.linkedin.com/in/%s/", username));
        final By fullNamePath = By.xpath("/html/body/div[7]/div[3]/div/div/div/div/div[3]/div/div/main/div/section/div[2]/div[2]/div/div[1]/h1");
        String fullName = super.driver.findElement(fullNamePath).getText();
        System.out.println("Full name :"+ fullName);

        final By infoPath = By.xpath("/html/body/div[7]/div[3]/div/div/div/div/div[3]/div/div/main/div/div/div[5]/section/div");
        String info = super.driver.findElement(infoPath).getText();
        System.out.println("info:"+info);
    }



}

