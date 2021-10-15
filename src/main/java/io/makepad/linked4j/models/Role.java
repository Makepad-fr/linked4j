/* (C)2021 */
package io.makepad.linked4j.models;

public class Role {
    public final String name;
    public final String duration;
    public final String timeInterval;
    public final String location;
    public final String description;

    public Role(
            String name,
            String duration,
            String timeInterval,
            String location,
            String description) {
        this.name = name;
        this.duration = duration;
        this.timeInterval = timeInterval;
        this.location = location;
        this.description = description;
    }
}
