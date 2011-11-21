package com.android.btdt;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

public class QuizGameActivity extends QuizActivity {
	private TextSwitcher mQuestionText;
	private ImageSwitcher mQuestionImage;
	SharedPreferences mGameSettings;
	Hashtable<Integer, Question> mQuestions;
	QuizTask downloader;
		
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
			View view = LayoutInflater.from(
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
        
        mGameSettings = getSharedPreferences(GAME_PREFERENCES, Context.MODE_PRIVATE);
        
        mQuestions = new Hashtable<Integer, QuizGameActivity.Question>(QUESTION_BATCH_SIZE);
        
        mQuestionText = (TextSwitcher) findViewById(R.id.TextSwitcher_QuestionText);
        mQuestionText.setFactory(new MyTextSwitcherFactory());
        
        mQuestionImage = (ImageSwitcher) findViewById(R.id.ImageSwitcher_QuestionImage);
        mQuestionImage.setFactory(new MyImageSwitcherFactory());
        
        int curQuestionNumber = mGameSettings.getInt(GAME_PREFERENCES_CURRENT_QUESTION, 1);
        
        int curScore = mGameSettings.getInt(GAME_PREFERENCES_SCORE, 0);
        TextView scoreTextView = (TextView) findViewById(R.id.textViewScore);
        scoreTextView.setText(Integer.toString(curScore));
        
        downloader = new QuizTask();
        downloader.execute(TRIVIA_SERVER_QUESTIONS, curQuestionNumber);
    }
    
    @Override
	protected void onPause() {
    	if (downloader != null && downloader.getStatus() != AsyncTask.Status.FINISHED){
    		Log.d(DEBUG_TAG, "downloader.cancel");
    		pleaseWaitDialog.dismiss();
    		downloader.cancel(true);
    	}
    	
    	super.onPause();
	}

	public void onNoButton(View v) {
    	handleAnswerAndShowNextQuestion(false);
    	}
    
    private void handleAnswerAndShowNextQuestion(boolean bAnswer) {
    	int nextQuestionNumber = mGameSettings.getInt(GAME_PREFERENCES_CURRENT_QUESTION, 1) + 1;
    	
    	Editor edit = mGameSettings.edit();
    	edit.putInt(GAME_PREFERENCES_CURRENT_QUESTION, nextQuestionNumber);
    	if (bAnswer)
    	{
    		int newScore = mGameSettings.getInt(GAME_PREFERENCES_SCORE, 0) + 1;
    		edit.putInt(GAME_PREFERENCES_SCORE, newScore);
    		
            TextView scoreTextView = (TextView) findViewById(R.id.textViewScore);
            scoreTextView.setText(Integer.toString(newScore));
    	}
    	
    	edit.commit();
    	
    	if (mQuestions.containsKey(nextQuestionNumber)== false){
            downloader = new QuizTask();
            downloader.execute(TRIVIA_SERVER_QUESTIONS, nextQuestionNumber);
    	}
    	else
    		displayCurrentQuestion(nextQuestionNumber);
	}

	private void displayCurrentQuestion(int nextQuestionNumber) {
		if (mQuestions.containsKey(nextQuestionNumber))
    	{
    		TextSwitcher textSwitcher = (TextSwitcher) findViewById(R.id.TextSwitcher_QuestionText);
    		textSwitcher.setText(getQuestionText(nextQuestionNumber));
    		
    		ImageSwitcher imageSwitcher = (ImageSwitcher) findViewById(R.id.ImageSwitcher_QuestionImage);
    		imageSwitcher.setImageDrawable(getQuestionImageDrawable(nextQuestionNumber));
    	} else
    	{
    		handleNoQuestions();
    	}
	}

	public void onYesButton(View v) {
    	handleAnswerAndShowNextQuestion(true);
    	}
    
    /**
     * Helper method to configure the question screen when no questions were found.
     * Could be called for a variety of error cases, including no new questions, IO failures, or parser failures.
     */
    private void handleNoQuestions() {
        TextSwitcher questionTextSwitcher = (TextSwitcher) findViewById(R.id.TextSwitcher_QuestionText);
        questionTextSwitcher.setText(getResources().getText(R.string.no_questions));
        ImageSwitcher questionImageSwitcher = (ImageSwitcher) findViewById(R.id.ImageSwitcher_QuestionImage);
        questionImageSwitcher.setImageResource(R.drawable.noquestion);

        // Disable yes button
        Button yesButton = (Button) findViewById(R.id.Button_Yes);
        yesButton.setEnabled(false);

        // Disable no button
        Button noButton = (Button) findViewById(R.id.Button_No);
        noButton.setEnabled(false);
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
    
    /**
     * Returns a {@code String} representing the text for a particular question number
     * 
     * @param questionNumber
     *            The question number to get the text for
     * @return The text of the question, or null if {@code questionNumber} not found
     */
    private String getQuestionText(Integer questionNumber) {
        String text = null;
        Question curQuestion = (Question) mQuestions.get(questionNumber);
        if (curQuestion != null) {
            text = curQuestion.mText;
        }
        return text;
    }

    /**
     * Returns a {@code String} representing the URL to an image for a particular question
     * 
     * @param questionNumber
     *            The question to get the URL for
     * @return A {@code String} for the URL or null if none found
     */
    private String getQuestionImageUrl(Integer questionNumber) {
        String url = null;
        Question curQuestion = (Question) mQuestions.get(questionNumber);
        if (curQuestion != null) {
            url = curQuestion.mImageUrl;
        }
        return url;
    }
    
    private Drawable getQuestionImageDrawable(int questionNumber) {
    	Drawable image;
    	URL imageUrl;
    	try{
    		imageUrl = new URL(getQuestionImageUrl(questionNumber));
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

	private class Question {
		int mNumber;
		String mText;
		String mImageUrl;
		
		public Question(int questionNum, String questionText, String questionImageUrl) {
			mNumber = questionNum;
			mText = questionText;
			mImageUrl = questionImageUrl;
		}
	}
	
	ProgressDialog pleaseWaitDialog;

	class QuizTask extends AsyncTask<Object, Object, Boolean>{
		private static final String DEBUG_TAG = "QuizGameActivity$QuizTask";
		int startingQuestionNumber;

		@Override
		protected void onCancelled() {
			Log.i(DEBUG_TAG, "onCancelled");
			pleaseWaitDialog.dismiss();						
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!isCancelled()){				
				Log.d(DEBUG_TAG, "Download task complete");
				if (result)
					displayCurrentQuestion(startingQuestionNumber);
				else
					handleNoQuestions();
				pleaseWaitDialog.dismiss();
			}
			else
				Log.d(DEBUG_TAG, "onPostExecute, but cancelled");		
		}

		@Override
		protected void onPreExecute() {
			pleaseWaitDialog = ProgressDialog.show(QuizGameActivity.this,
					"Trivia quiz", "Downloading question batch...", true, true);
			pleaseWaitDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					QuizTask.this.cancel(true);
				}
			});
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			boolean bSuccess = false;
			startingQuestionNumber = (Integer)params[1];
			String pathToQuestions = params[0] +
				"?max=" +
				QUESTION_BATCH_SIZE +
				"&start=" +
				startingQuestionNumber;
			try {
				loadQuestionBatch(startingQuestionNumber,pathToQuestions);
				bSuccess = true;
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return bSuccess;
		}
		
		private void loadQuestionBatch(int startQuestionNumber, String questionsAddress) throws XmlPullParserException, IOException
		{
			mQuestions.clear();
			
			URL urlQuestions = new URL(questionsAddress);
			
			XmlPullParser questionBatch = XmlPullParserFactory.newInstance().newPullParser();
			questionBatch.setInput(urlQuestions.openStream(), null);
			
			//parse the XML
			int eventType = -1;
			
			while (eventType != XmlResourceParser.END_DOCUMENT){
				if (eventType == XmlResourceParser.START_TAG){
					//get the name of the tag
					String tagName = questionBatch.getName();
					
					if (tagName.equals(XML_TAG_QUESTION))
					{
						String strQuestionNumber = questionBatch.getAttributeValue(null, XML_TAG_QUESTION_ATTRIBUTE_NUMBER);
						Integer questionNum = new Integer(strQuestionNumber);
						String questionText = questionBatch.getAttributeValue(null, XML_TAG_QUESTION_ATTRIBUTE_TEXT);
						String questionImageUrl = questionBatch.getAttributeValue(null, XML_TAG_QUESTION_ATTRIBUTE_IMAGEURL);
						
						mQuestions.put(questionNum, new Question(questionNum, questionText, questionImageUrl));
					}
				}
				eventType = questionBatch.next();
			}	
		}
	}
}