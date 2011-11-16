package com.android.btdt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class QuizSettingsActivity extends QuizActivity {
	static final int DATE_DIALOG_ID = 0;
	static final int PASSWORD_DIALOG_ID = 1;
	static final int PLACE_DIALOG_ID = 2;
	
	static final int TAKE_AVATAR_CAMERA_REQUEST = 1;
	static final int TAKE_AVATAR_GALLERY_REQUEST = 2;
	
	SharedPreferences mGameSettings;
	GPSCoords mFavLoc;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        mGameSettings = getSharedPreferences(GAME_PREFERENCES, Context.MODE_PRIVATE);
        
        initNicknameEdit();
        
        initEmailEdit();
        
        initGenderEdit();
        
        initDOBText();
        
        initPasswordInfoText();
        
        updateCurrentScoreLabel();
        
        initAvatarImageButton();
        
        initFavLoc();
        
        initFavoritePlacePicker();
    }

	private void initFavLoc() {
		mFavLoc = new GPSCoords(mGameSettings.getFloat(GAME_PREFERENCES_LONGITUDE, 0), 
        						mGameSettings.getFloat(GAME_PREFERENCES_LATITUDE, 0),
        						mGameSettings.getString(GAME_PREFERENCES_LOCATION_NAME, ""));
	}

	private void initAvatarImageButton() {
		ImageButton avatarButton = (ImageButton)findViewById(R.id.imageButton_avatar);
        if (mGameSettings.contains(GAME_PREFERENCES_AVATAR)) {
			String strAvatarUri = mGameSettings
					.getString(GAME_PREFERENCES_AVATAR,
							"android.resource://com.androidbook.peakbagger/drawable/avatar");
			Uri imageUri = Uri.parse(strAvatarUri);
			avatarButton.setImageURI(imageUri);
		} else {
			avatarButton.setImageResource(R.drawable.avatar);
		}
        
        avatarButton.setOnLongClickListener(
        		new View.OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						Intent pickPhoto = new Intent(Intent.ACTION_PICK);
						pickPhoto.setType("image/*");
						startActivityForResult(pickPhoto, TAKE_AVATAR_GALLERY_REQUEST);
						return true;
					}
				});
	}

	private void initPasswordInfoText() {
		final TextView passwordInfo = (TextView) findViewById(R.id.textView_PasswordState);
        if (mGameSettings.contains(GAME_PREFERENCES_PASSWORD))
			passwordInfo.setText(R.string.password_set);
        else
        	passwordInfo.setText(R.string.password_not_set);
	}

	private void initDOBText() {
		if (mGameSettings.contains(GAME_PREFERENCES_DOB))
		{
			long msBirthDate = mGameSettings.getLong(GAME_PREFERENCES_DOB, 0);
			final TextView textDOB = (TextView) findViewById(R.id.textView_DOBState);
			textDOB.setText(DateFormat.format("MMMM dd yyyy", msBirthDate));
		}
	}

	private void initGenderEdit() {
		Spinner spinner = (Spinner) findViewById(R.id.Spinner_Gender);
        
        //first set the initial value, so we won't fire the OnItemSelected event
        if (mGameSettings.contains(GAME_PREFERENCES_GENDER)){
        	int gender = mGameSettings.getInt(GAME_PREFERENCES_GENDER, 0);
        	spinner.setSelection(gender);
        }
        spinner.setOnItemSelectedListener(
        		new AdapterView.OnItemSelectedListener() {
        		public void onItemSelected(AdapterView<?> parent, View itemSelected,
        		int selectedItemPosition, long selectedId) {
        		Editor editor = mGameSettings.edit();
        		editor.putInt(GAME_PREFERENCES_GENDER, selectedItemPosition);
        		editor.commit();
        		}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub					
				}
        		});
	}

	private void initEmailEdit() {
		if (mGameSettings.contains(GAME_PREFERENCES_EMAIL)){
        	final EditText emailText = (EditText) findViewById(R.id.EditText_Email);
        	emailText.setText(mGameSettings.getString(GAME_PREFERENCES_EMAIL, ""));
        }
	}

	private void initNicknameEdit() {
		if (mGameSettings.contains(GAME_PREFERENCES_NICKNAME)){
        	final EditText nicknameText = (EditText) findViewById(R.id.EditText_Nickname);
        	nicknameText.setText(mGameSettings.getString(GAME_PREFERENCES_NICKNAME, ""));
        }
	}

	private void updateCurrentScoreLabel() {
		final TextView currentScore = (TextView) findViewById(R.id.textViewCurrentScoreValue);
        currentScore.setText(Integer.toString(mGameSettings.getInt(GAME_PREFERENCES_SCORE, 0)));
	}
    
    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		Editor editor = mGameSettings.edit();
		
		final EditText nicknameText = (EditText) findViewById(R.id.EditText_Nickname);
		editor.putString(GAME_PREFERENCES_NICKNAME, nicknameText.getText().toString());
		
		final EditText emailText = (EditText) findViewById(R.id.EditText_Email);
		editor.putString(GAME_PREFERENCES_EMAIL, emailText.getText().toString());
		editor.commit();
	}

	public void onSetPasswordButtonClick(View view){
    	showDialog(PASSWORD_DIALOG_ID);
    }
    
    public void onPickDateButtonClick(View view){
    	showDialog(DATE_DIALOG_ID);
    }
    
    @Override
    protected void onDestroy() {
	    Log.d(DEBUG_TAG, "SHARED PREFERENCES");
	    Log.d(DEBUG_TAG, "Nickname is: "
	    + mGameSettings.getString(GAME_PREFERENCES_NICKNAME, "Not set"));
	    Log.d(DEBUG_TAG, "Email is: "
	    + mGameSettings.getString(GAME_PREFERENCES_EMAIL, "Not set"));
	    Log.d(DEBUG_TAG, "Gender (M=1, F=2, U=0) is: "
	    + mGameSettings.getInt(GAME_PREFERENCES_GENDER, 0));
	    // We are not saving the password yet
	    Log.d(DEBUG_TAG, "Password is: "
	    + mGameSettings.getString(GAME_PREFERENCES_PASSWORD, "Not set"));
	    // We are not saving the date of birth yet
	    Log.d(DEBUG_TAG, "DOB is: "
	    + DateFormat.format("MMMM dd, yyyy", mGameSettings.getLong(
	    GAME_PREFERENCES_DOB, 0)));
	    super.onDestroy();
    }

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case DATE_DIALOG_ID:
			final TextView dob = (TextView) findViewById(R.id.textView_DOBState);
			Calendar now = Calendar.getInstance();
			DatePickerDialog dateDialog = new DatePickerDialog(this,
					new DatePickerDialog.OnDateSetListener() {						
						@Override
						public void onDateSet(DatePicker view, int year, int monthOfYear,
								int dayOfMonth) {
							Time dateOfBirth = new Time();
							dateOfBirth.set(dayOfMonth, monthOfYear, year);
							long dtDob = dateOfBirth.toMillis(true);
							dob.setText(DateFormat.format("MMMM dd yyyy", dtDob));
							Editor editor = mGameSettings.edit();
							editor.putLong(GAME_PREFERENCES_DOB, dtDob);
							editor.commit();
						}
					},
					now.get(Calendar.YEAR),
					now.get(Calendar.MONTH),
					now.get(Calendar.DAY_OF_MONTH));
			return dateDialog;
		case PASSWORD_DIALOG_ID:
		{
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final View layout = inflater.inflate(R.layout.password_dialog,
												(ViewGroup)findViewById(R.id.root));
			
			final EditText p1 = (EditText) layout.findViewById(R.id.editText_Pwd1);
			final EditText p2 = (EditText) layout.findViewById(R.id.editText_Pwd2);
			final TextView error = (TextView) layout.findViewById(R.id.TextView_PwdProblem);
			
			p2.addTextChangedListener(new TextWatcher() {				
				@Override
				public void afterTextChanged(Editable s) {
					String sPass1 = p1.getText().toString();
					String sPass2 = p2.getText().toString();
					if (sPass1.equals(sPass2)){
						error.setText(R.string.passwords_match);
					}
					else{
						error.setText(R.string.passwords_dont_match);
					}					
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}
			});
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(layout);
			builder.setTitle(R.string.set_password);
			
			builder.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String sPass1 = p1.getText().toString();
							String sPass2 = p2.getText().toString();
							if (sPass1.equals(sPass2)){
								Editor editor = mGameSettings.edit();
								editor.putString(GAME_PREFERENCES_PASSWORD, sPass1);
								editor.commit();
								
								TextView passwordInfo = (TextView) findViewById(R.id.textView_PasswordState);
								passwordInfo.setText(R.string.password_set);
							}
							else{
								Log.d(DEBUG_TAG, "Passwords do not match. "
										+ "Not saving. Keeping old password (if set).");
							}
							QuizSettingsActivity.this.removeDialog(PASSWORD_DIALOG_ID);
						}
					});
			
			builder.setNegativeButton(android.R.string.cancel, 
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							QuizSettingsActivity.this.removeDialog(PASSWORD_DIALOG_ID);					
						}
					});
			
			AlertDialog passwordDialog = builder.create();
			return passwordDialog;
		}
		case PLACE_DIALOG_ID:
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final View layout = inflater.inflate(R.layout.fav_place_dialog,
					(ViewGroup)findViewById(R.id.root));
			
			final TextView placeCoordinates = (TextView) layout.findViewById(R.id.TextView_FavPlaceCoords_Info);
			final EditText placeName = (EditText) layout.findViewById(R.id.EditText_FavPlaceName);
			placeName.setOnKeyListener(new View.OnKeyListener() {
				
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                        String strPlaceName = placeName.getText().toString();
                        if ((strPlaceName != null) && (strPlaceName.length() > 0)) {
                            // Try to resolve string into GPS coords
                        	mFavLoc.name = strPlaceName;
        					fixCoordinatesFromLocationName();        					
        					saveFavoriteLocation();

                            placeCoordinates.setText(formatCoordinates(mFavLoc.latitude, mFavLoc.longitude));
                            return true;
                        }
                    }
					return false;
				}
			});
			final Button mapButton = (Button)layout.findViewById(R.id.Button_MapIt);
			mapButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mFavLoc.name = placeName.getText().toString();
					fixCoordinatesFromLocationName();
					
					saveFavoriteLocation();
					placeCoordinates.setText(formatCoordinates(mFavLoc.latitude, mFavLoc.longitude));
					
					  // Launch map with gps coords
                    String geoURI = String.format(Locale.US, "geo:%f,%f?z=10", mFavLoc.latitude, mFavLoc.longitude);
                    Uri geo = Uri.parse(geoURI);
                    Intent geoMap = new Intent(Intent.ACTION_VIEW, geo);
                    startActivity(geoMap);
				}
			});
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(layout);
			builder.setTitle(R.string.favorite_place);
			builder.setPositiveButton(android.R.string.ok, 
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							QuizSettingsActivity.this.removeDialog(PLACE_DIALOG_ID);					
						}
					});
			builder.setNegativeButton(android.R.string.cancel, 
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							QuizSettingsActivity.this.removeDialog(PLACE_DIALOG_ID);					
						}
					});
			AlertDialog favLocationDialog = builder.create();
			return favLocationDialog;
		}
		return null;
	}
	
	class GPSCoords{
		float longitude;
		float latitude;
		String name;
		
		public GPSCoords(float longitude, float latitude, String name){
			this.longitude = longitude;
			this.latitude = latitude;
			this.name = name;
		}
	}

	private boolean fixCoordinatesFromLocationName() {
		final Geocoder coder = new Geocoder(getApplicationContext());
        boolean bResolvedAddress = false;

        try {
            List<Address> geocodeResults = coder.getFromLocationName(mFavLoc.name,1);
            Iterator<Address> locations = geocodeResults.iterator();

            while (locations.hasNext()) {
                Address loc = locations.next();
                mFavLoc.latitude = (float) loc.getLatitude();
                mFavLoc.longitude = (float) loc.getLongitude();
                bResolvedAddress = true;
            }
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "Failed to geocode location from coordinates", e);
        }
        return bResolvedAddress;
	}
	
	private boolean getLastLocation(){
		boolean bResolvedCoordinates = false;
		try {
            LocationManager locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
            Location recentLoc = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mFavLoc.latitude = (float) recentLoc.getLatitude();
            mFavLoc.longitude = (float) recentLoc.getLongitude();
            mFavLoc.name = getApplicationContext().getString(R.string.current_location);
            bResolvedCoordinates = fixLocationNameFromCoordinates();
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "Failed to get coordinates from location name failed", e);
        }
        return bResolvedCoordinates;
	}

	private boolean fixLocationNameFromCoordinates() {		 
		final Geocoder coder = new Geocoder(getApplicationContext());
        boolean bResolvedAddress = false;

        try {
            List<Address> geocodeResults = coder.getFromLocation(mFavLoc.latitude, mFavLoc.longitude, 1);
            Iterator<Address> locations = geocodeResults.iterator();

            while (locations.hasNext()) {
                Address loc = locations.next();
                String featureName = loc.getFeatureName();
                mFavLoc.name = (featureName != null ? 
        								featureName : 
        								formatCoordinates(mFavLoc.latitude, mFavLoc.longitude));
                bResolvedAddress = true;
            }
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "Failed to geocode location from coordinates", e);
        }
        return bResolvedAddress;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch(id){
		case DATE_DIALOG_ID:
			DatePickerDialog dateDialog = (DatePickerDialog) dialog;
			int iDay, iMonth, iYear;
			if (mGameSettings.contains(GAME_PREFERENCES_DOB))
			{
				long msBirthDate = mGameSettings.getLong(GAME_PREFERENCES_DOB, 0);
				Time dateOfBirth = new Time();
				dateOfBirth.set(msBirthDate);
				iDay = dateOfBirth.monthDay;
				iMonth = dateOfBirth.month;
				iYear = dateOfBirth.year;
			}
			else{
				Calendar cal = Calendar.getInstance();
				iDay = cal.get(Calendar.DAY_OF_MONTH);
				iMonth = cal.get(Calendar.MONTH);
				iYear = cal.get(Calendar.YEAR);
			}
			
			dateDialog.updateDate(iYear, iMonth, iDay);
			return;
		case PLACE_DIALOG_ID:
			AlertDialog placeDialog = (AlertDialog) dialog;
			
			final EditText editTextPlaceName = (EditText) placeDialog.findViewById(R.id.EditText_FavPlaceName);
			final TextView textViewCoordinates = (TextView) placeDialog.findViewById(R.id.TextView_FavPlaceCoords_Info);
			
			boolean bHasLocationName = mGameSettings.contains(GAME_PREFERENCES_LOCATION_NAME);
			boolean bHasCoordinates = mGameSettings.contains(GAME_PREFERENCES_LATITUDE)
								&& mGameSettings.contains(GAME_PREFERENCES_LONGITUDE);
			
			if (!bHasLocationName && bHasCoordinates)
				bHasLocationName = fixLocationNameFromCoordinates();
			else if (bHasLocationName && !bHasCoordinates)
				bHasCoordinates = fixCoordinatesFromLocationName();
			else if (!bHasLocationName && !bHasCoordinates)
				bHasCoordinates = bHasLocationName = getLastLocation();
			
			editTextPlaceName.setText(mFavLoc.name);
			String strCoordinates = formatCoordinates(mFavLoc.latitude, mFavLoc.longitude);
			textViewCoordinates.setText(strCoordinates);
		
			return;
		}
	}
	
	private String formatCoordinates(float latitude, float longitude) {
		StringBuilder builder = new StringBuilder();
		builder.append(latitude);
		builder.append(", ");
		builder.append(longitude);
		return builder.toString();
	}

	public void onResetScoreButtonClick(View view)
	{
		Editor editor = mGameSettings.edit();
		editor.remove(GAME_PREFERENCES_CURRENT_QUESTION);
		editor.remove(GAME_PREFERENCES_SCORE);
		editor.commit();
		updateCurrentScoreLabel();
	}
	
	public void onLaunchCamera(View v){
		Intent pictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(pictureIntent, TAKE_AVATAR_CAMERA_REQUEST);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case TAKE_AVATAR_CAMERA_REQUEST:
			if (resultCode == Activity.RESULT_CANCELED){
			}
			else if (resultCode == Activity.RESULT_OK){
				Bitmap cameraPic = (Bitmap) data.getExtras().get("data");
				saveAvatar(cameraPic);
			}
			break;
		case TAKE_AVATAR_GALLERY_REQUEST:
			if (resultCode == Activity.RESULT_CANCELED){
			}
			else if (resultCode == Activity.RESULT_OK){
				Uri photoUri = data.getData();
				
				try {
					Bitmap galleryPic = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
					saveAvatar(galleryPic);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		}
	}
	
	private void saveAvatar(Bitmap avatar){
		ImageButton imageButtonAvatar = (ImageButton) findViewById(R.id.imageButton_avatar);
		
		Resources res = getResources();
		int dips = (int) res.getDimension(R.dimen.avatar_size);
		int maxSide = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dips,res.getDisplayMetrics());
		Bitmap scaledAvatar = createScaledBitmapKeepingAspectRatio(avatar, maxSide);
		
		String strAvatarFileName = "avatar.jpg";
		try {
			scaledAvatar.compress(CompressFormat.JPEG, 100, openFileOutput(strAvatarFileName, MODE_PRIVATE));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Uri imageUriToSaveCameraImageTo = Uri.fromFile(new File(
				QuizSettingsActivity.this.getFilesDir(), strAvatarFileName));

		Editor editor = mGameSettings.edit();
		editor.putString(GAME_PREFERENCES_AVATAR,
				imageUriToSaveCameraImageTo.getPath());
		editor.commit();
		
		imageButtonAvatar.setImageBitmap(scaledAvatar);
	}
	
	private Bitmap createScaledBitmapKeepingAspectRatio(Bitmap bitmap, int maxSide){
		int orgHeight = bitmap.getHeight();
		int orgWidth = bitmap.getWidth();
		
		int scaledHeight = (orgHeight >= orgWidth ? maxSide 
				: (int)((float)maxSide * ((float)orgHeight/(float)orgWidth)));
		
		int scaledWidth = (orgWidth >= orgHeight ? maxSide 
				: (int)((float)maxSide * ((float)orgWidth/(float)orgHeight)));
		
		Bitmap scaledGalleryPic = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
		return scaledGalleryPic;
	}
	
	private void initFavoritePlacePicker(){
		TextView textViewFavoritePlace = (TextView) findViewById(R.id.TextView_FavoritePlace_Info);
		if (mGameSettings.contains(GAME_PREFERENCES_LOCATION_NAME))
			textViewFavoritePlace.setText(mGameSettings.getString(GAME_PREFERENCES_LOCATION_NAME, ""));
		else
			textViewFavoritePlace.setText(R.string.no_favorit_place_set);
	}
	
	public void onPickPlaceButtonClick(View view) {
		showDialog(PLACE_DIALOG_ID);
	}

	private void saveFavoriteLocation() {
		Editor editor = mGameSettings.edit();
		editor.putFloat(GAME_PREFERENCES_LATITUDE, mFavLoc.latitude);
		editor.putFloat(GAME_PREFERENCES_LONGITUDE, mFavLoc.longitude);
		editor.putString(GAME_PREFERENCES_LOCATION_NAME, mFavLoc.name);
		editor.commit();
	}
}