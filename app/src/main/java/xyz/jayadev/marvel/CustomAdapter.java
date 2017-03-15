package xyz.jayadev.marvel;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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

import io.realm.Realm;
import io.realm.RealmResults;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private ArrayList<DataModel> dataSet;
    ImageView imageView;
    Context context;
    final List<Target> targets = new ArrayList<Target>();

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        ImageView imageViewIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.text);
                this.imageViewIcon = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    public CustomAdapter(ArrayList<DataModel> data, Context c) {
        this.dataSet = data;
        this.context = c;
//        Log.d("Alpha:custom","caled");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);

        view.setOnClickListener(MainActivity.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        TextView textViewName = holder.textViewName;
        imageView = holder.imageViewIcon;
        textViewName.setText(dataSet.get(listPosition).getName());
        Realm realm = Realm.getInstance(context);
        RealmResults<DataModel> result2 = realm.where(DataModel.class)
                .equalTo("id", dataSet.get(listPosition).getId())
                .findAll();
        final Target t = new Target() {

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
//                Log.d("Alpha", "OnBitmapLoaded");
                new SaveData(new SaveData.TaskListener() {
                    @Override
                    public void onFinished(String result) {
//                        Log.d("Alpha", "callback: i" +" "+ result);
                        Realm realm = Realm.getInstance(context);
//
                        realm.beginTransaction();
                        DataModel dt = realm.createObject(DataModel.class);
                        dt.setName(dataSet.get(listPosition).getName());
                        dt.setId(dataSet.get(listPosition).getId());
                        dt.setImage(result);
                        realm.commitTransaction();

                    }
                }, bitmap, dataSet.get(listPosition).getId() + "").execute();
                targets.remove(this);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
//                                Log.d("Alpha", "onP");
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                targets.remove(this);

            }
        };
        targets.add(t);


        if (result2.isEmpty()) {
//            Log.d("Alpha:realmresults","Empty "+result2.size());
            Picasso.with(context)
                    .load(dataSet.get(listPosition).getImage())
                    .into(t);
            Picasso.with(context).load(dataSet.get(listPosition).getImage()).into(imageView);

        } else {
            DataModel u = result2.get(0);
//            Log.d("Alpha:realmresults",u.getId()+" ");
            if (u.getImage() != null) {
                Picasso.with(context).load(new File(u.getImage())).
                        into(imageView);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}
