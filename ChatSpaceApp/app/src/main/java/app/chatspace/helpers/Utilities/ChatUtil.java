package app.chatspace.helpers.Utilities;


import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import app.chatspace.helpers.SettingKeys;

/***
 * Provides utilities to operate within a chat.
 *
 * @author Misels Kaporins
 */
public class ChatUtil
{
    private SharedPreferences preferences;

    private static final String KEY_SHARED_PREF = SettingKeys.SHARED_PREF;
    private static final int KEY_MODE_PRIVATE = 0;
    private static final String KEY_SESSION_ID = "sessionId", FLAG_MESSAGE = "message";

    /***
     * Constructs ChatUtil object
     * @param context context of calling activity
     */
    public ChatUtil(Context context)
    {
        preferences = context.getSharedPreferences(KEY_SHARED_PREF, KEY_MODE_PRIVATE);
    }

    /***
     * Stores chat session ID in shared preferences
     * @param sessionId
     */
    public void saveSessionId(String sessionId)
    {
        Editor editor = preferences.edit();
        editor.putString(KEY_SESSION_ID, sessionId);
        editor.commit();
    }

    /***
     * Gets current chat session ID
     * @return
     */
    public String getSessionId()
    {
        return preferences.getString(KEY_SESSION_ID, null);
    }

    /**
     * Creates a JSON structure for the chat message to send.
     * @param message Message sent by the user
     * @return Message string in JSON format with applied message-structure
     */
    public String prepareJSON(String message)
    {
        String json = null;

        try
        {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_MESSAGE);
            jObj.put("sessionId", getSessionId());
            jObj.put("message", message);

            json = jObj.toString();
        }
        catch (JSONException e)
        {
            Log.d(ChatUtil.class.getSimpleName(), e.toString());
        }

        return json;
    }
}