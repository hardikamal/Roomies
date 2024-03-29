package com.phaseii.rxm.roomies.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.phaseii.rxm.roomies.R;
import com.phaseii.rxm.roomies.database.RoomiesContract;
import com.phaseii.rxm.roomies.exception.RoomXpnseMngrException;
import com.phaseii.rxm.roomies.helper.RoomiesConstants;
import com.phaseii.rxm.roomies.helper.RoomiesHelper;
import com.phaseii.rxm.roomies.service.MiscService;
import com.phaseii.rxm.roomies.service.MiscServiceImpl;
import com.phaseii.rxm.roomies.service.RoomService;
import com.phaseii.rxm.roomies.service.RoomServiceImpl;
import com.phaseii.rxm.roomies.service.UserService;
import com.phaseii.rxm.roomies.service.UserServiceImpl;
import com.phaseii.rxm.roomies.view.AlphaForegroundColorSpan;
import com.phaseii.rxm.roomies.view.RoomiesScrollView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.phaseii.rxm.roomies.helper.RoomiesConstants.APP_ERROR;
import static com.phaseii.rxm.roomies.helper.RoomiesConstants.IS_GOOGLE_FB_LOGIN;

public class ProfileActivity extends ActionBarActivity {

	private static final int REQUEST_CODE = 1;
	private static final int USER = 0;
	private static final int ROOM = 1;
	private AlphaForegroundColorSpan mAlphaForegroundColorSpan;
	private SpannableString mSpannableString;
	private ImageView coloredBackgroundView;
	private int lastTopValueAssigned = 0;
	private Bitmap bitmap;
	private Toast mToast;
	private String username;
	private SharedPreferences.Editor mEditor;
	private SharedPreferences mSharedPreferences;
	private boolean isGoogleFBLogin;
	private Toolbar mToolbar;
	private int currentApiVersion;
	private UserService userService;
	private RoomService roomService;
	private MiscService miscService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		currentApiVersion = Build.VERSION.SDK_INT;

		userService = new UserServiceImpl(getBaseContext());
		roomService = new RoomServiceImpl(getBaseContext());
		miscService = new MiscServiceImpl(getBaseContext());

		mSpannableString = new SpannableString("Profile");
		mAlphaForegroundColorSpan = new AlphaForegroundColorSpan(0xFFFFFF);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mToolbar.setTitle("Profile");
		setSupportActionBar(mToolbar);
		final ColorDrawable cd = new ColorDrawable(getResources().getColor(R.color.primary));
		cd.setAlpha(0);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("");
		actionBar.setBackgroundDrawable(cd);
		actionBar.setHomeAsUpIndicator(R.drawable.ic_roomies_back);

		RoomiesScrollView scrollViewHelper = (RoomiesScrollView) findViewById(
				R.id.scrollViewHelper);

		scrollViewHelper.setOnScrollViewListener(new RoomiesScrollView.OnScrollViewListener() {
			@Override
			public void onScrollChanged(RoomiesScrollView v, int l, int t, int oldl, int oldt) {
				setTitleAlpha(255 - getAlphaforActionBar(v.getScrollY()));
				cd.setAlpha(getAlphaforActionBar(v.getScrollY()));
				if (currentApiVersion >= Build.VERSION_CODES.HONEYCOMB) {
					parallaxImage(coloredBackgroundView);
				}
			}

			private void parallaxImage(View view) {
				Rect rect = new Rect();
				view.getLocalVisibleRect(rect);
				if (lastTopValueAssigned != rect.top) {
					lastTopValueAssigned = rect.top;
					view.setY((float) (rect.top / 2.0));
				}
			}

			private int getAlphaforActionBar(int scrollY) {
				int minDist = 0, maxDist = (int) TypedValue.applyDimension(TypedValue
								.COMPLEX_UNIT_DIP, 250,
						getResources().getDisplayMetrics());
				if (scrollY > maxDist) {
					return 255;
				} else {
					if (scrollY < minDist) {
						return 0;
					} else {
						return (int) ((255.0 / maxDist) * scrollY);
					}
				}
			}

		});

		populateFeilds();
		setUpEditors();

	}


	private void setTitleAlpha(float alpha) {
		if (alpha < 1) {
			alpha = 1;
		}
		mAlphaForegroundColorSpan.setAlpha(alpha);
		mSpannableString.setSpan(mAlphaForegroundColorSpan, 0, mSpannableString.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		getSupportActionBar().setTitle(mSpannableString);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && null != data)
			try {
				if (bitmap != null) {
					bitmap.recycle();
				}
				InputStream stream = getContentResolver().openInputStream(data.getData());
				bitmap = BitmapFactory.decodeStream(stream);
				stream.close();
				coloredBackgroundView.setImageBitmap(bitmap);
				coloredBackgroundView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				String username = getSharedPreferences(RoomiesConstants
						.ROOM_INFO_FILE_KEY, Context.MODE_PRIVATE).
						getString(RoomiesConstants.NAME, null);

				File file = new File(getFilesDir(),
						username + getResources().getString(R.string.PROFILEJPG));
				FileOutputStream fos = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
				file.getAbsolutePath();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void populateFeilds() {

		mSharedPreferences = getSharedPreferences(RoomiesConstants.ROOM_INFO_FILE_KEY,
				MODE_PRIVATE);
		username = mSharedPreferences.getString(RoomiesConstants.NAME, null);
		String mailVal = mSharedPreferences.getString(RoomiesConstants.EMAIL_ID, null);
		String roomAliasVal = mSharedPreferences.getString(RoomiesConstants.ROOM_ALIAS, null);
		String noOfMembersVal = mSharedPreferences.getString(RoomiesConstants.ROOM_NO_OF_MEMBERS,
				null);
		isGoogleFBLogin = mSharedPreferences.getBoolean(IS_GOOGLE_FB_LOGIN, false);

		mSharedPreferences = getSharedPreferences(RoomiesConstants.ROOM_BUDGET_FILE_KEY,
				MODE_PRIVATE);
		String rentVal = mSharedPreferences.getString(RoomiesConstants.RENT_MARGIN, null);
		String maidVal = mSharedPreferences.getString(RoomiesConstants.MAID_MARGIN, null);
		String electricityVal = mSharedPreferences.getString(RoomiesConstants.ELECTRICITY_MARGIN,
				null);
		String miscellaneousVal = mSharedPreferences.getString(RoomiesConstants.MISC_MARGIN, null);

		setupBackground();
		TextView savedName = (TextView) findViewById(R.id.saved_name);
		savedName.setText(username);

		TextView savedMail = (TextView) findViewById(R.id.saved_mail);
		savedMail.setText(mailVal);

		TextView name = (TextView) findViewById(R.id.name);
		name.setText(username);

		TextView email = (TextView) findViewById(R.id.email);
		email.setText(mailVal);

		TextView roomAlias = (TextView) findViewById(R.id.room_alias);
		roomAlias.setText(roomAliasVal);

		TextView noOfMembers = (TextView) findViewById(R.id.no_of_members);
		noOfMembers.setText(noOfMembersVal);

		TextView rent = (TextView) findViewById(R.id.rent);
		rent.setText(rentVal);

		TextView maid = (TextView) findViewById(R.id.maid);
		maid.setText(maidVal);

		TextView electricity = (TextView) findViewById(R.id.electricity);
		electricity.setText(electricityVal);

		TextView misc = (TextView) findViewById(R.id.misc);
		misc.setText(miscellaneousVal);

		if (isGoogleFBLogin) {
			findViewById(R.id.name_edit_button).setVisibility(View.GONE);
			findViewById(R.id.email_edit_button).setVisibility(View.GONE);
		}

	}

	private void setUpEditors() {
		if (!isGoogleFBLogin) {
			makeEditable("name", USER);
			makeEditable("email", USER);
		}
		makeEditable("room_alias", ROOM);
		makeEditable("no_of_members", ROOM);
		makeEditable("rent", ROOM);
		makeEditable("maid", ROOM);
		makeEditable("electricity", ROOM);
		makeEditable("misc", ROOM);
	}

	private void makeEditable(final String feildId, final int mode) {
		Resources resources = getResources();
		String packageName = getPackageName();
		int resId = resources.getIdentifier(feildId, "id", packageName);
		int resEditId = resources.getIdentifier(feildId + "_edit", "id", packageName);
		int resEditButtonId = resources.getIdentifier(feildId + "_edit_button", "id",
				packageName);
		final int resSaveButtonId = resources.getIdentifier(feildId + "_save_button", "id",
				packageName);
		final TextView field = (TextView) findViewById(resId);
		final EditText field_edit = (EditText) findViewById(resEditId);
		final Button field_edit_button = (Button) findViewById(resEditButtonId);
		final Button field_save_button = (Button) findViewById(resSaveButtonId);
		field_save_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (field_save_button.getVisibility() == View.VISIBLE) {
					if (!TextUtils.isEmpty(field_edit.getText().toString().trim())) {
						boolean isUpdateSuccessful = false;
						if (isGoogleFBLogin) {
							username = getSharedPreferences(RoomiesConstants.ROOM_INFO_FILE_KEY,
									MODE_PRIVATE).getString(RoomiesConstants.EMAIL_ID, null);
						}
						switch (mode) {
							case USER:

								if ("name".equals(feildId)) {
									if ((userService.update(username,
											field_edit.getText().toString(),
											RoomiesContract.UserCredentials.COLUMN_NAME_USERNAME)) &&
											(roomService.updateRoomMargins(username, feildId,
													field_edit.getText().toString())) && (miscService.updateUser(
											username, field_edit.getText().toString()))) {
										isUpdateSuccessful = true;
									}

								} else {
									isUpdateSuccessful = userService.update(username,
											field_edit.getText().toString(),
											RoomiesContract.UserCredentials.COLUMN_NAME_EMAIL_ID);
								}
								break;
							case ROOM:
								/*if ("no_of_members".equals(feildId)) {
									SharedPreferences.Editor mEditor = getSharedPreferences
											(RoomiesConstants.ROOM_INFO_FILE_KEY,
													MODE_PRIVATE).edit();
									mEditor.putString(RoomiesConstants.ROOM_NO_OF_MEMBERS,
											field_edit.getText().toString());
									mEditor.apply();
									isUpdateSuccessful = true;
								} else {*/
								isUpdateSuccessful = roomService.updateRoomMargins(username,
										feildId, field_edit.getText().toString());


								break;
							default:
								break;

						}
						if (isUpdateSuccessful) {
							RoomiesHelper.createToast(getBaseContext(), feildId + " updated",
									mToast);
							populateFeilds();
						}
					}
					field.setVisibility(View.VISIBLE);
					field_edit.setVisibility(View.INVISIBLE);
					field_edit_button.setVisibility(View.VISIBLE);
					field_save_button.setVisibility(View.INVISIBLE);
					if (currentApiVersion >= Build.VERSION_CODES.LOLLIPOP) {
						mToolbar.setVisibility(View.VISIBLE);
					}
				}
			}
		});
		field_edit_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (field_edit_button.getVisibility() == View.VISIBLE) {
					field.setVisibility(View.INVISIBLE);
					field_edit.setVisibility(View.VISIBLE);
					field_save_button.setVisibility(View.VISIBLE);
					field_edit_button.setVisibility(View.INVISIBLE);
					if (currentApiVersion >= Build.VERSION_CODES.LOLLIPOP) {
						mToolbar.setVisibility(View.GONE);
					}
				}
			}
		});
	}

	private void setupBackground() {
		coloredBackgroundView = (ImageView) findViewById(
				R.id.colored_background_view);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		username = getSharedPreferences(RoomiesConstants.ROOM_INFO_FILE_KEY,
				MODE_PRIVATE).getString(RoomiesConstants.NAME, null);
		Bitmap bitmap = BitmapFactory.decodeFile(new File(getFilesDir(),
				username + getResources().getString(
						R.string.PROFILEJPG)).getAbsolutePath(), options);
		if (null != bitmap) {
			coloredBackgroundView.setImageBitmap(bitmap);
			coloredBackgroundView.setScaleType(ImageView.ScaleType.FIT_XY);
		}
		if (!isGoogleFBLogin) {
			coloredBackgroundView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					startActivityForResult(intent, REQUEST_CODE);
				}
			});
		}
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_demo, menu);
		return true;
	}*/

	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}*/

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		try {
			RoomiesHelper.startActivityHelper(this,
					getResources()
							.getString(R.string.HomeScreen_Activity), null, true);
		} catch (RoomXpnseMngrException e) {
			RoomiesHelper.createToast(this, APP_ERROR, mToast);
			System.exit(0);
		}
	}
}
