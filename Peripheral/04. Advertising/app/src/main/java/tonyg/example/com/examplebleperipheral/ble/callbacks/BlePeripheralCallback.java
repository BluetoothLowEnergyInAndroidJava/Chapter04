package tonyg.example.com.examplebleperipheral.ble.callbacks;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * BlePeripheralCallback has callbacks to support notifications
 * related to advertising and data transmission
 *
 * @author Tony Gaitatzis backupbrain@gmail.com
 * @date 2015-12-18
 */
public abstract class BlePeripheralCallback {

    /**
     * Advertising Started
     */
    public abstract void onAdvertisingStarted();

    /**
     * Advertising Could not Start
     */
    public abstract void onAdvertisingFailed(int errorCode);

    /**
     * Advertising Stopped
     */
    public abstract void onAdvertisingStopped();

}


