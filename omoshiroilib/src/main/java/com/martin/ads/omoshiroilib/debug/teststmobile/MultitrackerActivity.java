package com.martin.ads.omoshiroilib.debug.teststmobile;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.martin.ads.omoshiroilib.R;
import com.sensetime.stmobileapi.STMobile106;

/**
 * 
 * @author MatrixCV
 *
 * Activity
 * 
 */
public class MultitrackerActivity extends Activity {
	
	static MultitrackerActivity instance = null;
	TextView fpstText, actionText;
	
	/**
	 * 
	 * 重力传感器
	 * 
	 */
	static Accelerometer acc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		显示方向
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.debug_activity_multitracker);
		fpstText = (TextView)findViewById(R.id.fpstext);
		actionText = (TextView) findViewById(R.id.actionText);
		instance = this;
		
		/**
		 * 
		 * 开启重力传感器监听
		 * 
		 */
		acc = new Accelerometer(this);
		acc.start();
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.multitracker, menu);
//		return true;
//	}
	
	@Override
	public void onResume() {
		super.onResume();
		final FaceOverlapFragment fragment = (FaceOverlapFragment) getFragmentManager()
				.findFragmentById(R.id.overlapFragment);
		fragment.registTrackCallback(new FaceOverlapFragment.TrackCallBack() {
			
			@Override
			public void onTrackdetected(final int value, final float pitch, final float roll, final float yaw, final int eye_dist,
										final int id, final int eyeBlink, final int mouthAh, final int headYaw, final int headPitch, final int browJump, STMobile106 landmarks) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						fpstText.setText("hahhahahahFPS: " + value+"\nPITCH: "+pitch+"\nROLL: "+roll+"\nYAW: "+yaw+"\nEYE_DIST:"+eye_dist);
						actionText.setText("ID:"+id+"\nEYE_BLINK:"+eyeBlink+"\nMOUTH_AH:"+mouthAh+"\nHEAD_YAW:"+headYaw+"\nHEADPITCH:"+headPitch+"\nBROWJUMP:"+browJump);
					}
				});
			}
		});
	}
}
