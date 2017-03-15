package xyz.jayadev.marvel;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class CustomAdapterCache extends RecyclerView.Adapter<CustomAdapterCache.MyViewHolder> {

    private ArrayList<DataModel> dataSet;
    final List<Target> targets = new ArrayList<Target>();
    ImageView imageView;
    Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        ImageView imageViewIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.text);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    public CustomAdapterCache(ArrayList<DataModel> data, Context c) {
        this.dataSet = data;
        this.context = c;
//        Log.d("Alpha:custom","caled");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
//        Log.d("Alpha","onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);
        view.setOnClickListener(MainActivity.myOnClickListener);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
//        Log.d("Alpha","onBindViewHolder"+ Environment.getExternalStorageDirectory().getAbsolutePath());
        TextView textViewName = holder.textViewName;
        imageView = holder.imageViewIcon;
        textViewName.setText(dataSet.get(listPosition).getName());
//        Log.d("Alpha",dataSet.get(listPosition).getName() +":"+dataSet.get(listPosition).getImage());
        if (dataSet.get(listPosition).getImage() != null) {
            Picasso.with(context).load(new File(dataSet.get(listPosition).getImage())).
                    into(imageView);
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
