package com.unimar.app;

import com.unimar.backup.LocalBD;
import com.unimar.objects.LoginRes;
import com.unimar.validadores.Seguridad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

/**
 * A login screen that offers login via email/password.
 * 
 */
public class LoginActivity extends Activity  {

	private UserLoginTask mAuthTask = null;
	
	// UI references.
	private AutoCompleteTextView mEmailView;
	private EditText mPasswordView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// Set up the login form.
		mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

		mPasswordView = (EditText) findViewById(R.id.password);

		Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
		mEmailSignInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});

		findViewById(R.id.login_form);
		//mProgressView = findViewById(R.id.login_progress);
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		String email = mEmailView.getText().toString();
		String password = mPasswordView.getText().toString();

		mAuthTask = new UserLoginTask(this, email, password);
		mAuthTask.execute((Void) null);
	}

	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		Context context;
		String mEmail;
		String mPassword;
		Boolean validado; 

		UserLoginTask(Context _context, String email, String password) {
			this.context = _context;					
			this.mEmail = email;
			this.mPassword = password;
			this.validado = false;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				Seguridad seguridad = new Seguridad(this.context);
				LoginRes oLoginRes = seguridad.autenticarUsuario(mEmail, mPassword);
				if (oLoginRes != null){
					this.validado = (oLoginRes.getId() == 0);
				}
			} catch (Exception e) {
				this.validado = false;
			}
			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			if (success){
				if (this.validado) {
					LocalBD.grabarUsarioLogin(this.context, this.mEmail, this.mPassword);
					finish();
					Intent inicio_intent = new Intent(this.context, MainActivity.class);
					startActivity(inicio_intent);
				} else {
					mPasswordView.setError(getString(R.string.error_incorrect_password));
					mPasswordView.requestFocus();
				}
			}
		}
	}
}
