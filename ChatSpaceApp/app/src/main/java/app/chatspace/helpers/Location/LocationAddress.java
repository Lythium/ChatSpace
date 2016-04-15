package app.chatspace.helpers.Location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/***
 * Responsible for providing methods to deal with location address.
 *
 * @author Misels Kaporins
 */
public class LocationAddress
{
    // Tag for the LogCat
    private static final String TAG = LocationAddress.class.getSimpleName();

    /***
     * Retrieves the address data based on provided latitude and longitude
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @param context the Context of the calling Activity
     * @return
     */
    public String getAddressFromLocation(final double latitude, final double longitude, final Context context)
    {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = null;
        try
        {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0)
            {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                result = sb.toString();
            }
        }
        catch (IOException e)
        {
            Log.e(TAG, "Unable to connect to Geocoder", e);
        }

        return result;
    }
}
