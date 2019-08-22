package org.church.rockmobile.adapter;

import org.church.rockmobile.CampusFragment;
import org.church.rockmobile.FeedFragment;
import org.church.rockmobile.GiveFragment;
import org.church.rockmobile.GroupsFragment;
import org.church.rockmobile.LiveStreamFragment;
import org.church.rockmobile.R;
import org.church.rockmobile.SeriesFragment;
import org.church.rockmobile.SeriesFragmentV1;
import org.church.rockmobile.SettingsFragment;
import org.church.rockmobile.StoryFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

	Context mContext;

	public AppSectionsPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		mContext = context;
	}

	@Override
	public Fragment getItem(int i) {
		switch (i) {
		case 0:
			return new FeedFragment();

		case 1:
			return new GroupsFragment();

		case 2:
			return new SeriesFragmentV1();

		case 3:
			return new LiveStreamFragment();

		case 4:
			return new StoryFragment();

		case 5:
			return new CampusFragment();

		case 6:
			return new GiveFragment();

		case 7:
			return new SettingsFragment();

		default:
			return new FeedFragment();
		}
	}

	@Override
	public int getCount() {
		return 8;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case 0:
			return mContext.getResources().getString(R.string.feed);
		case 1:
			return mContext.getResources().getString(R.string.groups);
		case 2:
			return mContext.getResources().getString(R.string.messages);
		case 3:
			return mContext.getResources().getString(R.string.livestream);
		case 4:
			return mContext.getResources().getString(R.string.stories);
		case 5:
			return mContext.getResources().getString(R.string.campuses);
		case 6:
			return mContext.getResources().getString(R.string.give);
		case 7:
			return mContext.getResources().getString(R.string.settings);
		default:
			return mContext.getResources().getString(R.string.livestream);
		}
	}
}
