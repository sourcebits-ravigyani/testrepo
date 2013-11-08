package com.vuzix.sg.common;

/**
 * Created with IntelliJ IDEA.
 * User: splisson
 * Date: 10/24/13
 * Time: 5:31 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IMessageHandler {
    public void onReceivedMessage(String destinationClientId, String destinationApplicationId, String message);
}
