package com.sba.sinhalaphotoeditor.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sba.sinhalaphotoeditor.MostUsedMethods.Methods;
import com.sba.sinhalaphotoeditor.activities.EditorActivity;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.SQLiteDatabase.DatabaseHelper;
import com.sba.sinhalaphotoeditor.activities.MainActivity;
import com.sba.sinhalaphotoeditor.singleton.ImageList;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class PreviuoslyEditedImageAdapter extends RecyclerView.Adapter<PreviuoslyEditedImageAdapter.MyViewHolder>
{

    private ArrayList<Bitmap> images = new ArrayList<>();
    private ArrayList<Integer> ids = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();

    private Context context;

    private DatabaseHelper helper;

    private Bitmap selectedImage = null;

    private ProgressDialog pdLoading;





    public PreviuoslyEditedImageAdapter(Context context, ArrayList<Bitmap> images, ArrayList<Integer> ids, ArrayList<String> dates)
    {
        this.images = images;
        this.ids = ids;
        this.dates = dates;
        this.context = context;
        helper = new DatabaseHelper(context);

        pdLoading = new ProgressDialog(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,final int position)
    {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        int percent = (int) (dpHeight / 4);
        holder.image.setLayoutParams(new ConstraintLayout.LayoutParams(percent, ViewGroup.LayoutParams.MATCH_PARENT));
        Glide.with(context).load(images.get(position)).into(holder.image);
        if(dates != null && dates.size() > position)
        {
            holder.date.setText(dates.get(position));
        }

        Log.d("adapterSize","" + images.size());

        holder.delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete Image");
                builder.setIcon(R.drawable.delete);
                builder.setMessage("Are sure you want to delete?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(helper.deleteImage(ids.get(position)))
                        {
                            ids.remove(position);
                            images.remove(position);
                            dates.remove(position);


                            Methods.showCustomToast(context,context.getResources().getString(R.string.removed_text));
                            notifyDataSetChanged();
                        }
                        else
                        {
                            Methods.showCustomToast(context,context.getString(R.string.something_went_wrong_text));
                        }

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });

    }

    @Override
    public int getItemCount()
    {
        return images.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView image;
        TextView date;
        ImageView delete;

        public MyViewHolder(@NonNull View view)
        {
            super(view);

            image = view.findViewById(R.id.image);
            date = view.findViewById(R.id.date);
            delete = view.findViewById(R.id.delete);



            Animation top = AnimationUtils.loadAnimation(context, R.anim.bottomtotop);
            top.setDuration(500);
            view.setAnimation(top);
            top.start();


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    selectedImage = images.get(getAdapterPosition());
                    new AsyncCaller().execute();
                }
            });



        }
    }
    public class AsyncCaller extends AsyncTask<Void, Void, Void>
    {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }
        @Override
        protected Void doInBackground(Void... params)
        {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here

            if(!isCancelled())
            {
                if(selectedImage != null)
                {
                    ImageList.getInstance().clearImageList();

                    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                    Display display = wm.getDefaultDisplay();
                    DisplayMetrics metrics = new DisplayMetrics();
                    display.getMetrics(metrics);


                    ImageList.getInstance().addBitmap(selectedImage,false);

                   if(context instanceof MainActivity)
                   {
                       ((MainActivity)context).startActivityForRecentEdits();
                   }
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            pdLoading.dismiss();
        }

    }

}
