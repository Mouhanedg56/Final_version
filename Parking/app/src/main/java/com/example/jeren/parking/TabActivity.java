package com.example.jeren.parking;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.attr.data;

public class TabActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    List<String> dataGrandLyon;
    String reponseGrandLyon;
    ArrayList<Integer> list_parking;
    double[] coord;
    String destination;
    String date;
    String depart_time;
    int rayon;
    ArrayList<Integer> parkingsAvecTR;
    ArrayList<String> etat_parkingsAvecTR;
    private LatLng destinationcoord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container);

        //ActionBar actionBar = getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


       mViewPager = (ViewPager) findViewById(R.id.viewpager);
       setupViewPager(mViewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



            Intent intent = getIntent();
            destination = intent.getStringExtra("destination");
            date = intent.getStringExtra("date");
            depart_time = intent.getStringExtra("depart_time");
            rayon = intent.getExtras().getInt("rayon");
            parkingsAvecTR = intent.getIntegerArrayListExtra("parkingsAvecTR");
            etat_parkingsAvecTR = intent.getStringArrayListExtra("etat_parkingsAvecTR");
            list_parking = intent.getIntegerArrayListExtra("list_parking");
            System.out.println("From TabActivity: etat");
            System.out.println(etat_parkingsAvecTR);





///            Bundle bundle = new Bundle();
///            bundle.putIntegerArrayList("parkingsAvecTR", parkingsAvecTR);
///            bundle.putStringArrayList("etat_parkingsAvecTR", etat_parkingsAvecTR);



///            getSupportFragmentManager().beginTransaction().replace(R.id.content, argumentFragment, "LISTE").commit();//now replace the argument fragment




    }

    private void setupViewPager(ViewPager mViewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        ///Fragment myList = ;//Get Fragment Instance
        ///Fragment map = ;
        System.out.println("SetupViewPager");
        adapter.addFragment(new Map(), "First");
        adapter.addFragment(new MyList(), "Second");
        mViewPager.setAdapter(adapter);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(), Welcome.class);
        startActivityForResult(myIntent, 0);
        return true;

    }

    public ArrayList<Integer> getParkingsAvecTR(){
        return parkingsAvecTR;
    }

    public ArrayList<String> getEtat_parkingsAvecTR(){
        return etat_parkingsAvecTR;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            System.out.println("ViewPagerAdapter");
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "PLAN";
                case 1:
                    return "LISTE";
            }
            return null;
        }
    }

    public class JSONtask extends AsyncTask<URL, String, String>{

        @Override
        protected String doInBackground(URL... params) {
            String link = "https://download.data.grandlyon.com/ws/rdata/pvo_patrimoine_voirie.pvoparkingtr/all.json";
            try {
                URL url = new URL(link);
                URLConnection  uc = url.openConnection();
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
                System.out.println("JSON task executed");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            destinationcoord = getLocationFromAddress(getApplicationContext(), destination);
            double[] coord = new double[]{destinationcoord.longitude, destinationcoord.latitude};
            System.out.println("DataGrandLyon");
            for (int i =0;i<dataGrandLyon.size();i++){
                int Index = Parking.findIndexbyID(dataGrandLyon.get(i).split(",")[0].replace("(",""));
                if (list_parking.contains(Index) ){
                    parkingsAvecTR.add(Index);
                    etat_parkingsAvecTR.add(dataGrandLyon.get(i).split(",")[2]);
                }
            }
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


}




    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */









