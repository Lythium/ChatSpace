package app.chatspace.helpers;

/**
 * Class that contains shared setting keys across all application classes
 *
 * @author Misels Kaporins
 */
public class SettingKeys
{
    public static final String SEVER_URL = "ws://192.168.0.9:8080/ChatSpaceServer/chat";
    public static final String ENCODING = "UTF-8";
    public static final String SHARED_PREF = "CHATSPACE_PREFERENCES";

    // Location service settings
    public static final long MIN_DISTANCE_FOR_UPDATE = 50; // 50 meters
    public static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2; // 2 minutes
}
