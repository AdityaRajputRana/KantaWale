package com.guru.kantewala.rest.response;

import com.guru.kantewala.Models.PlanDetails;
import com.guru.kantewala.Models.PlanHistoryModel;

import java.util.ArrayList;

public class PackHistoryRP {
    ArrayList<PlanDetails> history;

    public PackHistoryRP() {
    }

    public ArrayList<PlanDetails> getHistory() {
        if (history == null)
            return new ArrayList<>();
        return history;
    }
}
