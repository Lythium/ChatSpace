package chat.space.chat;

/***
 * Haversine class that is used for distance calculation between two points.
 *
 * @author Misels Kaporins
 *
 */
public class Haversine
{
	private final static int R = 6371; // Earth radius in km

	/***
	 * Calculates the distance based on haversine formula
	 * @param lat1 Latitude of the first chat user
	 * @param long1 Longitude of the first chat user
	 * @param lat2 Latitude of the second chat user
	 * @param long2 Latitude of the second chat user
	 * @return Distance between two users
	 */
	public double calculateDistance(double lat1, double long1, double lat2, double long2)
	{
		double latDistance = toRadians(lat2-lat1);
		double longDistance = toRadians(long2-long1);
		double a = Math.sin(latDistance / 2) *
				Math.sin(latDistance / 2) +
				Math.cos(toRadians(lat1)) *
				Math.cos(toRadians(lat2)) *
				Math.sin(longDistance / 2) *
				Math.sin(longDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

		return R * c;
	}

	/**
	 * Transforms value to radians
	 * @param value
	 * @return
	 */
	private double toRadians(double value)
	{
		return value * Math.PI / 180;
	}
}
