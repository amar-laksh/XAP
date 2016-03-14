package com.app.amar.XAP;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.apps.amar.xap.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressWarnings("ALL")
public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private UserLoginTask mAuthTask = null;

    // UI references.
    private View mProgressView;
    private View mLoginFormView;
    private static String year;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final Spinner email = (Spinner) findViewById(R.id.dept_spinner);
        email.setOnItemSelectedListener(this);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.dept_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        email.setAdapter(adapter);
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);


        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress(true);
                final String Email = email.getSelectedItem().toString();
                EditText pass = (EditText)findViewById(R.id.password);
                final String Password = pass.getText().toString();
                mAuthTask = new UserLoginTask(Email, Password);
                mAuthTask.execute((Void) null);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        year = item;


        // Showing selected spinner item
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }





    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean>  {

        private String mEmail;
        private String mPassword;
        private String attendance="";
        private String studentName="";
        private String studentClass="";


        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
            int zero = Integer.parseInt(mPassword.substring(0,1));
            if(mPassword.length()==1){
                if(zero!=0){
                    mPassword = "00" + mPassword;
                }
            }
            else if(mPassword.length() >1){
                mPassword = "0"+mPassword;
            }

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String url1 = "http://xaviers.edu.in/demouser/Checklogin.php";
            String url2 = "http://xaviers.edu.in/demouser/StudentAttendance.php";
            Connection.Response result = null;
            Document res = null;
            if(year!=""){
                mEmail = year;
            }
            try {

                result = (Connection.Response) Jsoup.connect(url1)
                        .data("myusername", mEmail.toString())
                        .data("mypassword", mPassword.toString())
                        .data("Submit", "Login")
                        .method(Connection.Method.POST)
                        .execute();

                res = Jsoup.connect(url2)
                        .data("drpcourse", mEmail.toString())
                        .data("txtrollno", mPassword.toString())
                        .data("Submit", "Show Attendance")
                        .cookies(result.cookies())
                        .post();
                Elements r = res.select("form[name=form1]").select("h4");
                attendance = res.toString();
                String res1 = res.select("form[name=form1]").select("tbody").text();
                studentName = null;
                Pattern pattern = Pattern.compile("Name:[\\t\\n ]*(.*)[\\t\\n ]*Subject");
                Matcher matcher = pattern.matcher(res1);
                if (matcher.find()) {
                    studentName = matcher.group(1);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }



            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                studentClass = mEmail.toString();
                Intent myIntent = new Intent(LoginActivity.this, homeActivity.class);
                myIntent.putExtra("attendance", attendance); //Optional parameters
                myIntent.putExtra("studentClass",studentClass);
                myIntent.putExtra("studentName",studentName);
                LoginActivity.this.startActivity(myIntent);

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

