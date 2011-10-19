package com.android.btdt;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

public class QuizGameActivity extends QuizActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    getMenuInflater().inflate(R.menu.gameoptions, menu);
	    menu.findItem(R.id.help_menu_item).setIntent(
	    		new Intent(this, QuizHelpActivity.class));
	    menu.findItem(R.id.settings_menu_item).setIntent(
	    		new Intent(this, QuizSettingsActivity.class));
	    return true;
    }
}