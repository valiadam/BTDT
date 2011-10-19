package com.android.btdt;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.widget.TextView;

public class QuizHelpActivity extends QuizActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);      
        
        try{
        	InputStream iFile = getResources().openRawResource(R.raw.quizhelp);
        	TextView helpTextView = (TextView) findViewById(R.id.TextView_HelpText);
        	String strFile = inputStreamToString(iFile);
        	helpTextView.setText(strFile);        	
        }
        catch (Exception ex)
        {}
    }
    
    public String inputStreamToString(InputStream is) throws IOException {
    	StringBuffer sBuffer = new StringBuffer();
    	DataInputStream dataIO = new DataInputStream(is);
    	String strLine = null;
    	while ((strLine = dataIO.readLine()) != null)
    		sBuffer.append(strLine + "\n");
    	
    	dataIO.close();    	
    	is.close();
    	return sBuffer.toString();    	
    }
}