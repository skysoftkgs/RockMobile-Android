package org.church.rockmobile.adapter;

import java.util.List;

import org.church.rockmobile.GroupDetailActivity;
import org.church.rockmobile.R;
import org.church.rockmobile.RockMobileApplication;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.model.EventModel;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.RequestModel;
import org.church.rockmobile.model.ThreadMessageModel;
import org.church.rockmobile.model.ThreadModel;
import org.church.rockmobile.model.UserModel;
import org.church.rockmobile.service.PushNotificationService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.ParseException;

public class RequestNotificationListAdapter extends BaseAdapter {

	// Declare Variables
	private static final int TYPE_REQUEST = 0;
    private static final int TYPE_EMPTY = 1;
    
	Context mContext;
	LayoutInflater mInflater;
	private List<RequestModel> mRequestList;

	public RequestNotificationListAdapter(Context context,
			List<RequestModel> requestList) {
		this.mContext = context;
		this.mRequestList = requestList;
		mInflater = LayoutInflater.from(mContext);
	}

	public class ViewHolder {
		TextView requestTextView;
		ImageButton acceptImageButton;
		ImageButton rejectImageButton;
	}

	@Override
    public int getItemViewType(int position) {
    	if(mRequestList == null || mRequestList.size() == 0){
    		return TYPE_EMPTY;
    	}else{
    		return TYPE_REQUEST;
    	}
	}
    	
	@Override
    public int getViewTypeCount() {
        return 2;
    }
	
	@Override
	public int getCount() {
		if(mRequestList == null || mRequestList.size() == 0){
    		return 1;
    	}else{
    		return mRequestList.size();
    	}
	}

	@Override
	public Object getItem(int position) {
		if(mRequestList == null || mRequestList.size() == 0){
    		return null;
    	}else{
    		return mRequestList.get(position);
    	}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup parent) {
		final ViewHolder holder;
			holder = new ViewHolder();
			int viewType = getItemViewType(position);
        	switch(viewType){
	        	case TYPE_REQUEST:
					view = mInflater.inflate(R.layout.item_request, null);
					holder.requestTextView = (TextView) view
							.findViewById(R.id.textView_request);
					holder.acceptImageButton = (ImageButton) view
							.findViewById(R.id.imageButton_accept);
					holder.rejectImageButton = (ImageButton) view
							.findViewById(R.id.imageButton_reject);
					
					final RequestModel requestModel = mRequestList.get(position);
					final GroupModel group = requestModel.getGroup();
					final EventModel event = requestModel.getEvent();
					final UserModel fromUser = requestModel.getFromUser();
					final UserModel groupInvitationUser = requestModel
							.getGroupInvitationUser();
					
					final String type = requestModel.getType();
					if (type.equals(Constants.REQUEST_TYPE_GROUP_JOIN)) {
						holder.requestTextView.setText(Html.fromHtml("<b>"
								+ fromUser.getRealUsername()
								+ "</b> requested access to <b>" + group.getTitle()
								+ "</b> group."));

					} else if (type.equals(Constants.REQUEST_TYPE_EVENT_ADD)) {
						holder.requestTextView.setText(Html.fromHtml("<b>"
								+ fromUser.getRealUsername() + "</b> has added <b>"
								+ event.getTitle() + "</b> event in <b>" + group.getTitle()
								+ "</b> group."));

					} else if (type.equals(Constants.REQUEST_TYPE_EVENT_CHANGE)) {
						holder.requestTextView
								.setText(Html.fromHtml(String
										.format("<b>%s</b> changed the <b>%s</b> event to <b>%s</b> event in <b>%s</b> gruop",
												fromUser.getRealUsername(), requestModel
														.getOldEvent().getTitle(), event
														.getTitle(), group.getTitle())));

					} else if (type.equals(Constants.REQUEST_TYPE_GROUP_INVITATION)) {
						if (requestModel.getGroupInviteManager().equals(
								Constants.REQUEST_GROUP_INVITE_MANAGER_OWNER)) {
							holder.requestTextView.setText(Html.fromHtml("<b>"
									+ groupInvitationUser.getRealUsername()
									+ "</b> sent invitation to you to <b>"
									+ group.getTitle() + "</b> group."));

						} else if (requestModel.getGroupInviteManager().equals(
								Constants.REQUEST_GROUP_INVITE_MANAGER_ADMIN)) {
							holder.requestTextView.setText(Html.fromHtml("<b>"
									+ groupInvitationUser.getRealUsername()
									+ "</b> sent invitation to <b>"
									+ fromUser.getRealUsername() + "</b> to <b>"
									+ group.getTitle() + "</b> group."));
						}
						
					} else if (type.equals(Constants.REQUEST_TYPE_MESSAGE_FLAG)) {
						ThreadMessageModel threadMessage = requestModel.getThreadMessage();
						ThreadModel thread = requestModel.getThread();
						holder.requestTextView.setText(Html.fromHtml(String.format("<b>%s</b> flagged <b>%s</b> message of <b>%s</b> thread in <b>%s</b> gruop",
										fromUser.getRealUsername(),
										threadMessage.getMessage(),
										thread.getTitle(),
										group.getTitle())));

					} else if (type.equals(Constants.REQUEST_TYPE_THREAD_FLAG)) {
						ThreadModel thread = requestModel.getThread();
						holder.requestTextView.setText(Html.fromHtml(String.format("<b>%s</b> flagged <b>%s</b> thread in <b>%s</b> gruop",
										fromUser.getRealUsername(),
										thread.getTitle(),
										group.getTitle())));

					}

					holder.acceptImageButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							RockMobileApplication.getInstance()
									.showProgressFullScreenDialog((Activity) mContext);
							PushNotificationService.getInstance().acceptRequest(requestModel, new PushNotificationService.ParseFunctionCallback() {
								
								@Override
								public void done(String result, ParseException err) {
									// TODO Auto-generated method stub
									RockMobileApplication.getInstance()
									.hideProgressDialog();
									RequestNotificationListAdapter.this
									.notifyDataSetChanged();
								}
							});
						}
					});

					holder.rejectImageButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							RockMobileApplication.getInstance()
									.showProgressFullScreenDialog((Activity) mContext);

							PushNotificationService.getInstance().rejectRequest(requestModel, new PushNotificationService.ParseFunctionCallback() {
								
								@Override
								public void done(String result, ParseException err) {
									// TODO Auto-generated method stub
									RockMobileApplication.getInstance()
									.hideProgressDialog();
//									AppManager.mRequestsList.remove(requestModel);
									RequestNotificationListAdapter.this.notifyDataSetChanged();
								}
							});
						}
					});

					view.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if (type.equals(Constants.REQUEST_TYPE_GROUP_JOIN)
									|| type.equals(Constants.REQUEST_TYPE_EVENT_ADD)
									|| type.equals(Constants.REQUEST_TYPE_EVENT_CHANGE)) {
								Intent intent = new Intent(mContext, GroupDetailActivity.class);
					        	GroupDetailActivity.mGroup = group;
					        	mContext.startActivity(intent);
							}
						}
					});

					break;
					
	        	case TYPE_EMPTY:
	        		view = mInflater.inflate(R.layout.item_no_requests, null);
        	}
        	

		return view;
	}
}