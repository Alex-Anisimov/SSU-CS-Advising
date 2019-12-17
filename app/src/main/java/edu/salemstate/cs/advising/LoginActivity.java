package edu.salemstate.cs.advising;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class LoginActivity extends AppCompatActivity {

    // Variables declaration
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    private EditText etId;
    private EditText etEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Reference variables
        etId = (EditText) findViewById(R.id.SSUid);
        etEmail = (EditText) findViewById(R.id.SSUemail);
    }


    // If Login is pressed
    public void checkLogin(View arg0) {

        // Check if ID and Email are entered
        if (etId.getText().toString().trim().length() == 0 || etEmail.getText().toString().trim().length() == 0) {
            // ID and Email are not entered
            if (etId.getText().toString().trim().length() == 0 && etEmail.getText().toString().trim().length() == 0) {
                etId.setError("ID is required!");  //////
                etId.requestFocus();
                etEmail.setError("Email is required!");  //////
                etId.requestFocus();
            }
            // ID is not entered
            else if (etId.getText().toString().trim().length() == 0) {  ////
                etId.setError("ID is required!");  //////
                etId.requestFocus();
            }
            // Email is not entered
            else if (etEmail.getText().toString().trim().length() == 0) { ////
                etEmail.setError("Email is required!");  //////
                etEmail.requestFocus();
            }

        }
        else
        {
            // Declare and Reference variables
            final String id;
            final String email = etEmail.getText().toString();
            String S = "s";

            // Assign and Check ID if first char is 'S'
            if ((etId.getText().toString().toLowerCase().substring(0, 1)).equals(S)) {

                StringBuilder temp = new StringBuilder((etId.getText().toString().toLowerCase()));
                temp.deleteCharAt(0);
                id = temp.toString();
            }
            else {
                id = etId.getText().toString();
            }

            // Login
            new Login().execute(id, email);
        }
    }


    // Login class
    private class Login extends AsyncTask<String, String, String[]>
    {
        ProgressDialog pdLoading = new ProgressDialog(LoginActivity.this);
        HttpURLConnection con;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Show loading dialog
            pdLoading.setMessage("\tSigning in...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String[] doInBackground(String... params) {

            // Assign variables
            String result[] = new String[3];
            String link = getResources().getString(R.string.link);

            try {

                // Login url
                url = new URL(link+"/rest/login.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                result[0] = "exception";
                return result;
            }

            try {
                // Setup connection
                con = (HttpURLConnection)url.openConnection();
                con.setReadTimeout(READ_TIMEOUT);
                con.setConnectTimeout(CONNECTION_TIMEOUT);
                con.setRequestMethod("POST");

                // Set send and receive
                con.setDoInput(true);
                con.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("id", params[0])
                .appendQueryParameter("email", params[1]);
                String Parameters = builder.build().getEncodedQuery();

                // Send
                OutputStream osw = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(osw, "UTF-8"));
                writer.write(Parameters);
                writer.flush();
                writer.close();
                osw.close();
                con.connect();

            } catch (IOException e) {
                e.printStackTrace();
                result[0] = "exception";
                return result;
            }

            try {
                int response = con.getResponseCode();

                // Check if connection made
                if (response == HttpURLConnection.HTTP_OK) {

                    // Read
                    InputStream input = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder temp = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        temp.append(line);
                    }

                    String temp1 = temp.toString();

                    // Assign results
                    result[0] = temp1;
                    result[1] = params[0];
                    result[2] = params[1];

                    // Return to onPostExecute
                    return result;

                }else{
                    result[0] = "unsuccessful";
                    return result;
                }

            } catch (IOException e) {
                e.printStackTrace();
                result[0] = "exception";
                return result;

            } finally {
                con.disconnect();
            }
        }


        @Override
        protected void onPostExecute(String result[]) {

            //Dismiss loading dialog
            pdLoading.dismiss();

            // If result is true, advisee logged in
            if(result[0].equalsIgnoreCase("true"))
            {
                // Start Main Activity and pass variables
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                intent.putExtra("id", result[1]);
                intent.putExtra("email", result[2]);
                startActivity(intent);
                LoginActivity.this.finish();

            // If result is false, show error (wrong id and/or email)
            }else if (result[0].equalsIgnoreCase("false")){
                etId.setError("Incorrect ID or Email.");  //////
                etId.requestFocus();

            // If result is exception or unsuccessful, show error (connection problem)
            } else if (result[0].equalsIgnoreCase("exception") || result[0].equalsIgnoreCase("unsuccessful")) {

                // Show error dialog
                new AlertDialog.Builder(LoginActivity.this)
                .setTitle("Connection Problem!")
                .setMessage("Please try again later.")
                .setPositiveButton("Ok", null)
                .show();
            }
        }

    }
}
