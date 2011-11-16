package com.android.btdt;

import android.app.Activity;

public class QuizActivity extends Activity {
	public static final String LOG_TAG = "BTDT";
	public static final String DEBUG_TAG = "BTDT_DEBUG";
	public static final String GAME_PREFERENCES = "GamePrefs";
	public static final String LAST_LAUNCH = "LastLaunch"; // String
	public static final String GAME_PREFERENCES_NICKNAME = "Nickname"; // String
	public static final String GAME_PREFERENCES_EMAIL = "Email"; // String
	public static final String GAME_PREFERENCES_PASSWORD = "Password"; // String
	public static final String GAME_PREFERENCES_DOB = "DOB"; // Long
	public static final String GAME_PREFERENCES_GENDER = "Gender"; // Int
	public static final String GAME_PREFERENCES_SCORE = "Score"; // Int
	public static final String GAME_PREFERENCES_CURRENT_QUESTION = "CurQuestion"; //Int
	public static final String GAME_PREFERENCES_AVATAR = "Avatar"; //String
	public static final String GAME_PREFERENCES_LOCATION_NAME = "Location"; //String
	public static final String GAME_PREFERENCES_LONGITUDE = "Longitude"; //Float
	public static final String GAME_PREFERENCES_LATITUDE = "Latitude"; //Float
	public static final String XML_TAG_QUESTION_BLOCK = "questions";
	public static final String XML_TAG_QUESTION = "question";
	public static final String XML_TAG_QUESTION_ATTRIBUTE_NUMBER = "number";
	public static final String XML_TAG_QUESTION_ATTRIBUTE_TEXT = "text";
	public static final String XML_TAG_QUESTION_ATTRIBUTE_IMAGEURL = "imageUrl";
	public static final int QUESTION_BATCH_SIZE = 15;
}