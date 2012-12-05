package info.guardianproject.mrapp;
import info.guardianproject.mrapp.R;
import info.guardianproject.mrapp.lessons.LessonListView;
import info.guardianproject.mrapp.server.LoginActivity;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.app.ActionBar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class HomeActivity extends com.WazaBe.HoloEverywhere.sherlock.SActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
     * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best
     * to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    
    Button mButtonNewStory;
    Button mButtonStartALesson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
        setContentView(R.layout.activity_home);
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding tab.
        // We can also use ActionBar.Tab#select() to do this if we have a reference to the
        // Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        
        checkCreds ();
    }

    //if the user hasn't registered with the user, show the login screen
    private void checkCreds ()
    {
    	
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
       
        String user = settings.getString("user",null);
        
        if (user == null)
        {
        	Intent intent = new Intent(this,LoginActivity.class);
        	startActivity(intent);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	
		if (item.getItemId() == R.id.menu_settings)
		{
			showPreferences();
		}
		else if (item.getItemId() == R.id.menu_login)
		{
			showLogin();
		}
		
		return super.onOptionsItemSelected(item);
	}
    
	private void showPreferences ()
	{
		Intent intent = new Intent(this,SimplePreferences.class);
		startActivity(intent);
	}
	
	private void showLogin ()
	{
		Intent intent = new Intent(this,LoginActivity.class);
		startActivity(intent);
	}

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            Fragment fragment = new HomeSectionFragment();
            
            if (i == 1) {
            	 fragment = new ProjectsSectionFragment();
            }
            
            
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return getString(R.string.title_home_activity).toUpperCase();
                case 1: return getString(R.string.title_home_projects).toUpperCase();
            }
            return null;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    
    public static class HomeSectionFragment extends Fragment {
       
        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_home_activity, null);
            
                ((Button) view.findViewById(R.id.buttonNewStory)).setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), StoryNewActivity.class));
                    }
                });
                
                ((Button) view.findViewById(R.id.buttonStartALesson)).setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), LessonsActivity.class));
                    }
                });
           
            return view;
        }
        
        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            setUserVisibleHint(true);
        }
    }
    
    public static class ProjectsSectionFragment extends Fragment {
       
    	ProjectsListView listView;
    	
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	listView = new ProjectsListView(getActivity());
         
            return listView;
        }

        
        
        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            setUserVisibleHint(true);
        }



		@Override
		public void onResume() {
			
			super.onResume();
			
			listView.refresh();
		}
        
    }

    
}
