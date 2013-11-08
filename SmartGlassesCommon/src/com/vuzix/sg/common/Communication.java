package com.vuzix.sg.common;

/**
 * Created with IntelliJ IDEA.
 * User: splisson
 * Date: 10/24/13
 * Time: 5:07 PM
 *
 */

import android.os.Parcelable;

/**
 * This class abstracts communication. Concrete implementation will support bluetooth, USB and TCP/IP
 *
 */
public abstract class Communication
{

    public final static String SUCCESS = "SUCCESS";
    public final static String FAILURE = "FAILURE";

    public final static String MSG_GET_APPLICATIONS = "com.vuzix.message.get_applications";
    public final static String MSG_GET_TIME = "com.vuzix.message.get_time";
    public final static String MSG_SG_DEVICE_ALIVE =  "com.vuzix.message.device_is_alive";
    public final static String MSG_LAUNCH_APPLICATION = "com.vuzix.message.launch_application";

    public final static String MSG_RESET_STOPWATCH = "com.vuzix.message.resetStopwatch";
    public final static String MSG_START_STOPWATCH = "com.vuzix.message.startStopwatch";
    public final static String MSG_PAUSE_STOPWATCH = "com.vuzix.message.pauseStopwatch";
    public final static String MSG_SET_TIMER = "com.vuzix.message.setTimer";
    public final static String MSG_START_TIMER = "com.vuzix.message.startTimer";
    public final static String MSG_PAUSE_TIMER = "com.vuzix.message.pauseTimer";

    private IMessageHandler messageHandler;

    public void setMessageHandler(IMessageHandler messageHandler)
    {
        this.messageHandler = messageHandler;
    }

    public void onReceivedMessage(String message)
    {
        //Get these params from Message or API
        messageHandler.onReceivedMessage(null, null, message);
    }

    public abstract boolean isActive();
    public abstract void start();
    public abstract void stop();
    public abstract void connectDevice(String deviceAddress);

    public abstract boolean sendMessage(String destinationClientId, String destinationApplicationId, String message);
}
