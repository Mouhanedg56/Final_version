package com.example.jeren.parking;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

///import android.app.Fragment;
import android.os.SystemClock;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.Map;
import java.util.ArrayList;


/**
 * Created by jeren on 2017/2/15.
 */

public class MyList extends Fragment {


    public  ListView lv;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private View view;

    private String destination;
    private String date;
    private String depart_time;
    private LatLng destinationcoord;
    private ArrayList<Integer> list_parking;
    List<String> dataGrandLyon;
    String reponseGrandLyon;
    ArrayList<Integer> parkingsAvecTR;
    ArrayList<String> etat_parkingsAvecTR;
    private RadioButton radioDis;
    private RadioButton radioProba;
    private RadioGroup radioGroup;
    int rayon;
    boolean payant;
    boolean gratuit;
    double coord[];
    SimpleAdapter adapter;
    ArrayList<Map<String, Object>> listData;




    // private List<String> data = new ArrayList<String>();

    public MyList(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.list_layout, container, false);
        lv = (ListView) view.findViewById(R.id.listview);
        radioDis = (RadioButton) view.findViewById(R.id.radio_distance);
        radioProba = (RadioButton) view.findViewById(R.id.radio_proba);
        radioGroup = (RadioGroup) view.findViewById(R.id.radio_group1);



        ///System.out.println(getArguments().getString("data"));

        Intent intent = getActivity().getIntent();
        destination = intent.getStringExtra("destination");
        date = intent.getStringExtra("date");
        depart_time = intent.getStringExtra("depart_time");
        rayon = intent.getExtras().getInt("rayon");
        parkingsAvecTR = intent.getIntegerArrayListExtra("parkingsAvecTR");
        etat_parkingsAvecTR = intent.getStringArrayListExtra("etat_parkingsAvecTR");
        payant = intent.getBooleanExtra("payant",false);
        gratuit = intent.getBooleanExtra("gratuit",false);
        list_parking = intent.getIntegerArrayListExtra("list_parking");
        destinationcoord = getLocationFromAddress(getActivity(), destination);
        coord = new double[] {destinationcoord.longitude, destinationcoord.latitude};

        try {
            listData = getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            adapter = new SimpleAdapter(getActivity(), listData, R.layout.model_list,
                    new String[]{"name", "capa", "distance", "proba", "frais","imcapa","imdis","imfrais"},
                    new int[]{R.id.name, R.id.capacity, R.id.disTxt, R.id.proba, R.id.fraisTxt,R.id.capa,R.id.distance,R.id.frais});
        } catch (Exception e) {
            e.printStackTrace();
        }
        lv.setAdapter(adapter);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(getActivity()).addApi(AppIndex.API).build();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if(R.id.radio_distance == checkedId){
                    System.out.println("List parking avant:");
                    System.out.println(list_parking);
                    trier_distance(list_parking,coord);
                    System.out.println("List parking après:");
                    System.out.println(list_parking);
                    try {
                        listData = getData();
                        try {

                            adapter = new SimpleAdapter(getActivity(), listData, R.layout.model_list,
                                    new String[]{"name", "capa", "distance", "proba", "frais","imcapa","imdis","imfrais"},
                                    new int[]{R.id.name, R.id.capacity, R.id.disTxt, R.id.proba, R.id.fraisTxt,R.id.capa,R.id.distance,R.id.frais});
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        lv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                else if(R.id.radio_proba == checkedId){

                }

            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent intent = new Intent(view.getContext(), InfoWindowActivity.class);
                intent.putExtra("indexParking", list_parking.get(position));
                intent.putExtra("distance",Parking.distance(coord, list_parking.get(position)));
                intent.putIntegerArrayListExtra("parkingsAvecTR",parkingsAvecTR);
                intent.putStringArrayListExtra("etat_parkingsAvecTR",etat_parkingsAvecTR);
                getActivity().startActivity(intent);
            }
        });
        return view;





    }

/*    public double[] coord = {Parking.getCoordIndex(2)[0],Parking.getCoordIndex(2)[1]};
    public int[] L=Parking.parkingsProches(coord,10000);*/



    //trier par distance  array: liste d'indice de parking
    public void trier_distance(ArrayList<Integer> array,double[] coord){
        ArrayList<Double> distance= new ArrayList<>();
        ArrayList<Double> temp = new ArrayList<>();
        ArrayList<Integer> indiceTemp = new ArrayList<>();
        ArrayList<Integer> temp2 = new ArrayList<>();
        for (int i =0;i<array.size();i++) {
            distance.add(Parking.distance(coord, array.get(i)));
        }

        int i = 0;

        double major = 0;
        int majorInd = 0;
        while( i<distance.size()) {
            for ( int pointeur = 0; pointeur < distance.size();pointeur++) {
                if ( !indiceTemp.contains(pointeur) & major < distance.get(pointeur) )
                {
                    major = distance.get(pointeur);
                    majorInd = pointeur;
                }
            }
            indiceTemp.add(majorInd);
            i++;
            major = 0;
        }

        int aux = 0;
        for (int k = indiceTemp.size() - 1;k>=0;k--){
            temp2.add(array.get(indiceTemp.get(k)));
        }
        System.out.println("temp2");
        System.out.println(temp2);
        list_parking = temp2;
    }

/*
    public static ArrayList<Integer>[] trier_proba(ArrayList<Integer> array,double[] coord){
    double[] proba={};
    Integer[] array1 = (Integer[]) array.toArray(new Integer[array.size()]);
    double temp1;
    int temp2;
    for (int i =0;i<array.size();i++) {
        proba[i] = Parking.distance(coord, array1[i]);
    }
    for(int i=0;i<proba.length;i++) {

        for (int j =0; j < proba.length-i-1; j++) {

            if (proba[j] > proba[j+1]) {

                temp1 = proba[j];
                temp2 = array[j];

                proba[j] = proba[j+1];
                array[j] = array[j+1];

                proba[j+1] = temp1;
                array[j+1] = temp2;

            }
        }
    }
    return array;
}
*/















    private ArrayList<Map<String, Object>> getData() throws Exception {

        Parking.init(getActivity().getApplicationContext());
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;








        for (int i =0;i<list_parking.size();i++) {
            map = new HashMap<String, Object>();
            map.put("name", Parking.getNomIndex(list_parking.get(i)));
            if (parkingsAvecTR.contains(list_parking.get(i)) && etat_parkingsAvecTR.get(parkingsAvecTR.indexOf(list_parking.get(i))).equals("DONNEES INDISPONIBLES")) {
                map.put("capa", Parking.getCapaciteIndex(list_parking.get(i)));
                map.put("imcapa", R.drawable.car);
            }
            else if (parkingsAvecTR.contains(list_parking.get(i))) {
                System.out.println(etat_parkingsAvecTR.get(parkingsAvecTR.indexOf(list_parking.get(i))));
                System.out.println(Parking.getCapaciteIndex(list_parking.get(i)));
                map.put("capa", etat_parkingsAvecTR.get(parkingsAvecTR.indexOf(list_parking.get(i))).split(" ")[0] + "/" + Parking.getCapaciteIndex(list_parking.get(i)));
                map.put("imcapa", R.drawable.car);
            }
            else {
                map.put("capa", Parking.getCapaciteIndex(list_parking.get(i)));
                map.put("imcapa", R.drawable.car);
            }
            map.put("distance", (int) Parking.distance(coord,list_parking.get(i)));
            map.put("imdis", R.drawable.info);
            map.put("proba", "70");
            map.put("frais", Parking.getReglementationIndex(list_parking.get(i)));
            map.put("imfrais", R.drawable.euro1);

            list.add(map);
        }

        return list;


       /*if (L.length<1) {
           return list;
       }
       else {
            map = new HashMap<String, Object>();
            map.put("name", Parking.getNomIndex(L[0]));
            map.put("capa", Parking.getCapaciteIndex(L[0]));
            map.put("type", Parking.getProprietaireIndex(L[0]));
            map.put("proba", "70");
            map.put("frais", Parking.getReglementationIndex(L[0]));
            list.add(map);
            if (L.length>=2) {
                for (int j = 1; j < L.length; j++) {
                    map = new HashMap<String, Object>();
                    map.put("name", Parking.getNomIndex(L[j]));
                    map.put("capa", Parking.getCapaciteIndex(L[j]));
                    map.put("type", Parking.getProprietaireIndex(L[j]));
                    map.put("proba", "70");
                    map.put("frais", Parking.getReglementationIndex(L[j]));
                    list.add(map);
                }
            }
*/


    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Mylist Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
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

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;


        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null || address.size() == 0) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }


}
