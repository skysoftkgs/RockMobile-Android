package org.church.rockmobile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.model.UserModel;
import org.church.rockmobile.widget.RoundedImageView;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class SettingsProfileActivity extends Activity implements OnClickListener{
	
	public final String TAG = "SettingsProfileActivity";
	private final int PICK_FROM_CAMERA = 1101;
	private final int PICK_FROM_FILE = 1102;
	
	RoundedImageView profileImageView;
	EditText nameEditText;
	EditText emailEditText;
	Button editButton;
	UserModel mCurrentUser;
	Bitmap mAvatarBitmap;
	Boolean mIsProfilePictureChanged;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_settings_profile);
	    
	    initUI();
	    
	    //track event
  		Map<String, String> params = new HashMap<String, String>();
  		params.put("username", UserModel.getCurrentUser().getUsername());
  		FlurryAgent.logEvent("Profile screen", params); 
	}
	
	public void initUI(){
		mCurrentUser = (UserModel)ParseUser.getCurrentUser();
		
		ImageButton backImageButton = (ImageButton) findViewById(R.id.imageButton_back);
		backImageButton.setOnClickListener(this);
		
		ImageButton saveImageButton = (ImageButton) findViewById(R.id.imageButton_save);
		saveImageButton.setOnClickListener(this);
		
		editButton = (Button) findViewById(R.id.button_edit);
		editButton.setOnClickListener(this);
		
		nameEditText = (EditText) findViewById(R.id.editText_name);
		emailEditText = (EditText) findViewById(R.id.editText_email);
		emailEditText.setEnabled(false);
		if(AppManager.mAnonymousLogin == true){
			nameEditText.setText("Guest");
			nameEditText.setEnabled(false);
			emailEditText.setText("");
			saveImageButton.setVisibility(View.INVISIBLE);
			editButton.setVisibility(View.INVISIBLE);
			
		}else{
			nameEditText.setText(mCurrentUser.getRealUsername());
			String email = mCurrentUser.getEmail();
			emailEditText.setText(email.replace(Constants.CHURCH_ID + "_", ""));
		}
		profileImageView = (RoundedImageView) findViewById(R.id.imageView_profile);
		if(mCurrentUser.getParseFile("avatar") != null){
	        AppManager.getInstance().mImageLoader.displayImage(mCurrentUser.getParseFile("avatar").getUrl(),
	        		profileImageView, AppManager.getInstance().options, null);
        }
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.button_edit:
				selectImageFromCameraOrGallery();
				break;
			
			case R.id.imageButton_save:
				saveProfile();
				break;
				
			case R.id.imageButton_back:
				super.onBackPressed();
				break;
		}
	}
	
	public void selectImageFromCameraOrGallery(){
		AlertDialog.Builder buildSingle = new AlertDialog.Builder(SettingsProfileActivity.this);
		buildSingle.setTitle(getResources().getString(R.string.import_photo));
		final ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(SettingsProfileActivity.this,
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
					mAvatarBitmap = UtilityMethods.rotateBitmap(bm, f.getAbsolutePath());
					if(mAvatarBitmap != null){
						mAvatarBitmap = UtilityMethods.getCropedBitmap(mAvatarBitmap, getResources().getDimensionPixelSize(R.dimen.settings_profile_picture_width));
						profileImageView.setImageBitmap(mAvatarBitmap);
						mIsProfilePictureChanged = true;
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
				mAvatarBitmap = UtilityMethods.rotateBitmap(bm, photoPath);
				if(mAvatarBitmap != null){
					mAvatarBitmap = UtilityMethods.getCropedBitmap(mAvatarBitmap, getResources().getDimensionPixelSize(R.dimen.settings_profile_picture_width));
					profileImageView.setImageBitmap(mAvatarBitmap);
					mIsProfilePictureChanged = true;
				}
				
				cursor.close();				
			}
			break;
	    }
	}
	
	public void saveProfile(){
		if(!UtilityMethods.checkIsEmptyForEditText(SettingsProfileActivity.this, nameEditText, "User name "))
			return;
		
		UtilityMethods.hideKeyboard(this);
				
		//set profile picture
		if(mAvatarBitmap != null && mIsProfilePictureChanged == true){
			
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			byte[] avatarBitmapByteArray = null;
			try {
				mAvatarBitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
				avatarBitmapByteArray = stream.toByteArray();
				stream.flush();
				stream.close();
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if(avatarBitmapByteArray != null){
				ParseFile profilePictureFile = new ParseFile("image.png", avatarBitmapByteArray);
				mCurrentUser.put("avatar", profilePictureFile);
			}
		}
		
		RockMobileApplication.getInstance().showProgressFullScreenDialog(SettingsProfileActivity.this);
		mCurrentUser.setFirstName(nameEditText.getText().toString());
		mCurrentUser.setLastName("");
		mCurrentUser.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException err) {
				// TODO Auto-generated method stub
				RockMobileApplication.getInstance().hideProgressDialog();
				if(err == null){
					RockMobileApplication.getInstance().showToast(SettingsProfileActivity.this, R.string.user_information_saved, Toast.LENGTH_SHORT);
					mIsProfilePictureChanged = false;
					
				}else{
					RockMobileApplication.getInstance().showToast(SettingsProfileActivity.this, R.string.server_error, Toast.LENGTH_SHORT);
				}
			}
		});
	}
}
