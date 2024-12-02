//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package utils;

import java.util.Date;

public class Timer {
    private Date startTime = new Date();
    private Date endTime = new Date();
    private String name;

    public Timer(String name) {
        this.name = name;
    }

    public void start() {
        this.startTime = new Date();
    }

    public String end() {
        this.endTime = new Date();
        return this.toString();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name + " Time is " + (this.endTime.getTime() - this.startTime.getTime()) / 1000L + " second.";
    }
}
