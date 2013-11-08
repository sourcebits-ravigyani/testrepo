package com.vuzix.sg.sdk;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import com.vuzix.sg.common.BluetoothCommunication;
import com.vuzix.sg.common.Communication;
import com.vuzix.sg.common.IMessageHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: splisson
 * Date: 10/24/13
 * Time: 3:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class SGDeviceSDK implements IMessageHandler
{
    private Context context;
    private Communication communication;
    private PackageManager packageManager;
    private TimerStopwatchCallbacks timerStopwatchCallbacks;

    public  SGDeviceSDK(Context context)
    {
        this.context = context;
        communication = new BluetoothCommunication(context);

        communication.setMessageHandler(this);
        packageManager = context.getPackageManager();
    }

    public void setCommunication(Communication communication) {
        this.communication = communication;
        communication.setMessageHandler(this);
    }

    /**
     * Returns the status of the smart glasses connected, if any
     *
     * Seb, is this not better to name it as isActive, as it returns a boolean ( or is this an enum)
     * Also, dont understand why we can not depend on the bluetooth/usb communication layer's status to determine the status of the device
     *
     * Also see restartStatusRunnable
     * @return true if the smart glasses has returned a keep alive message recently enough
     */
    public boolean getStatus()
    {
        if(communication == null)
            return false;

        return true;
    }

    /**
     * Starts the communication service
     */
    public void start()
    {
        communication.start();
    }

    /**
     * Disconnect the current connection
     */
    public void close()
    {
        if(this.communication != null)
            this.communication.stop();
    }


    public void setTimerStopwatchCallbacks(TimerStopwatchCallbacks timerStopwatchCallbacks)
    {
        this.timerStopwatchCallbacks = timerStopwatchCallbacks;
    }

    /**
     * Sends a message to a destination application on a given destination client, via the communication layer
     * This method should usually not be used by application using the SDK. Dedicated methods of the SDK should be used instead.
     *
     * @param destinationClientId   identifies the destination client entity
     * @param destinationApplicationId identifies the destination application of the message
     * @param message the message itself
     * @return
     */
    public boolean sendMessage(String destinationClientId, String destinationApplicationId, String message)
    {
        communication.sendMessage(destinationClientId, destinationApplicationId, message);
        return true;
    }

    private void getApplications(boolean allApplications, String destinationClientId, String destinationApplicationId)
    {
        //get a list of installed apps.
        List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        JSONArray jArray = new JSONArray();
        //The returned list is an array of <String APPLICATION_ID, String APP_NAME, Binary APP_ICON, Unsigned Priority, String APP_VERSION>
        for (ApplicationInfo appInfo : packages)
        {
            JSONObject jObj = new JSONObject();
            try
            {
                PackageInfo packageInfo = packageManager.getPackageInfo(appInfo.packageName, 0);

                jObj.put("app_id", packageInfo.packageName);
                jObj.put("app_name", appInfo.loadLabel(packageManager));
                jObj.put("app_icon", encodeDrawableToString(appInfo.loadIcon(packageManager)));
                jObj.put("app_priority", 0);
                jObj.put("app_version", packageInfo.versionName);

                jArray.put(jObj);
            }
            catch(JSONException ex)
            {

            }
            catch (Exception ex)
            {

            }
        }
        communication.sendMessage(destinationClientId, destinationApplicationId, jArray.toString());
    }

    private String encodeDrawableToString(Drawable drawable)
    {
        if(drawable == null)
            return null;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BitmapDrawable bitDw = ((BitmapDrawable) drawable);
        Bitmap bitmap = bitDw.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapByte = stream.toByteArray();

        return Base64.encodeToString(bitmapByte, Base64.DEFAULT);
    }


    /**
     * IMessageHandler method called when receiving a message from the communication layer
     *
     * @param message
     */
    @Override
    public void onReceivedMessage(String destinationClientId, String destinationApplicationId, String message)
    {
        if(destinationApplicationId.equalsIgnoreCase(Communication.MSG_GET_APPLICATIONS))
        {
            getApplications(true, destinationClientId, destinationApplicationId);
        }

        if(destinationApplicationId.equalsIgnoreCase(Communication.MSG_GET_TIME))
        {
            String time = String.format("%l", System.currentTimeMillis());
            communication.sendMessage(destinationClientId, destinationApplicationId, time);
        }

        if(destinationApplicationId.equalsIgnoreCase(Communication.MSG_LAUNCH_APPLICATION))
        {
            try
            {
                Intent LaunchIntent = this.packageManager.getLaunchIntentForPackage(message);
                context.startActivity(LaunchIntent);
                communication.sendMessage(destinationClientId, destinationApplicationId, Communication.SUCCESS);
            }
            catch (ActivityNotFoundException ex)
            {
                communication.sendMessage(destinationClientId, destinationApplicationId, Communication.FAILURE);
            }
        }

        if(destinationApplicationId.equalsIgnoreCase(Communication.MSG_RESET_STOPWATCH))
        {
            if(timerStopwatchCallbacks != null)
            {
                timerStopwatchCallbacks.resetStopwatch();
                communication.sendMessage(destinationClientId, destinationApplicationId, Communication.SUCCESS);
            }
            else
                communication.sendMessage(destinationClientId, destinationApplicationId, Communication.FAILURE);
        }

        if(destinationApplicationId.equalsIgnoreCase(Communication.MSG_START_STOPWATCH))
        {
            if(timerStopwatchCallbacks != null)
            {
                timerStopwatchCallbacks.startStopwatch();
                communication.sendMessage(destinationClientId, destinationApplicationId, Communication.SUCCESS);
            }
            else
                communication.sendMessage(destinationClientId, destinationApplicationId, Communication.FAILURE);
        }

        if(destinationApplicationId.equalsIgnoreCase(Communication.MSG_PAUSE_STOPWATCH))
        {
            if(timerStopwatchCallbacks != null)
            {
                timerStopwatchCallbacks.pauseStopwatch();
                communication.sendMessage(destinationClientId, destinationApplicationId, Communication.SUCCESS);
            }
            else
                communication.sendMessage(destinationClientId, destinationApplicationId, Communication.FAILURE);
        }
        if(destinationApplicationId.equalsIgnoreCase(Communication.MSG_SET_TIMER))
        {
            if(timerStopwatchCallbacks != null)
            {
                timerStopwatchCallbacks.setTimer(Integer.valueOf(message));
                communication.sendMessage(destinationClientId, destinationApplicationId, Communication.SUCCESS);
            }
            else
                communication.sendMessage(destinationClientId, destinationApplicationId, Communication.FAILURE);
        }
        if(destinationApplicationId.equalsIgnoreCase(Communication.MSG_START_TIMER))
        {
            if(timerStopwatchCallbacks != null)
            {
                timerStopwatchCallbacks.startTimer();
                communication.sendMessage(destinationClientId, destinationApplicationId, Communication.SUCCESS);
            }
            else
                communication.sendMessage(destinationClientId, destinationApplicationId, Communication.FAILURE);
        }
        if(destinationApplicationId.equalsIgnoreCase(Communication.MSG_PAUSE_TIMER))
        {
            if(timerStopwatchCallbacks != null)
            {
                timerStopwatchCallbacks.pauseTimer();
                communication.sendMessage(destinationClientId, destinationApplicationId, Communication.SUCCESS);
            }
            else
                communication.sendMessage(destinationClientId, destinationApplicationId, Communication.FAILURE);
        }

    }
}
