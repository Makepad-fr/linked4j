/* (C)2021 */
package io.makepad.linked4j.models;

public class FormationItem {
    private String title;
    private String role;
    private boolean graduated;
    private String description;

    public FormationItem(String title, String role, String description, boolean graduated) {
        this.description = description;
        this.role = role;
        this.title = title;
        this.graduated = graduated;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isGraduated() {
        return graduated;
    }

    public void setGraduated(boolean graduated) {
        this.graduated = graduated;
    }
}
