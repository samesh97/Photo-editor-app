package com.sba.sinhalaphotoeditor.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sba.sinhalaphotoeditor.callbacks.OnAsyncTaskState;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.database.DatabaseHelper;
import com.sba.sinhalaphotoeditor.activities.MainActivity;
import com.sba.sinhalaphotoeditor.aynctask.AddImageToArrayListAsyncTask;
import com.sba.sinhalaphotoeditor.singleton.ImageList;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecentImageAdapter extends RecyclerView.Adapter<RecentImageAdapter.MyViewHolder>
{

    private ArrayList<Bitmap> images = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();
    private Context context;
    private Bitmap selectedImage = null;
    private Dialog dialog;
    int percent;





    public RecentImageAdapter(Context context, ArrayList<Bitmap> images, ArrayList<Integer> ids, ArrayList<String> dates)
    {
        this.images = images;
        this.dates = dates;
        this.context = context;
        DatabaseHelper helper = new DatabaseHelper(context);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        //float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        percent = (int) (dpHeight / 4);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_image_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,final int position)
    {

        holder.itemView.setLayoutParams(new ConstraintLayout.LayoutParams(percent, ViewGroup.LayoutParams.MATCH_PARENT));

        if(images.size() > position)
        {

            Glide.with(context).load(images.get(position)).into(holder.image);
            if(dates != null && dates.size() > position)
            {
                holder.date.setText(dates.get(position));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    selectedImage = images.get(position);
                    showProgressDialog();
                    ImageList.getInstance().clearImageList();
                    AddImageToArrayListAsyncTask task = new AddImageToArrayListAsyncTask(selectedImage, new OnAsyncTaskState() {
                        @Override
                        public void startActivityForResult()
                        {
                            hideProgressDialog();
                            if(context instanceof MainActivity)
                            {
                                ((MainActivity)context).startActivityForRecentEdits();
                            }
                        }
                    });

                    task.execute();
                }
            });
        }
        else
        {
            holder.itemView.setOnClickListener(null);

        }


//        holder.delete.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle("Delete Image");
//                builder.setIcon(R.drawable.delete);
//                builder.setMessage("Are sure you want to delete?");
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which)
//                    {
//                        if(helper.deleteImage(ids.get(position)))
//                        {
//                            ids.remove(position);
//                            images.remove(position);
//                            dates.remove(position);
//
//
//                            Methods.showCustomToast(context,context.getResources().getString(R.string.removed_text));
//                            notifyDataSetChanged();
//                        }
//                        else
//                        {
//                            Methods.showCustomToast(context,context.getString(R.string.something_went_wrong_text));
//                        }
//
//                    }
//                });
//                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//                builder.show();
//            }
//        });

    }

    @Override
    public int getItemCount()
    {
        if(images.isEmpty())
        {
            return 0;
        }
        return Math.max(images.size(), 5);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView image;
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



        }
    }
    public void showProgressDialog()
    {
        if(dialog == null)
        {
            dialog = new Dialog(context,R.style.CustomBottomSheetDialogTheme);
        }

        View view = LayoutInflater.from(context).inflate(R.layout.progress_dialog,null);
        ProgressBar bar = view.findViewById(R.id.progressBar);
        bar.setVisibility(View.VISIBLE);
//        Glide.with(context).asGif().load(R.drawable.loading_gif).into(bar);
        dialog.setContentView(view,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        dialog.show();
    }
    public void hideProgressDialog()
    {
        if(dialog != null)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.progress_dialog,null);
            ProgressBar bar = view.findViewById(R.id.progressBar);
            bar.setVisibility(View.GONE);
            dialog.setContentView(view,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            dialog.hide();
        }
    }

}
