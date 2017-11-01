package com.ayteksokmen.langner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ayteksokmen.langner.Model.Person;
import com.ayteksokmen.langner.Util.Constants;
import com.ayteksokmen.langner.Util.SharedPrefUtil;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import static com.firebase.ui.auth.ui.AcquireEmailHelper.RC_SIGN_IN;

/**
 * Created by sem0025 on 25.09.2017.
 */

public class LoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

	private EditText userName, password;
	private TextView signUp;
	private RelativeLayout googleCard, faceCard;
	private ImageView logo;
	private ScrollView login;
	private DatabaseReference database;
	CallbackManager mCallbackManager;
	private int ANIMATION_DURATION = 500;
	private FirebaseAuth mAuth;
	private GoogleApiClient mGoogleApiClient;
	private Context mContext;
	private FrameLayout fl;
	private Boolean hasAge = false;
	private Boolean hasGender = false;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		FacebookSdk.sdkInitialize(this);

		setContentView(R.layout.activity_login);
		mContext = this;

		googleCard = (RelativeLayout) findViewById(R.id.googleLoginCard);
		faceCard = (RelativeLayout) findViewById(R.id.facebookLoginCard);
		fl = (FrameLayout) findViewById(R.id.contentFrame);

		signUp = (TextView) findViewById(R.id.signUp);
		logo = (ImageView) findViewById(R.id.logo);

		Intent i = new Intent(LoginActivity.this, MainActivity.class);
		if (sharedPreferences.getString("userId", "").equals(""))
			getLoginUI();
		else {
			updateLastSeen();
			finish();
			startActivity(i);
		}
		googleCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				signIn();
			}
		});

		mCallbackManager = CallbackManager.Factory.create();
		final LoginButton loginButton = (LoginButton) findViewById(R.id.faceLogin);
		loginButton.setReadPermissions("email", "public_profile");
		loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {
				handleFacebookAccessToken(loginResult.getAccessToken());
			}

			@Override
			public void onCancel() {
			}

			@Override
			public void onError(FacebookException error) {
			}
		});

		faceCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				loginButton.callOnClick();
			}
		});
		mAuth = FirebaseAuth.getInstance();


		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.default_web_client_id))
				.requestEmail()
				.requestProfile()
				.build();

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
				.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
				.addScope(new Scope(Scopes.PROFILE))
				.build();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mCallbackManager.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RC_SIGN_IN) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			if (result.isSuccess()) {
				GoogleSignInAccount account = result.getSignInAccount();
				firebaseAuthWithGoogle(account);
			} else {
			}
		}
	}

	private void signIn() {
		Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
		startActivityForResult(signInIntent, RC_SIGN_IN);
	}

	private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
		Log.d("", "firebaseAuthWithGoogle:" + acct.getId());

		AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							String photoURL;
							Log.d("", "signInWithCredential:success");
							FirebaseUser user = mAuth.getCurrentUser();
							if (user != null) {
								photoURL = String.valueOf(user.getPhotoUrl());
							} else {
								photoURL = "";
							}
							updateFirebaseToken(user, new SharedPrefUtil(getApplicationContext()).getString(Constants.ARG_FIREBASE_TOKEN, null), photoURL);
						} else {
							// If sign in fails, display a message to the user.
							Log.w("", "signInWithCredential:failure", task.getException());
							Toast.makeText(LoginActivity.this, "Authentication failed.",
									Toast.LENGTH_SHORT).show();

						}

						// ...
					}
				});
	}

	private void handleFacebookAccessToken(final AccessToken token) {
		AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							String photoURL = "";
							FirebaseUser user = task.getResult().getUser();
							if (!user.getProviderData().isEmpty() && user.getProviderData().size() > 1)
								photoURL = "https://graph.facebook.com/" + user.getProviderData().get(1).getUid() + "/picture?type=large";

							GraphRequest request = GraphRequest.newMeRequest(
									token,
									new GraphRequest.GraphJSONObjectCallback() {
										@Override
										public void onCompleted(
												JSONObject object,
												GraphResponse response) {
											try {
												sharedPreferences.edit().putString("userGender", object.getString("gender")).apply();
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
									});
							Bundle parameters = new Bundle();
							parameters.putString("fields", "id,name,gender");
							request.setParameters(parameters);
							request.executeAsync();

							updateFirebaseToken(user, new SharedPrefUtil(getApplicationContext()).getString(Constants.ARG_FIREBASE_TOKEN, null), photoURL);
						} else {
							Toast.makeText(LoginActivity.this, "Authentication failed.",
									Toast.LENGTH_SHORT).show();
						}

					}
				});
	}

	private void updateLastSeen() {
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		FirebaseDatabase.getInstance()
				.getReference()
				.child(Constants.ARG_USERS)
				.child(user.getUid())
				.child("lastSeen")
				.setValue(java.util.Calendar.getInstance().getTime());
	}

	private void updateFirebaseToken(final FirebaseUser user, String token, @Nullable final String photoUrl) {
		FirebaseDatabase.getInstance()
				.getReference()
				.child(Constants.ARG_USERS)
				.child(user.getUid())
				.child(Constants.ARG_FIREBASE_TOKEN)
				.setValue(token);
		updateLastSeen();
		sharedPreferences.edit().putString("userName", user.getDisplayName()).apply();
		sharedPreferences.edit().putString("userId", user.getUid()).apply();
		sharedPreferences.edit().putString("userEmail", user.getEmail()).apply();
		sharedPreferences.edit().putString("userPhotoUrl", photoUrl).apply();

		DatabaseReference database = FirebaseDatabase.getInstance().getReference();
		database.child(Constants.ARG_USERS)
				.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				hasGender = dataSnapshot.hasChild("gender");
				hasAge = dataSnapshot.hasChild("age");

				if (hasAge) {
					Person person = dataSnapshot.getValue(Person.class);
					sharedPreferences.edit().putString("userGender", person.getGender())
							.putString("userAge", person.getAge())
							.putString("from", person.getFrom())
							.putString("learnLang", person.getLearningLanguage())
							.putString("speakLang", person.getTalkingLanguage())
							.apply();
				}

				addUserToDatabase(photoUrl);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});

	}

	public void getLoginUI() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				Display display = getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				int height = size.y;

				TranslateAnimation animation = new TranslateAnimation(0, 0, (height / 2), 0);
				animation.setDuration(ANIMATION_DURATION);
				animation.setInterpolator(new DecelerateInterpolator());
				animation.setFillAfter(true);
				animation.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						new Handler().post(new Runnable() {
							@Override
							public void run() {
								YoYo.with(Techniques.FadeInDown).duration(700).playOn(googleCard);
								googleCard.setVisibility(View.VISIBLE);
								YoYo.with(Techniques.FadeInDown).duration(700).playOn(faceCard);
								faceCard.setVisibility(View.VISIBLE);
							}
						});
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}
				});
				logo.setVisibility(View.VISIBLE);
				logo.startAnimation(animation);
			}
		}, ANIMATION_DURATION);
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

	}

	@Override
	public void onBackPressed() {
		if (fl.getVisibility() != View.VISIBLE)
			super.onBackPressed();
	}

	public void addUserToDatabase(final String photoUrl) {
		if (!hasAge) {
			Intent i = new Intent(LoginActivity.this, ProfileInfoActivity.class);
			i.putExtra("photoUrl", photoUrl);
			finish();
			startActivity(i);
		} else {
			Intent i = new Intent(LoginActivity.this, MainActivity.class);
			finish();
			startActivity(i);
		}
	}
}
