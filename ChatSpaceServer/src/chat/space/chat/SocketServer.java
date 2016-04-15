package chat.space.chat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Maps;

/***
 * Represents a web socket end-point
 *
 * @author Misels Kaporins
 *
 */
@ServerEndpoint("/chat")
public class SocketServer
{
	// List of all existing chat spaces
	private static final List<ChatSpace> chats = Collections.synchronizedList(new ArrayList<ChatSpace>());

	// Mapping between the session and chat member nickname
	private static final HashMap<String, String> nameSessionMap = new HashMap<String, String>();

	// Mapping between the chat space and the set with all the live sessions
	private static final HashMap<ChatSpace, Set<Session>> chatSessionMap = new HashMap<ChatSpace, Set<Session>>();

	// Distance around to look for chats
	private static final double DISTANCE = 50.0;

	/***
	 * Gets query parameters from URL
	 *
	 * @param query URL query
	 * @return Mapping from a parameter to a value
	 */
	public static Map<String, String> getQueryMap(String query)
	{
		Map<String, String> map = Maps.newHashMap();
		if (query != null)
		{
			String[] params = query.split("&");
			for (String param : params)
			{
				String[] nameval = param.split("=");
				map.put(nameval[0], nameval[1]);
			}
		}

		return map;
	}

	private JSONUtils jsonUtils = new JSONUtils();

	/***
	 * Finds a chat space for a user
	 * @param sessionId chat session ID
	 * @return ChatSpace to join
	 */
	public ChatSpace findChat(String sessionId)
	{
		// Iterate through all chat spaces
		synchronized(chats)
		{
			Iterator<ChatSpace> i = chats.iterator();
			while (i.hasNext())
			{
				ChatSpace c = i.next();

				// Get a list of session for a particular chat space
				Set<Session> sessionSet = chatSessionMap.get(c);

				// Iterate through the set of session for this chat space
				for (Iterator<Session> it = sessionSet.iterator(); it.hasNext(); )
				{
					Session s = it.next();

					// Check if sessionId is within that set
					if (s.getId().equals(sessionId))
					{
						return c;
					}
				}
			}
		}

		// No chat found
		return null;
	}

	/**
	 * Called when a connection is closed.
	 *
	 * @param session User chat session
	 *
	 * */
	@OnClose
	public void onClose(Session session)
	{
		// Get the client name that closed connection
		String name = nameSessionMap.get(session.getId());
		ChatSpace chat = findChat(session.getId());

		// Get all live sessions of that chat
		Set<Session> sessions = chatSessionMap.get(chat);

		// Remove the session from sessions list
		sessions.remove(session);

		// Notify all the clients about person exit
		sendMessageToAll(chat, session.getId(), name, " has left the chatspace.", false, true);
	}

	/**
	 * Called on message reception from any client
	 *
	 * @param message JSON message from user
	 * */
	@OnMessage
	public void onMessage(String message, Session session)
	{
		String msg = null;

		try
		{
			JSONObject jObj = new JSONObject(message);
			msg = jObj.getString("message");
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		// Send the message to all clients of particular chat space
		ChatSpace chat = findChat(session.getId());
		sendMessageToAll(chat, session.getId(), nameSessionMap.get(session.getId()),msg, false, false);
	}

	/***
	 * Called when a socket connection is opened to a server.
	 *
	 * @param session Chat session
	 */
	@OnOpen
	public void onOpen(Session session)
	{
		Map<String, String> queryParams = getQueryMap(session.getQueryString());

		String name = "";
		double latitude;
		double longitude;

		if (queryParams.containsKey("name")
				&& queryParams.containsKey("latitude")
				&& queryParams.containsKey("longitude"))
		{
			latitude = new Double(queryParams.get("latitude"));
			longitude = new Double(queryParams.get("longitude"));

			// Take name from GET parameter
			name = queryParams.get("name");
			try
			{
				name = URLDecoder.decode(name, "UTF-8");
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}

			Haversine h = new Haversine();
			boolean chatFound = false;
			ChatSpace existingChat = null;

			for (ChatSpace c : chats)
			{
				// Calculate the distance between current user location and existing chat
				double distance = h.calculateDistance(latitude, longitude, c.latitude, c.longitude);
				if (distance <= DISTANCE)
				{
					chatFound = true;
					existingChat = c;
					break;
				}
			}

			if (chatFound)
			{
				Set<Session> sessionSet = chatSessionMap.get(existingChat);

				// Mapping client name and session id
				nameSessionMap.put(session.getId(), name);

				// Adding session to a particular chat space
				sessionSet.add(session);

				// Send details about the session to a user
				try
				{
					session.getBasicRemote().sendText(jsonUtils.createDetailsJsonMsg(session.getId(), "Session details"));
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

				sendMessageToAll(existingChat, session.getId(), name, " has joined the chat space!", true,false);
			}
			else
			{
				nameSessionMap.put(session.getId(), name);
				ChatSpace newChat = new ChatSpace(latitude, longitude);
				chats.add(newChat);

				Set<Session> sessionSet = Collections.synchronizedSet(new HashSet<Session>());
				sessionSet.add(session);

				chatSessionMap.put(newChat, sessionSet);

				// Send details about the session to a user
				try
				{
					session.getBasicRemote().sendText(jsonUtils.createDetailsJsonMsg(session.getId(), "Session details"));
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Method to send message to all clients
	 *
	 * @param sessionId
	 * @param message message to be sent to clients
	 * @param isNewClient flag to identify that message is about new person joined
	 * @param isExit flag to identify that a person left the conversation
	 * */
	private void sendMessageToAll(ChatSpace chat, String sessionId, String name, String message, boolean isNewClient, boolean isExit)
	{
		Set<Session> sessions = chatSessionMap.get(chat);

		for (Session s : sessions)
		{
			String json = null;

			if (isNewClient)
			{
				json = jsonUtils.createNewUserJsonMsg(sessionId, name, message,sessions.size());

			}
			else if (isExit)
			{
				// Checking if the person has left the conversation
				json = jsonUtils.createExitJsonMsg(sessionId, name, message,sessions.size());
			}
			else
			{
				// Normal chat conversation message
				json = jsonUtils.createSendToAllJsonMsg(sessionId, name, message);
			}

			try
			{
				System.out.println("Sending message to: " + sessionId + ", " + json);
				s.getBasicRemote().sendText(json);
			}
			catch (IOException e)
			{
				System.out.println("Error in sending. " + s.getId() + ", " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}