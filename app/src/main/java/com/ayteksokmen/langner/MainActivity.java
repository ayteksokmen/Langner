package com.ayteksokmen.langner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ayteksokmen.langner.Fragment.ExploreFragment;
import com.ayteksokmen.langner.Fragment.MainFragment;
import com.ayteksokmen.langner.Fragment.ProfileFragment;
import com.ayteksokmen.langner.Util.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by sem0025 on 18.09.2017.
 */

public class MainActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

	private DrawerLayout drawerLayout;
	private boolean gps_enabled;
	private boolean network_enabled;
	private int REQUEST_CHECK_SETTINGS = 1;
	private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
	private DatabaseReference database;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerLayout.setScrimColor(Color.TRANSPARENT);

		final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

		SampleFragmentPagerAdapter adapter = new SampleFragmentPagerAdapter(getSupportFragmentManager(),
				MainActivity.this);
		viewPager.setAdapter(adapter);

		final TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
		tabLayout.setupWithViewPager(viewPager);
		viewPager.setOffscreenPageLimit(2);

		for (int i = 0; i < tabLayout.getTabCount(); i++) {
			TabLayout.Tab tab = tabLayout.getTabAt(i);
			tab.setCustomView(adapter.getTabView(i));
		}

		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if (FirebaseAuth.getInstance().getCurrentUser() == null) {
			Intent i = new Intent(MainActivity.this, LoginActivity.class);
			i.putExtra("from", "main");
			startActivity(i);
		} else {
			Toast.makeText(this,
					"Welcome " + FirebaseAuth.getInstance()
							.getCurrentUser()
							.getDisplayName(),
					Toast.LENGTH_LONG)
					.show();
		}

		database = FirebaseDatabase.getInstance().getReference();

		try {
			gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
			gps_enabled = false;
		}

		try {
			network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
			network_enabled = false;
		}
		showPhoneStatePermission();
		permissionDialog();
		guessCurrentPlace();

		((ImageView) tabLayout.getTabAt(0).getCustomView().findViewById(R.id.imgView)).setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

		tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				((ImageView) tab.getCustomView().findViewById(R.id.imgView)).setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {
				((ImageView) tab.getCustomView().findViewById(R.id.imgView)).setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});

	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

	}

	public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
		final int PAGE_COUNT = 3;
		private String tabTitles[] = new String[]{"HOME", "SEARCH", "PROFILE"};
		private Context context;

		public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
			super(fm);
			this.context = context;
		}

		@Override
		public int getCount() {
			return PAGE_COUNT;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case 0:
					return ExploreFragment.newInstance();
				case 1:
					return MainFragment.newInstance();
				case 2:
					String userId = sharedPreferences.getString("userId", "0");
					return ProfileFragment.newInstance(userId);
			}
			return null;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return tabTitles[position];
		}

		private int[] imageResId = {R.drawable.home, R.drawable.explore, R.drawable.profile};

		View getTabView(int position) {
			View v;
//			if (position != 2) {
			v = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
			ImageView img = (ImageView) v.findViewById(R.id.imgView);
			img.setImageResource(imageResId[position]);
//			} else {
//				v = LayoutInflater.from(context).inflate(R.layout.profile_item_tab, null);
//				ImageView img = (ImageView) v.findViewById(R.id.imgView);
//				String userPhotoUrl = sharedPreferences.getString("userPhotoUrl", "");
//				Picasso.with(context).load(userPhotoUrl).into(img);
//			}
			return v;
		}
	}

	public void permissionDialog() {
		if (!gps_enabled && !network_enabled) {
			displayLocationSettingsRequest(getApplicationContext());
		}
	}

	private void guessCurrentPlace() {
		if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return;
		}

		GoogleApiClient mGoogleApiClient = new GoogleApiClient
				.Builder(this)
				.addApi(Places.GEO_DATA_API)
				.addApi(Places.PLACE_DETECTION_API)
				.enableAutoManage(this, this)
				.build();
		PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
		result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
			@Override
			public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {

				if (likelyPlaces.getCount() > 0) {
					float temp = likelyPlaces.get(0).getLikelihood();
					for (PlaceLikelihood placeLikelihood : likelyPlaces) {
						if (placeLikelihood.getLikelihood() < temp) {
							temp = placeLikelihood.getLikelihood();
						}
						Log.d("KONUM", String.format("Place '%s' has likelihood: %g",
								placeLikelihood.getPlace().getName(),
								placeLikelihood.getLikelihood()));
					}
					final double longitude = likelyPlaces.get(0).getPlace().getLatLng().longitude;
					double latitude = likelyPlaces.get(0).getPlace().getLatLng().latitude;
					sharedPreferences.edit().putString("latitude", String.valueOf(latitude)).apply();
					sharedPreferences.edit().putString("longitude", String.valueOf(longitude)).apply();
					likelyPlaces.release();

					database.child(Constants.ARG_USERS)
							.child(sharedPreferences.getString("userId", ""))
							.child("latitude")
							.setValue(latitude)
							.addOnCompleteListener(new OnCompleteListener<Void>() {
								@Override
								public void onComplete(@NonNull Task<Void> task) {
									if (task.isSuccessful()) {
										database.child(Constants.ARG_USERS)
												.child(sharedPreferences.getString("userId", ""))
												.child("longitude")
												.setValue(longitude);
									}
								}
							});
				}
			}
		});
	}

	private void showPhoneStatePermission() {
		int permissionCheck = ContextCompat.checkSelfPermission(
				MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
		if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
					android.Manifest.permission.ACCESS_FINE_LOCATION)) {
				showExplanation("Permission Needed", "Rationale", android.Manifest.permission.ACCESS_FINE_LOCATION, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
			} else {
				requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
			}
		} else {
			Log.d("Permission Already !", "");
		}
	}

	private void showExplanation(String title,
								 String message,
								 final String permission,
								 final int permissionRequestCode) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle(title)
				.setMessage(message)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						requestPermission(permission, permissionRequestCode);
					}
				});
		builder.create().show();
	}

	private void requestPermission(String permissionName, int permissionRequestCode) {
		ActivityCompat.requestPermissions(MainActivity.this,
				new String[]{permissionName}, permissionRequestCode);
	}


	private void displayLocationSettingsRequest(Context context) {
		GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
				.addApi(LocationServices.API).build();
		googleApiClient.connect();

		LocationRequest locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(10000);
		locationRequest.setFastestInterval(10000 / 2);

		LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
		builder.setAlwaysShow(true);

		PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
		result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
			@Override
			public void onResult(LocationSettingsResult result) {
				final Status status = result.getStatus();
				switch (status.getStatusCode()) {
					case LocationSettingsStatusCodes.SUCCESS:
						Log.i("", "All location settings are satisfied.");
						break;
					case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
						Log.i("", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

						try {
							// Show the dialog by calling startResolutionForResult(), and check the result
							// in onActivityResult().
							status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
						} catch (IntentSender.SendIntentException e) {
							Log.i("", "PendingIntent unable to execute request.");
						}
						break;
					case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
						Log.i("", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
						break;
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
									Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 200) {
			if (resultCode == RESULT_OK) {
				Toast.makeText(this,
						"Successfully signed in. Welcome!",
						Toast.LENGTH_LONG)
						.show();
			} else {
				Toast.makeText(this,
						"We couldn't sign you in. Please try again later.",
						Toast.LENGTH_LONG)
						.show();

				// Close the app
				finish();
			}
		}

		if (requestCode == REQUEST_CHECK_SETTINGS) {
			permissionDialog();
		}
	}
}