package org.church.rockmobile;

import io.vov.vitamio.utils.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.model.StoryModel;
import org.church.rockmobile.model.UserModel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.RefreshCallback;
import com.parse.SaveCallback;

public class StoryDetailActivity extends Activity implements OnClickListener{

	public final String TAG = "StoryDetailActivity";
	
	ImageButton mBackImageButton;
	ImageButton mShareImageButton;
	ImageButton mBookmarkImageButton;
	ImageView mCoverPhotoImageView;
	TextView mTimeTextView;
	TextView mTitleTextView;
	TextView mNameTextView;
	WebView mContentWebView;
	
	public static StoryModel mStory;
	ParseUser mCurrentUser;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_story_detail);
	    
	    initUI();
	    
	    //track event
  		Map<String, String> params = new HashMap<String, String>();
  		params.put("username", UserModel.getCurrentUser().getUsername());
  		FlurryAgent.logEvent("Story detail", params); 
	}
	 
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()){
			case R.id.imageButton_back:
				super.onBackPressed();
				break;
				
			case R.id.imageButton_share:
				shareStory();
				break;
				
			case R.id.imageButton_bookmark:
				if(mStory.getIsBookMarked()){
					unSetBookMark();
				}else{
					setBookMark();
				}
				break;
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	public void initUI(){
		mCurrentUser = ParseUser.getCurrentUser();
		
		mBackImageButton = (ImageButton) findViewById(R.id.imageButton_back);
		mBackImageButton.setOnClickListener(this);
		
		mShareImageButton = (ImageButton) findViewById(R.id.imageButton_share);
		mShareImageButton.setOnClickListener(this);
				
		mBookmarkImageButton = (ImageButton) findViewById(R.id.imageButton_bookmark);
		mBookmarkImageButton.setOnClickListener(this);
		
		mStory.refreshInBackground(new RefreshCallback() {
			
			@Override
			public void done(ParseObject object, ParseException arg1) {
				// TODO Auto-generated method stub
				if(object != null){
					mStory = (StoryModel)object;
					if(mStory.getBookmarkUserList() != null && mStory.getBookmarkUserList().contains(mCurrentUser.getObjectId())){
						mBookmarkImageButton.setImageResource(R.drawable.btn_bookmark_selected);
						mStory.setIsBookMarked(true);
					}else{
						mBookmarkImageButton.setImageResource(R.drawable.btn_bookmark_normal);
						mStory.setIsBookMarked(false);
					}
				}
			}
		});
		
		mTimeTextView = (TextView) findViewById(R.id.textView_time);
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
		if(mStory.getStoryDate() != null)
			mTimeTextView.setText(sdf.format(mStory.getStoryDate()));
		
		mTitleTextView = (TextView) findViewById(R.id.textView_title);
		mTitleTextView.setText(mStory.getTitle());
		
		mNameTextView = (TextView) findViewById(R.id.textView_authorName);
		String author = mStory.getAuthor();
		if(author != null)
			mNameTextView.setText("by " + author);
		
		mContentWebView = (WebView) findViewById(R.id.webView_content);
		String mimeType = "text/html";
        String encoding = "UTF-8";
        mContentWebView.loadDataWithBaseURL("", mStory.getDescription(), mimeType, encoding, "");
//		mContentTextView.setText(Html.fromHtml(mStory.getDescription()));
		
		mCoverPhotoImageView = (ImageView) findViewById(R.id.imageView_coverPhoto);
		ViewGroup.LayoutParams imageViewParams = mCoverPhotoImageView.getLayoutParams();
	    imageViewParams.height = (int) (AppManager.getInstance().mScreenWidth / 2.105);
	    mCoverPhotoImageView.setLayoutParams(imageViewParams);
	    
		if(mStory.getPhotoFile() != null){        
	        // display cover photo image
	        AppManager.getInstance().mImageLoader.displayImage(mStory.getPhotoFile().getUrl(),
	        		mCoverPhotoImageView, AppManager.getInstance().options, null, null);
	        Log.d(">>Image", mStory.getPhotoFile().getUrl());
        }else if(mStory.getImageLink() != null){
        	AppManager.getInstance().mImageLoader.displayImage(mStory.getImageLink(),
	        		mCoverPhotoImageView, AppManager.getInstance().options, null, null);
        	Log.d(">>Image", mStory.getImageLink());
        }else{
        	mCoverPhotoImageView.setVisibility(View.GONE);
        }
	}
	
	public void setBookMark(){
		RockMobileApplication.getInstance().showProgressFullScreenDialog(this);
		mStory.addUserToBookmarkUserList(mCurrentUser.getObjectId());
		mStory.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException err) {
				// TODO Auto-generated method stub
				RockMobileApplication.getInstance().hideProgressDialog();
				if(err == null){
					mBookmarkImageButton.setImageResource(R.drawable.btn_bookmark_selected);
					updateStoryWithBookmark(true);
				}
			}
		});
	}
	
	public void unSetBookMark(){
		RockMobileApplication.getInstance().showProgressFullScreenDialog(this);
		mStory.removeUserFromBookmarkUserList(mCurrentUser.getObjectId());
		mStory.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException err) {
				// TODO Auto-generated method stub
				RockMobileApplication.getInstance().hideProgressDialog();
				if(err == null){
					mBookmarkImageButton.setImageResource(R.drawable.btn_bookmark_normal);
					updateStoryWithBookmark(false);
				}
			}
		});
	}
	
	public void updateStoryWithBookmark(boolean isBookMarked){
		mStory.setIsBookMarked(isBookMarked);
		
		if(isBookMarked){
			AppManager.mBookmarkStoryList.add(mStory);
			RockMobileApplication.getInstance().showToast(StoryDetailActivity.this, "Bookmarked", Toast.LENGTH_SHORT);
		}else{
			mStory.removeStoryFromList(AppManager.mBookmarkStoryList);
			RockMobileApplication.getInstance().showToast(StoryDetailActivity.this, "Unbookmarked", Toast.LENGTH_SHORT);
		}
	}
	
	@SuppressLint("SdCardPath")
	public void shareStory() {
		String url = mStory.getShareLink();
		if(url == null){
			url = "http://www.fishermenlabs.com/rockmobilecms/";
		}
		
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("*/*");
		
		Bitmap icon = null;
		if(mStory.getPhotoFile() != null){
			icon = AppManager.getInstance().mImageLoader.loadImageSync(mStory.getPhotoFile().getUrl());
		}else if(mStory.getImageLink() != null){
			icon = AppManager.getInstance().mImageLoader.loadImageSync(mStory.getImageLink());
		}

		if(icon != null){
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
			File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
			try {
			    f.createNewFile();
			    FileOutputStream fo = new FileOutputStream(f);
			    fo.write(bytes.toByteArray());
			    fo.close();
			} catch (IOException e) {                       
			        e.printStackTrace();
			}
			shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
		}
			
//		shareIntent.putExtra(Intent.EXTRA_SUBJECT, mStory.getTitle());
		shareIntent.putExtra(Intent.EXTRA_TEXT, url);
		startActivity(Intent.createChooser(shareIntent, "Share Story"));
    }
}
