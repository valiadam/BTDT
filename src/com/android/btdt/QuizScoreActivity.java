package com.android.btdt;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;
import android.widget.TableLayout;

public class QuizScoreActivity extends QuizActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scores);
        
        initializeTabs();
        
        fillScoreTables();
    }

	private void fillScoreTables() {
		XmlResourceParser mockAllScores = getResources().getXml(R.xml.allscores);
        XmlResourceParser mockFriendScores = getResources().getXml(R.xml.friendscores);
        
        TableLayout tableAllScores = (TableLayout) findViewById(R.id.TableLayout_AllScores);
        try {
        	initializeHeaderRow(tableAllScores);
			fillScoreTable(mockAllScores, tableAllScores);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TableLayout tableFriendsScores = (TableLayout) findViewById(R.id.TableLayout_FriendScores);
		try {
			initializeHeaderRow(tableFriendsScores);
			fillScoreTable(mockFriendScores, tableFriendsScores);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
    
    private void fillScoreTable(XmlResourceParser scores, TableLayout table) throws XmlPullParserException, IOException{
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
        			insertScoreRow(table, scoreValue, scoreRank, scoreUserName);
        		}        		
        	}
        	eventType = scores.next();
        }
        
        if (!bFoundScores){
        	insertNoScoresRow(table);
        }
    }

	private void insertNoScoresRow(TableLayout table) {
		final TableRow newRow = new TableRow(this);
		TextView noResults = new TextView(this);
		noResults.setText(getResources().getString(R.string.no_scores));
		newRow.addView(noResults);
		table.addView(newRow);
	}

	private void insertScoreRow(TableLayout table, String scoreValue,
			String scoreRank, String scoreUserName) {
		final TableRow scoreRow = new TableRow(this);
		int textColor = getResources().getColor(R.color.title_color);
		float textSize = getResources().getDimension(R.dimen.help_text_size);
		addTextToRowWithValues(scoreRow, scoreUserName, textColor, textSize);
		addTextToRowWithValues(scoreRow, scoreValue, textColor, textSize);
		addTextToRowWithValues(scoreRow, scoreRank, textColor, textSize);
		table.addView(scoreRow);
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
}