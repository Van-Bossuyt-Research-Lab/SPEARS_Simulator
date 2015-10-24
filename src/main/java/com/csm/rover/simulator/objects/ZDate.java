package com.csm.rover.simulator.objects;

import java.io.Serializable;
import java.util.Calendar;

public class ZDate implements Serializable {
    
    private static final long serialVersionUID = 1354011586595017310L;
	
    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;
    public int second;
    
    private String format = "MM/dd/yyyy hh:mm:ss";
    
    public ZDate(){
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DAY_OF_MONTH);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
        second = cal.get(Calendar.SECOND);
    }
    
    public ZDate(int year, int month, int day){
        this.year = year;
        this.month = month;
        this.day = day;
        hour = 0;
        minute = 0;
        second = 0;
    }
    
    public ZDate(int year, int month, int day, int hour, int minute){
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        second = 0;
    }
    
    public ZDate(int year, int month, int day, int hour, int minute, int second){
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }
    
    public String toString(){
        char[] form = format.toCharArray();
        char[] hold;
        int x = 0;
        while (x < form.length){
            try {
                if (form[x] == 'y'){
                    if (form[x+1] == 'y' && form[x+2] == 'y' && form[x+3] == 'y'){
                        hold = (year + "").toCharArray();
                        form[x] = hold[0];
                        form[x+1] = hold[1];
                        form[x+2] = hold[2];
                        form[x+3] = hold[3];
                    }
                }
                if (form[x] == 'M'){
                    if (form[x+1] == 'M'){
                        if (month < 10){
                            hold = (month + "").toCharArray();
                            form[x] = '0';
                            form[x+1] = hold[0];
                        }
                        else {
                            hold = (month + "").toCharArray();
                            form[x] = hold[0];
                            form[x+1] = hold[1];
                        }
                    }
                }
                if (form[x] == 'd'){
                    if (form[x+1] == 'd'){
                        if (day < 10){
                            hold = (day + "").toCharArray();
                            form[x] = '0';
                            form[x+1] = hold[0];
                        }
                        else {
                            hold = (day + "").toCharArray();
                            form[x] = hold[0];
                            form[x+1] = hold[1];
                        }
                    }
                }
                if (form[x] == 'h'){
                    if (form[x+1] == 'h'){
                        if (hour < 10){
                            hold = (hour + "").toCharArray();
                            form[x] = '0';
                            form[x+1] = hold[0];
                        }
                        else {
                            hold = (hour + "").toCharArray();
                            form[x] = hold[0];
                            form[x+1] = hold[1];
                        }
                    }
                }
                if (form[x] == 'm'){
                    if (form[x+1] == 'm'){
                        if (minute < 10){
                            hold = (minute + "").toCharArray();
                            form[x] = '0';
                            form[x+1] = hold[0];
                        }
                        else {
                            hold = (minute + "").toCharArray();
                            form[x] = hold[0];
                            form[x+1] = hold[1];
                        }
                    }
                }
                if (form[x] == 's'){
                    if (form[x+1] == 's'){
                        if (second < 10){
                            hold = (second + "").toCharArray();
                            form[x] = '0';
                            form[x+1] = hold[0];
                        }
                        else {
                            hold = (second + "").toCharArray();
                            form[x] = hold[0];
                            form[x+1] = hold[1];
                        }
                    }
                }
            } catch (Exception e) {}
            x++;
        }
        String out = "";
        x = 0;
        while (x < form.length){
            out = out + form[x];
            x++;
        }
        return out;
    }
    
    public String toString(String tempformat){
        String hold = format;
        format = tempformat;
        String out = toString();
        format = hold;
        return out;
    }
    
    public void setFormat(String format){
        this.format = format;
    }
    
    public String getFormat(){
        return format;
    }
    
    public boolean equals(ZDate to){
        return (to.toString()).equals(toString());
    }
    
    public boolean equals(String to){
        return to.equals(toString());
    }
    
    public void setYear(int year){
        if (year > 0){
            this.year = year;
        }
    }
    
    public int getYear(){
        return year;
    }
    
    public void setMonth(int month){
        if (month > 0 && month < 13){
            this.month = month;
        }
    }
    
    public int getMonth(){
        return month;
    }
    
    public void setDay(int day){
        if (day > 0 && day <= getDaysinMonth(month)){
            this.day = day;
        }
    }
    
    public int getDay(){
        return day;
    }
    
    public void setHour(int hour){
        if (hour > -1 && hour < 24){
            this.hour = hour;
        }
    }
    
    public int getHour(){
        return hour;
    }
    
    public void setMinute(int min){
        if (min >= 0 && min < 60){
            this.minute = min;
        }
    }
    
    public int getMinute(){
        return minute;
    }
    
    public void setSecond(int sec){
        if (sec >=0 && sec < 60){
            second = sec;
        }
    }
    
    public int getSecond(){
        return second;
    }
    
    static int getDaysinMonth(int month){
        switch (month){
            case 1:
                return 31;
            case 2:
                return 28;
            case 3:
                return 31;
            case 4:
                return 30;
            case 5:
                return 31;
            case 6:
                return 30;
            case 7:
                return 31;
            case 8:
                return 31;
            case 9:
                return 30;
            case 10:
                return 31;
            case 11:
                return 30;
            case 12:
                return 31;
            default:
                return 0;
        }
    }
    
    public void advanceClock(){
        int work = getSecond() + 1;
        if (work == 60){
            setSecond(0);
            work = getMinute() + 1;
            if (work == 60){
                setMinute(0);
                work = getHour() + 1;
                if (work == 24){
                    setHour(0);
                    work = getDay() + 1;
                    if (work > getDaysinMonth(getMonth())){
                        setDay(1);
                        work = getMonth() + 1;
                        if (work > 12){
                            setMonth(1);
                            setYear(getYear() + 1);
                        }
                        else {
                            setMonth(work);
                        }
                    }
                    else {
                        setDay(work);
                    }
                }
                else {
                    setHour(work);
                }
            }
            else {
                setMinute(work);
            }
        }
        else {
            setSecond(work);
        }
    }
    
}
