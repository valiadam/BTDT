package com.android.btdt;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class QuizSplashActivity extends QuizActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        logStartup();
        
        setContentView(R.layout.splash);
        
        startAnimations();
    }	

	private void startAnimations() {
		startLogo1Animation();
        startLogo2Animation();
        startTableItemsAnimation();
	}

	private void startTableItemsAnimation() {
		Animation spinin = AnimationUtils.loadAnimation(this, R.anim.custom_anim);
        LayoutAnimationController controller = new LayoutAnimationController(spinin);
        controller.setOrder(LayoutAnimationController.ORDER_RANDOM);
        TableLayout table = (TableLayout) findViewById(R.id.TableLayout01);
        for (int i = 0; i < table.getChildCount(); i++) {
	        TableRow row = (TableRow) table.getChildAt(i);
	        row.setLayoutAnimation(controller);
        }
	}

	private void startLogo2Animation() {
		Animation fade2 = AnimationUtils.loadAnimation(this, R.anim.fade_in2);
        fade2.setAnimationListener(new AnimationListener() {
        	public void onAnimationEnd(Animation animation) 
        	{
        		startActivity(new Intent(QuizSplashActivity.this, QuizMenuActivity.class));
        		QuizSplashActivity.this.finish();
        	}

			@Override
			public void onAnimationRepeat(Animation animation) {				
			}

			@Override
			public void onAnimationStart(Animation animation) {	
			}
        	});
        TextView logo2 = (TextView) findViewById(R.id.TextViewBottomTitle);
        logo2.startAnimation(fade2);
	}

	private void startLogo1Animation() {		
		TextView logo1 = (TextView) findViewById(R.id.TextViewTopTitle);		
        Animation fade1 = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        logo1.startAnimation(fade1);		
	}

	private void logStartup() {
		SharedPreferences prefs = getSharedPreferences(GAME_PREFERENCES, MODE_PRIVATE);
		if (prefs.contains(LAST_LAUNCH))
        {
        	Log.i(LOG_TAG, "last started on: " + prefs.getString(LAST_LAUNCH, ""));
        }
        else
        {
        	Log.i(LOG_TAG, "never started before");
        }
        
        SharedPreferences.Editor prefEditor = prefs.edit();
        Date now = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//"dd-MM-YYYYY HH:mm:ss");
        String lastLaunch = dateFormatter.format(now);
        prefEditor.putString(LAST_LAUNCH, lastLaunch);
        prefEditor.commit();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Stop the animation
		TextView logo1 = (TextView) findViewById(R.id.TextViewTopTitle);
		logo1.clearAnimation();
		TextView logo2 = (TextView) findViewById(R.id.TextViewBottomTitle);
		logo2.clearAnimation();
		
		TableLayout table = (TableLayout) findViewById(R.id.TableLayout01);
		for (int i = 0; i < table.getChildCount(); i++) {
			TableRow row = (TableRow) table.getChildAt(i);
			row.clearAnimation();
		}
	}
}