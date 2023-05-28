package com.guru.kantewala.Models;

import com.guru.kantewala.Tools.TimeUtils;

public class SubscriptionPackage {
    String title;
    String body;
    int price;
    int validityInDays;
    int noOfStates;
    public boolean isMinimum;
    int id;

    public int getPrice() {
        return price;
    }

    public SubscriptionPackage() {
    }

    public String getTitle() {
        if (title != null && !title.isEmpty())
            return title;

        TimeUtils.TimeRep rep = new TimeUtils().daysToTimeRep(validityInDays);
        String time = (rep.time.equals("1"))?"":rep.time;
        String s = "₹";
        s = s+String.valueOf(price) + "/" + time + rep.units;
//        s= s+ price + "/" + validityInDays + " days";
        return s;
    }

    public String getBody() {
        if (body != null && !body.isEmpty())
            return body;

        return String.format("You will unlock any %d State of your choosing and can order anything from those %d State for %d days.",
                noOfStates, noOfStates, validityInDays);
    }

    public int getId() {
        return id;
    }
}
