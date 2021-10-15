/* (C)2021 */
package io.makepad.linked4j.models;

public class School {
    public final String name;
    public final String url;

    /**
     * Function creates a new instance of School object
     *
     * @param name The name of the school
     * @param url The LinkedIn link of the school
     */
    public School(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
