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
import android.widget.ListView;
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
    int rayon;
    boolean payant;
    boolean gratuit;



    // private List<String> data = new ArrayList<String>();

    public MyList(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.list_layout, container, false);
        lv = (ListView) view.findViewById(R.id.listview);
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


        System.out.println("From List: etat");
        System.out.println(etat_parkingsAvecTR);

        SimpleAdapter adapter = null;
        try {
            adapter = new SimpleAdapter(getActivity(), getData(), R.layout.model_list,
                    new String[]{"name", "capa", "distance", "proba", "frais","imcapa","imdis","imfrais"},
                    new int[]{R.id.name, R.id.capacity, R.id.disTxt, R.id.proba, R.id.fraisTxt,R.id.capa,R.id.distance,R.id.frais});
        } catch (Exception e) {
            e.printStackTrace();
        }
        lv.setAdapter(adapter);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(getActivity()).addApi(AppIndex.API).build();
        return view;


    }

/*    public double[] coord = {Parking.getCoordIndex(2)[0],Parking.getCoordIndex(2)[1]};
    public int[] L=Parking.parkingsProches(coord,10000);*/



    //trier par distance  array: liste d'indice de parking
    public static int[] trier_distance(int[] array,double[] coord){
        double[] distance={};
        double temp1;
        int temp2;
        for (int i =0;i<array.length;i++) {
            distance[i] = Parking.distance(coord, array[i]);
        }
        for(int i=0;i<distance.length;i++) {

            for (int j = i + 1; j < distance.length; j++) {

                if (distance[i] > distance[j]) {

                    temp1 = distance[i];
                    temp2 = array[i];

                    distance[i] = distance[j];
                    array[i] = array[j];

                    distance[j] = temp1;
                    array[j] = temp2;

                }
            }
        }
        return array;
    }

    //trier par proba  array: liste d'indice de parking















    private ArrayList<Map<String, Object>> getData() throws Exception {

        Parking.init(getActivity().getApplicationContext());
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        int[] L = {2, 1, 3, 4};
        /*
        map.put("name", Parking.getNomIndex(L[0]));
        map.put("capa", Parking.getCapaciteIndex(L[0]));
        map.put("type", Parking.getProprietaireIndex(L[0]));
        map.put("proba", "70");
        map.put("frais", Parking.getReglementationIndex(L[0]));
        list.add(map);
        */




        destinationcoord = getLocationFromAddress(getActivity(), destination);
        double[] coord = new double[]{destinationcoord.longitude, destinationcoord.latitude};


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
            map.put("distance", Parking.distance(coord,list_parking.get(i)));
            map.put("imdis", R.drawable.info);
            map.put("proba", "70");
            map.put("frais", Parking.getReglementationIndex(list_parking.get(i)));
            map.put("imfrais", R.drawable.euro);

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
            if (address == null) {
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


    public class JSONtask extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... params) {
            String link = "https://download.data.grandlyon.com/ws/rdata/pvo_patrimoine_voirie.pvoparkingtr/all.json";
            try {
                URL url = new URL(link);
                URLConnection uc = url.openConnection();
                String basicAuth = "Basic ZmVsaXBlLmxvdXJlbmNvLWFuZ2VsaW0tdmllaXJhQGVjbDE2LmVjLWx5b24uZnI6UEVwbHVzY2hhdWRAODI=";
                uc.setRequestProperty ("Authorization", basicAuth);
                InputStream in = uc.getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            reponseGrandLyon = result;
        }
    }

    private static List<String> parseJSon(String data) throws JSONException, UnsupportedEncodingException {
        if (data == null)
            return new ArrayList<String>() ;

        List<String> parkings = new ArrayList<String>();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonParkings = jsonData.getJSONArray("values");
        for (int i = 0; i < jsonParkings.length(); i++) {
            JSONObject jsonPark = jsonParkings.getJSONObject(i);
            String encoded = jsonPark.getString("nom");
            String decoded = new String(encoded.getBytes("ISO-8859-1"));
            parkings.add("(" + jsonPark.getString("pkgid") + "," + decoded + "," + jsonPark.getString("etat") + "," + jsonPark.getString("last_update") + ")");
        }
        return parkings;
    }
}
