package com.hackaproject.foodswap.foodswap;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

class EventRowViewHolder extends RecyclerView.ViewHolder  {
    public TextView foodText;
    public TextView dateText;
    public ImageView statusIcon;

    public EventRowViewHolder(View itemView) {
        super(itemView);
        this.foodText = itemView.findViewById(R.id.foodCardView);
        this.dateText = itemView.findViewById(R.id.dateCardView);
        this.statusIcon = itemView.findViewById(R.id.statusIconCardView);
    }
}
