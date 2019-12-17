package edu.salemstate.cs.advising;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static edu.salemstate.cs.advising.LoginActivity.CONNECTION_TIMEOUT;
import static edu.salemstate.cs.advising.LoginActivity.READ_TIMEOUT;



public class AppointmentsActivity extends AppCompatActivity {

    // Declarations
    ListView listView;
    private AppointmentAdapter arrayAdapter;
    private Button book;
    private FrameLayout frame;
    boolean selected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        // Link assignment
        String link = getResources().getString(R.string.link);

        // Reference list view
        listView = (ListView) findViewById(R.id.listView);

        // Get list of available appointment
        getAvailableAppointments(link+"/rest/appointments.php");
    }

    // Get list of Available Appointments
    private void getAvailableAppointments(final String urlWebService) {

        class GetAvailableAppointments extends AsyncTask<Void, Void, String> {

            // Loading dialog
            ProgressDialog pdLoading = new ProgressDialog(AppointmentsActivity.this);

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                // Set loading dialog
                pdLoading.setMessage("\tLoading...");
                pdLoading.setCancelable(false);
                pdLoading.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                // Reference to text view
                TextView noAppointments = (TextView)findViewById(R.id.noAppointments);

                // No available appointments text view set visible if no appointments available
                if (s.equalsIgnoreCase("noAppointments")){
                    noAppointments.setVisibility(View.VISIBLE);
                }

                // Load Available Appointments into list view
                try {
                    loadIntoListView(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Dismiss loading dialog
                pdLoading.dismiss();
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {

                    URL url = new URL(urlWebService);

                    // Get variables from previous activity
                    Intent intent1 = getIntent();
                    String id = intent1.getStringExtra("id");
                    String email = intent1.getStringExtra("email");

                    // LinkedHashMap create and assign parameters
                    Map<String,Object> params = new LinkedHashMap<>();
                    params.put("id", id);
                    params.put("email", email);
                    params.put("task", "showAvailableAppointments");

                    // String builder object create and append parameters
                    StringBuilder post = new StringBuilder();
                    for (Map.Entry<String,Object> param : params.entrySet()) {
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
                    String availableAppointments;
                    while ((availableAppointments = bufferedReader.readLine()) != null) {
                        sb.append(availableAppointments + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        // Get Available Appointments
        GetAvailableAppointments getAvailableAppointments = new GetAvailableAppointments();
        getAvailableAppointments.execute();
    }


    // On Back button press
    @Override
    public void onBackPressed() {

        // If appointment is selected, deselect appointment
        if(selected) {
            frame.setVisibility(View.INVISIBLE);
            book.setVisibility(View.INVISIBLE);
            listView.setSelector(R.color.transparent);
            selected = false;
        } else {
            // Close this Activity, Start previous activity and pass variables
            Intent intent1 = getIntent();
            Intent intent = new Intent(AppointmentsActivity.this, MainActivity.class);
            intent.putExtra("id", intent1.getStringExtra("id"));
            intent.putExtra("email", intent1.getStringExtra("email"));
            startActivity(intent);
            this.finish();
        }
    }


    // Book Appointment
    private class BookAppointment extends AsyncTask<String, String, String[]>
    {
        // Create loading dialog
        ProgressDialog pdLoading = new ProgressDialog(AppointmentsActivity.this);

        // Declarations
        HttpURLConnection con;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Set loading dialog
            pdLoading.setMessage("\tBooking...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String[] doInBackground(String... params) {

            // Assign variables
            String link = getResources().getString(R.string.link);
            String result[] = new String[3];

            try {

                // Book Appointment url
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
                .appendQueryParameter("task", "bookAptmt");
                String Parameters = builder.build().getEncodedQuery();

                // Send
                OutputStream osw = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(osw, "UTF-8"));
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

            // If result is true, Appointment is Booked
            if(result[0].equalsIgnoreCase("true"))
            {
                // Confirmation of Booked Appointment dialog
                new AlertDialog.Builder(AppointmentsActivity.this, R.style.AlertDialogTheme)
                        .setTitle("Confirmation!")
                        .setMessage("Appointment is scheduled.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Close this Activity, Start previous activity and pass variables
                                Intent intent1 = getIntent();
                                Intent intent = new Intent(AppointmentsActivity.this,MainActivity.class);
                                intent.putExtra("id", intent1.getStringExtra("id"));
                                intent.putExtra("email", intent1.getStringExtra("email"));
                                startActivity(intent);
                                AppointmentsActivity.this.finish();
                            }

                        })
                        .show();

            // If result is alredyBooked, show error (appointment is already booked)
            }else if (result[0].equalsIgnoreCase("alreadyBooked")){

                // Warning of already Booked Appointment dialog
                new AlertDialog.Builder(AppointmentsActivity.this, R.style.AlertDialogTheme)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Warning!")
                .setMessage("You have scheduled appointment already.")
                .setPositiveButton("Ok", null )
                .show();

            // If result is false, show error (appointment is not available)
            }else if (result[0].equalsIgnoreCase("false")){

                // Warning of unavailable Appointment dialog
                new AlertDialog.Builder(AppointmentsActivity.this, R.style.AlertDialogTheme)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Warning!")
                .setMessage("This appointment is no longer available")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Refresh current activity
                        startActivity(getIntent());
                        AppointmentsActivity.this.finish();

                        finish();
                        startActivity(getIntent());
                    }
                        })

                        .show();


            // If result is exception or unsuccessful, show error (connection problem)
            } else if (result[0].equalsIgnoreCase("exception") || result[0].equalsIgnoreCase("unsuccessful")) {

                // Show error dialog
                new AlertDialog.Builder(AppointmentsActivity.this, R.style.AlertDialogTheme)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Connection Problem!")
                .setMessage("Please try again later.")
                .setPositiveButton("Ok", null )
                .show();
            }
        }
    }


    ArrayList <Appointment> Appointments= new ArrayList<Appointment>(); ////////////


    // Load Available Appointments into list view
    private void loadIntoListView(String availableAppointments) throws JSONException {
        JSONArray jsonArray = new JSONArray(availableAppointments);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            // Get intent to access variables
            Intent intent = getIntent();

            // Populate appointments
            Appointments.add(new Appointment(intent.getStringExtra("id"), obj.getString("Appointment_ID"), obj.getString("Aptmt_Date"), obj.getString("Aptmt_Time"))); ////////
        }

        arrayAdapter= new AppointmentAdapter(Appointments,getApplicationContext()); /////////
        listView.setAdapter(arrayAdapter);


        // Reference variables
        book = findViewById(R.id.book);
        frame = findViewById(R.id.frame3);


        // Set list view click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            int lastPosition;

            // On appointment click
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Deselect appointment if already selected
                if(position == lastPosition) {
                    if (selected) {
                        listView.setSelector(R.color.transparent);
                        selected = false;
                        lastPosition = position;
                        frame.setVisibility(View.INVISIBLE);
                        book.setVisibility(View.INVISIBLE);

                    // Select another appointment
                    } else {
                        listView.setSelector(R.color.select);
                        selected = true;
                        lastPosition = position;
                        frame.setVisibility(View.VISIBLE);
                        book.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Select appointment
                    listView.setSelector(R.color.select);
                    selected = true;
                    lastPosition = position;
                    frame.setVisibility(View.VISIBLE);
                    book.setVisibility(View.VISIBLE);
                }

                final Appointment appointment = Appointments.get(position);


                // Set Book Appointment button click listener
                book.setOnClickListener(new View.OnClickListener() {

                    // On book click
                    public void onClick(View v) {

                        // Show confirmation request dialog
                        new AlertDialog.Builder(AppointmentsActivity.this, R.style.AlertDialogTheme)
                        .setTitle("Book this appointment ?")
                        .setMessage(appointment.getDate() + "\n" + appointment.getTime())
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            //If Booking confirmed
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            // Book Appointment
                                new BookAppointment().execute(appointment.getAdviseeId(), appointment.getAppointmentId());
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                    }
                });
            }
        });
    }
}
