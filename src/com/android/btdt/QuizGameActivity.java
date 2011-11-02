package com.android.btdt;

import java.io.InputStream;
import java.net.URL;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.ViewSwitcher.ViewFactory;

public class QuizGameActivity extends QuizActivity {
	
	private TextSwitcher mQuestionText;
	private ImageSwitcher mQuestionImage;
	
	public class MyImageSwitcherFactory implements ViewFactory {

		@Override
		public View makeView() {
			ImageView view = (ImageView) LayoutInflater.from(
					getApplicationContext()).inflate(R.layout.image_switcher_view,
														mQuestionImage,
														false);
			return view;
		}
	}
	
	public class MyTextSwitcherFactory implements ViewFactory {

		@Override
		public View makeView() {
			ImageView view = (ImageView) LayoutInflater.from(
					getApplicationContext()).inflate(R.layout.text_switcher_view,
														mQuestionText,
														false);
			return view;
		}
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        
        mQuestionText = (TextSwitcher) findViewById(R.id.TextSwitcher_QuestionText);
        mQuestionText.setFactory(new MyTextSwitcherFactory());
        
        mQuestionImage = (ImageSwitcher) findViewById(R.id.ImageSwitcher_QuestionImage);
        mQuestionImage.setFactory(new MyImageSwitcherFactory());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    getMenuInflater().inflate(R.menu.gameoptions, menu);
	    menu.findItem(R.id.help_menu_item).setIntent(
	    		new Intent(this, QuizHelpActivity.class));
	    menu.findItem(R.id.settings_menu_item).setIntent(
	    		new Intent(this, QuizSettingsActivity.class));
	    menu.findItem(R.id.scores_menu_item).setIntent(
	    		new Intent(this, QuizScoreActivity.class));
	    return true;
    }
    
    private Drawable getQuestionImageDrawable(int questionNumber) {
    	Drawable image;
    	URL imageUrl;
    	try{
    		imageUrl = new URL(getAuestionImageUrl(questionNumber));
    		InputStream stream = imageUrl.openStream();
    		Bitmap bitmap = BitmapFactory.decodeStream(stream);
    		image = new BitmapDrawable(getResources() ,bitmap);
    	}catch(Exception ex)
    	{
    		Log.e(DEBUG_TAG, "Decoding bitmap stream failed");
    		image = getResources().getDrawable(R.drawable.half);//TODO: fix default image
    	}
    	return image;
    }

	private String getAuestionImageUrl(int questionNumber) {
		// TODO Auto-generated method stub
		return null;
	}
}