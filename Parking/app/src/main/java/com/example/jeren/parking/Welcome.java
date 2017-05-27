package com.example.jeren.parking;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Layout;
import android.text.format.DateFormat;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.google.android.gms.vision.text.Text;

import java.util.Calendar;
import java.util.HashMap;
import java.util.zip.Inflater;


/**
 * Created by jeren on 2017/2/5.
 */

public class Welcome extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Button recherche;
    private CheckBox check;

    private int yearFinal;
    private int monthFinal;
    private int dayFinal;
    private int hourFinal;
    private int minuteFinal;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private String todate;

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
    private TextView date;
    private TextView moment;
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

        int adressenontrouve;
        Intent activityReopenned = getIntent();
        adressenontrouve = activityReopenned.getIntExtra("adressenontrouve",0);
        if (adressenontrouve==1){Toast.makeText(getApplicationContext(), "Veuillez saisir une autre adresse", Toast.LENGTH_SHORT).show();}


        date = (TextView) findViewById(R.id.dateTxtAccueil);
        moment = (TextView) findViewById(R.id.timeTxtAccueil);
        recherche = (Button) findViewById(R.id.recherche);
        radioGratuit = (RadioButton) findViewById(R.id.radio_gratuit); ///parkings gratuits selectionnés
        radioPayant = (RadioButton) findViewById(R.id.radio_payant); /// parkings payants selectionnés
        radioJemenfou = (RadioButton) findViewById(R.id.radio_jemenfou); /// parkings payants ou gratuits (je m'en fou)
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);

        Calendar c = Calendar.getInstance();
        hourFinal = c.get(Calendar.HOUR_OF_DAY);
        minuteFinal = c.get(Calendar.MINUTE);
        yearFinal = c.get(Calendar.YEAR);
        monthFinal = c.get(Calendar.MONTH)+1;
        dayFinal = c.get(Calendar.DAY_OF_MONTH);


        String zeroHour = "";
        String zeroMinute = "";
        String zeroDay = "";
        String zeroMonth = "";
        if (dayFinal<10) {zeroDay = "0";}
        if (monthFinal<10) {zeroMonth = "0";}
        if (hourFinal<10) {zeroMonth = "0";}
        if (minuteFinal<10) {zeroMinute = "0";}
        date.setText(zeroDay + dayFinal + "/" + zeroMonth + monthFinal +"/" + yearFinal);
        moment.setText(zeroHour + hourFinal +":" + zeroMinute + minuteFinal);


        date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    Calendar  c = Calendar.getInstance();
                    year = c.get(Calendar.YEAR);
                    month = c.get(Calendar.MONTH);
                    day = c.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(Welcome.this, Welcome.this, year, month, day);
                    datePickerDialog.show();
                }
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void  onClick(View view){
                Calendar  c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Welcome.this, Welcome.this, year, month, day);
                datePickerDialog.show();
            }
        });

        moment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    Calendar  c = Calendar.getInstance();
                    hour = c.get(Calendar.HOUR_OF_DAY);
                    minute = c.get(Calendar.MINUTE);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(Welcome.this, Welcome.this, hour, minute, true);
                    timePickerDialog.show();
                }
            }
        });
        moment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void  onClick(View view){
                Calendar  c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(Welcome.this, Welcome.this, hour, minute, true);
                timePickerDialog.show();

            }
        });

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


    }

    public void GoToMap(View v){
        String todestination = destination.getText().toString(); ///prendre le texte de la destination saisie par l'utilisateur
        String todate = date.getText().toString(); ///prendre le jour, le mois et l'année que l'utilisateur a choisi
        double depart_time = (double) (hourFinal  + minuteFinal/60); //idem pour l'heure et le minute. Unité des heures
        if (!isOnline()) {
            try {
                System.out.println("is not online");
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                return;
            }
            catch (Exception e){e.printStackTrace();                 System.out.println("online exception");}
        }

        if (todestination.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Veuillez saisir la destination", Toast.LENGTH_SHORT).show();
            return;
        }

        if (date.getText().equals("")) {
            Toast.makeText(getApplicationContext(), "Veuillez choisir la date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (moment.getText().equals("")) {
            Toast.makeText(getApplicationContext(), "Veuillez choisir l'heure", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2){
        yearFinal = i;
        monthFinal = i1 + 1;
        dayFinal = i2;


        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(Welcome.this, Welcome.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker picker, int i, int i1){
        hourFinal = i;
        minuteFinal = i1;
        String zeroHour = "";
        String zeroMinute = "";
        String zeroDay = "";
        String zeroMonth = "";
        if (dayFinal<10) {zeroDay = "0";}
        if (monthFinal<10) {zeroMonth = "0";}
        if (hourFinal<10) {zeroHour = "0";}
        if (minuteFinal<10) {zeroMinute = "0";}
        date.setText(zeroDay + dayFinal + "/" + zeroMonth + monthFinal +"/" + yearFinal);
        moment.setText(zeroHour + hourFinal + ":" + zeroMinute + minuteFinal);

    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo == null){return false;}
        if (!netInfo.isConnected()){return false;}
        if (!netInfo.isAvailable()){return false;}
        return true;
    }
}