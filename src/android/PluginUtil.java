package com.lenlee.plugin.namap;

import android.app.Activity;

public class PluginUtil{
    /*
        get R ID
    */
    public static int getAppResource(Activity activity,String name,String type){
        return  activity.getResources().getIdentifier(name,type,activity.getPackageName());
    }

}