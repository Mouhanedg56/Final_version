package com.example.jeren.parking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.HashMap;
import java.util.zip.Inflater;


/**
 * Created by jeren on 2017/2/5.
 */

public class Welcome extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private Button recherche;
    private Spinner year;
    private Spinner month;
    private Spinner day;
    private Spinner hour;
    private Spinner minute;
    private CheckBox check;

    /// Rayon
    private static SeekBar seek_bar;
    private static TextView mRayonTextview;
    int rayon;
    
    ///propostion d'adresses
    private static final String LOG_TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView destination;
    private TextView mNameTextView;
    private TextView mAddressTextView;
    private TextView mIdTextView;
    private TextView mPhoneTextView;
    private TextView mWebTextView;
    private TextView mAttTextView;
    private RadioButton radioPayant;
    private RadioButton radioGratuit;
    private RadioButton radioJemenfou;
    private RadioGroup radioGroup;
    private HashMap<String,String> moiscorrespondant = new HashMap<>();
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accueil);
        getApplicationContext();
        moiscorrespondant.put("Janvier","01");
        moiscorrespondant.put("Février","02");
        moiscorrespondant.put("Mars","03");
        moiscorrespondant.put("Avril","04");
        moiscorrespondant.put("Mai","05");
        moiscorrespondant.put("Juin","06");
        moiscorrespondant.put("Juillet","07");
        moiscorrespondant.put("Août","08");
        moiscorrespondant.put("Septembre","09");
        moiscorrespondant.put("Octobre","10");
        moiscorrespondant.put("Novembre","11");
        moiscorrespondant.put("Décembre","12");


        year = (Spinner) findViewById(R.id.year);
        month = (Spinner) findViewById(R.id.month);
        day = (Spinner) findViewById(R.id.day);
        hour = (Spinner) findViewById(R.id.hour);
        minute = (Spinner) findViewById(R.id.minute);
        recherche = (Button) findViewById(R.id.recherche);
        radioGratuit = (RadioButton) findViewById(R.id.radio_gratuit); ///parkings gratuits selectionnés
        radioPayant = (RadioButton) findViewById(R.id.radio_payant); /// parkings payants selectionnés
        radioJemenfou = (RadioButton) findViewById(R.id.radio_jemenfou); /// parkings payants ou gratuits (je m'en fou)
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);

        mGoogleApiClient = new GoogleApiClient.Builder(Welcome.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        destination = (AutoCompleteTextView) findViewById(R.id.address); ///Proposer une liste des adresses

        destination.setOnItemClickListener(mDestinationClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        destination.setAdapter(mPlaceArrayAdapter);
        
        setupUI(findViewById(R.id.welcome_layout));
        Fseekbar();
        checkini();


    }

    public void GoToMap(View v){
        String todestination = destination.getText().toString(); ///prendre le texte de la destination saisie par l'utilisateur
        String todate = day.getSelectedItem().toString() + "/" + moiscorrespondant.get(month.getSelectedItem().toString()) + "/" + year.getSelectedItem().toString(); ///prendre le jour, le mois et l'année que l'utilisateur a choisi
        System.out.println("date: ");
        System.out.println(todate);
        double depart_time = ( Double.parseDouble(hour.getSelectedItem().toString())  + Double.parseDouble(minute.getSelectedItem().toString())/60); //idem pour l'heure et le minute. Unité des heures
        if (todestination.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }


        rayon = seek_bar.getProgress(); /// prendre le rayon choisi dans la seekbar
        Intent i = new Intent("com.example.jeren.parking.TRANSITION"); /// creation de l'intent pour aller à l'activité TransitionActivity
        i.putExtra("destination",todestination); ///information qui va être emmène avec l'intent
        i.putExtra("date",todate);
        i.putExtra("depart_time",depart_time);
        i.putExtra("rayon",rayon);
        if (radioJemenfou.isChecked()) /// si payant ou gratuit choisi
        {
            System.out.println("all true");
            i.putExtra("payant",true);
            i.putExtra("gratuit",true);
        }
        else
        {
            i.putExtra("payant", radioPayant.isChecked());
            i.putExtra("gratuit", radioGratuit.isChecked());
        }

        startActivity(i);
        finish();

    }

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN); ///fermer l'application si boutton de retourner tappé.
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(Welcome.this); ///cacher le clavier si on tappe dehors le clavier
                    return false;
                }
            });
        }


        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private AdapterView.OnItemClickListener mDestinationClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();
            if (attributions != null) {
                mAttTextView.setText(Html.fromHtml(attributions.toString()));
            }
        }
    };


    public void Fseekbar(){ ///choix du rayon
        seek_bar=(SeekBar)findViewById(R.id.seekBar);
        seek_bar.setMax(2000);
        mRayonTextview=(TextView)findViewById(R.id.rayonval);
        mRayonTextview.setText(seek_bar.getProgress()+"m");

        seek_bar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int progress_val;
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                        progress_val=progress;
                        mRayonTextview.setText(progress+"m");

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mRayonTextview.setText(progress_val+"m");
                    }
                }
        );

    }
    public void checkini() {
        check = (CheckBox) findViewById(R.id.checkBox); /// partir maintenant
        check.setChecked(false);
//        ((Spinner) year).getSelectedView().setEnabled(false);
//        year.setEnabled(false);
//        ((Spinner) month).getSelectedView().setEnabled(false);
//        month.setEnabled(false);
//        ((Spinner) day).getSelectedView().setEnabled(false);
//        day.setEnabled(false);
//        ((Spinner) hour).getSelectedView().setEnabled(false);
//        hour.setEnabled(false);
//        ((Spinner) minute).getSelectedView().setEnabled(false);
//        minute.setEnabled(false);
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                             @Override
                                             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { ///bloquer la choix de date si on veut partir maintenant
                                                 if (buttonView.isChecked()) {
                                                     ((Spinner) year).getSelectedView().setEnabled(false);
                                                     year.setEnabled(false);
                                                     ((Spinner) month).getSelectedView().setEnabled(false);
                                                     month.setEnabled(false);
                                                     ((Spinner) day).getSelectedView().setEnabled(false);
                                                     day.setEnabled(false);
                                                     ((Spinner) hour).getSelectedView().setEnabled(false);
                                                     hour.setEnabled(false);
                                                     ((Spinner) minute).getSelectedView().setEnabled(false);
                                                     minute.setEnabled(false);//checked
                                                 } else {
                                                     ((Spinner) year).getSelectedView().setEnabled(true);
                                                     year.setEnabled(true);
                                                     ((Spinner) month).getSelectedView().setEnabled(true);
                                                     month.setEnabled(true);
                                                     ((Spinner) day).getSelectedView().setEnabled(true);
                                                     day.setEnabled(true);
                                                     ((Spinner) hour).getSelectedView().setEnabled(true);
                                                     hour.setEnabled(true);
                                                     ((Spinner) minute).getSelectedView().setEnabled(true);
                                                     minute.setEnabled(true);//not checked
                                                 }

                                             }


                                         }
        );
    }

}