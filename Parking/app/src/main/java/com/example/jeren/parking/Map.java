package com.example.jeren.parking;

/**
 * Created by jeren on 2017/2/5.
 */

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.zip.Inflater;

import Module.DirectionFinder;
import Module.DirectionFinderListener;
import Module.IconGenerator;
import Module.Parking;
import Module.Route;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;

import static android.content.ContentValues.TAG;

public  class Map extends Fragment implements OnMapReadyCallback {

    GoogleMap mMap;
    MapView mMapView;
    View rootView;
    private Button btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private String destination;
    private String date;
    private String depart_time;
    private LatLng destinationcoord;
    private ArrayList<Integer> list_parking;
    int rayon;
    ArrayList<Integer> parkingsAvecTR;
    ArrayList<String> etat_parkingsAvecTR;
    HashMap MarkerCoord = new HashMap();
    HashMap MarkerNom = new HashMap();
    HashMap MarkerCap = new HashMap();
    HashMap MarkerGestionnaire = new HashMap();
    HashMap MarkerIndex = new HashMap();
    RelativeLayout relativeLayout;
    TextView adressepopup;
    TextView nompopup;
    TextView capacitepopup;
    TextView gestionnairepopup;
    ImageView LaunchGoogle;
    Marker selectedMarker;
    String selectedMarkerAdress;
    Marker destinationMarker;

    public Map() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mapfragment, container, false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            /*
            btnFindPath = (Button) rootView.findViewById(R.id.btnFindPath);
            etOrigin = (EditText) rootView.findViewById(R.id.etOrigin);
            etDestination = (EditText) rootView.findViewById(R.id.etDestination);

            // Get the intent extra params
               Intent intent = getActivity().getIntent();
                destination = intent.getStringExtra("destination");
                date = intent.getStringExtra("date");
                depart_time = intent.getStringExtra("depart_time");
            */
        // Create a textview for test
               /*
            TextView test = (TextView) rootView.findViewById(R.id.Test);
            test.setText("Destination : " + destination + " " + date + " " + depart_time);
            btnFindPath.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendRequest();
                }
            });
            */
        Intent intent = getActivity().getIntent();
        destination = intent.getStringExtra("destination");
        date = intent.getStringExtra("date");
        depart_time = intent.getStringExtra("depart_time");
        rayon = intent.getExtras().getInt("rayon");
        parkingsAvecTR = intent.getIntegerArrayListExtra("parkingsAvecTR");
        etat_parkingsAvecTR = intent.getStringArrayListExtra("etat_parkingsAvecTR");
        list_parking = intent.getIntegerArrayListExtra("list_parking");
        mMapView = (MapView) rootView.findViewById(R.id.mapfrag);
        System.out.println("From Map: etat");
        System.out.println(etat_parkingsAvecTR);
        return rootView;


    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getActivity());
        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        destinationcoord = getLocationFromAddress(getActivity(), destination);
        double[] coord = new double[]{destinationcoord.longitude, destinationcoord.latitude};
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationcoord, 15));
        nompopup = (TextView) getView().findViewById(R.id.nompopup);
        gestionnairepopup = (TextView) getView().findViewById(R.id.gestionnairepopup);
        capacitepopup = (TextView) getView().findViewById(R.id.capacitepopup);
        adressepopup = (TextView) getView().findViewById(R.id.adressepopup);
        LaunchGoogle = (ImageView) getView().findViewById(R.id.gotomaps);
        destinationMarker = mMap.addMarker(new MarkerOptions().position(destinationcoord).title(destination));


        try {
            Parking.init(getActivity().getApplicationContext());
            for (int i = 0; i < list_parking.size(); i++) {
                double[] coor_park = Parking.getCoordIndex(list_parking.get(i));
                if (Parking.gratuit(list_parking.get(i))) {
                    MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(coor_park[1], coor_park[0])).title(Parking.getNomIndex(list_parking.get(i))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    Marker marker = mMap.addMarker(markerOptions);
                    MarkerCap.put(marker, Parking.getCapaciteIndex(list_parking.get(i)));
                    MarkerNom.put(marker, Parking.getNomIndex(list_parking.get(i)));
                    MarkerCoord.put(marker, Parking.getCoordIndex(list_parking.get(i)));
                    MarkerIndex.put(marker, list_parking.get(i));
                    MarkerGestionnaire.put(marker, Parking.getGestionnaireIndex(list_parking.get(i)));
                } else {
                    MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(coor_park[1], coor_park[0])).title(Parking.getNomIndex(list_parking.get(i))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    Marker marker = mMap.addMarker(markerOptions);
                    MarkerCap.put(marker, Parking.getCapaciteIndex(list_parking.get(i)));
                    MarkerNom.put(marker, Parking.getNomIndex(list_parking.get(i)));
                    MarkerCoord.put(marker, Parking.getCoordIndex(list_parking.get(i)));
                    MarkerIndex.put(marker, list_parking.get(i));
                    MarkerGestionnaire.put(marker, Parking.getGestionnaireIndex(list_parking.get(i)));
                }
            }
            relativeLayout = (RelativeLayout) getView().findViewById(R.id.popupwindow);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                System.out.println("Map clicked");
                System.out.println(relativeLayout);
                if (relativeLayout.getVisibility() == View.VISIBLE){ relativeLayout.setVisibility(View.INVISIBLE);}

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (MarkerNom.containsKey(marker)) {
                    nompopup.setText((String) MarkerNom.get(marker));
                    gestionnairepopup.setText((String) MarkerGestionnaire.get(marker));
                    if (parkingsAvecTR.contains(MarkerIndex.get(marker))){
                        if (etat_parkingsAvecTR.get(parkingsAvecTR.indexOf(MarkerIndex.get(marker))).equals("DONNEES INDISPONIBLES"))
                        {
                            capacitepopup.setText("Données Indisponibles");
                        }
                        else {
                            capacitepopup.setText(etat_parkingsAvecTR.get(parkingsAvecTR.indexOf(MarkerIndex.get(marker))));
                        }
                    }
                    else{capacitepopup.setText("Données Indisponibles");}

                    relativeLayout.setVisibility(View.INVISIBLE);
                    selectedMarker = marker;
                    String filterAddress = "";
                    new FillPopUpWindow(selectedMarker).execute();}
                else
                {relativeLayout.setVisibility(View.INVISIBLE);}
                /*
                Geocoder geoCoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> addresses =
                            geoCoder.getFromLocation(((double[]) MarkerCoord.get(marker))[1], ((double[]) MarkerCoord.get(marker))[0], 1);

                    if (addresses.size() > 0) {
                        for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++)
                            filterAddress += addresses.get(0).getAddressLine(i) + ",";
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (Exception e2) {
                    // TODO: handle exception
                    e2.printStackTrace();
                }
                selectedMarkerAdress = filterAddress;
                adressepopup.setText(filterAddress);

*/
                return false;

            }
        });

        LaunchGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uriintent = "google.navigation:q=" + selectedMarkerAdress + "&mode=d";
                ///Double.toString(((double[]) MarkerCoord.get(selectedMarker))[1]) +"," + Double.toString(((double[]) MarkerCoord.get(selectedMarker))[0]) +"&mode=d";
                Uri gmmIntentUri = Uri.parse(uriintent);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

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
    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;


        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }


    private void addIcon(GoogleMap map, IconGenerator iconFactory, CharSequence text, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).
                position(position).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

        map.addMarker(markerOptions);
    }

    public class FillPopUpWindow extends AsyncTask<Marker,Void,String>{
        Marker marker;
        public FillPopUpWindow(Marker Argmarker){
            super();
            marker = Argmarker;

        }
        protected String doInBackground(Marker... arg) {

            String filterAddress = "";
            Geocoder geoCoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addresses =
                        geoCoder.getFromLocation(((double[]) MarkerCoord.get(marker))[1], ((double[]) MarkerCoord.get(marker))[0], 1);

                if (addresses.size() > 0) {
                    for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++) {
                        if (i == 1) {
                            filterAddress += addresses.get(0).getAddressLine(i) + ".";
                        }
                        else{filterAddress += addresses.get(0).getAddressLine(i) + ", ";}

                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (Exception e2) {
                // TODO: handle exception
                e2.printStackTrace();
            }
            selectedMarkerAdress = filterAddress;
            return selectedMarkerAdress;

        }
        protected void onPostExecute(String result) {
            adressepopup.setText(result);
            relativeLayout.setVisibility(View.VISIBLE);
            return;
        }
    }
}




