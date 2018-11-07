package com.hackaproject.foodswap.foodswap;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hackaproject.foodswap.foodswap.datamodels.Event;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<EventRowViewHolder> {

    public List<Event> eventsList;
    private HomeActivity context;

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

//        StatusIconTint.setTint(context, holder.applicationStatusIcon, stage);

        //go to Application Information when item in Applications List is clicked
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo create and go to new page for viewing bookings
//                //else a single click will take you to the application information page
//                Intent intent = new Intent(context, ApplicationInformationActivity.class);
//                intent.putExtra(ApplicationTable.COLUMN_ID, event.getApplicationID());
//                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }
}
