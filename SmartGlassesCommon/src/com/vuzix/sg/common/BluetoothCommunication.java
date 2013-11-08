package com.vuzix.sg.common;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * This class is the implementation of Bluetooth Communication
 * This can only be accessed from SDKPartnerSDK
 *
 */
public class BluetoothCommunication extends Communication
{
    private BluetoothChatService chatService = null;

    public BluetoothCommunication(Context context)
    {
        chatService = new BluetoothChatService(context, handler);
    }

    @Override
    public boolean isActive()
    {
        return (chatService.getState() != BluetoothChatService.STATE_NONE);
    }

    @Override
    public void start()
    {
        chatService.start();
    }

    @Override
    public void stop()
    {
        chatService.stop();
    }

    @Override
    public void connectDevice(String deviceAddress)
    {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = adapter.getRemoteDevice(deviceAddress);
        chatService.connect(device, true);
    }

    @Override
    public boolean sendMessage(String destinationClientId, String destinationApplicationId, String message)
    {
        // Check that we're actually connected before trying anything
        if (chatService.getState() != BluetoothChatService.STATE_CONNECTED)
            return false;

        // Check that there's actually something to send
        if (message.length() > 0)
        {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            chatService.write(send);
        }
        return true;
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case BluetoothChatService.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    onReceivedMessage(readMessage);
                    break;
                case BluetoothChatService.MESSAGE_STATE_CHANGE:
                    break;
                case BluetoothChatService.MESSAGE_WRITE:
                    break;
                case BluetoothChatService.MESSAGE_DEVICE_NAME:
                    break;
                case BluetoothChatService.MESSAGE_UNABLE_TO_CONNECT:
                    break;
                case BluetoothChatService.MESSAGE_CONNECTION_LOST:
                    break;
            }
        }
    };
}
