package com.example.jeren.parking;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class TabActivity extends AppCompatActivity {


    ViewPager mViewPager;
    private TabLayout tabLayout;
    ArrayList<Integer> list_parking;
    String destination;
    String date;
    String weekOuSemaine;
    double depart_time;
    int rayon;
    ArrayList<Integer> parkingsAvecTR;
    ArrayList<String> etat_parkingsAvecTR;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Intent intent = getIntent();
        destination = intent.getStringExtra("destination");
        date = intent.getStringExtra("date");
        depart_time = intent.getDoubleExtra("depart_time",0);
        weekOuSemaine = intent.getStringExtra("weekOuSemaine");
        rayon = intent.getExtras().getInt("rayon");
        parkingsAvecTR = intent.getIntegerArrayListExtra("parkingsAvecTR");
        etat_parkingsAvecTR = intent.getStringArrayListExtra("etat_parkingsAvecTR");
        list_parking = intent.getIntegerArrayListExtra("list_parking");

    }

    private void setupViewPager(ViewPager mViewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Map(), "First");
        adapter.addFragment(new MyList(), "Second");
        mViewPager.setAdapter(adapter);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(), Welcome.class);
        startActivityForResult(myIntent, 0);
        return true;

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
    @Override
    public void onBackPressed() {
        Intent i = new Intent("com.example.jeren.parking.WELCOME");
        startActivity(i);
        finish();
    }

}