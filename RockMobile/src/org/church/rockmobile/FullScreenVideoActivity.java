/*
 * Copyright (C) 2013 yixia.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.church.rockmobile;

import java.util.HashMap;
import java.util.Map;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.model.UserModel;

import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FullScreenVideoActivity extends Activity implements OnInfoListener, OnBufferingUpdateListener {

	/**
	 * TODO: Set the path variable to a streaming video URL or a local media file
	 * path.
	 */
	private String path = "";
	private Uri uri;
	private VideoView mVideoView;
	private boolean isStart;
	
	ProgressBar pb;
	TextView downloadRateView, loadRateView;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_full_screen_video);
		mVideoView = (VideoView) findViewById(R.id.buffer);
		pb = (ProgressBar) findViewById(R.id.probar);

		downloadRateView = (TextView) findViewById(R.id.download_rate);
		loadRateView = (TextView) findViewById(R.id.load_rate);
		Bundle bundle = getIntent().getExtras();
		path = bundle.getString(Constants.BUNDLE_VIDEO_URL);
		
		//if audio, then display image
		String imageUrl = bundle.getString(Constants.BUNDLE_IMAGE_URL);
		boolean isAudio = bundle.getBoolean(Constants.BUNDLE_IS_AUDIO);
		if(isAudio){
			ImageView imageView = (ImageView)findViewById(R.id.imageView);
			AppManager.getInstance().mImageLoader.displayImage(imageUrl,
					imageView, AppManager.getInstance().options, null, null);
			imageView.setVisibility(View.VISIBLE);
		}
		
		if (path == "") {
			// Tell the user to provide a media file URL/path.
			Toast.makeText(FullScreenVideoActivity.this, "Please edit VideoBuffer Activity, and set path" + " variable to your media file URL/path", Toast.LENGTH_LONG).show();
			return;
		} else {
			/*
			 * Alternatively,for streaming media you can use
			 * mVideoView.setVideoURI(Uri.parse(URLstring));
			 */
			
//			if(icicle != null){
//				int seekValue = icicle.getInt("pos");
//				mVideoView.seekTo(seekValue);
//				return;
//				
//			}
			uri = Uri.parse(path);
			mVideoView.setVideoURI(uri);
			mVideoView.setMediaController(new MediaController(this));
			mVideoView.requestFocus();
			mVideoView.setOnInfoListener(this);
			mVideoView.setOnBufferingUpdateListener(this);
			mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mediaPlayer) {
					// optional need Vitamio 4.0
					mediaPlayer.setPlaybackSpeed(1.0f);
				}
			});
		}
		
		//track event
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", UserModel.getCurrentUser().getUsername());
		FlurryAgent.logEvent("Video screen", params); 
		
		mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);

	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		switch (what) {
		case MediaPlayer.MEDIA_INFO_BUFFERING_START:
			if (mVideoView.isPlaying()) {
				mVideoView.pause();
				isStart = true;
//				pb.setVisibility(View.VISIBLE);
//				downloadRateView.setVisibility(View.VISIBLE);
//				loadRateView.setVisibility(View.VISIBLE);

			}
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_END:
			if (isStart) {
				mVideoView.start();
				pb.setVisibility(View.GONE);
				downloadRateView.setVisibility(View.GONE);
				loadRateView.setVisibility(View.GONE);
			}
			break;
		case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
			downloadRateView.setText("" + extra + "kb/s" + "  ");
			break;
		}
		return true;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		loadRateView.setText(percent + "%");
	}

//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		// TODO Auto-generated method stub
//		super.onSaveInstanceState(outState);
//	
//		outState.putInt("pos", (int) mVideoView.getCurrentPosition());
//		mVideoView.stopPlayback();
//	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	
}
