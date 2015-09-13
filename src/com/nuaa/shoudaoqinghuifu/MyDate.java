package com.nuaa.shoudaoqinghuifu;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;

@SuppressWarnings("serial")
public class MyDate implements Serializable, Comparable<MyDate> {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    public MyDate() {
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public MyDate(int year, int month, int day, int hour, int minute) {
        super();
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    @Override
    public String toString() {
        if (minute >= 10) {
            return year + "-" + month + "-" + day + " " + hour + ":" + minute;
        } else {
            return year + "-" + month + "-" + day + " " + hour + ":" + "0"
                    + minute;
        }
    }

    public String toSave() {
        return year + "-" + month + "-" + day + "-" + hour + "-" + minute;
    }

    @Override
    public int compareTo(@NonNull MyDate another) {
        Calendar tmpDate1 = Calendar.getInstance();
        tmpDate1.set(this.year, this.month - 1, this.day, this.hour,
                this.minute);

        Calendar tmpDate2 = Calendar.getInstance();
        tmpDate2.set(another.year, another.month - 1, another.day,
                another.hour, another.minute);

        if (tmpDate1.before(tmpDate2)) {
            return -1;
        } else if (tmpDate1.after(tmpDate2)) {
            return 1;
        } else {
            return 0;
        }
    }
}
