package app.chatspace;

import android.os.Bundle;

import app.chatspace.helpers.Utilities.ByteUtil;
import app.chatspace.helpers.ChatListAdapter;
import app.chatspace.helpers.Message;
import app.chatspace.helpers.Utilities.ChatUtil;
import app.chatspace.helpers.SettingKeys;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

public class ChatActivity extends ActionBarActivity
{
    private Button btnSend;
    private EditText inputMsg;

    private Future<WebSocket> client;

    private ChatListAdapter adapter;
    private List<Message> listMessages;
    private ListView listViewMessages;

    private ChatUtil chatUtil;

    // User nickname
    private String name = null;
    private double latitude;
    private double longitude;

    // JSON flags to identify the type of JSON message
    private static final String TAG_NEW = "new",
            TAG_MESSAGE = "message",
            TAG_OWN = "self",
            TAG_EXIT = "exit";

    // Tag for the LogCat
    private static final String TAG = ChatActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        btnSend = (Button) findViewById(R.id.btnSend);
        inputMsg = (EditText) findViewById(R.id.inputMsg);
        listViewMessages = (ListView) findViewById(R.id.list_view_messages);

        chatUtil = new ChatUtil(getApplicationContext());

        // Getting the user name, latitude and longitude from the Main Activity
        Intent i = getIntent();
        name = i.getStringExtra("name");
        latitude = i.getDoubleExtra("latitude", 0.0);
        longitude = i.getDoubleExtra("longitude", 0.0);

        listMessages = new ArrayList<Message>();
        adapter = new ChatListAdapter(this, listMessages);
        listViewMessages.setAdapter(adapter);

        Log.i(TAG, "Logged with latitude: " + String.valueOf(latitude) + " and longitude: " + String.valueOf(longitude));
        initiateSocketConnection(latitude, longitude);

        btnSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendMessageToServer(chatUtil.prepareJSON(inputMsg.getText().toString()));
                inputMsg.setText("");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.action_quit)
        {
            finish();
            //System.exit(0);
        }
        else if(item.getItemId() == R.id.action_about)
        {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /***
     * Creates a web socket connection with a chat server
     * @param latitude
     * @param longitude
     */
    private void initiateSocketConnection(double latitude, double longitude)
    {
        try
        {
            client = AsyncHttpClient.getDefaultInstance().websocket(
                    SettingKeys.SEVER_URL + "?name=" + URLEncoder.encode(name, SettingKeys.ENCODING) + "&latitude=" + String.valueOf(latitude) + "&longitude=" + String.valueOf(longitude),
                    "my-protocol",
                    new AsyncHttpClient.WebSocketConnectCallback()
                    {
                        @Override
                        public void onCompleted(Exception ex, WebSocket webSocket)
                        {
                            if (ex != null)
                            {
                                Log.e(TAG, ex.toString());
                                return;
                            }

                            webSocket.setStringCallback(new WebSocket.StringCallback()
                            {
                                public void onStringAvailable(String s)
                                {
                                    Log.d(TAG, String.format("Got string message! %s", s));
                                    parseMessage(s);
                                }
                            });

                            webSocket.setDataCallback(new DataCallback()
                            {
                                public void onDataAvailable(DataEmitter emitter, ByteBufferList byteBufferList)
                                {
                                    Log.d(TAG, String.format("I got some bytes! %s", ByteUtil.bytesToHex(byteBufferList.getAllByteArray())));

                                    // Message will be in JSON format
                                    parseMessage(ByteUtil.bytesToHex(byteBufferList.getAllByteArray()));

                                    byteBufferList.recycle();
                                }
                            });
                        }
                    });
        }
        catch (UnsupportedEncodingException e)
        {
            Log.e(TAG, e.toString());
        }
    }

    /***
     * Method to send message to a web socket server
     * @param message Message to send
     */
    private void sendMessageToServer(String message)
    {
        if (message.isEmpty())
        {
            showToast("Please enter your message first!");
        }

        WebSocket socket = client.tryGet();

        if (socket != null && socket.isOpen())
        {
            client.tryGet().send(message);
        }
        else
        {
            showToast("Oops, something went wrong. Looks like our server is offline.");
            Log.w(TAG, "WebSocket client is not open or null.");
        }
    }

    /**
     * Parses JSON message received from a web socket server.
     * @param msg received JSON message
     */
    private void parseMessage(final String msg)
    {
        try
        {
            JSONObject jObj = new JSONObject(msg);
            String flag = jObj.getString("flag");

            // Message belongs to a current user
            if (flag.equalsIgnoreCase(TAG_OWN))
            {
                String sessionId = jObj.getString("sessionId");
                chatUtil.saveSessionId(sessionId);

                Log.i(TAG, "Session id: " + chatUtil.getSessionId());
            }
            // New message received in a chat space
            else if (flag.equalsIgnoreCase(TAG_MESSAGE))
            {
                String message = jObj.getString("message");
                String sessionId = jObj.getString("sessionId");
                String fromName = jObj.getString("name");
                boolean isSelf = false;

                if (sessionId.equals(chatUtil.getSessionId()))
                {
                    fromName = name;
                    isSelf = true;
                }

                Message m = new Message(fromName, message, isSelf);
                appendMessage(m, isSelf);
            }
            // New person has joined a chat space
            else if (flag.equalsIgnoreCase(TAG_NEW))
            {
                String onlineCount = jObj.getString("onlineCount");

                showToast("You have joined your local chat space! There are " + onlineCount + " people around.");
            }
            // Person has left a chatspace
            else if (flag.equalsIgnoreCase(TAG_EXIT))
            {
                String name = jObj.getString("name");
                String message = jObj.getString("message");

                showToast(name + message);
            }
        }
        catch (JSONException e)
        {
            // Parsing exception has occured
            Log.e(TAG, e.toString());
        }
    }

    /***
     * Appends a message to a chat view
     * @param msg message to append to a chat
     * @param isSelf indicates whether it is own message
     */
    private void appendMessage(final Message msg, final boolean isSelf)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                listMessages.add(msg);

                adapter.notifyDataSetChanged();

                if (!isSelf)
                {
                    notificationSound();
                }
            }
        });
    }

    /***
     * Shows a toast message to a user
     * @param message message to show
     */
    private void showToast(final String message)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    /***
     * Plays device sound notification
     */
    public void notificationSound()
    {
        try
        {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        WebSocket socket = client.tryGet();

        if (socket != null && socket.isOpen())
        {
            socket.close();
        }

        Log.d(TAG, "Sockets session was not active when activity was destroyed.");
    }
}