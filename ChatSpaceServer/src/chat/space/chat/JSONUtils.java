package chat.space.chat;

import org.json.JSONException;
import org.json.JSONObject;

/***
 * Provides necessary utilities for JSON messages
 *
 * @author Misels Kaporins
 *
 */
public class JSONUtils
{
	private static final String FLAG_SELF = "self",
			FLAG_NEW = "new",
			FLAG_MESSAGE = "message",
			FLAG_EXIT = "exit";

	/***
	 * Creates an instance of JSONUtils object
	 */
	public JSONUtils()
	{
	}

	/***
	 * Creates a JSON object with client details
	 * @param sessionId chat session ID
	 * @param message message
	 * @return created JSON
	 */
	public String createDetailsJsonMsg(String sessionId, String message)
	{
		String json = null;

		try
		{
			JSONObject jObj = new JSONObject();
			jObj.put("flag", FLAG_SELF);
			jObj.put("sessionId", sessionId);
			jObj.put("message", message);
			json = jObj.toString();
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		return json;
	}

	/***
	 * Creates a JSON message when somebody lefts the chat
	 * @param sessionId chat session ID
	 * @param name user name
	 * @param message message
	 * @param onlineCount number of people online
	 * @return created JSON
	 */
	public String createExitJsonMsg(String sessionId, String name, String message, int onlineCount)
	{
		String json = null;

		try
		{
			JSONObject jObj = new JSONObject();
			jObj.put("flag", FLAG_EXIT);
			jObj.put("name", name);
			jObj.put("sessionId", sessionId);
			jObj.put("message", message);
			jObj.put("onlineCount", onlineCount);
			json = jObj.toString();
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		return json;
	}

	/***
	 * Creates a JSON message when new client joins a chat
	 * @param sessionId chat session ID
	 * @param name user name
	 * @param message message
	 * @param onlineCount number of people online
	 * @return created JSON
	 */
	public String createNewUserJsonMsg(String sessionId, String name, String message, int onlineCount)
	{
		String json = null;
		try
		{
			JSONObject jObj = new JSONObject();
			jObj.put("flag", FLAG_NEW);
			jObj.put("name", name);
			jObj.put("sessionId", sessionId);
			jObj.put("message", message);
			jObj.put("onlineCount", onlineCount);
			json = jObj.toString();
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		return json;
	}

	/***
	 * Creates a message that needs to be sent to all users
	 * @param sessionId chat session ID
	 * @param fromName the user sent a message
	 * @param message message
	 * @return created JSON
	 */
	public String createSendToAllJsonMsg(String sessionId, String fromName, String message)
	{
		String json = null;

		try
		{
			JSONObject jObj = new JSONObject();
			jObj.put("flag", FLAG_MESSAGE);
			jObj.put("sessionId", sessionId);
			jObj.put("name", fromName);
			jObj.put("message", message);
			json = jObj.toString();
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		return json;
	}
}