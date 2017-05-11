package com.example.jeren.parking;

/**
 * Created by Felipe Vieira on 08/03/2017.
    Pour appeler la classe Parking et utiliser ses méthodes, appeler tout d'abord le
    methode:

 Parking.init(getApplicationContext());

 Ensuite, on peut utiliser les autres methodes.

 */

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class Parking {

    private static ArrayList<String> nomList = new ArrayList<String>();
    private static ArrayList<String> idParkingList = new ArrayList<String>();
    private static ArrayList<String> idParkingcriterList = new ArrayList<String>();
    private static ArrayList<String> communeList = new ArrayList<String>();
    private static ArrayList<String> proprietaireList = new ArrayList<String>();
    private static ArrayList<String> gestionnaireList = new ArrayList<String>();
    private static ArrayList<String> idfournisseurList = new ArrayList<String>();
    private static ArrayList<String> voieentreeList = new ArrayList<String>();
    private static ArrayList<String> voiesortieList = new ArrayList<String>();
    private static ArrayList<String> avancementList = new ArrayList<String>();
    private static ArrayList<String> anneeList = new ArrayList<String>();
    private static ArrayList<String> typeParkingList = new ArrayList<String>();
    private static ArrayList<String> situationList = new ArrayList<String>();
    private static ArrayList<String> ParkingtempsreelList = new ArrayList<String>();
    private static ArrayList<String> gabaritList = new ArrayList<String>();
    private static ArrayList<String> capaciteList = new ArrayList<String>();
    private static ArrayList<String> capacite2rmList = new ArrayList<String>();
    private static ArrayList<String> capaciteveloList = new ArrayList<String>();
    private static ArrayList<String> capaciteautopartageList = new ArrayList<String>();
    private static ArrayList<String> capacitepmrList = new ArrayList<String>();
    private static ArrayList<String> usageList = new ArrayList<String>();
    private static ArrayList<String> vocationList = new ArrayList<String>();
    private static ArrayList<String> reglementationList = new ArrayList<String>();
    private static ArrayList<String> fermetureList = new ArrayList<String>();
    private static ArrayList<String> observationList = new ArrayList<String>();
    private static ArrayList<String> codetypeList = new ArrayList<String>();
    private static ArrayList<String> gidList = new ArrayList<String>();
    private static ArrayList<String[]> coordList = new ArrayList<String[]>();



    public Parking() {


    }
    //public private createParkings(){
    public static void main(String[] args){


    }






    public static String getNomIndex(int i)
    {
        return nomList.get(i);
    }


    public static int getIndexNom(String s)
    {
        return nomList.indexOf(s);
    }

    public static String getIdIndex(int i)
    {
        return idParkingList.get(i);
    }

    public static String getFermetureIndex(int i) {return fermetureList.get(i);}

    public static  String getCommuneIndex(int i)
    {
        return communeList.get(i);
    }

    public static  String getProprietaireIndex(int i)
    {
        return proprietaireList.get(i);
    }

    public static  String getGestionnaireIndex(int i)
    {
        return gestionnaireList.get(i);
    }

    public static  String getEntreeIndex(int i)
    {
        return voieentreeList.get(i);
    }

    public static String getSortieIndex(int i)
    {
        return voiesortieList.get(i);
    }

    public static  String getAvancementIndex(int i)
    {
        return avancementList.get(i);
    }

    public static  String getSituationIndex(int i)
    {
        return situationList.get(i);
    }

    public static String getTRIndex(int i)
    {
        return ParkingtempsreelList.get(i);
    }

    public static String getCapaciteIndex(int i)
    {
        return capaciteList.get(i);
    }

    public static  String getUsageIndex(int i)
    {
        return usageList.get(i);
    }

    public static String getVocationIndex(int i)
    {
        return vocationList.get(i);
    }

    public static  String getReglementationIndex(int i)
    {
        return reglementationList.get(i);
    }

    public static int findIndexbyID(String s){ return idParkingcriterList.indexOf(s);}

    //Determiner si le Parking est Payant ou gratuit. True si gratuit
    public static  boolean gratuit(int i)
    {
        return reglementationList.get(i).equals("Gratuit");
    }



    public static double[] getCoordIndex(int i)
    {
        double[] cd = {Double.valueOf(coordList.get(i)[0]), Double.valueOf(coordList.get(i)[1])};
        return cd;
    }




    public static void init(Context context) throws Exception

    {

        try {
            InputStream is = context.getAssets().open("infocomplete.txt");



            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String text = new String(buffer);
            String[] lines;
            lines = text.split("\n");
            for (int i = 0; i<=998; i++)
            {

                String[] infosParking = lines[i].split("\\{ ")[2].replace(", ", ":").replace(": ", ":").replace("\\","").split(":");
                String [] coordParking = lines[i].split("\\{ ")[3].replace("]", "[").split(" \\[ ")[1].split(", ");


                nomList.add(infosParking[1]);
                idParkingList.add(infosParking[3]);
                idParkingcriterList.add(infosParking[5]);
                communeList.add(infosParking[7]);
                proprietaireList.add(infosParking[9]);
                gestionnaireList.add(infosParking[11]);
                idfournisseurList.add(infosParking[13]);
                voieentreeList.add(infosParking[15]);
                voiesortieList.add(infosParking[17]);
                avancementList.add(infosParking[19]);
                anneeList.add(infosParking[21]);
                typeParkingList.add(infosParking[23]);
                situationList.add(infosParking[25]);
                ParkingtempsreelList.add(infosParking[27]);
                gabaritList.add(infosParking[29]);
                capaciteList.add(infosParking[31]);
                capacite2rmList.add(infosParking[33]);
                capaciteveloList.add(infosParking[35]);
                capaciteautopartageList.add(infosParking[37]);
                capacitepmrList.add(infosParking[39]);
                usageList.add(infosParking[41]);
                vocationList.add(infosParking[43]);
                reglementationList.add(infosParking[45]);
                fermetureList.add(infosParking[47]);
                observationList.add(infosParking[49]);
                codetypeList.add(infosParking[51]);
                gidList.add(infosParking[53]);
                coordList.add(coordParking);



            }

        }
        catch (UnsupportedEncodingException e)
        {
            System.out.println(e.getMessage());
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }


    }

    //(lat, lon)
    public double distanceParkings(int a, int b)
    {
        double[] coordA = Parking.getCoordIndex(a);
        double[] coordB = Parking.getCoordIndex(b);
        double pi = Math.PI;


        int R = 6378137;
        double dLat = coordA[0]*pi/180 - coordB[0]*pi/180;
        double dLon = coordA[1]*pi/180 - coordB[1]*pi/180;
        double lat1 = coordA[0]*pi/180;
        double lat2 = coordB[0]*pi/180;
        double angle = Math.sin(dLat/2) * Math.sin(dLat/2) +  Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(angle), Math.sqrt(1-angle));
        return R * c;
    }

    public static double distance(double[] coordA, int b)
    {
        double[] coordB = Parking.getCoordIndex(b);
        double pi = Math.PI;


        int R = 6378137;
        double dLat = coordA[0]*pi/180 - coordB[0]*pi/180;
        double dLon = coordA[1]*pi/180 - coordB[1]*pi/180;
        double lat1 = coordA[0]*pi/180;
        double lat2 = coordB[0]*pi/180;
        double angle = Math.sin(dLat/2) * Math.sin(dLat/2) +  Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(angle), Math.sqrt(1-angle));
        return R * c;
    }

/// rayon en metres
    /*
    public static int[] parkingsProches(double[] coord, int rayon)
    {
        boolean[] dedans = new boolean[999];
        int count = 0;
        for (int i = 0; i<999;i++)
        {
            if (Parking.distance(coord, i) < rayon)
            {
                dedans[i] = true;
                count++;
            }
        }
        int[] parkings = new int[count];
        int index = 0;


        for (int k = 0; k<dedans.length;k++)
        {
            if (dedans[k])
            {
                parkings[index] = k;
                index++;
            }
        }
        return parkings;
    }
    */
    public static ArrayList<Integer> parkingsProches(double[] coord, int rayon)
    {

        boolean[] dedans = new boolean[999];
        int count = 0;
        for (int i = 0; i<999;i++)
        {
            if (Parking.distance(coord, i) < rayon)
            {
                dedans[i] = true;
                count++;
            }
        }

        ArrayList<Integer> parkings = new ArrayList<>();

        for (int k = 0; k<dedans.length;k++)
        {
            if (dedans[k])
            {
                parkings.add(k);
            }
        }
        return parkings;
    }

}


