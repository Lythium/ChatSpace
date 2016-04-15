package chat.space.chat;

/***
 * Represents a chat particular space
 *
 * @author Misels Kaporins
 *
 */
public class ChatSpace
{
	public int chatId;
	public double latitude;
	public double longitude;

	/***
	 * Creates an instance of ChatSpace object
	 */
	public ChatSpace()
	{
	}
	/***
	 * Creates an instance of a chat space based on provided location
	 * @param latitude latitude of the location
	 * @param longitude longitude of the location
	 */
	public ChatSpace(double latitude, double longitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
	}
}