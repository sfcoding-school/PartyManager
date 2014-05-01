package com.partymanager.app.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.partymanager.app.ProfileActivity;

/**
 * Created by luca on 5/1/14.
 */
public class helperFacebook {

    private static String facebookId;

    public static String getFacebookId(Context context){
        if (facebookId != null)
            return facebookId;
        else{
            SharedPreferences prefs = context.getSharedPreferences(ProfileActivity.class.getSimpleName(),context.MODE_PRIVATE);
            String id = prefs.getString("reg_id","");
            if (id.isEmpty()) {
                Log.e("HELPER_FACEBOOK", "id facebook not found.");
                return null;
            }else{
                facebookId = id;
                return facebookId;
            }
        }
    }
}
