package com.android.btdt;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class QuizMenuActivity extends QuizActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        
        populateMenu();
    }
    
    private void populateMenu() {
		ListView menuList = (ListView) findViewById(R.id.ListView_Menu);
        
        String[] items = {getResources().getString(R.string.menu_item_play),
        		getResources().getString(R.string.menu_item_scores),
        		getResources().getString(R.string.menu_item_settings),
        		getResources().getString(R.string.menu_item_help)};
        
        ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, R.layout.menu_item, items);
        menuList.setAdapter(adapt);
	}
}