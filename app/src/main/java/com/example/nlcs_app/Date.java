package com.example.nlcs_app;

import java.util.Scanner;


public class Date
{
    private int day, month, year;
    public Date() {
        day = 1;
        month = 1;
        year = 1;
    }
    public Date( int tday, int tmonth, int tyear) {
        day = tday;
        month = tmonth;
        year = tyear;
    }
    public Date(Date d) {
        day = d.day;
        month = d.month;
        year = d.year;
    }
    public boolean checkDate() {
        if( year < 0)
            return false;
        if( month > 12)
            return false;
        if( month == 2)
        {
            if( year % 4 == 0)
            {
                if( day > 29 || day <= 0)
                    return false;
            }
            else
            if( day > 28 || day <= 0)
                return false;
        }
        else
        {
            if( month == 4 || month == 6 || month == 9 || month == 11)
            {
                if( day > 30 || day <= 0)
                    return false;
            }
            else
            if( day > 31 || day <= 0)
                return false;
        }
        return true;
    }
    public void showDay() {
        if( day > 9)
            System.out.print(day);
        else
            System.out.print("0" + day);
    }
    public void showMonth() {
        if( month > 9)
            System.out.print(month);
        else
            System.out.print("0" + month);
    }
    public String toStringDay() {
        if( day > 9)
            return Integer.toString(day);
        else
            return ("0" + day);
    }
    public String toStringMonth() {
        if( month > 9)
            return Integer.toString(month);
        else
            return ("0" + month);
    }
    public String toStringYear() {

        if( year > 999)
            return Integer.toString(year);
        else
        if( year > 99)
            return ("0" + year);
        else
        if( year > 9)
            return ("00" + year);
        else
            return  ("0" + year);

    }
    public void showDate() {
        showDay();
        System.out.print("/");
        showMonth();
        System.out.print("/" + year);
    }
    public String toString() {
        return (toStringDay() + "/" + toStringMonth() + "/" + toStringYear());
    }
    public void showDate(Date da)
    {
        System.out.print(da.day + "/ " + da.month + "/ " + da.year);
    }
    public void enterDate() {
        String d, m, y;
        Scanner sc = new Scanner (System.in);
        do
        {
            System.out.print("Enter day: ");
            d = sc.nextLine();
            day = Integer.parseInt(d);
            System.out.print("Enter month: ");
            m = sc.nextLine();
            month = Integer.parseInt(m);
            System.out.print("Enter year: ");
            y = sc.nextLine();
            year = Integer.parseInt(y);
            if( !checkDate() )
                System.out.print("\n    Error, type again\n");
        }while( !checkDate() );
    }
    public void insertDate( String dayString) {
        char[] dayChar = new char[2];
        char[] monthChar = new char[2];
        char[] yearChar = new char[4];

        dayString.getChars(0, 2, dayChar  , 0);
        dayString.getChars(6, 10, yearChar    , 0);
        dayString.getChars(3, 5, monthChar  , 0);

        String d = new String(dayChar);
        String m = new String(monthChar);
        String y = new String(yearChar);

        day = Integer.parseInt(d);

        month = Integer.parseInt(m);

        year = Integer.parseInt(y);
    }
    public void incDate(Date d) {
        int maxyear = 12;
        if( d.month +1 <= maxyear)
        {
            d.month ++;
            d.day = 1;
        }
        else
        {
            d.month = 1;
            d.year ++;
            d.day = 1;
        }
    }
    public Date addDay( int nbDay) {
        //0 1  2  3  4  5  6  7  8  9  10  11 12
        int maxDate[] = {0,31,28,31,30,31,30,31,31,30,31,30,31};
        int tempDay = day;
        Date tempDate = new Date( day, month, year);
        if( tempDate.year % 2 == 0) maxDate[2] = 29;
        while( nbDay > 0)
        {
            tempDay = tempDate.day;
            if( nbDay +tempDate.day <= maxDate[tempDate.month])
            {
                tempDate.day += nbDay;
                nbDay = 0;
            }
            else
            {
                incDate(tempDate);
                if( tempDate.year % 2 == 0) maxDate[2] = 29;
                nbDay -= maxDate[tempDate.month-1] - tempDay +1;
                System.out.println("maxday " + (maxDate[tempDate.month-1] - tempDay +1));
            }
        }
        return tempDate;
    }
    public Date tmDate()
    {
        return addDay(1);
    }
    public int getday() {
        return day;
    }
    public int getmonth() {
        return month;
    }
    public int getyear() {
        return year;
    }
    public static void main(String[] args) {
        Date d = new Date();
        d.enterDate();
        d.showDate();
        d.showDate(d.addDay(12));
        d.showDate(d.tmDate());
        Scanner sc = new Scanner (System.in);
        d.insertDate("10/02/2000");
    }
}