package org.church.rockmobile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.UserModel;
import org.church.rockmobile.service.PushNotificationService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class NewGroupActivity extends Activity implements OnClickListener{

	public final String TAG = "NewGroupActivity";
	private final int PICK_FROM_CAMERA = 1001;
	private final int PICK_FROM_FILE = 1002;
	
	TextView mTitleTextView;
	RelativeLayout mAddNewPhotoLayout;
	RelativeLayout mEditPhotoLayout;
	Spinner mCategorySpinner;
	ImageButton mApplyImageButton;
	ImageButton mCloseImageButton;
	ImageButton mImportImageButton;
	ImageButton mTrashImageButton;
	ImageButton mEditImageButton;
	ImageButton mCategoryDetailImageButton;
	EditText mNameEditText;
	EditText mAboutEditText;
	ImageView mGroupPictureImageView;
	ImageView mBlurPictureImageView;
	Switch mGroupPermissionSwitch;
	
	Bitmap mGroupPicture;
	public static GroupModel mGroup;
	
	boolean mIsGroupPictureChanged;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_new_group);
	    
	    initUI();
	    
	    //track event
  		Map<String, String> params = new HashMap<String, String>();
  		params.put("username", UserModel.getCurrentUser().getUsername());
  		FlurryAgent.logEvent("New group", params); 
	}
	 
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.imageButton_apply:
				addNewGroup();
				break;
				
			case R.id.imageButton_close:
				super.onBackPressed();
				break;
				
			case R.id.imageButton_importPhoto:
				selectImageFromCameraOrGallery();
				break;
				
			case R.id.imageButton_trash:
				removeGroupPicture();
				break;
				
			case R.id.imageButton_edit:
				selectImageFromCameraOrGallery();
				break;
				
			case R.id.imageButton_categoryDetail:
				Intent intent = new Intent(this, CategoryGroupsActivity.class);
				intent.putExtra(Constants.BUNDLE_CATEGORY, mCategorySpinner.getSelectedItem().toString());
				startActivity(intent);
				break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{ 
	    super.onActivityResult(requestCode, resultCode, data);
	    System.out.println(" ****** R code"+requestCode+" res code"+resultCode+"data"+data);
	    
	    Bitmap bm = null;
	    switch (requestCode) {
	    	    
		case PICK_FROM_CAMERA:
			if(resultCode == Activity.RESULT_OK)
		    {
				File f = new File(Environment.getExternalStorageDirectory().toString());
				for(File temp : f.listFiles()){
					if(temp.getName().equals("temp.jpg")){
						f = temp;
						break;
					}
				}
				try{
					//display selected image					
					bm = UtilityMethods.decodeFile(new File(f.getAbsolutePath()),400, 400);
					mGroupPicture = UtilityMethods.rotateBitmap(bm, f.getAbsolutePath());
					if(mGroupPicture != null){
						showGroupPicture();
					}
					
				}catch(Exception e){
					e.printStackTrace();
				}
		    }
			break;
			
		case PICK_FROM_FILE:
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(
						selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[0]);
				String photoPath = cursor.getString(columnIndex);

				//display selected image
				bm = UtilityMethods.decodeFile(new File(photoPath), 400, 400);
				mGroupPicture = UtilityMethods.rotateBitmap(bm, photoPath);
				if(mGroupPicture != null){
					showGroupPicture();
				}
				
				cursor.close();				
			}
			break;
	    }
	}
	
	public void initUI(){
		mTitleTextView = (TextView) findViewById(R.id.textView_title);
		
		mAddNewPhotoLayout = (RelativeLayout) findViewById(R.id.layout_add_photo);
		mEditPhotoLayout = (RelativeLayout) findViewById(R.id.layout_edit_photo);
		mAddNewPhotoLayout.setVisibility(View.VISIBLE);
		mEditPhotoLayout.setVisibility(View.GONE);
		
		mGroupPictureImageView = (ImageView) findViewById(R.id.imageView_groupPicture);
		mBlurPictureImageView = (ImageView) findViewById(R.id.imageView_blurPicture);
		
		mCategorySpinner = (Spinner) findViewById(R.id.spinner_category);
		
		mApplyImageButton = (ImageButton) findViewById(R.id.imageButton_apply);
		mApplyImageButton.setOnClickListener(this);
		
		mCloseImageButton = (ImageButton) findViewById(R.id.imageButton_close);
		mCloseImageButton.setOnClickListener(this);
		
		mImportImageButton = (ImageButton) findViewById(R.id.imageButton_importPhoto);
		mImportImageButton.setOnClickListener(this);
		
		mTrashImageButton = (ImageButton) findViewById(R.id.imageButton_trash);
		mTrashImageButton.setOnClickListener(this);
		
		mEditImageButton = (ImageButton) findViewById(R.id.imageButton_edit);
		mEditImageButton.setOnClickListener(this);
		
		mCategoryDetailImageButton = (ImageButton) findViewById(R.id.imageButton_categoryDetail);
		mCategoryDetailImageButton.setOnClickListener(this);
		
		mNameEditText = (EditText) findViewById(R.id.editText_name);
		mAboutEditText = (EditText) findViewById(R.id.editText_about);
		
		mGroupPermissionSwitch = (Switch) findViewById(R.id.switch_group_permission);
		
		if(AppManager.getInstance().getCategories() == null){
			RockMobileApplication.getInstance().showProgressFullScreenDialog(NewGroupActivity.this);
			
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Category");
			query.orderByDescending("title");
			query.setLimit(Constants.PARSE_QUERY_MAX_LIMIT_COUNT);
			query.findInBackground(new FindCallback<ParseObject>(){
	
				@Override
				public void done(List<ParseObject> objList, ParseException err) {
					// TODO Auto-generated method stub
					if(objList != null && objList.size() > 0){
						objList = UtilityMethods.sortBySortingValue(objList);
						AppManager.mCategories = new String[objList.size()];
						for(int i=0;i<objList.size();i++){
							AppManager.mCategories[i] = objList.get(i).getString("title");
						}
						
						setCategoriesForSpinner();
						RockMobileApplication.getInstance().hideProgressDialog();
					}
				}
				
			});
			
		}else{
			 
			setCategoriesForSpinner();
		}
		
		if(mGroup != null){
			mTitleTextView.setText("EDIT GROUP");
			mNameEditText.setText(mGroup.getTitle());
			mCategorySpinner.setSelection(UtilityMethods.getIndexofArray(AppManager.mCategories, NewGroupActivity.mGroup.getCategory()));
			mAboutEditText.setText(mGroup.getDescription());
			mGroupPermissionSwitch.setChecked(!mGroup.getIsPublic());
			if(mGroup.getPhotoFile() != null){
				AppManager.getInstance().mImageLoader.displayImage(mGroup.getPhotoFile().getUrl(),
						mGroupPictureImageView, AppManager.getInstance().options, new SimpleImageLoadingListener() {
					 @Override
					 public void onLoadingStarted(String imageUri, View view) {
						 
					 }
		
					 @Override
					 public void onLoadingFailed(String imageUri, View view,
							 FailReason failReason) {
		
					 }
		
					 @Override
					 public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						 mGroupPicture = loadedImage;
						 mAddNewPhotoLayout.setVisibility(View.GONE);
						 mEditPhotoLayout.setVisibility(View.VISIBLE);						 
					 }
				 }, new ImageLoadingProgressListener() {
					 @Override
					 public void onProgressUpdate(String imageUri, View view, int current,
							 int total) {
	
					 }
				});
			}
		}
	}

	public void setCategoriesForSpinner(){
		ArrayAdapter<String> adapterCategory = new ArrayAdapter<String>(NewGroupActivity.this,
				R.layout.spinner_item, AppManager.mCategories);
		mCategorySpinner.setAdapter(adapterCategory);
		adapterCategory.setDropDownViewResource(R.layout.spinner_dropdown_item);
		mCategorySpinner.setSelection(getOtherCategoryIndex());	//By default category would be 'Other'
	}
	
	private int getOtherCategoryIndex(){
		for(int i = 0; i < AppManager.mCategories.length; i++){
			if(AppManager.mCategories[i].equalsIgnoreCase("Other")){
				return i;
			}
		}
		return 0;	//In this case 'Other' category wasn't found... So Setting default as the first one.
	}
	
	public void selectImageFromCameraOrGallery(){
		AlertDialog.Builder buildSingle = new AlertDialog.Builder(NewGroupActivity.this);
		buildSingle.setTitle(getResources().getString(R.string.import_photo));
		final ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(NewGroupActivity.this,
				android.R.layout.select_dialog_item);
		aAdapter.add(getResources().getString(R.string.take_picture));
		aAdapter.add(getResources().getString(R.string.camera_roll));

		buildSingle.setNegativeButton(getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});

		buildSingle.setAdapter(aAdapter,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String photoOptionChosen = aAdapter.getItem(which)
								.toString();
						System.gc();
						if (photoOptionChosen.equals(getResources().getString(R.string.take_picture))) {
							
							Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				            File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");							
				            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
				            startActivityForResult(intent, PICK_FROM_CAMERA);
				            
						} else if (photoOptionChosen.equals(getResources().getString(R.string.camera_roll))) {
							Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							intent.setType("image/*");
							startActivityForResult(Intent.createChooser(
									intent, "Select File"),
									PICK_FROM_FILE);// one can be replced
													// with any action code
						}
					}
				});
		buildSingle.show();
	}
	
	public void showGroupPicture(){
		mAddNewPhotoLayout.setVisibility(View.GONE);
		mEditPhotoLayout.setVisibility(View.VISIBLE);
		mGroupPictureImageView.setImageBitmap(mGroupPicture);
		mIsGroupPictureChanged = true;
	}
	
	public void removeGroupPicture(){
		mAddNewPhotoLayout.setVisibility(View.VISIBLE);
		mEditPhotoLayout.setVisibility(View.GONE);
		mGroupPictureImageView.setImageBitmap(null);
		mGroupPicture = null;
	}
	
	public void addNewGroup(){
		if(!UtilityMethods.checkIsEmptyForEditText(NewGroupActivity.this, mNameEditText, "Group Name "))
			return;
		if(TextUtils.isEmpty(mAboutEditText.getText().toString().trim())){
			RockMobileApplication.getInstance().showToast(this, R.string.you_must_enter_description, Toast.LENGTH_LONG);
			return;
		}
		
		UtilityMethods.hideKeyboard(this);
		RockMobileApplication.getInstance().showProgressFullScreenDialog(NewGroupActivity.this);
		
		if(mGroup == null)
			mGroup = new GroupModel();
		
		//set group picture
		if(mGroupPicture != null && mIsGroupPictureChanged){
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			byte[] groupPictureByteArray = null;
			try {
				mGroupPicture.compress(Bitmap.CompressFormat.PNG, 70, stream);
				groupPictureByteArray = stream.toByteArray();
				stream.flush();
				stream.close();
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if(groupPictureByteArray != null){
				ParseFile groupPictureFile = new ParseFile("image.png", groupPictureByteArray);
				mGroup.setPhotoFile(groupPictureFile);
			}
		}
		
		mGroup.setChurchId(Constants.CHURCH_ID);
		mGroup.setTitle(mNameEditText.getText().toString());
		mGroup.setCategory(mCategorySpinner.getSelectedItem().toString());
		mGroup.setDescription(mAboutEditText.getText().toString());
		mGroup.setAdminUserList(Arrays.asList((UserModel)ParseUser.getCurrentUser()));
		if(mGroupPermissionSwitch.isChecked())
			mGroup.setIsPublic(false);
		else
			mGroup.setIsPublic(true);
		
		mGroup.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException error) {
				// TODO Auto-generated method stub
				RockMobileApplication.getInstance().hideProgressDialog();
				if(error == null){
					if(mTitleTextView.getText().toString().equals("EDIT GROUP")){
						RockMobileApplication.getInstance().showToast(NewGroupActivity.this, R.string.group_changed, Toast.LENGTH_SHORT);
						PushNotificationService.getInstance().sendGroupRefresh(mGroup, null);
						for(GroupModel group : AppManager.mMyGroupsList){
							if(mGroup.getObjectId().equals(group.getObjectId())){
								group.fetchInBackground(null);
							}
						}
						
					}else{
						AppManager.mMyGroupsList.add(0, mGroup);
						RockMobileApplication.getInstance().showToast(NewGroupActivity.this, R.string.new_group_saved, Toast.LENGTH_SHORT);
					}
					
					Intent returnIntent = new Intent();
					setResult(RESULT_OK, returnIntent);
					finish();
				}
			}
		});
	}
}
