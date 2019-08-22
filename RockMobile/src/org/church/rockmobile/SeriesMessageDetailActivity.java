package org.church.rockmobile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.model.SeriesMessageModel;
import org.church.rockmobile.model.UserModel;

import com.flurry.android.FlurryAgent;
import com.newrelic.com.google.gson.Gson;
import com.parse.ParseFile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SeriesMessageDetailActivity extends Activity implements OnClickListener {

	public final String TAG = "SeriesMessageDetailActivity";

	ImageButton mBackImageButton;
	Button mListenButton;
	MediaPlayer mMediaPlayer;
	boolean isExtractOriginalTitle = false;

	public static SeriesMessageModel mSeriesMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_series_message_detail);

		if (savedInstanceState != null) {
			mSeriesMessage = new Gson().fromJson(savedInstanceState.getString(TAG), SeriesMessageModel.class);
		}

		// track event
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", UserModel.getCurrentUser().getUsername());
		FlurryAgent.logEvent("Series Message Detail", params);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initUI();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_play:
			String videoUrl = "";
			ParseFile videoFile = mSeriesMessage.getVideoFile();
			if (videoFile != null)
				videoUrl = videoFile.getUrl();
			else if (mSeriesMessage.getVideoLink() != null)
				videoUrl = mSeriesMessage.getVideoLink();

			Intent intent = new Intent(this, FullScreenVideoActivity.class);
			intent.putExtra(Constants.BUNDLE_VIDEO_URL, videoUrl);
			startActivity(intent);
			break;

		case R.id.imageButton_back:
			super.onBackPressed();
			break;

		case R.id.imageButton_share:
			shareMessage();
			break;

		case R.id.button_listen_now:
			if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
				stopAudio();
				mListenButton.setText("Listen Now");

			} else {
				playAudio();
				if (mMediaPlayer != null && mMediaPlayer.isPlaying())
					mListenButton.setText("Stop Now");
			}
			break;
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// stopVideo();
		// stopAudio();
	}

	@SuppressLint({ "SimpleDateFormat" })
	public void initUI() {
		Bundle bundle = getIntent().getExtras();
		int position = bundle.getInt(Constants.BUNDLE_MESSAGE_POSITION);
		isExtractOriginalTitle = bundle.getBoolean(Constants.BUNDLE_IS_EXTRACT_ORIGINAL_TITLE);
		mBackImageButton = (ImageButton) findViewById(R.id.imageButton_back);
		mBackImageButton.setOnClickListener(this);

		Button playButton = (Button) findViewById(R.id.button_play);
		playButton.setOnClickListener(this);
		if (mSeriesMessage.getVideoFile() == null
				&& (mSeriesMessage.getVideoLink() == null || mSeriesMessage.getVideoLink().isEmpty())) {
			playButton.setVisibility(View.GONE);
		}

		ImageButton shareImageButton = (ImageButton) findViewById(R.id.imageButton_share);
		shareImageButton.setOnClickListener(this);

		mListenButton = (Button) findViewById(R.id.button_listen_now);
		mListenButton.setOnClickListener(this);
		if (mSeriesMessage.getAudioFile() == null
				&& (mSeriesMessage.getAudioLink() == null || mSeriesMessage.getAudioLink().isEmpty())) {
			mListenButton.setVisibility(View.GONE);
		}

		ImageView messageImageView = (ImageView) findViewById(R.id.imageView_message);
		if (mSeriesMessage.getPhotoFile() != null) {
			AppManager.getInstance().mImageLoader.displayImage(mSeriesMessage.getPhotoFile().getUrl(), messageImageView,
					AppManager.getInstance().options, null, null);
		} else if (mSeriesMessage.getImageLink() != null) {
			AppManager.getInstance().mImageLoader.displayImage(mSeriesMessage.getImageLink(), messageImageView,
					AppManager.getInstance().options, null, null);
		}

		TextView partTextView = (TextView) findViewById(R.id.textView_part);
		partTextView.setText("PART " + String.valueOf(position + 1));

		TextView titleTextView = (TextView) findViewById(R.id.textView_title);
		titleTextView.setText(isExtractOriginalTitle ? extractOriginalTitleFromModifiedTitle(mSeriesMessage.getTitle()) : mSeriesMessage.getTitle());

		TextView dateTextView = (TextView) findViewById(R.id.textView_date);
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
		dateTextView.setText(sdf.format(mSeriesMessage.getStartDate()));

		TextView recapTextView = (TextView) findViewById(R.id.textView_recap);
		recapTextView.setText(mSeriesMessage.getRecap());
	}

	// public void initAudio(){
	// ParseFile audioFile = mSeriesMessage.getAudioFile();
	// mMediaPlayer = new MediaPlayer();
	// try {
	// if(audioFile != null)
	// mMediaPlayer.setDataSource(audioFile.getUrl());
	// else if(mSeriesMessage.getAudioLink() != null)
	// mMediaPlayer.setDataSource(mSeriesMessage.getAudioLink());
	// else
	// return;
	//
	// mMediaPlayer.prepare();
	// } catch (IllegalArgumentException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (SecurityException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IllegalStateException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// public void stopVideo(){
	//
	// }

	public void playAudio() {
		// if(mMediaPlayer == null)
		// initAudio();
		//
		// mMediaPlayer.start();
		String audioUrl = null;
		if (mSeriesMessage.getAudioFile() != null) {
			audioUrl = mSeriesMessage.getAudioFile().getUrl();

		} else if (mSeriesMessage.getAudioLink() != null) {
			audioUrl = mSeriesMessage.getAudioLink();
		}
		Intent intent = new Intent(this, FullScreenVideoActivity.class);
		if (mSeriesMessage.getPhotoFile() != null) {
			intent.putExtra(Constants.BUNDLE_IMAGE_URL, mSeriesMessage.getPhotoFile().getUrl());
		}
		intent.putExtra(Constants.BUNDLE_VIDEO_URL, audioUrl);
		intent.putExtra(Constants.BUNDLE_IS_AUDIO, true);
		startActivity(intent);
	}

	public void stopAudio() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer = null;
		}
	}

	public void shareMessage() {
		String url = mSeriesMessage.getShareLink();
		if (url == null) {
			url = "http://www.fishermenlabs.com/rockmobilecms/";
		}

		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("*/*");

		Bitmap icon = null;
		if (mSeriesMessage.getPhotoFile() != null) {
			icon = AppManager.getInstance().mImageLoader.loadImageSync(mSeriesMessage.getPhotoFile().getUrl());
		} else if (mSeriesMessage.getImageLink() != null) {
			icon = AppManager.getInstance().mImageLoader.loadImageSync(mSeriesMessage.getImageLink());
		}

		if (icon != null) {
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

		// shareIntent.putExtra(Intent.EXTRA_SUBJECT, mStory.getTitle());
		shareIntent.putExtra(Intent.EXTRA_TEXT, url);
		startActivity(Intent.createChooser(shareIntent, "Share Message"));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(TAG, new Gson().toJson(mSeriesMessage));
	}

	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (mSeriesMessage == null) {
			mSeriesMessage = new Gson().fromJson(savedInstanceState.getString(TAG), SeriesMessageModel.class);
		}
	}
	
	private String extractOriginalTitleFromModifiedTitle(String title) {
		Pattern pattern = Pattern.compile("- Part \\d+, (.*)");
		Matcher matcher = pattern.matcher(title);
		if (matcher.find()) {
			String originalTitle = matcher.group(1).trim();
			if (title != null && !TextUtils.isEmpty(title)) {
				return originalTitle;
			}
		}
		return title;
	}
}
