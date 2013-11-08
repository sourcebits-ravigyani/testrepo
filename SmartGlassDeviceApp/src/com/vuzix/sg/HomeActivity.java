package com.vuzix.sg;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;
import com.vuzix.sg.sdk.SGDeviceSDK;

public class HomeActivity extends Activity {

    // Local Bluetooth adapter
    private BluetoothAdapter bluetoothAdapter = null;

    // Member object for the chat services
    private SGDeviceSDK sdkService = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the window layout
        setContentView(R.layout.activity_home);

        // Get local Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (bluetoothAdapter == null)
        {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!bluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 1000);
            // Otherwise, setup the chat session
        }
        else
        {
            if (sdkService == null)
                setupChat();
        }
    }

    @Override
    public synchronized void onResume()
    {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (sdkService != null)
        {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (sdkService.getStatus())
            {
                sdkService.start();
            }
        }
    }

    private void setupChat()
    {
        // Initialize the BluetoothChatService to perform bluetooth connections
        sdkService = new SGDeviceSDK(this);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (sdkService != null)
            sdkService.close();
    }

    private void ensureDiscoverable()
    {
        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
        {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
}
