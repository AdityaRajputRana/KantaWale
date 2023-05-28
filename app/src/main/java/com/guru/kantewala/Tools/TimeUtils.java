package com.guru.kantewala.Tools;

public class TimeUtils {
    public class TimeRep{
        public String time;
        public String units;

        public TimeRep() {
        }
    }

    public TimeRep daysToTimeRep(int days){
        TimeRep td = new TimeRep();
        int months = (int) (days/30);
        int years = (int) (days/365);

        if (days<30){
            td.time = String.valueOf(days);
            td.units = "day";
        } else if (months<12){
            td.time = String.valueOf(months);
            td.units = "month";
        } else {
            td.time = String.valueOf(years);
            td.units = "year";
        }

        return td;
    }


}
