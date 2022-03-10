/* (C)2021 */
package io.makepad.linked4j.utils;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PageHelpers {
    private final Page page;
    private static final Logger logger = LogManager.getLogger(PageHelpers.class);

    public PageHelpers(Page page) {
        this.page = page;
    }

    /**
     * Get the scrollHeight of the current page
     *
     * @return The scrollHeight of the object
     */
    private int getScrollHeight() {
        return (int) this.page.evaluate("() => document.body.scrollHeight;");
    }

    /**
     * Function scrolls given height
     *
     * @param height The height to scroll
     */
    public void scrollTo(int height) {
        this.page.evaluate(String.format("() => window.scrollTo(0,%d)", height));
    }

    /** Function scrolls to the top of the page */
    public void scrollToTheTop() {
        this.scrollTo(0);
    }

    /**
     * Function scrolls the page until the given selector appears
     *
     * @param selector The selector of the element
     */
    public void scrollUntilElementAppears(String selector) {
        try {
            logger.debug("Checking if element is on the page");
            page.waitForSelector(selector);
            logger.info("Element is already on the page. No need to scroll");
            return;
        } catch (TimeoutError e) {
            logger.debug("Scrolling to the beginning of the page");
            this.scrollToTheTop();
            logger.info("Scrolled to the top of the page");
        }
        int i = 0;
        while (true) {
            try {
                logger.debug("Waiting for element");
                page.waitForSelector(selector);
                logger.info("Element is appeared");
                return;
            } catch (TimeoutError ignore) {
                logger.warn("Element does not appeared");
                i += getScrollHeight() / 10;
                logger.debug(String.format("Scrolling until %d", i));
                this.scrollTo(i);
                logger.info("Scrolled");
            }
        }
    }

    /**
     * Function returns an empty string if the element is not present. If it's present returns the
     * text content of the element
     *
     * @param selector
     * @return The text content of the element or an empty string
     */
    public String textContent(String selector) {
        try {
            return this.page.textContent(selector);
        } catch (TimeoutError e) {
            return "";
        }
    }
}
