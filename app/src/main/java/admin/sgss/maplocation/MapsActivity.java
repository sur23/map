package admin.sgss.maplocation;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    Location mLastLocation;
    FusedLocationProviderClient mFusedLocationClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=1;
    private static boolean state = false;
    private float DEFAULT_ZOOM=5;
    LatLng current_latlong;
    LatLng  mumbai_airport_latlong;
    LatLng  chennai_airport_latlong;
    BitmapDescriptor bitmapDescriptor;
    private List<Address> addresses;
    double mumbai_airport_latitude=19.0896;
    double mumbai_airport_longitude=72.8656;
    double chennai_airport_latitude=12.9941;
    double chennai_airport_longitude=80.1709;
    double current_longitude=0.0;
    double current_latitude=0.0;
    String title=null;
    ArrayList<LatLng> MarkerPoints1;
    ArrayList<LatLng> MarkerPoints2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Initializing
        MarkerPoints1 = new ArrayList<>();
        // Initializing
        MarkerPoints2 = new ArrayList<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        state = NetworkUtil.getConnectivityStatusString(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if(!state)
        {
            Toast.makeText(getApplicationContext(),"No Internet Connection!",Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void showMumbaiAirportLocation(LatLng current_latlong){
        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        bitmapDescriptor
                = BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_RED);
        // Add a marker in Mumbai Airport and move the camera
        mumbai_airport_latlong = new LatLng(mumbai_airport_latitude,mumbai_airport_longitude);
        try {
            addresses = geocoder.getFromLocation(mumbai_airport_latitude,mumbai_airport_longitude, 1); //1 num of possible location returned
            String address = addresses.get(0).getAddressLine(0); //0 to obtain first possible address
            //create your custom title
            title = address;
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMap.addMarker(new MarkerOptions().position(mumbai_airport_latlong).icon(bitmapDescriptor).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mumbai_airport_latlong, DEFAULT_ZOOM));
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Getting URL to the Google Directions API
          String url = getUrl(current_latlong, mumbai_airport_latlong);
          Log.d("onMapClick", url.toString());
          FetchUrlForMumbai FetchUrl = new FetchUrlForMumbai();
        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);

    }

    private void showChennaiAirportLocation(LatLng current_latlong){
        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        bitmapDescriptor
                = BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_BLUE);
        // Add a marker in Chennai Airport and move the camera
        chennai_airport_latlong = new LatLng(chennai_airport_latitude,chennai_airport_longitude);
        try {
            addresses = geocoder.getFromLocation(chennai_airport_latitude,chennai_airport_longitude, 1); //1 num of possible location returned
            String address = addresses.get(0).getAddressLine(0); //0 to obtain first possible address
            //create your custom title
            title = address;
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMap.addMarker(new MarkerOptions().position(chennai_airport_latlong).icon(bitmapDescriptor).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chennai_airport_latlong, DEFAULT_ZOOM));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Getting URL to the Google Directions API
        String url = getUrl(current_latlong, chennai_airport_latlong);
        Log.d("onMapClick", url.toString());
        FetchUrlForChennai FetchUrl = new FetchUrlForChennai();

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);

    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */

        bitmapDescriptor
                = BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_GREEN);
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationClient.getLastLocation();
                final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    public List<Address> addresses;

                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastLocation = (Location) task.getResult();
                            current_latitude=mLastLocation.getLatitude();
                            current_longitude= mLastLocation.getLongitude();
                            current_latlong = new LatLng(current_latitude,current_longitude);
                            try {
                                addresses = geocoder.getFromLocation(current_latitude,current_longitude, 1); //1 num of possible location returned
                                String address = addresses.get(0).getAddressLine(0); //0 to obtain first possible address
                                //create your custom title
                                title = address;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            mMap.addMarker(new MarkerOptions().position(current_latlong).icon(bitmapDescriptor).title(title));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_latlong, DEFAULT_ZOOM));
                            mMap.getUiSettings().setZoomControlsEnabled(true);

                            // Draw Polyline function from current location to mumbai airport and chennai airport
                            showMumbaiAirportLocation(current_latlong);
                            showChennaiAirportLocation(current_latlong);

                        } else {
                            LatLng default_latlong = new LatLng(20.5937,78.9629);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(default_latlong, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    // Fetches data from url passed
    private class FetchUrlForMumbai extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTaskForMumbai parserTask = new ParserTaskForMumbai();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    // Fetches data from url passed
    private class FetchUrlForChennai extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTaskForChennai parserTask = new ParserTaskForChennai();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }



    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTaskForMumbai extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTaskForChennai extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLUE);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }


}
