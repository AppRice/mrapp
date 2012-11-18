package info.guardianproject.mrapp;

import java.io.IOException;
import java.io.InputStream;

import net.micode.soundrecorder.SoundRecorder;

import org.json.JSONException;

import info.guardianproject.mrapp.model.Template;
import info.guardianproject.mrapp.model.Template.Clip;
import info.guardianproject.mrapp.ui.OverlayCamera;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.WazaBe.HoloEverywhere.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.animoto.android.views.DraggableGridView;
import com.animoto.android.views.OnRearrangeListener;

public class SceneEditorNoSwipe extends com.WazaBe.HoloEverywhere.sherlock.SActivity implements ActionBar.TabListener {

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    protected boolean templateStory = false; 
    
    protected Menu mMenu = null;
    
    private Context mContext = null;
     
    private String templateJsonPath = null;
    
    private int storyMode = STORY_MODE_VIDEO;
    
    public final static int STORY_MODE_VIDEO = 0;
    public final static int STORY_MODE_AUDIO = 1;
    public final static int STORY_MODE_PHOTO = 2;
    public final static int STORY_MODE_ESSAY = 3;
    
    private final static int RESULT_AUDIO = 777;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra("template_story")) {
        	templateStory = true;
        }
        
        if (getIntent().hasExtra("template_path")) {
        	templateJsonPath = getIntent().getStringExtra("template_path");
        }
        
        if (getIntent().hasExtra("story_mode"))
        {
        	storyMode = getIntent().getIntExtra("story_mode", STORY_MODE_VIDEO);
        }
        
        mContext = getBaseContext();

        setContentView(R.layout.activity_scene_editor_no_swipe);
        
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // For each of the sections in the app, add a tab to the action bar.
        actionBar.addTab(actionBar.newTab().setText(R.string.tab_add_clips).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.tab_order).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.tab_publish).setTabListener(this));
        
        showHelp();
    }
    
    private void showHelp (){
    	
    	Toast.makeText(this, getString(R.string.help_clip_select),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getSupportActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	mMenu = menu;
        getSupportMenuInflater().inflate(R.menu.activity_scene_editor, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	if (templateStory) {
            		NavUtils.navigateUpTo(this, new Intent(this, StoryTemplate.class));
            	} else {
            		NavUtils.navigateUpFromSameTask(this);
            	}
                return true;
            case R.id.itemForward:
            	int idx = getSupportActionBar().getSelectedNavigationIndex();
            	getSupportActionBar().setSelectedNavigationItem(Math.min(2, idx+1));
            	return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, show the tab contents in the container
        int layout = R.layout.fragment_add_clips;

        if (mMenu != null) {
	        mMenu.findItem(R.id.itemInfo).setVisible(false);
	        mMenu.findItem(R.id.itemTrim).setVisible(false);
        }

        if (tab.getPosition() == 0) {
        	if (mMenu != null) {
        		mMenu.findItem(R.id.itemForward).setEnabled(true);
        	}
        	layout = R.layout.fragment_add_clips;
        	
        } else if (tab.getPosition() == 1) {
            layout = R.layout.fragment_order_clips;

        	if (mMenu != null) {
	            mMenu.findItem(R.id.itemInfo).setVisible(true);
	            mMenu.findItem(R.id.itemTrim).setVisible(true);
		        mMenu.findItem(R.id.itemForward).setEnabled(true);
        	}
        } else if (tab.getPosition() == 2) {
            layout = R.layout.fragment_story_publish;
            mMenu.findItem(R.id.itemForward).setEnabled(false);
        }
        String tag = "" + layout;
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(tag+"");
        
        if (fragment == null) 
        {        	
            try {
				fragment = new SceneChooserFragment(layout, fm, templateJsonPath);

	            Bundle args = new Bundle(); 
	            args.putInt(SceneChooserFragment.ARG_SECTION_NUMBER, tab.getPosition() + 1);
	            fragment.setArguments(args);
	            fm.beginTransaction()
	                    .replace(R.id.container, fragment, tag)
//	                    .addToBackStack(null)
	                    .commit();
			} catch (IOException e) {
				Log.e("SceneEditr","IO erorr", e);
				
			} catch (JSONException e) {
				Log.e("SceneEditr","json error", e);
				
			}
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public class SceneChooserFragment extends Fragment {
    	private final static String TAG = "DummySectionFragment";
        int layout;
        ViewPager mClipViewPager;
        View mView = null;
        ClipPagerAdapter mClipPagerAdapter;
        
        /**
         * The sortable grid view that contains the clips to reorder on the Order tab
         */
        protected DraggableGridView mDGV;
        
        public SceneChooserFragment(int layout, FragmentManager fm, String templatePath) throws IOException, JSONException {
            this.layout = layout;
            
            mClipPagerAdapter = new ClipPagerAdapter(fm, templatePath);
        }

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View view = inflater.inflate(layout, null);
            if (this.layout == R.layout.fragment_add_clips) {
              // Set up the clip ViewPager with the clip adapter.
              mClipViewPager = (ViewPager) view.findViewById(R.id.viewPager);
              mClipViewPager.setPageMargin(-75);
              mClipViewPager.setPageMarginDrawable(R.drawable.ic_action_forward_gray);
              mClipViewPager.setOffscreenPageLimit(5);
              
            } else if (this.layout == R.layout.fragment_order_clips) {
            	mDGV = (DraggableGridView) view.findViewById(R.id.DraggableGridView01);
            	
            	ImageView iv = new ImageView(getActivity());
            	iv.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.cliptype_close));
            	mDGV.addView(iv);
            	
            	iv = new ImageView(getActivity());
            	iv.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.cliptype_detail));
            	mDGV.addView(iv);
            	
            	iv = new ImageView(getActivity());
            	iv.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.cliptype_long));
            	mDGV.addView(iv);
            	
            	iv = new ImageView(getActivity());
            	iv.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.cliptype_medium));
            	mDGV.addView(iv);
            	
            	iv = new ImageView(getActivity());
            	iv.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.cliptype_wide));
            	mDGV.addView(iv);
            	
            	mDGV.setOnRearrangeListener(new OnRearrangeListener() {
					
					@Override
					public void onRearrange(int arg0, int arg1) {
						// TODO Auto-generated method stub
						Log.d(TAG, "grid rearranged");
					}
				});
            	
            	mDGV.setOnItemClickListener(new OnItemClickListener() {
            		
            		@Override
        			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            			Log.d(TAG, "item clicked");
            		}
				});
            } else if (this.layout == R.layout.fragment_story_publish) {
            }
            return view;
        }
        
        @Override
        public void onResume() {
            super.onResume();
            if (this.layout == R.layout.fragment_add_clips) {
    
                (new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPostExecute(Void result) {
                        mClipViewPager.setAdapter(mClipPagerAdapter);
                    }
    
                  @Override
                  protected Void doInBackground(Void... params) {
                      // TODO Auto-generated method stub
                      return null;
                  }
                }).execute();
            } else if (this.layout == R.layout.fragment_order_clips) {
            } else if (this.layout == R.layout.fragment_story_publish) {
            }
        }
        
        /**
         * A {@link FragmentPagerAdapter} that returns a fragment corresponding to the clips we are editing
         */
        public class ClipPagerAdapter extends FragmentPagerAdapter {


            private Template sTemplate;
            
            public ClipPagerAdapter(FragmentManager fm, String path) throws IOException, JSONException {
                super(fm);
              
                loadStoryTemplate(path);
            }


            private void loadStoryTemplate (String path) throws IOException, JSONException
            {
            	sTemplate = new Template();
            	sTemplate.parseAsset(mContext, path);
            	
            	
            	
            }
            
            @Override
            public Fragment getItem(int i) {
            	Template.Clip clip = sTemplate.getClips().get(i);
                Fragment fragment = new ClipThumbnailFragment(clip);
                return fragment;
            }

            @Override
            public int getCount() {
                return 5;
            }
        }
    }
    
    
    
    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public class ClipThumbnailFragment extends Fragment {
    	
    	private Template.Clip clip;
    	
        public ClipThumbnailFragment(Template.Clip clip) {
        	this.clip = clip;
        }

        public static final String ARG_CLIP_TYPE_ID = "clip_type_id";

        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            
        	View view = inflater.inflate(R.layout.fragment_add_clips_page, null);

        	try {
        		
        		ImageView iv = (ImageView)view.findViewById(R.id.clipTypeImage);
	            
        		if (clip.mShotType != -1)
        		{
        			TypedArray drawableIds = getActivity().getResources().obtainTypedArray(R.array.cliptype_thumbnails);
	            
        			int drawableId = drawableIds.getResourceId(clip.mShotType, 0); 
	            
        			iv.setImageResource(drawableId);
        		}
        		else if (clip.mArtwork != null)
        		{
        			iv.setImageBitmap(BitmapFactory.decodeStream(getActivity().getAssets().open(clip.mArtwork)));
        		}
        		
	            if (clip.mShotSize != null)
	            	((TextView)view.findViewById(R.id.clipTypeShotSize)).setText(clip.mShotSize);
	            
	            ((TextView)view.findViewById(R.id.clipTypeGoal)).setText(clip.mGoal);
	            ((TextView)view.findViewById(R.id.clipTypeDescription)).setText(clip.mDescription);
	            ((TextView)view.findViewById(R.id.clipTypeGoalLength)).setText(clip.mLength);
	            ((TextView)view.findViewById(R.id.clipTypeTip)).setText(clip.mTip);
	            ((TextView)view.findViewById(R.id.clipTypeSecurity)).setText(clip.mSecurity);
	            
	            iv.setOnClickListener(new OnClickListener()
	            {
	
					@Override
					public void onClick(View v) {
					//	int cIdx = mClipViewPager.getCurrentItem();
						
						openCaptureMode (clip);
						
					}
	          	  
	            });
            
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            
            return view;
        }
    }



	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void openCaptureMode (Clip clip)
	{

		if (storyMode == STORY_MODE_AUDIO)
		{
			Intent i = new Intent(mContext, SoundRecorder.class);
			i.setType("audio/3gpp");
			startActivityForResult(i,RESULT_AUDIO);

		}
		else
		{
			Intent i = new Intent(mContext, OverlayCamera.class);
			i.putExtra("group", clip.mShotType);
			startActivity(i);
		}
	}

}
