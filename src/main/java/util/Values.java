package util;

public class Values {
    public static int APPLICATION_MIN_WIDTH = 1000;
    public static int APPLICATION_MIN_HEIGHT = 600;

    public static int DEVICE_SIZE = 50;
    public static int DEVICE_MIN_SIZE = 50;
    public static int DEVICE_MAX_SIZE = 100;
    public static int DEVICE_DEFAULT_POSITION = 100;
    public static int DEVICE_STROKE = 1;

    public static int FONT_SIZE = 16;

    public static int DEVICE_SWITCH_ASSOCIATION_TABLE_SIZE_LIMIT = 1000;

    public static int DEVICE_FIREWALL_NUMBER_OF_PORTS = 2;
    public static int DEVICE_HUB_NUMBER_OF_PORTS = 8;
    public static int DEVICE_ROUTER_NUMBER_OF_PORTS = 8;
    public static int DEVICE_SWITCH_NUMBER_OF_PORTS = 8;

    public static int PORT_DEFAULT_VLAN_ID = 1;
    public static int PORT_VLAN_ID_MIN_VALUE = 1;
    public static int PORT_VLAN_ID_MAX_VALUE = 1001;

    public static int ENGINE_MILLISECONDS_PAUSE = 33;

    public static double LABEL_COLOR_MIN_HEIGHT = 20.0;
    public static double LABEL_COLOR_MIN_WIDTH = 50.0;

    public static int MESSAGE_SIZE = 50;
    public static int MESSAGE_PROGRESS_MAX = 1000;
    public static int MESSAGE_PROGRESS_STEP = 30;
    public static int MESSAGE_PROGRESS_MIN_STEP = 10;
    public static int MESSAGE_PROGRESS_MAX_STEP = 300;

    public static String DIALOG_NEW_TITLE = "Start new project";
    public static String DIALOG_NEW_CONTENT = "Do you want to create a new project?\nAll unsaved data will be lost.";

    public static String DIALOG_OPEN_TITLE = "Open project";
    public static String DIALOG_OPEN_CONTENT = "Do you want to open a project?\nAll unsaved data will be lost.";

    public static int DIALOG_OPTIONS_MIN_WIDTH = 400;

    public static String REGEX_IP_ADDRESS_WITH_MASK = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])(?:\\.[01]?\\d\\d?|\\.2[0-4]\\d|\\.25[0-5]){3}(?:/[0-2]\\d|/3[0-2])?$";

    public static String MESSAGE_RECORD_ADDED = "Record has been added";

    public static String ERROR_INVALID_NETWORK_IP_ADDRESS = "Provided network address is incorrect";
    public static String ERROR_INVALID_IP_ADDRESS = "Provided ip address is incorrect";
    public static String ERROR_INVALID_VLAN_ID = "Provided VLAN ID is incorrect";

    public static String ERROR_ADDRESS_IS_NOT_A_NETWORK_ADDRESS = "Provided address is not a network address";
    public static String ERROR_ADDRESS_IS_NOT_A_HOST_ADDRESS = "Provided address is not a host address";
    public static String ERROR_NO_FREE_PORT_AVAILABLE = " has no free port available";
    public static String ERROR_OPEN_FILE_ERROR = "There was an error while opening a file";
    public static String ERROR_CANNOT_CONNECT_TO_SAME_DEVICE = "You cannot connect the device with itself";
}
