package com.example.task101;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OrdersRecyclerViewAdapter extends RecyclerView.Adapter<OrdersRecyclerViewAdapter.ViewHolder> {

    //declare variables
    private ArrayList<Order> orders;
    private Context context;
    // click listeners variables
    private ItemClickListener itemClickListener;
    private ItemClickListener shareClickListener;

    // constructor
    public OrdersRecyclerViewAdapter(ArrayList<Order> orders, Context context) {
        this.orders = orders;
        this.context = context;
    }

    // create layout for each view holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_layout, parent, false);
        return new ViewHolder(view);
    }

    // modify the UI elements in each of the view holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.image.setImageBitmap(Util.getBitmapFromBytesArray(orders.get(position).getGoodImageByte()));
        holder.receiverName.setText(orders.get(position).getReceiverName());
        holder.date.setText(orders.get(position).getDate());
        holder.time.setText(orders.get(position).getTime());
        holder.location.setText(orders.get(position).getLocation());
    }

    // return the number of data
    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void shareClickListener(ItemClickListener shareClickListener) {
        this.shareClickListener = shareClickListener;
    }

    // view holder or container for each item in the recycler view
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        // view variables
        TextView receiverName, date, time, location;
        ImageView image, shareImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // obtain views
            receiverName = itemView.findViewById(R.id.truckNameTextView);
            date = itemView.findViewById(R.id.dateTextView);
            time = itemView.findViewById(R.id.orderTimeTextView);
            location = itemView.findViewById(R.id.locationTextView);
            image = itemView.findViewById(R.id.truckImageView);
            shareImage = itemView.findViewById(R.id.shareButton);

            // set on click listener for the itemView and the share button
            itemView.setOnClickListener(this);
            shareImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            // listener for the entire Order item
            if (view == itemView) {
                itemClickListener.onClick(view, getAdapterPosition()); //Defined in HomeActivity and MyOrdersActivity
            }
            // listener for the share button
            if (view == shareImage) {
                shareClickListener.onShareClick(view, getAdapterPosition()); //Defined in HomeActivity and MyOrdersActivity
            }
        }
    }
}
