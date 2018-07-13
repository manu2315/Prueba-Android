package com.example.asus.prueba_android.Activities;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus.prueba_android.Classes.Common;
import com.example.asus.prueba_android.Interfaces.IGoogleApi;
import com.example.asus.prueba_android.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    private List<LatLng> polylineList;
    private Marker marker;
    private float v;
    private double lat,lng;
    private Handler handler;
    private LatLng startPos,endPos;
    private int index,next;
    private Button btnGo;
    private EditText txtMyLocation, txtDestination;
    private String sMyLocation, sDestination;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline,greyPolyline;

    private LatLng myLocation;

    IGoogleApi mService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
        bindUI();
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sDestination = txtDestination.getText().toString();
                sMyLocation = txtMyLocation.getText().toString();
                sDestination = sDestination.replace(" ","+");
                sMyLocation = sMyLocation.replace(" ","+");
                mapFragment.getMapAsync(MapsActivity.this);
            }
        });

        mService = Common.getGoogleAPi();
    }

    private void bindUI(){
        polylineList = new ArrayList<>();
        btnGo = (Button) findViewById(R.id.btnGo);
        txtDestination = (EditText)findViewById(R.id.txtDestination);
        txtMyLocation = (EditText)findViewById(R.id.txtMyLocation);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Sydney and move the camera
        final LatLng sydney = new LatLng(24.14222, -110.31083);
        mMap.addMarker(new MarkerOptions().position(sydney).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
        .target(googleMap.getCameraPosition().target)
        .zoom(17)
        .bearing(30)
        .tilt(45)
        .build()));

        String requestUrl = null;
        try{
            requestUrl = "https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=driving&"+
                    "transit_routing_preference=less_driving&"+
                    "origin="+sydney.latitude+","+sydney.longitude+"&"+
                    "destination="+sDestination+"&"+
                    "key="+getResources().getString(R.string.google_direction_key);
            Log.d("URL",requestUrl);
            mService.getDataFromGoogleApi(requestUrl)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            try{

                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                JSONArray jsonArray = jsonObject.getJSONArray("routes");
                                for (int i=0; i<jsonArray.length();i++){
                                    JSONObject route = jsonArray.getJSONObject(i);
                                    JSONObject poly = route.getJSONObject("overview_polyline");
                                    String polyline = poly.getString("points");
                                    polylineList = decodePoly(polyline);



                                }

                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for(LatLng latLng: polylineList)
                                    builder.include(latLng);
                                LatLngBounds bounds = builder.build();
                                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,2);
                                mMap.animateCamera(mCameraUpdate);

                                polylineOptions = new PolylineOptions();
                                polylineOptions.color(Color.GRAY);
                                polylineOptions.width(5);
                                polylineOptions.startCap(new SquareCap());
                                polylineOptions.endCap(new SquareCap());
                                polylineOptions.jointType(JointType.ROUND);
                                polylineOptions.addAll(polylineList);
                                greyPolyline = mMap.addPolyline(polylineOptions);


                                blackPolylineOptions = new PolylineOptions();
                                blackPolylineOptions.color(Color.BLACK);
                                blackPolylineOptions.width(5);
                                blackPolylineOptions.startCap(new SquareCap());
                                blackPolylineOptions.endCap(new SquareCap());
                                blackPolylineOptions.jointType(JointType.ROUND);
                                blackPolylineOptions.addAll(polylineList);
                                blackPolyline = mMap.addPolyline(blackPolylineOptions);

                                mMap.addMarker(new MarkerOptions().position(polylineList.get(polylineList.size()-1)));

                                //Animacion

                                final ValueAnimator polylineAnimator = ValueAnimator.ofInt(0,100);
                                polylineAnimator.setDuration(2000);
                                polylineAnimator.setInterpolator(new LinearInterpolator());
                                polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        List<LatLng> points = greyPolyline.getPoints();
                                        int percentValue =(int) animation.getAnimatedValue();
                                        int size = points.size();
                                        int newPoints= (int) (size * (percentValue/100.0f));
                                        List<LatLng> p = points.subList(0, newPoints);
                                        blackPolyline.setPoints(p);

                                    }
                                });
                                polylineAnimator.start();
                                marker = mMap.addMarker(new MarkerOptions().position(sydney)
                                .flat(true)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));

                                handler = new Handler();
                                index = -1;
                                next =1;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(index < polylineList.size()-1){
                                            index++;
                                            next = index+1;

                                        }
                                        if(index < polylineList.size()-1){
                                            startPos = polylineList.get(index);
                                            endPos= polylineList.get(next);
                                        }
                                        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0,1);
                                        valueAnimator.setDuration(3000);
                                        valueAnimator.setInterpolator(new LinearInterpolator());
                                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                            @Override
                                            public void onAnimationUpdate(ValueAnimator animation) {
                                                v = animation.getAnimatedFraction();
                                                lng = v*endPos.longitude+(1-v)
                                                        *startPos.longitude;
                                                lat = v*endPos.latitude+(1-v)
                                                        *startPos.latitude;

                                                LatLng newPos = new LatLng(lat,lng);
                                                marker.setPosition(newPos);
                                                marker.setAnchor(0.5f,0.5f);
                                                marker.setRotation(getBearing(startPos,newPos));
                                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                                .target(newPos)
                                                .zoom(15.5f)
                                                .build()));

                                            }
                                        });

                                        valueAnimator.start();
                                        handler.postDelayed(this,3000);
                                    }
                                },3000);



                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                            Toast.makeText(MapsActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private float getBearing(LatLng startPos, LatLng newPos) {
        double lat = Math.abs(startPos.latitude - newPos.latitude);
        double lng = Math.abs(startPos.longitude-newPos.longitude);
        if(startPos.latitude< newPos.latitude && startPos.longitude<newPos.longitude){
            return (float) (Math.toDegrees(Math.atan(lng/lat)));
        }
        else if(startPos.latitude >= newPos.latitude && startPos.longitude<newPos.longitude) {
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        }
        else if(startPos.latitude >= newPos.latitude && startPos.longitude>=newPos.longitude) {
            return (float) (Math.toDegrees(Math.atan(lng / lat))+180);
        }
        else if(startPos.latitude< newPos.latitude && startPos.longitude>=newPos.longitude) {
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        }
        return -1;
    }

    private List<LatLng> decodePoly(String encoded) {
        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

}
