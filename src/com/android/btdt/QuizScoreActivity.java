package com.android.btdt;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;
import android.widget.TableLayout;

public class QuizScoreActivity extends QuizActivity {
	
	int mProgressCounter = 0;
	ScoreDownloaderTask allScoreDownloader;
	ScoreDownloaderTask friendsScoreDownloader;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.scores);
        
        initializeTabs();
        
        fillScoreTables();
    }

	@Override
	protected void onPause() {		
		cancelTaskIfRunning(allScoreDownloader);
		cancelTaskIfRunning(friendsScoreDownloader);
		super.onPause();
	}

	private void cancelTaskIfRunning(AsyncTask<?, ?, ?> task) {
		if (task != null && task.getStatus()!=AsyncTask.Status.FINISHED)
			task.cancel(true);
	}

	private void fillScoreTables() {		
        TableLayout tableAllScores = (TableLayout) findViewById(R.id.TableLayout_AllScores);
        initializeHeaderRow(tableAllScores);        
        allScoreDownloader = new ScoreDownloaderTask();
        allScoreDownloader.execute(TRIVIA_SERVER_SCORES, tableAllScores);
        
		TableLayout tableFriendsScores = (TableLayout) findViewById(R.id.TableLayout_FriendScores);
		initializeHeaderRow(tableFriendsScores);
		friendsScoreDownloader = new ScoreDownloaderTask();
		int playerId = 2008;
		friendsScoreDownloader.execute(TRIVIA_SERVER_SCORES+"?playerId="+playerId, tableFriendsScores);
		}
	
    /**
     * Add a header {@code TableRow} to the {@code TableLayout} (styled)
     * 
     * @param scoreTable
     *            the {@code TableLayout} that the header row will be added to
     */
    private void initializeHeaderRow(TableLayout scoreTable) {
        // Create the Table header row
        TableRow headerRow = new TableRow(this);
        int textColor = getResources().getColor(R.color.logo_color);
        float textSize = getResources().getDimension(R.dimen.help_text_size);
        addTextToRowWithValues(headerRow, getResources().getString(R.string.username), textColor, textSize);
        addTextToRowWithValues(headerRow, getResources().getString(R.string.score), textColor, textSize);
        addTextToRowWithValues(headerRow, getResources().getString(R.string.rank), textColor, textSize);
        scoreTable.addView(headerRow);
    }
    
	private void addTextToRowWithValues(TableRow scoreRow,
			String text, int textColor, float textSize) {
		TextView textView = new TextView(this);
		textView.setTextSize(textSize);
		textView.setTextColor(textColor);
		textView.setText(text);
		scoreRow.addView(textView);
	}

	private void initializeTabs() {
		TabHost host = (TabHost) findViewById(R.id.TabHost1);
        host.setup();
        
        TabSpec allScoresTab = host.newTabSpec("allTab");
        allScoresTab.setIndicator(getResources().getString(R.string.all_scores),
        		getResources().getDrawable(android.R.drawable.star_on));
        allScoresTab.setContent(R.id.ScrollViewAllScores);
        host.addTab(allScoresTab);
        
        TabSpec friendsScoresTab = host.newTabSpec("friendsTab");
        friendsScoresTab.setIndicator(getResources().getString(R.string.friends_scores),
        		getResources().getDrawable(android.R.drawable.star_on));
        friendsScoresTab.setContent(R.id.ScrollViewFriendScores);
        host.addTab(friendsScoresTab);
        
        host.setCurrentTabByTag("allTab");
	}
	
	public class ScoreDownloaderTask extends AsyncTask<Object, String, Boolean>{
		@Override
		protected void onCancelled() {
			mProgressCounter --;
			if (mProgressCounter <= 0){
				mProgressCounter = 0;
				QuizScoreActivity.this.setProgressBarIndeterminateVisibility(false);
			}
		}
		@Override
		protected void onPostExecute(Boolean result) {
			mProgressCounter --;
			if (mProgressCounter <= 0){
				mProgressCounter = 0;
				QuizScoreActivity.this.setProgressBarIndeterminateVisibility(false);
			}
		}
		@Override
		protected void onPreExecute() {
			mProgressCounter ++;
			QuizScoreActivity.this.setProgressBarIndeterminateVisibility(true);
		}
		@Override
		protected void onProgressUpdate(String... values) {
			if (values.length == 3)
				insertScoreRow(table, values[0], values[1], values[2]);
			else
				insertNoScoresRow(table);
		}
		private static final String DEBUG_TAG = "SocreDownloaderTask";
		TableLayout table;
		@Override
		protected Boolean doInBackground(Object... params) {
			boolean bSuccess = false;
			String pathToScores = (String)params[0];
			table = (TableLayout)params[1];
			XmlPullParser scores = null;
			URL xmlUrl;
			try {
				xmlUrl = new URL(pathToScores);
				scores = XmlPullParserFactory.newInstance().newPullParser();
				scores.setInput(xmlUrl.openStream(), null);
				if (scores != null){					
					fillScoreTable(scores);
					bSuccess = true;
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return bSuccess;
		}
		
		private void fillScoreTable(XmlPullParser scores) throws XmlPullParserException, IOException{
	    	int eventType = -1;
	    	 boolean bFoundScores = false;
	        
	        while (eventType != XmlResourceParser.END_DOCUMENT) {
	        	if (eventType == XmlResourceParser.START_TAG) {
	        		String strName = scores.getName();
	        		if (strName.equals("score")){
	        			bFoundScores = true;
	        			String scoreValue = scores.getAttributeValue(null, "score");
	        			String scoreRank = scores.getAttributeValue(null, "rank");
	        			String scoreUserName = scores.getAttributeValue(null, "username");
	        			publishProgress(scoreValue, scoreRank, scoreUserName);	        			
	        		}        		
	        	}
	        	eventType = scores.next();
	        }
	        
	        if (!bFoundScores){
	        	publishProgress();
	        }
	    }

		private void insertNoScoresRow(TableLayout table) {
			final TableRow newRow = new TableRow(QuizScoreActivity.this);
			TextView noResults = new TextView(QuizScoreActivity.this);
			noResults.setText(getResources().getString(R.string.no_scores));
			newRow.addView(noResults);
			table.addView(newRow);
		}

		private void insertScoreRow(TableLayout table, String scoreValue,
				String scoreRank, String scoreUserName) {
			final TableRow scoreRow = new TableRow(QuizScoreActivity.this);
			int textColor = getResources().getColor(R.color.title_color);
			float textSize = getResources().getDimension(R.dimen.help_text_size);
			addTextToRowWithValues(scoreRow, scoreUserName, textColor, textSize);
			addTextToRowWithValues(scoreRow, scoreValue, textColor, textSize);
			addTextToRowWithValues(scoreRow, scoreRank, textColor, textSize);
			table.addView(scoreRow);
		}
	}
}