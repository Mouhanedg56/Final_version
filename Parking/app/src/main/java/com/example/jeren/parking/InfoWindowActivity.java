package com.example.jeren.parking;

import android.app.ActionBar;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InfoWindowActivity extends AppCompatActivity {

    String distance;
    String reglementation;
    String fermeture;
    String capacite;
    String gestionnaire;
    String weekOuSemaine;
    int indexParking;
    double depart_time;
    double[] coord;
    String address;
    TextView nomView;
    TextView addresseView;
    TextView reglementationView;
    TextView gestionnaireView;
    TextView fermetureView;
    TextView previsionView;
    TextView placeslibresView;
    ImageView LaunchGoogle;
    ArrayList<Integer> parkingsAvecTR;
    ArrayList<String> etat_parkingsAvecTR;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_window);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gestionnaireView = (TextView) findViewById(R.id.gestionnaireInfo);
        fermetureView = (TextView) findViewById(R.id.fermetureInfo);
        previsionView = (TextView) findViewById(R.id.previsionInfo);
        placeslibresView = (TextView) findViewById(R.id.placeslibresInfo);
        reglementationView = (TextView) findViewById(R.id.reglementationInfo);
        LaunchGoogle = (ImageView) findViewById(R.id.gotomapsInfo);

        ActionBar actionBar = getActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nomView = (TextView) findViewById(R.id.nomInfo);
        addresseView = (TextView) findViewById(R.id.adresseInfo);

        Intent intent = getIntent();
        indexParking = intent.getIntExtra("indexParking",0);
        distance = intent.getStringExtra("distance");
        parkingsAvecTR = intent.getIntegerArrayListExtra("parkingsAvecTR");
        etat_parkingsAvecTR = intent.getStringArrayListExtra("etat_parkingsAvecTR");
        weekOuSemaine = intent.getStringExtra("weekOuSemaine");
        depart_time = intent.getDoubleExtra("depart_time",0);
        coord = Parking.getCoordIndex(indexParking);
        reglementation = Parking.getReglementationIndex(indexParking);
        fermeture = Parking.getFermetureIndex(indexParking);
        capacite = Parking.getCapaciteIndex(indexParking);
        gestionnaire = Parking.getGestionnaireIndex(indexParking);
        getSupportActionBar().setTitle(Parking.getNomIndex(indexParking));

        ///Remplissage du layout
        new FillInfo().execute();
        nomView.setText(Parking.getNomIndex(indexParking));
        String gestionnaireCapitalized = Parking.getGestionnaireIndex(indexParking).substring(0, 1).toUpperCase() + Parking.getGestionnaireIndex(indexParking).substring(1);
        gestionnaireView.setText(gestionnaireCapitalized);
        setProbaText(Parking.getProba(weekOuSemaine,indexParking,depart_time));
        fermetureView.setText(Parking.getFermetureIndex(indexParking));
        reglementationView.setText(Parking.getReglementationIndex(indexParking));



        if (parkingsAvecTR.contains(indexParking)){
            if (etat_parkingsAvecTR.get(parkingsAvecTR.indexOf(indexParking)).equals("DONNEES INDISPONIBLES"))
            {
                placeslibresView.setText("Données Indisponibles");
            }
            else {
                placeslibresView.setText(etat_parkingsAvecTR.get(parkingsAvecTR.indexOf(indexParking)));
            }
        }
        else{placeslibresView.setText("Données Indisponibles");}


        LaunchGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uriintent = "google.navigation:q=" + address + "&mode=d";
                Uri gmmIntentUri = Uri.parse(uriintent);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }

    public void setProbaText(int proba){
        String probaText = String.valueOf(proba)+"%";
        previsionView.setText(probaText);
        if (proba>20){previsionView.setTextColor(getResources().getColor(R.color.greenProba));}
        else if (proba > 10){previsionView.setTextColor(getResources().getColor(R.color.orangeProba));}
        else if (proba >= 0){previsionView.setTextColor(getResources().getColor(R.color.redProba));}
        else{previsionView.setText(""); } /// en cas de pas avoir des données corcenant le parking
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class FillInfo extends AsyncTask<Void,Void,String> {

        public FillInfo(){
            super();


        }
        protected String doInBackground(Void... aVoid) {

            String filterAddress = "";
            Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addresses =
                        geoCoder.getFromLocation(coord[1], coord[0], 1);

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
            address = filterAddress;
            return address;

        }
        protected void onPostExecute(String result) {
            addresseView.setText(result);
            return;
        }
    }


}
