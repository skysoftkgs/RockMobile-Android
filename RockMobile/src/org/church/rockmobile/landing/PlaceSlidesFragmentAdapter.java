package org.church.rockmobile.landing;

import org.church.rockmobile.R;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

public class PlaceSlidesFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {

	private int[] Images = new int[] { R.drawable.tutorial_invitation, R.drawable.tutorial_notification_1,
	    R.drawable.tutorial_group, R.drawable.tutorial_message, R.drawable.tutorial_livestream
	
	};
	
//	protected static final int[] ICONS = new int[] { R.drawable.marker,
//	    R.drawable.marker, R.drawable.marker, R.drawable.marker };
	
	private int mCount = Images.length;
	
	public PlaceSlidesFragmentAdapter(FragmentManager fm) {
		super(fm);
	}
	
	@Override
	public Fragment getItem(int position) {
		return new PlaceSlideFragment(Images[position]);
	}
	
	@Override
	public int getCount() {
		return mCount;
	}
	
//	@Override
//	public int getIconResId(int index) {
//		return ICONS[index % ICONS.length];
//	}
	
	public void setCount(int count) {
		if (count > 0 && count <= 10) {
		    mCount = count;
		    notifyDataSetChanged();
		}
	}

	@Override
	public int getIconResId(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
}