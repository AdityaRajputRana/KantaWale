package com.guru.kantewala.Models;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.guru.kantewala.Tools.Constants;
import com.guru.kantewala.Tools.TimeUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class PlanDetails {
    int id, userId;
    String uid, purchasedOn, validOn, order_id;
    String states;
    boolean isActive, isExpired;
    String stateStr;


    String title;
    String body;
    int price;
    int validityInDays;
    int noOfStates;

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

    public boolean isActive() {
        return isActive;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public String getId() {
        return String.valueOf(id);
    }

    public String getPurchasedOn() {
        return purchasedOn;
    }

    public String getValidOn() {
        return validOn;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getStatesAsString() {
        if (stateStr != null)
            return stateStr;

        try {
            JSONArray array = new JSONArray(states);
            List<String> indianStatesList = Constants.getIndianStates();
            stateStr = "";
            for (int i = 0; i < array.length(); i++){
                if (i != 0){
                    stateStr = stateStr + ", ";
                }
                stateStr += indianStatesList.get(array.getInt(i));
            }

            return stateStr;
        } catch (JSONException e) {
            e.printStackTrace();
            return states;
        }

    }
}
