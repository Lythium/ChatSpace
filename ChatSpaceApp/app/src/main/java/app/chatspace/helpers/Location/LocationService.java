package app.chatspace.helpers.Location;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import app.chatspace.helpers.SettingKeys;

/***
 * Service class that enables communication between system location services and calling Activity.
 *
 * @author Misels Kaporins
 */
public class LocationService extends Service implements LocationListener
{
    protected LocationManager locationManager;
    private Location location;

    /***
     * Constructs the LocationService object
     * @param context context of the calling Activity
     */
    public LocationService(Context context)
    {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
    }

    /***
     * Gets current user location
     * @param provider LocationManager provider
     * @return location or null
     */
    public Location getLocation(String provider)
    {
        if (locationManager.isProviderEnabled(provider))
        {
            locationManager.requestLocationUpdates(provider, SettingKeys.MIN_TIME_FOR_UPDATE, SettingKeys.MIN_DISTANCE_FOR_UPDATE, this);
            if (locationManager != null)
            {
                location = locationManager.getLastKnownLocation(provider);
                return location;
            }
        }

        return null;
    }

    @Override
    public void onLocationChanged(Location location)
    {
    }

    @Override
    public void onProviderDisabled(String provider)
    {
    }

    @Override
    public void onProviderEnabled(String provider)
    {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }
}
