package org.church.rockmobile;

import org.church.rockmobile.adapter.AppSectionsPagerAdapter;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.landing.LandingActivity;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.ThreadMessageModel;
import org.church.rockmobile.model.ThreadModel;
import org.church.rockmobile.widget.CustomViewPager;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.newrelic.agent.android.NewRelic;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener{

	public CustomViewPager mViewPager;
	AppSectionsPagerAdapter mAppSectionsPagerAdapter;
	ActionBar mActionBar;
	private long mBackPressed;
	private boolean mFirstLoading;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirstLoading = true;
        
        NewRelic.withApplicationToken("AAb0cf503e8416359a01677d6987ca40740c3dea29").start(this.getApplication());
        
        setupActionBar();
        AppManager.getInstance().initDataManager(getApplicationContext());
        
        // Create the adapter that will return a fragment for each of the primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager(), this);
      
        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (CustomViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
            	mActionBar.setSelectedNavigationItem(position);
            	if(AppManager.mAnonymousLogin == true && (position == 0 || position == 1)){
            		AppManager.getInstance().logout(getApplicationContext());
            		Intent intent;	
	            	intent = new Intent(MainActivity.this, LandingActivity.class);
		            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
		            startActivity(intent);
            	}

            }
        });
        
        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
        	RelativeLayout view = (RelativeLayout) getLayoutInflater().inflate(R.layout.custom_tab, null);
            ImageView icon = (ImageView) view.findViewById(R.id.imageView_tabIcon);
            icon.setImageResource(AppManager.mTabIconArray.getResourceId(i, -1));
            TextView title = (TextView) view.findViewById(R.id.textView_tabTitle);
            title.setText(AppManager.mTabTitleArray[i]);
            
        	mActionBar.addTab(
        			mActionBar.newTab()
        					.setCustomView(view)
//        					.setIcon(R.drawable.icon_tab_groups)
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        
        if(ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser()))
        	mActionBar.setSelectedNavigationItem(2);
        
		ParseAnalytics.trackAppOpened(getIntent());
    }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		Bundle bundle = getIntent().getExtras();
		if(bundle == null)
			return;
		
		//go to specific page from notification
		try {
			if(bundle.getString("com.parse.Data") != null){
				JSONObject json = new JSONObject(bundle.getString("com.parse.Data"));
				if (json != null && mFirstLoading == true){
					if(json.getString("type").equals(Constants.PUSH_NOTIFICATION_TYPE_GROUP_INVITE)){
						Intent intent = new Intent(this, RequestNotificationActivity.class);
						startActivity(intent);
						
					}else if(json.getString("type").equals(Constants.PUSH_NOTIFICATION_TYPE_NEW_THREAD)){
						String threadId = json.getString("threadId");
						ParseQuery<ThreadModel> query = new ParseQuery<ThreadModel>("Thread");
						query.whereEqualTo("objectId", threadId);
						query.include("group");
						query.getFirstInBackground(new GetCallback<ThreadModel>() {

							@Override
							public void done(ThreadModel thread,
									ParseException arg1) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(MainActivity.this, ThreadMessagesActivity.class);
								ThreadMessagesActivity.mThread = thread;
								ThreadMessagesActivity.mGroup = thread.getGroup();
								startActivity(intent);
							}
						});
						
					}else if(json.getString("type").equals(Constants.PUSH_NOTIFICATION_TYPE_THREAD_MESSAGE)){
						String threadMessageId = json.getString("threadMessageId");
						ParseQuery<ThreadMessageModel> query = new ParseQuery<ThreadMessageModel>("ThreadMessage");
						query.whereEqualTo("objectId", threadMessageId);
						query.include("thread");
						query.getFirstInBackground(new GetCallback<ThreadMessageModel>() {

							@Override
							public void done(final ThreadMessageModel threadMessage,
									ParseException arg1) {
								// TODO Auto-generated method stub
								threadMessage.getThread().getGroup().fetchInBackground(new GetCallback<GroupModel>() {

									@Override
									public void done(GroupModel group, ParseException arg1) {
										// TODO Auto-generated method stub
										Intent intent = new Intent(MainActivity.this, ThreadMessagesActivity.class);
										ThreadMessagesActivity.mThread = threadMessage.getThread();
										ThreadMessagesActivity.mGroup = group;
										startActivity(intent);
									}
								});
							}
						});
					}
					
				}
			}
			
		}catch(Exception e){
			
		}
		
		mFirstLoading = false;
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		FlurryAgent.onStartSession(this, Constants.FLURRY_APP_KEY);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	@Override
    public void onBackPressed(){
    	if (mBackPressed + 3000 > System.currentTimeMillis()){
      
    		Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
        else{
            Toast.makeText(getBaseContext(),
                    "Press once again to exit!", Toast.LENGTH_SHORT)
                    .show();
        }
    	mBackPressed = System.currentTimeMillis();
    }
    
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		 mViewPager.setCurrentItem(tab.getPosition());
	}


	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
    
	public void setupActionBar() {
	    mActionBar = getActionBar();
	    mActionBar.setDisplayShowTitleEnabled(false);
	    mActionBar.setDisplayUseLogoEnabled(false);
	    mActionBar.setDisplayHomeAsUpEnabled(false);
	    mActionBar.setDisplayShowCustomEnabled(true);
	    mActionBar.setDisplayShowHomeEnabled(false);
		
        // Specify that we will be displaying tabs in the action bar.
	    mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}
    
	public void setTab(int index){
		mActionBar.setSelectedNavigationItem(index);
	}
}
