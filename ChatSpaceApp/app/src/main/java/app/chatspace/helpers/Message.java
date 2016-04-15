package app.chatspace.helpers;

/**
 * Represents an instance of a chat message.
 *
 * @author Misels Kaporins
 */
public class Message
{
    private String sender, message;
    private boolean ownMsg;

    /***
     * Constructs a Message object.
     * @param sender message sender
     * @param message message text
     * @param ownMsg own message identifier, true if it own message
     */
    public Message(String sender, String message, boolean ownMsg)
    {
        this.sender = sender;
        this.message = message;
        this.ownMsg = ownMsg;
    }

    /***
     * Returns the message text.
     * @return message text
     */
    public String getMessage()
    {
        return message;
    }

    /***
     * Sets the text of a message.
     * @param message text of the message
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /***
     * Returns message sender.
     * @return sender of the message
     */
    public String getSender()
    {
        return sender;
    }

    /***
     * Checks for own message
     * @return true if it is own message
     */
    public boolean isOwnMsg()
    {
        return ownMsg;
    }

    /***
     * Sets own message flag to the message
     * @param isOwn own message identifier
     */
    public void setOwnMsg(boolean isOwn)
    {
        this.ownMsg = isOwn;
    }

    /***
     * Sets the sender of the message
     * @param sender message sender
     */
    public void setSender(String sender)
    {
        this.sender = sender;
    }
}
