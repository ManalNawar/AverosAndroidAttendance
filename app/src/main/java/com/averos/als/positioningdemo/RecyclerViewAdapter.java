package com.averos.als.positioningdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<AttendanceList> attendancelist;

    public RecyclerViewAdapter(Context context , List attendancelist) {
        this.context = context;
        this.attendancelist = attendancelist;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_content,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {

        AttendanceList list = attendancelist.get(position);
        holder.beacon.setText("Calss: "+list.getBeacon());
        holder.time.setText("Time: "+list.getCreated_at());
    }

    @Override
    public int getItemCount() {
        return attendancelist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{ //implements View.OnClickListener

        private  TextView beacon;
        private  TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
           // itemView.setOnClickListener(this);
           // block_title = itemView.findViewById(R.id.block_title);
            beacon = itemView.findViewById(R.id.beacon);
            time = itemView.findViewById(R.id.time);
        }

//        @Override
//        public void onClick(View v) {
//            int position = getAdapterPosition();
//            Listitem item = listitems.get(position);
//
//            Intent intent = new Intent(context, InfoActivity.class);
//            intent.putExtra("name",item.getName());
//            intent.putExtra("description",item.getDescription());
//            intent.putExtra("age",item.getAge());
//            context.startActivity(intent);
//
//
//
//
//            //  Toast.makeText(context,item.getName(),Toast.LENGTH_SHORT).show();
//        }
    }

    private String dateformat(String datestring) throws ParseException {
        try{
        SimpleDateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpledate.parse(datestring);
        SimpleDateFormat simpledatee = new SimpleDateFormat("MMM DD");

        return simpledatee.format(date);
    }catch(ParseException e){
            e.getMessage();

    }
        return "";
    }
}
