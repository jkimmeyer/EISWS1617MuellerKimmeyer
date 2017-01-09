package eis1617.muellerkimmeyer.app;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Pflege");
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());



        FragmentManager.OnBackStackChangedListener mBackStackListener = new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                FragmentManager manager = getSupportFragmentManager();
                int count = manager.getBackStackEntryCount();
                if(count > 0) {
                    // TODO: Show back icon
                } else {
                    // TODO: Other icon
                }
            }
        };
        FragmentManager manager = getSupportFragmentManager();
        manager.addOnBackStackChangedListener(mBackStackListener);



        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.tab_icon_pflege);
        tabLayout.getTabAt(1).setIcon(R.drawable.tab_icon_dokumentation);
        tabLayout.getTabAt(2).setIcon(R.drawable.tab_icon_probleme);

        /*
        * QUELLENANGABE
        * OnTabSelectedListener zum Ändern des Titels
        * übernommen aus http://stackoverflow.com/questions/33227212/how-to-change-the-toolbar-title-using-design-library-tab-layout
        * Autor: "Khanna", Abruf am: 05.01.2017
        * Änderungen: Namen der Tabs angepasst
        */

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0:
                        mViewPager.setCurrentItem(0);
                        toolbar.setTitle("Pflege");
                        break;
                    case 1:
                        mViewPager.setCurrentItem(1);
                        toolbar.setTitle("Daten");
                        break;
                    case 2:
                        mViewPager.setCurrentItem(2);
                        toolbar.setTitle("Probleme");
                        break;
                    default:
                        mViewPager.setCurrentItem(tab.getPosition());
                        toolbar.setTitle("Pflege");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    public void onItemSelected (Wasserwechsel fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        String name = "Wasserwechsel";
        fragmentTransaction.addToBackStack(name);
        fragmentTransaction.commit();
        getSupportActionBar().setTitle(name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    Tab1Pflege tab1 = new Tab1Pflege();
                    return tab1;
                case 1:
                    Tab2Dokumentation tab2 = new Tab2Dokumentation();
                    return tab2;
                case 2:
                    Tab3Probleme tab3 = new Tab3Probleme();
                    return tab3;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Pflege";
                case 1:
                    return "Daten";
                case 2:
                    return "Probleme";
            }
            return null;
        }
    }

}
