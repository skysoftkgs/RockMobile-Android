package org.church.rockmobile.landing;

import org.church.rockmobile.R;
import org.church.rockmobile.common.AppManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;


public final class PlaceSlideFragment extends Fragment {
    int imageResourceId;

    public PlaceSlideFragment(int i) {
        imageResourceId = i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        ImageView image = new ImageView(getActivity());
        image.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        image.setScaleType(ScaleType.CENTER_INSIDE);
        image.setImageResource(imageResourceId);
        
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setLayoutParams(new LayoutParams());
        layout.setBackgroundColor(Color.TRANSPARENT);

        layout.setGravity(Gravity.CENTER);
        layout.addView(image);
        
        return layout;
    }
    
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
    	super.setUserVisibleHint(isVisibleToUser);
    	
    	if (isVisibleToUser) {
    		Button goButton = (Button)getActivity().findViewById(R.id.button_go);
    		goButton.setOnClickListener(new OnClickListener() {
			
    			@Override
    			public void onClick(View arg0) {
    				// TODO Auto-generated method stub
    				AppManager.getInstance().setIsTutorialPassed(getActivity(), true);
    				Intent intent = new Intent(getActivity(), LandingActivity.class);
    				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
    				startActivity(intent);
    			}
    		});
      
    		if(imageResourceId == R.drawable.tutorial_livestream){
    			goButton.setText(getResources().getString(R.string.tutorial_go));
      
    		}else{
    			goButton.setText(getResources().getString(R.string.tutorial_skip));
    		}
    	}  
    }
        
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }
}
