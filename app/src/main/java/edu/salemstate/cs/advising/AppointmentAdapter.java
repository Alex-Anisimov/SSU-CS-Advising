package edu.salemstate.cs.advising;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;



public class AppointmentAdapter extends ArrayAdapter<Appointment> {


    // Declarations
    private ArrayList<Appointment> Appointments;
    private int lastPosition = -1;


    private static class ViewHolder {

        // Declarations text view
        TextView aptmtDate;
        TextView aptmtTime;
    }


    // Constructor for ArrayAdapter
    public AppointmentAdapter(ArrayList<Appointment> appointments, Context context) {
        super(context, R.layout.row, appointments);
        this.Appointments = appointments;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {

        // Get appointment information for this position
        Appointment appointment = getItem(position);

        // Declare viewHolder
        ViewHolder viewHolder; // view lookup cache stored in tag

        // Inflate view, if no view
        if (view == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.row, parent, false);

            // Reference text view to view Holder
            viewHolder.aptmtDate = (TextView) view.findViewById(R.id.aptmt_date);
            viewHolder.aptmtTime = (TextView) view.findViewById(R.id.aptmt_time);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // Last appointment position update
        lastPosition = position;

        // Set Appointment Date and Time
        viewHolder.aptmtDate.setText(appointment.getDate());
        viewHolder.aptmtTime.setText(appointment.getTime());

        return view;
    }
}

