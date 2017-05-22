package fr.esigelec.bluetoothbot;

/**
 * Created by Paul on 22/05/2017.
 */

class Constants {
    private static final Constants ourInstance = new Constants();

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;
    public static final int BLUETOOTH_ON = 1000;
    public static final int BLUETOOTH_OFF = -1000;
    public static final int BLUETOOTH_DISCOVERY_LISTEN = 1001;
    public static final int BLUETOOTH_DISCOVERY_CANCELED = -1001;
    public static final int BLUETOOTH_CONNECTED = 1002;
    public static final int BLUETOOTH_CONNECTED_ERROR = -1002;
    public static final int BLUETOOTH_DISCOVERABLE = 1003;
    public static final int BLUETOOTH_CONNECTABLE = 1004;
    public static final int BLUETOOTH_NOT_CONNECTABLE = 1004;

    // Constants that indicate command to the robot
    public static final int EXIT_CMD = -1;
    public static final int GO_UP = 1;
    public static final int GO_DOWN = 2;
    public static final int GO_RIGHT = 3;
    public static final int GO_LEFT = 4;
    public static final int STOP = 5;
    public static final int SPEED_UP = 6;
    public static final int SPEED_DOWN = 7;
    public static final int MANUAL_MODE = 8;
    public static final int AUTO_MODE = 9;

    static Constants getInstance() {
        return ourInstance;
    }

    private Constants() {
    }
}
