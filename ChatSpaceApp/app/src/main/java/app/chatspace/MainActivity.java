package app.chatspace;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import app.chatspace.helpers.Location.LocationAddress;
import app.chatspace.helpers.Location.LocationService;

public class MainActivity extends Activity
{
    private Button btnJoin;
    private TextView txtAddress;
    private EditText txtName;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        txtName = (EditText) findViewById(R.id.name);
        txtAddress = (TextView) findViewById(R.id.address);
        btnJoin = (Button) findViewById(R.id.btnJoin);
        getLocation();

        btnJoin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (txtName.getText().toString().trim().length() > 0)
                {
                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);

                    String name = txtName.getText().toString().trim();
                    intent.putExtra("name", name);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);

                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please enter your chat nickname!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /***
     * Gets user location and updates the view
     */
    private void getLocation()
    {
        LocationService appLocationService = new LocationService(MainActivity.this);

        Location gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER);
        Location networkLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);

        if (gpsLocation != null)
        {
            latitude = gpsLocation.getLatitude();
            longitude = gpsLocation.getLongitude();

            LocationAddress locationAddress = new LocationAddress();
            String result = locationAddress.getAddressFromLocation(latitude, longitude, getApplicationContext());
            txtAddress.setText(result);
        }
        else if (networkLocation != null)
        {
            latitude = networkLocation.getLatitude();
            longitude = networkLocation.getLongitude();

            LocationAddress locationAddress = new LocationAddress();
            String result = locationAddress.getAddressFromLocation(latitude, longitude, getApplicationContext());
            txtAddress.setText(result);
        }
        else
        {
            enableLocation();
        }
    }

    /***
     * Shows the alert asking to enable a location
     */
    public void enableLocation()
    {
        AlertDialog.Builder dialogBox = new AlertDialog.Builder(MainActivity.this);
        dialogBox.setTitle("Your location cannot be retrieved.");
        dialogBox.setMessage("Please enable location provider to successfully use ChatSpace. Go to settings menu?");
        dialogBox.setPositiveButton("Yes",
                                      new DialogInterface.OnClickListener()
                                      {
                                          public void onClick(DialogInterface dialog, int which)
                                          {
                                              Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                              MainActivity.this.startActivity(intent);
                                          }
                                      });
        dialogBox.setNegativeButton("No",
                                      new DialogInterface.OnClickListener()
                                      {
                                          public void onClick(DialogInterface dialog, int which)
                                          {
                                              dialog.cancel();
                                              System.exit(0);
                                          }
                                      });
        dialogBox.show();
    }
}