package com.rao.tba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by ajmagat on 3/15/16.
 */
public class SMSListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("kljklj", "got stuff in sms");
        try {
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                // Text has been received
                Bundle bundle = intent.getExtras();
                SmsMessage[] msgs = null;

                String originAddress = "";
                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get("pdus");

                    msgs = new SmsMessage[pdus.length];

                    //String format = bundle.getString("format");
                    msgs[0] = SmsMessage.createFromPdu((byte[]) pdus[0]);
                    originAddress = msgs[0].getOriginatingAddress();
                }


                // Check if receiving SMS is part of a recipe
                if(EditRecipesFragment.mRecipeList != null) {
                    for (Recipe r : EditRecipesFragment.mRecipeList) {
                        if (r.getIfList().contains(TransitionIntentService.currentState)) {
                            if (r.getThenList().contains(Constants.RECEIVE_TEXT)) {
                                // There is a recipe that involves receiving an SMS
                                Intent localIntent = new Intent(Constants.BROADCAST_ACTION);
                                localIntent.putExtra("notif", true);
                                Notification temp = createSMSNotification(context, r, originAddress);
                                localIntent.putExtra("New Notification", temp.toString());
                                LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @brief Method to handle creation of notification
     */
    public Notification createSMSNotification(Context context, Recipe triggered, String originAddr) {
        try {
            // Get shared preferences
            SharedPreferences prefs = context.getSharedPreferences("RAOStore", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            String jsonString = prefs.getString(Constants.NOTIFICATION_MAP_NAME, (new JSONObject()).toString());
            JSONObject jsonObject = new JSONObject(jsonString);

            String action = triggered.getDoList().get(0);

            // Create null notification
            Notification not = null;
            System.out.println("Action is : " + action);
            // Check what this action was
            if (action.equals("Drop Pin")) {
                // If drop pin, get location and create notification
                Location notifLoc = InnerLocationService.sCurrentLocation;
                not = new Notification(triggered.getName(), action, notifLoc);

            } else if (action.equals("Silence Phone")) {
               // AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
              //  audio.setRingerMode(0);

                not = new Notification(triggered.getName(), action);
            } else if (action.equals(Constants.SEND_TEXT)) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(originAddr, null, "Sorry, user is currently busy", null, null);
                not = new Notification(triggered.getName(), action);
            }

            if (not != null) {
                // Add notification to shared preferences
                jsonObject.put(Integer.toString(jsonObject.length()), not.toString());
                editor.putString(Constants.NOTIFICATION_MAP_NAME, jsonObject.toString());
                editor.commit();
            }
            return not;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
