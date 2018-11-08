package com.hackaproject.foodswap.foodswap;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hackaproject.foodswap.foodswap.datamodels.Event;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class RecyclerAdapter extends RecyclerView.Adapter<EventRowViewHolder> {

    public List<Event> eventsList;
    private HomeActivity context;
    public static final String EVENT_ID = "EVENT_ID";

    public RecyclerAdapter(HomeActivity context, List<Event> eventsList) {
        this.eventsList = eventsList;
        this.context = context;
    }

    @NonNull
    @Override
    public EventRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        //inflates the card view layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_row_layout, parent, false);
        return new EventRowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventRowViewHolder holder, int i) {
        final Event event = eventsList.get(i);
        //adds the company name, role and last updated date to the cardView holder
        holder.foodText.setText("Food to make: " + event.getFood());
        holder.dateText.setText("When to meet: " + event.getDate());

        if(event.getStatus().equals("0")) {
            //pending
            holder.statusIcon.setImageResource(R.drawable.ic_access_time_black_24dp);
        } else if(event.getStatus().equals("1")) {
            //confirm
            holder.statusIcon.setImageResource(R.drawable.ic_compare_arrows_black_24dp);
        } else if(event.getStatus().equals("2")) {
            //done
            holder.statusIcon.setImageResource(R.drawable.ic_done_black_24dp);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(event.getEventid() != null) {
                    Intent intent = new Intent(context, MatchedEventActivity.class);
                    intent.putExtra(EVENT_ID, event.getEventid());
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Event still pending. Finding you a match!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    private String parseDate(String dateUTC) {
        DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = null;
        try {
            date = utcFormat.parse(dateUTC);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat gmtFormat = new SimpleDateFormat("dd/MM/yyyy");
        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        return gmtFormat.format(date);
    }
}
