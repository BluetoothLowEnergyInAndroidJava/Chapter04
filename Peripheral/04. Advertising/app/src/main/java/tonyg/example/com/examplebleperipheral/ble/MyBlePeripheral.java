package tonyg.example.com.examplebleperipheral.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import tonyg.example.com.examplebleperipheral.ble.callbacks.BlePeripheralCallback;

/**
 * This class creates a local Bluetooth Peripheral
 *
 * @author Tony Gaitatzis backupbrain@gmail.com
 * @date 2016-03-06
 */
public class MyBlePeripheral {
    /** Constants **/
    private static final String TAG = MyBlePeripheral.class.getSimpleName();


    /** Peripheral and GATT Profile **/
    public static final String ADVERTISING_NAME =  "MyDevice";

    /** Advertising settings **/

    // advertising mode can be one of:
    // - ADVERTISE_MODE_BALANCED,
    // - ADVERTISE_MODE_LOW_LATENCY,
    // - ADVERTISE_MODE_LOW_POWER
    int mAdvertisingMode = AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY;

    // transmission power mode can be one of:
    // - ADVERTISE_TX_POWER_HIGH
    // - ADVERTISE_TX_POWER_MEDIUM
    // - ADVERTISE_TX_POWER_LOW
    // - ADVERTISE_TX_POWER_ULTRA_LOW
    int mTransmissionPower = AdvertiseSettings.ADVERTISE_TX_POWER_HIGH;

    /** Callback Handlers **/
    public BlePeripheralCallback mBlePeripheralCallback;

    /** Bluetooth Stuff **/
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeAdvertiser mBluetoothAdvertiser;


    /**
     * Construct a new Peripheral
     *
     * @param context The Application Context
     * @param blePeripheralCallback The callback handler that interfaces with this Peripheral
     * @throws Exception Exception thrown if Bluetooth is not supported
     */
    public MyBlePeripheral(final Context context, BlePeripheralCallback blePeripheralCallback) throws Exception {
        mBlePeripheralCallback = blePeripheralCallback;

        // make sure Android device supports Bluetooth Low Energy
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            throw new Exception("Bluetooth Not Supported");
        }

        // get a reference to the Bluetooth Manager class, which allows us to talk to talk to the BLE radio
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Beware: this function doesn't work on some systems
        if(!mBluetoothAdapter.isMultipleAdvertisementSupported()) {
            throw new Exception ("Peripheral mode not supported");
        }

        mBluetoothAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();

        // Use this method instead for better support
        if (mBluetoothAdvertiser == null) {
            throw new Exception ("Peripheral mode not supported");
        }

    }

    /**
     * Get the system Bluetooth Adapter
     *
     * @return BluetoothAdapter
     */
    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }


    /**
     * Start Advertising
     *
     * @throws Exception Exception thrown if Bluetooth Peripheral mode is not supported
     */
    public void startAdvertising() {
        mBluetoothAdapter.setName(ADVERTISING_NAME);

        // Build Advertise settings with transmission power and advertise speed
        AdvertiseSettings advertiseSettings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(mAdvertisingMode)
                .setTxPowerLevel(mTransmissionPower)
                .setConnectable(false)
                .build();


        AdvertiseData.Builder advertiseBuilder = new AdvertiseData.Builder();
        // set advertising name
        advertiseBuilder.setIncludeDeviceName(true);

        AdvertiseData advertiseData = advertiseBuilder.build();

        // begin advertising
        try {
            mBluetoothAdvertiser.startAdvertising( advertiseSettings, advertiseData, mAdvertiseCallback );
        } catch (Exception e) {
            Log.e(TAG, "could not start advertising");
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Stop advertising
     */
    public void stopAdvertising() {
        if (mBluetoothAdvertiser != null) {
            mBluetoothAdvertiser.stopAdvertising(mAdvertiseCallback);
            mBlePeripheralCallback.onAdvertisingStopped();
        }
    }


    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {

        /**
         * Advertising Started
         *
         * @param settingsInEffect The AdvertiseSettings that worked
         */
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);

            mBlePeripheralCallback.onAdvertisingStarted();
        }

        /**
         * Advertising Failed
         *
         * @param errorCode the reason for failure
         */
        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            mBlePeripheralCallback.onAdvertisingFailed(errorCode);
        }
    };


}
