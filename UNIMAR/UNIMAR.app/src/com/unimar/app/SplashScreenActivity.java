package com.unimar.app;

import com.unimar.app.util.SystemUiHider;
import com.unimar.backup.LocalBD;
import com.unimar.objects.LoginRes;
import com.unimar.samobj.TB_Usuario;
import com.unimar.validadores.Seguridad;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SplashScreenActivity extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 5;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash_screen);

		final View contentView = findViewById(R.id.fullscreen_content);

		getActionBar().hide();
		
		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {

			@Override
			@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
			public void onVisibilityChange(boolean visible) {
				
				//delayedHide(AUTO_HIDE_DELAY_MILLIS);
				/*
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
					// If the ViewPropertyAnimator API is available
					// (Honeycomb MR2 and later), use it to animate the
					// in-layout UI controls at the bottom of the
					// screen.
					if (mControlsHeight == 0) {
						mControlsHeight = controlsView.getHeight();
					}
					if (mShortAnimTime == 0) {
						mShortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
					}
					controlsView.animate().translationY(visible ? 0 : mControlsHeight).setDuration(mShortAnimTime);
				} else {
					// If the ViewPropertyAnimator APIs aren't
					// available, simply show or hide the in-layout UI
					// controls.
					controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
				}

				if (visible && AUTO_HIDE) {
					// Schedule a hide().
					delayedHide(AUTO_HIDE_DELAY_MILLIS);
				}*/
			}
		});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				/*if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}*/
				mSystemUiHider.toggle();
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		//findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
		
		AutenticacionTask autenticar = new AutenticacionTask(getApplicationContext());
		autenticar.execute((Void) null);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		//delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	private class AutenticacionTask extends AsyncTask<Void, Void, Boolean>{

		Context context = null;
		Boolean bAutenticado = false;
		String cErrorMessage = "";
		
		public AutenticacionTask(Context context){
			this.context = context;
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			TB_Usuario usuario = LocalBD.obtenerConfiguracionLocal(this.context);
			if (usuario != null){
				String mEmail = usuario.getcUsuario();
				String mPassword = usuario.getcClave();
				if (!mEmail.isEmpty()){
					try{
						Thread.sleep(2000);
						Seguridad seguridad = new Seguridad(this.context);
						LoginRes oLoginRes = seguridad.autenticarUsuario(mEmail, mPassword);
						if (oLoginRes != null){
							bAutenticado = (oLoginRes.getId() == 0);
						}
					}
					catch(Exception ex){
						this.cErrorMessage = ex.getMessage();
						bAutenticado = false;
					}
				}
			}
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			if (success) {
				if (this.bAutenticado){
					//LocalBD.grabarUsarioLogin(this.context, mEmail, mPassword);
					finish();
					Intent inicio_intent = new Intent(this.context, MainActivity.class);
					startActivity(inicio_intent);
				}
				else {
					if (this.cErrorMessage.length()==0){
						Toast aviso = Toast.makeText(this.context, this.cErrorMessage, Toast.LENGTH_LONG);
						aviso.show();
					}
					try{
						finish();
						Intent inicio_intent = new Intent(this.context, LoginActivity.class);
						startActivity(inicio_intent);
					}catch(Exception ex){
						System.out.printf(ex.getMessage());
					}
				}
				/*Toast aviso = Toast.makeText(this.context, "fin de demo", Toast.LENGTH_LONG);
				aviso.show();*/
			}
		}
	}
}
