package org.church.rockmobile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.church.rockmobile.adapter.GroupsCategoryListAdapter;
import org.church.rockmobile.adapter.GroupsFeaturedListAdapter;
import org.church.rockmobile.adapter.MyGroupsListAdapter;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.common.NotificationService;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.landing.LoginActivity;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.GroupNotificationModel;
import org.church.rockmobile.model.UserModel;
import org.church.rockmobile.widget.HorizontialListView;
import org.church.rockmobile.widget.SegmentedGroup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.parse.FindCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class GroupsFragment extends BaseFragment implements OnClickListener, OnCheckedChangeListener {

	public static final int REQUEST_ADD_NEW_GROUP = 2000;
	private final int TAB_MY_GROUPS = 0;
	private final int TAB_ALL_GROUPS = 1;

	MainActivity mActivity;

	PullToRefreshGridView mPullRefreshMyGroupsGridView;
	GridView mMyGroupsGridView;
	ProgressBar mMyGroupsProgressBar;
	RelativeLayout mMyGroupsLayout;
	LinearLayout mAllGroupsLayout;
	RelativeLayout mNoGroupLayout;
	HorizontialListView mGroupsFeaturedListView;
	ListView mCategoriesListView;
	ImageButton mNewGroupImageButton;
	ImageButton mSearchImageButton;
	RadioButton mMyGroupsRadioButton;
	RadioButton mAllGroupsRadioButton;
	SegmentedGroup mCategorySegment;

	MyGroupsListAdapter mMyGroupsAdapter;
	GroupsFeaturedListAdapter mGroupsFeaturedAdapter;
	GroupsCategoryListAdapter mGroupsCategoryAdapter;

	boolean mIsMyGroupsLoading;
	boolean mIsAllGroupsLoading;
	boolean mIsCategorysLoading;
	int selectedaTab;

	private final Observer groupRefreshNotification = new Observer() {
		public void update(Observable observable, Object data) {
			if (mMyGroupsRadioButton.isChecked())
				loadMyGroups(false);
			else
				loadAllGroups();
		}
	};

	private final Observer threadMessageNotification = new Observer() {
		public void update(Observable observable, Object data) {
			if (mMyGroupsRadioButton.isChecked() && mMyGroupsAdapter != null)
				mMyGroupsAdapter.notifyDataSetChanged();
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_groups, container, false);
		mActivity = (MainActivity) getActivity();
		selectedaTab = TAB_MY_GROUPS;

		mMyGroupsProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar_myGroups);
		mAllGroupsLayout = (LinearLayout) rootView.findViewById(R.id.layout_allGroups);
		mMyGroupsLayout = (RelativeLayout) rootView.findViewById(R.id.layout_myGroups);
		mNoGroupLayout = (RelativeLayout) rootView.findViewById(R.id.layout_noGroup);

		mCategorySegment = (SegmentedGroup) rootView.findViewById(R.id.segment_category);
		mCategorySegment.setOnCheckedChangeListener(this);
		mMyGroupsRadioButton = (RadioButton) rootView.findViewById(R.id.btn_my_groups);
		mAllGroupsRadioButton = (RadioButton) rootView.findViewById(R.id.btn_all);

		mPullRefreshMyGroupsGridView = (PullToRefreshGridView) rootView.findViewById(R.id.gridView_myGroups);
		mMyGroupsGridView = mPullRefreshMyGroupsGridView.getRefreshableView();
		mPullRefreshMyGroupsGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				loadMyGroups(false);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
				mPullRefreshMyGroupsGridView.onRefreshComplete();
			}

		});
		mGroupsFeaturedListView = (HorizontialListView) rootView.findViewById(R.id.listView_featured);
		mGroupsFeaturedListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
				// TODO Auto-generated method stub
				GroupModel group = AppManager.mAllGroupsList.get(position);
				List<UserModel> adminUsersList = group.getAdminUserList();
				List<UserModel> joinedUsersList = group.getJoinedUserList();

				if (joinedUsersList != null
						&& UtilityMethods.containsParseUser(joinedUsersList, ParseUser.getCurrentUser())
						|| adminUsersList != null
								&& UtilityMethods.containsParseUser(adminUsersList, ParseUser.getCurrentUser())) { // group
																													// joined
					Intent intent = new Intent(mActivity, GroupDetailActivity.class);
					GroupDetailActivity.mGroup = group;
					startActivity(intent);

				} else { // no group member
					Intent intent = new Intent(mActivity, GroupAboutActivity.class);
					GroupAboutActivity.mGroup = group;
					startActivity(intent);
				}
			}
		});

		mCategoriesListView = (ListView) rootView.findViewById(R.id.listView_categories);

		mMyGroupsAdapter = new MyGroupsListAdapter(mActivity, AppManager.mMyGroupsList,
				AppManager.mGroupNotificationList);
		mMyGroupsGridView.setAdapter(mMyGroupsAdapter);

		// track event
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", UserModel.getCurrentUser().getUsername());
		FlurryAgent.logEvent("Groups", params);

		NotificationService.getInstance().addObserver(Constants.LOCAL_NOTIFICATION_TYPE_GROUP_REFRESH,
				groupRefreshNotification);
		NotificationService.getInstance().addObserver(Constants.LOCAL_NOTIFICATION_TYPE_THREAD_MESSAGE,
				threadMessageNotification);

		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (AppManager.mMyGroupsList.size() > 0 || mMyGroupsRadioButton.isChecked() == false) {
			mNoGroupLayout.setVisibility(View.GONE);
		} else {
			mNoGroupLayout.setVisibility(View.VISIBLE);
		}

		if (mMyGroupsRadioButton.isChecked())
			showMyGroups();

		else
			showAllGroups();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);

		// if anonymous user, go to login page
		if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
			ParseUser.logOut();
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			return;
		}

		if (mAllGroupsRadioButton.isChecked()) {
			mActivity.mViewPager.setSwipeable(false);
		}
		initActionBar();
	}

	public void showMyGroups() {
		mMyGroupsProgressBar.setVisibility(View.GONE);
		mMyGroupsLayout.setVisibility(View.VISIBLE);
		mAllGroupsLayout.setVisibility(View.GONE);
		if (AppManager.mMyGroupsList.size() == 0 || AppManager.mIsMyGroupsChanged == true)
			loadMyGroups(true);
		else {
			if (mMyGroupsAdapter == null) {
				mMyGroupsAdapter = new MyGroupsListAdapter(mActivity, AppManager.mMyGroupsList,
						AppManager.mGroupNotificationList);
				mMyGroupsGridView.setAdapter(mMyGroupsAdapter);

			} else {
				mMyGroupsAdapter.notifyDataSetChanged();
			}
		}
	}

	public void showAllGroups() {
		mMyGroupsLayout.setVisibility(View.GONE);
		mAllGroupsLayout.setVisibility(View.VISIBLE);
		showCategories();
		if (AppManager.mAllGroupsList.size() == 0 || AppManager.mIsAllGroupsChanged == true) {
			loadAllGroups();

		} else {
			if (mGroupsFeaturedAdapter == null) {
				mGroupsFeaturedAdapter = new GroupsFeaturedListAdapter(mActivity, AppManager.mAllGroupsList);
				mGroupsFeaturedListView.setAdapter(mGroupsFeaturedAdapter);
			} else {
				mGroupsFeaturedAdapter.notifyDataSetChanged();
			}
		}
	}

	public void loadMyGroups(boolean showProgressBar) {
		if (mIsMyGroupsLoading)
			return;

		mIsMyGroupsLoading = true;
		mNoGroupLayout.setVisibility(View.GONE);

		if (showProgressBar)
			mMyGroupsProgressBar.setVisibility(View.VISIBLE);
		ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Group");
		query1.whereContainsAll("adminUsers", Arrays.asList(ParseUser.getCurrentUser()));

		ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Group");
		query2.whereContainsAll("joinedUsers", Arrays.asList(ParseUser.getCurrentUser()));

		List<ParseQuery<ParseObject>> query0 = new ArrayList<ParseQuery<ParseObject>>();
		query0.add(query1);
		query0.add(query2);

		ParseQuery<ParseObject> query = ParseQuery.or(query0);
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.orderByAscending("title");
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> list, ParseException err) {
				// TODO Auto-generated method stub
				AppManager.mMyGroupsList.clear();

				if (list == null || list.size() == 0) {
					if (mMyGroupsRadioButton.isChecked()) {
						mNoGroupLayout.setVisibility(View.VISIBLE);
					}
					mIsMyGroupsLoading = false;
					mMyGroupsProgressBar.setVisibility(View.GONE);
					if (mMyGroupsAdapter != null) {
						mMyGroupsAdapter.notifyDataSetChanged();
					}
					mPullRefreshMyGroupsGridView.onRefreshComplete();

				} else {
					AppManager.mMyGroupsList.clear();
					List<ParseObject> sortedList = UtilityMethods.sortBySortingValue(list);
					for (ParseObject object : sortedList){
						AppManager.mMyGroupsList.add((GroupModel) object);
					}
					
					// get my groups notification count
					ParseQuery<GroupNotificationModel> query = new ParseQuery<GroupNotificationModel>(
							"GroupNotification");
					query.whereEqualTo("churchId", Constants.CHURCH_ID);
					query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
					query.findInBackground(new FindCallback<GroupNotificationModel>() {

						@Override
						public void done(List<GroupNotificationModel> groupNotificationList, ParseException err) {
							// TODO Auto-generated method stub
							AppManager.mGroupNotificationList.clear();
							AppManager.mGroupNotificationList.addAll(groupNotificationList);

							mNoGroupLayout.setVisibility(View.INVISIBLE);

							mMyGroupsAdapter = new MyGroupsListAdapter(mActivity, AppManager.mMyGroupsList,
									AppManager.mGroupNotificationList);
							mMyGroupsGridView.setAdapter(mMyGroupsAdapter);
							mIsMyGroupsLoading = false;
							mMyGroupsProgressBar.setVisibility(View.GONE);
							AppManager.mIsMyGroupsChanged = false;
							mPullRefreshMyGroupsGridView.onRefreshComplete();
						}
					});
				}
			}
		});
	}

	public void loadAllGroups() {
		if (mIsAllGroupsLoading)
			return;

		mIsAllGroupsLoading = true;
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Group");
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.whereEqualTo("isFeatured", true);
		query.orderByDescending("title");
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> list, ParseException err) {
				// TODO Auto-generated method stub
				if (list != null) {
					AppManager.mAllGroupsList.clear();
					List<ParseObject> sortedList = UtilityMethods.sortBySortingValue(list);
					for (ParseObject object : sortedList){
						GroupModel group = (GroupModel)object;
						if (UtilityMethods.containsParseUser(group.getGroupUserList(),
								ParseUser.getCurrentUser()) == false) {
							AppManager.mAllGroupsList.add(group);
						}
					}
				}
				
				mGroupsFeaturedAdapter = new GroupsFeaturedListAdapter(mActivity, AppManager.mAllGroupsList);
				mGroupsFeaturedListView.setAdapter(mGroupsFeaturedAdapter);
				mIsAllGroupsLoading = false;
				AppManager.mIsAllGroupsChanged = false;
			}
		});
	}

	public void showCategories() {
		if (AppManager.getInstance().getCategories() == null) {

			if (mIsCategorysLoading)
				return;

			mIsCategorysLoading = true;
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Category");
			query.whereEqualTo("churchId", Constants.CHURCH_ID);
			query.setLimit(Constants.PARSE_QUERY_MAX_LIMIT_COUNT);
			query.orderByAscending("title");
			query.findInBackground(new FindCallback<ParseObject>() {

				@Override
				public void done(List<ParseObject> objList, ParseException err) {
					// TODO Auto-generated method stub
					if (objList != null && objList.size() > 0) {
						objList = UtilityMethods.sortBySortingValue(objList);
						AppManager.mCategories = new String[objList.size()];
						for (int i = 0; i < objList.size(); i++) {
							AppManager.mCategories[i] = objList.get(i).getString("title");
						}

						mGroupsCategoryAdapter = new GroupsCategoryListAdapter(mActivity,
								UtilityMethods.convertStringsToList(AppManager.mCategories));
						mCategoriesListView.setAdapter(mGroupsCategoryAdapter);
					}

					mIsCategorysLoading = false;
				}

			});

		} else {

			mGroupsCategoryAdapter = new GroupsCategoryListAdapter(mActivity,
					UtilityMethods.convertStringsToList(AppManager.mCategories));
			mCategoriesListView.setAdapter(mGroupsCategoryAdapter);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.imageButton_plus:
			intent = new Intent(mActivity, NewGroupActivity.class);
			NewGroupActivity.mGroup = null;
			startActivityForResult(intent, REQUEST_ADD_NEW_GROUP);
			break;

		case R.id.imageButton_search:
			intent = new Intent(mActivity, GroupSearchActivity.class);
			startActivity(intent);
			break;
		}
	}

	// @Override
	// public void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// // TODO Auto-generated method stub
	// super.onActivityResult(requestCode, resultCode, data);
	// if(data != null && resultCode == Activity.RESULT_OK){
	// if(requestCode == REQUEST_ADD_NEW_GROUP){
	// mMyGroupsRadioButton.setChecked(true);
	// showMyGroups(true);
	// }
	// }
	// }

	public void initActionBar() {
		// customize actionbar
		LayoutInflater inflator = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.actionbar_groups, null);

		mActivity.mActionBar.setCustomView(v);
		mNewGroupImageButton = (ImageButton) v.findViewById(R.id.imageButton_plus);
		mNewGroupImageButton.setOnClickListener(this);

		mSearchImageButton = (ImageButton) v.findViewById(R.id.imageButton_search);
		mSearchImageButton.setOnClickListener(this);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		mNoGroupLayout.setVisibility(View.INVISIBLE);

		switch (checkedId) {
		case R.id.btn_my_groups:
			showMyGroups();
			mActivity.mViewPager.setSwipeable(true);
			selectedaTab = TAB_MY_GROUPS;
			break;

		case R.id.btn_all:
			showAllGroups();
			mActivity.mViewPager.setSwipeable(false);
			selectedaTab = TAB_ALL_GROUPS;
			break;
		}
	}

//	private void sortGroups(List<GroupModel> list) {
//		List<GroupModel> positiveSortingList = new ArrayList<GroupModel>();
//		List<GroupModel> zeroOrNullSortingList = new ArrayList<GroupModel>();
//		List<GroupModel> negativeSortingList = new ArrayList<GroupModel>();
//
//		for (GroupModel groupModel : list) {
//			int sorting = groupModel.getSorting();
//			if (sorting == 0) {
//				zeroOrNullSortingList.add(groupModel);
//			} else if (sorting > 0) {
//				positiveSortingList.add(groupModel);
//			} else {
//				negativeSortingList.add(groupModel);
//			}
//		}
//
//		Collections.sort(positiveSortingList, new Comparator<GroupModel>() {
//
//			@Override
//			public int compare(GroupModel lhs, GroupModel rhs) {
//				// TODO Auto-generated method stub
//				return rhs.getSorting().compareTo(lhs.getSorting());
//			}
//		});
//
//		Collections.sort(negativeSortingList, new Comparator<GroupModel>() {
//
//			@Override
//			public int compare(GroupModel lhs, GroupModel rhs) {
//				// TODO Auto-generated method stub
//				return rhs.getSorting().compareTo(lhs.getSorting());
//			}
//		});
//
//		Collections.sort(zeroOrNullSortingList, new Comparator<GroupModel>() {
//
//			@Override
//			public int compare(GroupModel lhs, GroupModel rhs) {
//				// TODO Auto-generated method stub
//				return lhs.getTitle().compareTo(rhs.getTitle());
//			}
//		});
//
//		list.clear();
//		list.addAll(positiveSortingList);
//		list.addAll(zeroOrNullSortingList);
//		list.addAll(negativeSortingList);
//	}
//	
//	private void sortCategories(List<ParseObject> objList) {
//		
//		List<ParseObject> positiveSortingList = new ArrayList<ParseObject>();
//		List<ParseObject> zeroOrNullSortingList = new ArrayList<ParseObject>();
//		List<ParseObject> negativeSortingList = new ArrayList<ParseObject>();
//
//		for (ParseObject categoryParseObject : objList) {
//			int sorting = Integer.parseInt(String.valueOf(categoryParseObject.getNumber("sorting") == null ? 0 : categoryParseObject.getNumber("sorting")));
//			if (sorting == 0) {
//				zeroOrNullSortingList.add(categoryParseObject);
//			} else if (sorting > 0) {
//				positiveSortingList.add(categoryParseObject);
//			} else {
//				negativeSortingList.add(categoryParseObject);
//			}
//		}
//
//		Collections.sort(positiveSortingList, new Comparator<ParseObject>() {
//
//			@Override
//			public int compare(ParseObject lhs, ParseObject rhs) {
//				// TODO Auto-generated method stub
//				Integer lhsSorting = Integer.parseInt(String.valueOf(lhs.getNumber("sorting") == null ? 0 : lhs.getNumber("sorting")));
//				Integer rhsSorting = Integer.parseInt(String.valueOf(rhs.getNumber("sorting") == null ? 0 : rhs.getNumber("sorting")));
//				return rhsSorting.compareTo(lhsSorting);
//			}
//		});
//
//		Collections.sort(negativeSortingList, new Comparator<ParseObject>() {
//
//			@Override
//			public int compare(ParseObject lhs, ParseObject rhs) {
//				// TODO Auto-generated method stub
//				Integer lhsSorting = Integer.parseInt(String.valueOf(lhs.getNumber("sorting") == null ? 0 : lhs.getNumber("sorting")));
//				Integer rhsSorting = Integer.parseInt(String.valueOf(rhs.getNumber("sorting") == null ? 0 : rhs.getNumber("sorting")));
//				return rhsSorting.compareTo(lhsSorting);
//			}
//		});
//
//		Collections.sort(zeroOrNullSortingList, new Comparator<ParseObject>() {
//
//			@Override
//			public int compare(ParseObject lhs, ParseObject rhs) {
//				// TODO Auto-generated method stub
//				return lhs.getString("title").compareTo(rhs.getString("title"));
//			}
//		});
//
//		objList.clear();
//		objList.addAll(positiveSortingList);
//		objList.addAll(zeroOrNullSortingList);
//		objList.addAll(negativeSortingList);
//	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		NotificationService.getInstance().removeObserver(Constants.LOCAL_NOTIFICATION_TYPE_GROUP_REFRESH,
				groupRefreshNotification);
		NotificationService.getInstance().removeObserver(Constants.LOCAL_NOTIFICATION_TYPE_THREAD_MESSAGE,
				threadMessageNotification);
	}
}
