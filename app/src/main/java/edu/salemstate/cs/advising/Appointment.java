package edu.salemstate.cs.advising;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class Appointment {

    // Variables declaration
    String AdviseeId, AppointmentId, date, time, time1, datename, dow;
    Date temp, temp1;


    // Constructor
    public Appointment(String AdviseeId, String AppointmentId, String date, String time) {
        this.AdviseeId=AdviseeId;
        this.AppointmentId=AppointmentId;
        this.date=date;
        this.time=time;
    }


    // Get Advisee ID
    public String getAdviseeId() {
        return AdviseeId;
    }


    // Get Appointment ID
    public String getAppointmentId() {
        return AppointmentId;
    }


    // Get Appointment Date
    public String getDate() {

        // Change Date format
        SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat day = new SimpleDateFormat("EEEE, MMMM d");
        try {
            temp = in.parse(date);
            datename = day.format(temp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return datename;
    }


    // Get Appointment Time
    public String getTime(){

        // Assign variables (separate time variable in two variables)
        String timeA[] = time.split("-");
        String startTime = timeA[0];
        String endTime = timeA[1];

        // Change Time format
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

        time1 = (startTime+ " - " +endTime);
        time1 = time1.replace("AM", "am").replace("PM","pm");

        return time1;
    }

}