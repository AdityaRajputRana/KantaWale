package com.guru.kantewala.Models;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.guru.kantewala.Tools.Constants;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class PlanDetails {
    int id, userId;
    String uid, purchasedOn, validOn, order_id;
    String states;
    String stateStr;

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
            ArrayList<String> indianStatesList = Constants.getIndianStatesArrayList();
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
