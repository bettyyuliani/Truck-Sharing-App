package com.example.task82;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task82.R;

import java.util.List;

public class TrucksRecyclerViewAdapter extends RecyclerView.Adapter<TrucksRecyclerViewAdapter.ViewHolder> {

    // variables
    private List<Truck> trucks;
    private Context context;

    // constructor
    public TrucksRecyclerViewAdapter(List<Truck> trucks, Context context) {
        this.trucks = trucks;
        this.context = context;
    }

    // create layout for each view holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.truck_layout, parent, false);
        return new ViewHolder(view);
    }

    // modify the UI elements in each of the view holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.image.setImageResource(trucks.get(position).getImage());
        holder.name.setText(trucks.get(position).getName());
        holder.description.setText(trucks.get(position).getDescription());
    }

    // get number of items for the adapter
    @Override
    public int getItemCount() {
        return trucks.size();
    }

    // view holder or container for each item in the recycler view
    public class ViewHolder extends RecyclerView.ViewHolder{

        // view variables
        TextView name, description;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // obtain view variables
            image = itemView.findViewById(R.id.truckImageView);
            name = itemView.findViewById(R.id.truckNameTextView);
            description = itemView.findViewById(R.id.truckDescTextView);

        }
    }
}
