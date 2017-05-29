package com.example.jeren.parking;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import static android.content.Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP;

public class TransitionActivity extends AppCompatActivity {
    String destination;
    String date;
    double depart_time;
    int rayon;
    ArrayList<Integer> parkingsAvecTR;
    ArrayList<String> etat_parkingsAvecTR;
    List<String> dataGrandLyon;
    String reponseGrandLyon;
    ArrayList<Integer> list_parking;
    private LatLng destinationcoord;
    CountDownLatch countDownLatch;
    boolean payant;
    boolean gratuit;
    String weekOuSemaine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
        countDownLatch = new CountDownLatch(1);
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
        depart_time = intent.getDoubleExtra("depart_time",0);
        rayon = intent.getIntExtra("rayon",0);
        payant = intent.getBooleanExtra("payant",true);
        gratuit = intent.getBooleanExtra("gratuit",true);

        destinationcoord = getLocationFromAddress(getApplicationContext(), destination);
        if (destinationcoord == null){
            Intent i = new Intent("com.example.jeren.parking.WELCOME");
            i.putExtra("adressenontrouve",1);
            i.addFlags(FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            startActivity(i);
            finish();
            return;}
        else{
            double[] coord = new double[]{destinationcoord.longitude, destinationcoord.latitude};}
        ///jours feries en France
        ArrayList<Date> jourFeries = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

        try {
            jourFeries.add(format.parse("01/01/2017"));
            jourFeries.add(format.parse("01/01/2018"));
            jourFeries.add(format.parse("01/01/2019"));
            jourFeries.add(format.parse("17/04/2017"));
            jourFeries.add(format.parse("02/04/2018"));
            jourFeries.add(format.parse("22/04/2019"));
            jourFeries.add(format.parse("01/05/2017"));
            jourFeries.add(format.parse("01/05/2018"));
            jourFeries.add(format.parse("01/05/2019"));
            jourFeries.add(format.parse("08/05/2017"));
            jourFeries.add(format.parse("08/05/2018"));
            jourFeries.add(format.parse("08/05/2019"));
            jourFeries.add(format.parse("25/05/2017"));
            jourFeries.add(format.parse("10/05/2018"));
            jourFeries.add(format.parse("30/05/2019"));
            jourFeries.add(format.parse("05/06/2017"));
            jourFeries.add(format.parse("21/05/2018"));
            jourFeries.add(format.parse("10/06/2019"));
            jourFeries.add(format.parse("14/07/2017"));
            jourFeries.add(format.parse("14/07/2018"));
            jourFeries.add(format.parse("14/07/2019"));
            jourFeries.add(format.parse("15/08/2017"));
            jourFeries.add(format.parse("15/08/2018"));
            jourFeries.add(format.parse("15/08/2019"));
            jourFeries.add(format.parse("01/11/2017"));
            jourFeries.add(format.parse("01/11/2018"));
            jourFeries.add(format.parse("01/11/2019"));
            jourFeries.add(format.parse("11/11/2017"));
            jourFeries.add(format.parse("11/11/2018"));
            jourFeries.add(format.parse("11/11/2019"));
            jourFeries.add(format.parse("25/12/2017"));
            jourFeries.add(format.parse("25/12/2018"));
            jourFeries.add(format.parse("25/12/2019"));
        }
        catch (ParseException e){e.printStackTrace();}

        ///vérifier le jour de la semaine
        Calendar calendar = Calendar.getInstance();
        try {
            Date date_depart = format.parse(date);
            calendar.setTime(date_depart);
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || jourFeries.contains(date_depart)){
                weekOuSemaine = "weekend";
            }
            else{ weekOuSemaine = "semaine";}
        } catch (ParseException e) {
            e.printStackTrace();
        }
        new JSONtask().execute();
        JSONThread jsonThread = new JSONThread();
        Thread threadGrandLyon = new Thread(jsonThread);
        threadGrandLyon.start();

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
            for (int i =0;i<dataGrandLyon.size()-1;i++){
                int Index = Parking.findIndexbyID(dataGrandLyon.get(i).split(",")[0].replace("(",""));
                if ( list_parking.contains(Index)){
                    parkingsAvecTR.add(Index);
                    etat_parkingsAvecTR.add(dataGrandLyon.get(i).split(",")[2]);
                }
            }
            reponseGrandLyon = result;
            }
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
            i.putExtra("weekOuSemaine",weekOuSemaine);
            i.putExtra("depart_time",depart_time);
            i.putExtra("rayon",rayon);
            i.putIntegerArrayListExtra("parkingsAvecTR",parkingsAvecTR);
            i.putStringArrayListExtra("etat_parkingsAvecTR",etat_parkingsAvecTR);
            i.putIntegerArrayListExtra("list_parking",list_parking);
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
