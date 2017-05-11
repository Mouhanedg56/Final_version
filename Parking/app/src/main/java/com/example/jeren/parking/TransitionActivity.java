package com.example.jeren.parking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class TransitionActivity extends AppCompatActivity {
    String destination;
    String date;
    String depart_time;
    int rayon;
    ArrayList<Integer> parkingsAvecTR;
    ArrayList<String> etat_parkingsAvecTR;
    List<String> dataGrandLyon;
    String reponseGrandLyon;
    ArrayList<Integer> list_parking;
    double[] coord;
    private LatLng destinationcoord;
    CountDownLatch countDownLatch;
    boolean payant;
    boolean gratuit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
        countDownLatch = new CountDownLatch(1);
        System.out.println("Count (from OnCreate):");
        System.out.println(countDownLatch.getCount());
        parkingsAvecTR = new ArrayList<Integer>();
        etat_parkingsAvecTR = new ArrayList<String>();
        try {
            Parking.init(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        destination = intent.getStringExtra("destination");
        date = intent.getStringExtra("date");
        depart_time = intent.getStringExtra("depart_time");
        rayon = intent.getIntExtra("rayon",0);
        payant = intent.getBooleanExtra("payant",true);
        gratuit = intent.getBooleanExtra("gratuit",true);
        new JSONtask().execute();
        JSONThread jsonThread = new JSONThread();
        Thread threadGrandLyon = new Thread(jsonThread);
        threadGrandLyon.start();

        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new JSONtask().execute();



                    }
                });

            }
        }).start();
        */

        WaitThread waitThread = new WaitThread();
        Thread thread = new Thread(waitThread);
        thread.start();






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
            try {
                dataGrandLyon = parseJSon(reponseGrandLyon);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            destinationcoord = getLocationFromAddress(getApplicationContext(), destination);
            if (destinationcoord == null){
                Intent i = new Intent("com.example.jeren.parking.WELCOME");
                startActivity(i);
                finish();}
            else{
            double[] coord = new double[]{destinationcoord.longitude, destinationcoord.latitude};
            list_parking = Parking.parkingsProches(coord,rayon);
            int k = 0;
            for (int j = list_parking.size()-1; j>= 0 ; j--){
                if (!gratuit)
                {
                    if (Parking.gratuit(list_parking.get(j))) {
                        list_parking.remove(j);
                    }
                }

                if (!payant)
                {
                    if (!Parking.gratuit(list_parking.get(j))){
                        list_parking.remove(j);
                    }
                }


            }
            /*
            while (k<list_parking.size()) {
                if (!gratuit)
                {
                    if (Parking.gratuit(list_parking.get(k))) {
                        list_parking.remove(k);
                    }
                }

                if (!payant)
                {
                    if (!Parking.gratuit(list_parking.get(k))){
                        list_parking.remove(k);
                    }
                }
                k++;
                }
                */
            System.out.println(rayon);
            System.out.println("DataGrandLyon (info et size)");
            System.out.println(dataGrandLyon);
            System.out.println(dataGrandLyon.size());
            for (int i =0;i<dataGrandLyon.size()-1;i++){
                int Index = Parking.findIndexbyID(dataGrandLyon.get(i).split(",")[0].replace("(",""));
                if ( list_parking.contains(Index)){
                    parkingsAvecTR.add(Index);
                    etat_parkingsAvecTR.add(dataGrandLyon.get(i).split(",")[2]);
                }
            }
            System.out.println("Countdown");
            System.out.println("Etat Parkings:");
            System.out.println(etat_parkingsAvecTR);
            reponseGrandLyon = result;
            System.out.println("Count from JSONtask");
            System.out.println(countDownLatch.getCount());}
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

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;


        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address.size() == 0 ){
                System.out.println("Address size = 0");
                return null;

            }
            if (address == null){
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

    public class WaitForReponse extends AsyncTask<Void,Void,Void> {
        private Context context;

        public WaitForReponse(Context context){
            this.context=context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                System.out.println("Count from wait for reponse:");
                System.out.println(countDownLatch.getCount());
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Intent i = new Intent(context, TabActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("destination",destination);
            i.putExtra("date",date);
            i.putExtra("depart_time",depart_time);
            i.putExtra("rayon",rayon);
            i.putIntegerArrayListExtra("parkingsAvecTR",parkingsAvecTR);
            i.putStringArrayListExtra("etat_parkingsAvecTR",etat_parkingsAvecTR);
            i.putIntegerArrayListExtra("list_parking",list_parking);
            System.out.print("Count onPostExecute:");
            System.out.println(countDownLatch.getCount());
            System.out.println("Intent Sent");
            context.startActivity(i);
            finish();

        }
    }

    public class WaitThread implements Runnable{
        public void run(){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new WaitForReponse( getApplicationContext()).execute();

        }
    }

    public class  JSONThread implements Runnable{
        @Override
        public void run() {
            new  JSONtask().execute();
            countDownLatch.countDown();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent("com.example.jeren.parking.WELCOME");
        startActivity(i);
        finish();
    }
}
