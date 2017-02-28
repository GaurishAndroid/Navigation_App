package com.example.abhisheki.myapplication;


import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;


import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import android.os.AsyncTask;
import android.util.Log;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;


import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,OnStreetViewPanoramaReadyCallback {

    private GoogleMap mMap;
    private StreetViewPanorama pPan;

    protected static final int RESULT_SPEECH1 = 1;
    protected static final int RESULT_SPEECH2 = 2;
    protected static final String googleKey = "AIzaSyDt_xmcWSdCouFtqKsnwKlnJaihEa0jYKI";
    protected static final float zoomLevel = 10.0f;


    private static final CharSequence[] MAP_TYPE_ITEMS =
            {"Road Map", "Hybrid", "Satellite", "Terrain"};


    MyDbHandler mydbhandler;
    ReadTask downloadTask = null;
    Route toSaveRoute     = null;
    LatLng StartPosition  = null;
    boolean IsNormal      = false;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mydbhandler = new MyDbHandler(this,null,null,1);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

        /** Add all button listners **/
        final ImageButton buttonSearch = (ImageButton) findViewById(R.id.btnsearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSearch();
            }
        });

        final ImageButton buttonRoute = (ImageButton) findViewById(R.id.btnRoute);
        buttonRoute.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onOfflineRoute();
            }
        });

        final ImageButton buttonSave = (ImageButton) findViewById(R.id.btnSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSave();
            }
        });

        final ImageButton btnVoiceFrom = (ImageButton) findViewById(R.id.btnVoiceOrigin);
        btnVoiceFrom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startVoiceFrom(btnVoiceFrom);
            }
        });

        final ImageButton btnVoiceTo = (ImageButton) findViewById(R.id.btnVoiceDest);
        btnVoiceTo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startVoiceFrom(btnVoiceTo);
            }
        });

        final Button buttonType = (Button) findViewById(R.id.btnType);
        buttonType.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onType();
            }
        });

        final Button buttonStreet = (Button) findViewById(R.id.btnStreet);
        buttonStreet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onStreetToggle();
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        onStreetToggle();
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
        pPan =  panorama;
        //panorama.setPosition(new LatLng(40.6927302,-73.9843212));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                // TODO Auto-generated method stub
                //lstLatLngs.add(point);
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(point));
            }
        });

        try {
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  }, 12 );

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                   ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                 mMap.setMyLocationEnabled(true);
            }


        } catch (Exception exp) {
            Toast.makeText(this, exp.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Searches the Origin and destination addresses and put marker
    */
    public void onSearch() {
        try {
            mMap.clear();
            EditText txtFrm = (EditText) findViewById(R.id.editOrigin);
            EditText txtTo = (EditText) findViewById(R.id.editDest);
            LatLng mysearch1 = null;
            LatLng mysearch2 = null;
            if (txtFrm != null || txtFrm.getText().toString() != "")
                mysearch1 = PutMarker(txtFrm.getText().toString());
            if (txtTo != null || txtTo.getText().toString() != "")
                mysearch2 = PutMarker(txtTo.getText().toString());

            if (mysearch1 != null && mysearch2 != null) {

                String url = getMapsApiDirectionsUrl(mysearch1, mysearch2, null);
                downloadTask = new ReadTask();
                downloadTask.execute(url);
                toSaveRoute = new Route();
                toSaveRoute.setOrigin(txtFrm.getText().toString());
                toSaveRoute.setDestination(txtTo.getText().toString());
                toSaveRoute.setOriginLat(mysearch1.latitude);
                toSaveRoute.setOriginLong(mysearch1.longitude);
                toSaveRoute.setDestLat(mysearch2.latitude);
                toSaveRoute.setDestLong(mysearch2.longitude);
                mMap.animateCamera( CameraUpdateFactory.zoomTo( zoomLevel ) );
                StartPosition = mysearch1;
            }
        } catch (Exception exp) {
            Toast.makeText(this, exp.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onOfflineRoute() {

        final CharSequence[] items = mydbhandler.GetOfflineRoutes(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Route");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                String strfind = items[item].toString();
                strfind = strfind.substring(0,strfind.indexOf("]"));
                EditText txtFrm = (EditText) findViewById(R.id.editOrigin);
                EditText txtTo = (EditText) findViewById(R.id.editDest);
                Route rt = mydbhandler.getRouteInfo(MapsActivity.this,strfind);
                StartPosition = new LatLng(rt.getOriginLat(),rt.getOriginLong());
                if(rt!=null) {
                    txtFrm.setText(rt.getOrigin());
                    txtTo.setText(rt.getDestination());
                    try {
                        mMap.clear();
                        setMarker(rt.getOriginLat(),rt.getOriginLong(),rt.getOrigin());
                        setMarker(rt.getDestLat(),rt.getDestLong(),rt.getDestination());
                        new ParserTask().execute(rt.getRoute());
                        mMap.animateCamera( CameraUpdateFactory.zoomTo( zoomLevel ) );
                    }
                    catch(Exception Exp)
                    {
                        Toast.makeText(MapsActivity.this, Exp.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Searh single basic address and put a marker
     * @param searchstr
     * @return
     */
    private LatLng PutMarker(String searchstr)
    {
        LatLng mysearch=null;
        if (searchstr != "") {
            List<Address> addresslist;
            Geocoder geocod = new Geocoder(this);
            Address adrs;
            try {
                addresslist = geocod.getFromLocationName(searchstr, 1);
                adrs = addresslist.get(0);
                mysearch = new LatLng(adrs.getLatitude(), adrs.getLongitude());
                mMap.addMarker(new MarkerOptions().position(mysearch).title(searchstr));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mysearch));
                return mysearch;
            } catch (Exception Exp) {
                Toast.makeText(this, Exp.getMessage(), Toast.LENGTH_SHORT).show();
                return mysearch;
            }

        }
        return mysearch;
    }

    public void setMarker(double  lat,double lng,String title)
    {

        LatLng mypos = new LatLng(lat,lng);
        mMap.addMarker(new MarkerOptions().position(mypos).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mypos));
    }

    /**
     * Change/toggle type of the Map
     */
    public void onType() {

        showMapTypeSelectorDialog();

      }

    public void onStreetToggle()
    {
        try
        {
            LinearLayout Top = (LinearLayout)findViewById(R.id.top);
            LinearLayout Bottom = (LinearLayout)findViewById(R.id.down);

            if(IsNormal)
            {
                Top.setVisibility(View.GONE);
                Bottom.setVisibility(View.VISIBLE);
                //Toast.makeText(this, String.valueOf(StartPosition.latitude) + "OOO" + String.valueOf(StartPosition.longitude)
                //        , Toast.LENGTH_SHORT).show();
                if(StartPosition!=null)
                    pPan.setPosition(StartPosition);
            }
            else
            {
                Bottom.setVisibility(View.GONE);
                Top.setVisibility(View.VISIBLE);
            }
            IsNormal = !IsNormal;

        } catch (Exception exp) {
            Toast.makeText(this, exp.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }
    /**
     * Save the Searched Route Information
     */
    public void onSave() {
        InsertRecord();
    }

    /**
     * Voice capture activity start
     * @param v
     */
    public void startVoiceFrom(View v) {
        Intent intent = new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        //Get the value using Intent from Speech
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

        try {
            switch(v.getId()) {
                case R.id.btnVoiceOrigin:
                    startActivityForResult(intent, RESULT_SPEECH1);
                    EditText txtTextFrm = (EditText) findViewById(R.id.editOrigin);
                    txtTextFrm.setText("");
                    break;
                case R.id.btnVoiceDest:
                    startActivityForResult(intent, RESULT_SPEECH2);
                    EditText txtTextTo = (EditText) findViewById(R.id.editDest);
                    txtTextTo.setText("");
                    break;
            }
        } catch (ActivityNotFoundException a) {
            //Show a Toast if the device is not supported
            Toast t = Toast.makeText(getApplicationContext(),
                    "Your device doesn't support Speech to Text",
                    Toast.LENGTH_SHORT);
            t.show();
        }
    }

    /** Write the code for the “onActivityResult” method so that when you speak something it will be converted into text automatically and vice versa. */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            ArrayList<String> text = null;
            EditText txtText=null;

            if (resultCode == RESULT_OK && null != data) {

                text = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
            }

            //check the requestCode as a case
            switch (requestCode) {
                case RESULT_SPEECH1:
                    txtText = (EditText) findViewById(R.id.editOrigin);
                    break;
                case RESULT_SPEECH2:
                    txtText = (EditText) findViewById(R.id.editDest);
                    break;
            }

            txtText.setText(text.get(0));
        }
        catch(Exception Exp){ Toast t = Toast.makeText(getApplicationContext(),
                Exp.getMessage(),
                Toast.LENGTH_SHORT);}
}

    /**
     * Prepare the URL to be sent to Google
     * @param origin
     * @param destination
     * @param betweenpoints
     * @return
     */
    private String getMapsApiDirectionsUrl(LatLng origin, LatLng destination, LatLng[] betweenpoints) {
        String waypoints = "origin="
                + origin.latitude + "," + origin.longitude + "&destination="
                + destination.latitude + "," + destination.longitude  ;

        MarkerOptions options = new MarkerOptions();
        options.position(origin);
        options.position(destination);
        mMap.addMarker(options);

        if(betweenpoints!=null)
            for (int i = 0; i < betweenpoints.length; i++) {
                if(i==0) waypoints += "&waypoints=optimize:true";
                waypoints += "|";
                waypoints += betweenpoints[i].latitude + "," + betweenpoints[0].longitude;
                options.position(betweenpoints[i]);
            }

        String params = waypoints;
        String output = "json";
        String mode = "mode=driving";
        String key = "key=" +  googleKey; //"AIzaSyDt_xmcWSdCouFtqKsnwKlnJaihEa0jYKI";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + params + "&" + mode + "&" + key;

        //url = "https://maps.googleapis.com/maps/api/directions/json?origin=LOWER%20MANHATTAN&destination=BOWERY&mode=driving&key=AIzaSyDt_xmcWSdCouFtqKsnwKlnJaihEa0jYKI";
        //url = "https://maps.googleapis.com/maps/api/directions/json?origin=40.722543,-73.998585&destination=40.7064,-74.0094&" +
        //        "waypoints=optimize:true|40.7057,-73.9964&key=AIzaSyDt_xmcWSdCouFtqKsnwKlnJaihEa0jYKI";
        return url;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    /**
     * Actually Insert record in the database using Db handler
     */
    private void InsertRecord()
    {
        try {
                toSaveRoute.setRoute(downloadTask.resultofdirection);
                mydbhandler.AddRoute(toSaveRoute);
                Toast.makeText(this, "Route Saved!", Toast.LENGTH_SHORT).show();
        }
        catch(Exception Exp)
        { Toast.makeText(this, Exp.getMessage().toString(), Toast.LENGTH_SHORT).show();}
    }

    private void showMapTypeSelectorDialog() {
        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "Select Map Type";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(fDialogTitle);

        // Find the current map type to pre-check the item representing the current state.
        int checkItem = mMap.getMapType() - 1;

        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.

                        // Perform an action depending on which item was selected.
                        switch (item) {
                            case 1:
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            case 2:
                                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            case 3:
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;
                            default:
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }
                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    private class ReadTask extends AsyncTask<String, Void, String> {

        public String resultofdirection="";

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            resultofdirection = result;
            new ParserTask().execute(result);
        }


    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {


        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            try {
                // traversing through routes
                for (int i = 0; i < routes.size(); i++) {
                    points = new ArrayList<>();
                    polyLineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = routes.get(i);
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    polyLineOptions.addAll(points);
                    polyLineOptions.width(4);
                    polyLineOptions.color(Color.BLUE);
                }

                mMap.addPolyline(polyLineOptions);
            }
            catch(Exception Exp) {
                Toast.makeText(MapsActivity.this, Exp.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}