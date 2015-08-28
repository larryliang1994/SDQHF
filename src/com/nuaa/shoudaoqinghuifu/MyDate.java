package com.nuaa.shoudaoqinghuifu;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;

@SuppressWarnings("serial")
public class MyDate implements Serializable, Comparable<MyDate> {
    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;

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
