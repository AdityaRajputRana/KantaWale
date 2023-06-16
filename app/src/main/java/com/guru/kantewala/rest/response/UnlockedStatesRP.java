package com.guru.kantewala.rest.response;

import android.app.Activity;
import android.content.Context;

import com.guru.kantewala.Tools.Methods;
import com.guru.kantewala.rest.api.APIMethods;
import com.guru.kantewala.rest.api.interfaces.APIResponseListener;

import java.util.ArrayList;

public class UnlockedStatesRP {
    ArrayList<Integer> unlockedStates;

    private ArrayList<Integer> getUnlockedStates() {
        if (unlockedStates == null)
            unlockedStates = new ArrayList<>();
        return unlockedStates;
    }

    public UnlockedStatesRP() {
    }

    public interface OnStatesListener{
        void onGotUnlockedStates(ArrayList<Integer> states);
    }

    private static UnlockedStatesRP rp;
    public static void getUnlockedStates(OnStatesListener listener, Activity context){
        if (rp != null){
            listener.onGotUnlockedStates(rp.getUnlockedStates());
        } else {
            APIMethods.getUnlockedStates(new APIResponseListener<UnlockedStatesRP>() {
                @Override
                public void success(UnlockedStatesRP response) {
                    rp = response;
                    getUnlockedStates(listener, context);
                }

                @Override
                public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                    Methods.showFailedAlert(context, code, message, redirectLink, retry, cancellable);
                    listener.onGotUnlockedStates(null);
                }
            });
        }
    }

    public static void loadUnlockStates(){
        if (rp == null){
            APIMethods.getUnlockedStates(new APIResponseListener<UnlockedStatesRP>() {
                @Override
                public void success(UnlockedStatesRP response) {
                    rp = response;
                }

                @Override
                public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                }
            });
        }
    }
}
