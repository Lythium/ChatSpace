package app.chatspace.helpers;

import app.chatspace.R;
import app.chatspace.helpers.Message;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/***
 * Class that acts as a bridge between AdapterView and the chat messages for the view.
 *
 * @author Misels Kaporins
 */
public class ChatListAdapter extends BaseAdapter
{
    private Context context;
    private List<Message> messagesItems;

    /***
     * Constructs an instance of ChatListAdapter
     * @param context context of calling Activity
     * @param navDrawerItems list of Navigation Drawer items
     */
    public ChatListAdapter(Context context, List<Message> navDrawerItems)
    {
        this.context = context;
        this.messagesItems = navDrawerItems;
    }

    @Override
    public int getCount()
    {
        return messagesItems.size();
    }

    @Override
    public Object getItem(int position)
    {
        return messagesItems.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Message m = messagesItems.get(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // Check whether I am the users is the message owner, if so, align it to the right, otherwise to the left
        if (messagesItems.get(position).isOwnMsg())
        {
            convertView = mInflater.inflate(R.layout.msg_right, null);
        }
        else
        {
            convertView = mInflater.inflate(R.layout.msg_left, null);
        }

        TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
        TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);

        txtMsg.setText(m.getMessage());
        lblFrom.setText(m.getSender());

        return convertView;
    }
}