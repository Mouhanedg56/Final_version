package com.example.jeren.parking;

/**
 * Created by jeren on 2017/2/5.
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import Module.Parking;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;

public  class Map extends Fragment implements OnMapReadyCallback {

    GoogleMap mMap;
    MapView mMapView;
    private String destination;
    private String weekOuSemaine;
    private double depart_time;
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
    TextView probapopup;
    ImageView LaunchGoogle;
    Marker selectedMarker;
    String selectedMarkerAdress;
    Marker destinationMarker;
    ProgressBar progressBar;

    public Map() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mapfragment, container, false);

        probapopup = (TextView) rootView.findViewById(R.id.probapopup);

        Intent intent = getActivity().getIntent();
        destination = intent.getStringExtra("destination");
        weekOuSemaine = intent.getStringExtra("weekOuSemaine");
        depart_time = intent.getDoubleExtra("depart_time", 0);
        rayon = intent.getExtras().getInt("rayon");
        parkingsAvecTR = intent.getIntegerArrayListExtra("parkingsAvecTR");
        etat_parkingsAvecTR = intent.getStringArrayListExtra("etat_parkingsAvecTR");
        list_parking = intent.getIntegerArrayListExtra("list_parking");
        mMapView = (MapView) rootView.findViewById(R.id.mapfrag);
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
        progressBar = (ProgressBar) getView().findViewById(R.id.progressBarPopUp);
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
                if (relativeLayout.getVisibility() == View.VISIBLE){ relativeLayout.setVisibility(View.INVISIBLE);}

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                probapopup.setText("");
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
                        setProbaText((Parking.getProba(weekOuSemaine, (int) MarkerIndex.get(marker),depart_time)));

                    }
                    else{capacitepopup.setText("Données Indisponibles");}

                    relativeLayout.setVisibility(View.INVISIBLE);
                    selectedMarker = marker;
                    String filterAddress = "";
                    new FillPopUpWindow(selectedMarker).execute();}
                else
                {relativeLayout.setVisibility(View.INVISIBLE);}

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



    public class FillPopUpWindow extends AsyncTask<Marker,Void,String>{
        Marker marker;
        public FillPopUpWindow(Marker Argmarker){
            super();
            marker = Argmarker;
            relativeLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
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
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }
    }

    ///fonction pour gérer la couleur du txt avec la proba et sa valeur
    public void setProbaText(int proba){
        String probaText = String.valueOf(proba)+"%";
        probapopup.setText(probaText);
        if (proba>20){probapopup.setTextColor(getActivity().getResources().getColor(R.color.greenProba));}
        else if (proba > 10){probapopup.setTextColor(getActivity().getResources().getColor(R.color.orangeProba));}
        else if (proba >= 0){probapopup.setTextColor(getActivity().getResources().getColor(R.color.redProba));}
        else{probapopup.setText(""); } /// en cas de pas avoir des données corcenant le parking
    }
}




