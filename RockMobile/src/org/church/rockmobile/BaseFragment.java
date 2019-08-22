package org.church.rockmobile;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;

public class BaseFragment extends Fragment{

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		
		MainActivity mainActivity = (MainActivity)getActivity();
		mainActivity.mViewPager.setSwipeable(true);
	}
}
