package edu.salemstate.cs.advising;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static edu.salemstate.cs.advising.LoginActivity.CONNECTION_TIMEOUT;
import static edu.salemstate.cs.advising.LoginActivity.READ_TIMEOUT;



public class MainActivity extends AppCompatActivity {


    // Declarations
    private Button showAppointments, cancel, completed;
    private Date temp;
    private String dateString, timeString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get Advisee, Appointment information
        new getAdviseeInformation().execute();
    }


    // On Back button press
    @Override
    public void onBackPressed() {

        // Confirmation of log out dialog
        new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme)
        .setTitle("Log out?")
        .setNegativeButton("No",null)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Close this activity, Start previous activity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        })
        .show();
    }



    // Get Advisee Infromation
    private class getAdviseeInformation extends AsyncTask<String[], Void, String[]> {

        // Loading dialog
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Set loading dialog
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }


        @Override
        protected void onPostExecute(final String result[] )   {

            // Reference
            TextView adviseeName = (TextView)findViewById(R.id.adviseeName);
            TextView advisorName = (TextView)findViewById(R.id.advisorName);
            TextView advisorRoom = (TextView)findViewById(R.id.roomNum);
            TextView appointmentInfo = (TextView)findViewById(R.id.appointmentInfo);
            TextView noAppointmentInfo = (TextView)findViewById(R.id.noAppointmentInfo);
            TextView appointmentTime = (TextView)findViewById(R.id.appointmentTime);
            TextView appointmentDate = (TextView)findViewById(R.id.appointmentDate);
            FrameLayout frametop = (FrameLayout) findViewById(R.id.framelinetop);
            FrameLayout framebottom = (FrameLayout) findViewById(R.id.framelinebottom);
            cancel = findViewById(R.id.cancel);
            completed = findViewById(R.id.completed);

            // Set Advisee Name, Advisor Name, Advisor Room
            adviseeName.setText(result[2]);
            advisorName.setText(result[4]);
            advisorRoom.setText(result[5]);

            // If no appointment is booked, hide time, date and show no booked appointment
            if(result[3].equalsIgnoreCase("0")) {
                appointmentTime.setVisibility(View.INVISIBLE);
                appointmentDate.setVisibility(View.INVISIBLE);
                noAppointmentInfo.setVisibility(View.VISIBLE);

            // If appointment is booked, show time, date and show booked appointment
            } else if (result[3].equalsIgnoreCase("1")) {
                appointmentTime.setText(getTime(result[7]));
                appointmentDate.setText(getDate(result[8]));
                appointmentInfo.setVisibility(View.VISIBLE);
                frametop.setVisibility(View.VISIBLE);
                framebottom.setVisibility(View.VISIBLE);

                // If appointment is not completed, show cancel button
                if (result[9].equalsIgnoreCase("0")) {
                    cancel.setVisibility(View.VISIBLE);
                }
                // If appointment is completed, show completed button
                else {
                    completed.setVisibility(View.VISIBLE);
                }
            }

            // Reference
            showAppointments = findViewById(R.id.showAppointments);

            // Dismiss loading
            pdLoading.dismiss();


            // Set View Available Appointments button click listener
            showAppointments.setOnClickListener(new View.OnClickListener() {

                // On View Available Appointments button click
                public void onClick(View v) {

                    // Close this activity, Start Appointments Activity and pass variables
                    Intent intent = getIntent();
                    Intent intent1 = new Intent(MainActivity.this, AppointmentsActivity.class);
                    intent1.putExtra("id", intent.getStringExtra("id"));
                    intent1.putExtra("email", intent.getStringExtra("email"));
                    startActivity(intent1);
                    MainActivity.this.finish();
                }
            });


            // Set Cancel Appointment button click listener
            cancel.setOnClickListener(new View.OnClickListener() {

                // On Cancel button click
                public void onClick(View v) {

                    // Cancel Appointment confirmation dialog
                    new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme)
                    .setTitle("Cancel this appointment ?")
                    .setMessage( getDate(result[8]) + "\n" + getTime(result[7]))
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        // On Yes click
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Get variables from intent and cancel booked appointment
                            Intent intent = getIntent();
                            new CancelAppointment().execute(intent.getStringExtra("id"), intent.getStringExtra("Appointment_ID"));
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
                }
            });
        }


        protected String[] doInBackground(String[]... params) {

            // Link assignment
            String link = getResources().getString(R.string.link);
            try {

                // Cancel Appointment url
                URL url = new URL(link+"/rest/appointments.php");

                // Get variables from previous activty
                Intent intent = getIntent();
                String id = intent.getStringExtra("id");
                String email = intent.getStringExtra("email");

                // LinkedHashMap create and assign parameters
                Map<String, Object> params1 = new LinkedHashMap<>();
                params1.put("id", id);
                params1.put("email", email);
                params1.put("task", "MainPageInfo");

                // String builder object create and append parameters
                StringBuilder post = new StringBuilder();
                for (Map.Entry<String, Object> param : params1.entrySet()) {
                    if (post.length() != 0) post.append('&');
                    post.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    post.append('=');
                    post.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                String Parameters = post.toString();

                // Open connection
                URLConnection con = url.openConnection();

                // Set send
                con.setDoOutput(true);

                // Send
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(Parameters);
                writer.flush();

                // Read
                StringBuilder sb = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String adviseeInfo;
                while ((adviseeInfo = bufferedReader.readLine()) != null) {
                    sb.append(adviseeInfo + "\n");
                }

                // Create result array
                String result[] = new String[10];

                // Populate array
                try {
                    JSONArray jsonArray = new JSONArray(sb.toString().trim());

                    // Loop through JSON array
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        // Result assignments
                        result[0] = obj.getString("Advisee_ID");
                        result[1] = obj.getString("Advisor_ID");
                        result[2] = obj.getString("Advisee_Name");
                        result[3] = obj.getString("Aptmtmade");
                        result[4] = obj.getString("Advisor_Name");
                        result[5] = obj.getString("Room_Num");
                        result[6] = obj.getString("Appointment_ID");
                        result[7] = obj.getString("Aptmt_Time");
                        result[8] = obj.getString("Aptmt_Date");
                        result[9] = obj.getString("show_not");

                        intent.putExtra("Appointment_ID", result[6]);
                    }
                    // Return to OnPostExecute
                    return result;

                } catch (JSONException e) {
                    e.printStackTrace();
                    // result[8] = "exception";
                    return result;
                }

            } catch (Exception e) {
                return null;
            }
        }
    }


    // Cancel Booked Appointment
    private class CancelAppointment extends AsyncTask<String, String, String[]>
    {
        // Loading dialog
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);

        HttpURLConnection con;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Set loading dialog
            pdLoading.setMessage("\tCanceling...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String[] doInBackground(String... params) {

            // Link assignment
            String link = getResources().getString(R.string.link);

            // Result array declaration
            String result[] = new String[3];

            try {

                // Cancel Appointment url
                url = new URL(link+"/rest/appointments.php");

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

                // Set Send and Receive
                con.setDoInput(true);
                con.setDoOutput(true);


                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("id", params[0])
                .appendQueryParameter("appointmentId", params[1])
                .appendQueryParameter("task", "cancel");
                String Parameters = builder.build().getEncodedQuery();

                // Send
                OutputStream osw = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(osw, "UTF-8"));
                writer.write(Parameters);
                writer.flush();
                writer.close();
                osw.close();
                con.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
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

                    // Return to OnPostExecute
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

            // Dismiss loading dialog
            pdLoading.dismiss();

            // If result is true, Appointment is Canceled
            if(result[0].equalsIgnoreCase("true"))
            {
                // Confirmation of Canceled Appointment dialog
                new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme)
                .setTitle("Confirmation!")
                .setMessage("Appointment is canceled!")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Refresh this Activity, Start previous activity and pass variables
                        Intent intent = new Intent(MainActivity.this,MainActivity.class);
                        intent.putExtra("id", intent.getStringExtra("id"));
                        intent.putExtra("email", intent.getStringExtra("email"));
                        startActivity(getIntent());
                        MainActivity.this.finish();
                    }
                })
                .show();

            // If result is false, Appointment is not canceled
            }else if (result[0].equalsIgnoreCase("false")){

                // Show error dialog
                new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Error!")
                .setMessage("Appointment was not canceled.")
                .setPositiveButton("Ok", null )
                .show();

            // If result is exception or unsuccessful, Connection problem
            } else if (result[0].equalsIgnoreCase("exception") || result[0].equalsIgnoreCase("unsuccessful")) {

                // Show error, dialog
                new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme)
                .setTitle("Connection Problem!")
                .setMessage("Please try again later.")
                .setPositiveButton("Ok", null )
                .show();
            }
        }
    }


    // Change Date format
    public String getDate(String date){

        SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat day = new SimpleDateFormat("EEEE, MMMM d");

        try {
            temp = in.parse(date);
            dateString = day.format(temp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }


    // Change Time format
    public String getTime(String time){

        String timeA[] = time.split("-");
        String startTime = timeA[0];
        String endTime = timeA[1];

        SimpleDateFormat in = new SimpleDateFormat("H:mm");
        SimpleDateFormat day = new SimpleDateFormat("h:mma");

        try {
            temp = in.parse(startTime);
            startTime = day.format(temp);
            temp = in.parse(endTime);
            endTime = day.format(temp);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Concatenate startTime and endTime and assign to timeString
        timeString = (startTime+ " - " +endTime);

        // Replace small letters with capitals
        timeString = timeString.replace("AM", "am").replace("PM","pm");

        return timeString;
    }
}