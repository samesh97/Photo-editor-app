package com.sba.sinhalaphotoeditor.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sba.sinhalaphotoeditor.EditorActivity;
import com.sba.sinhalaphotoeditor.MainActivity;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.SQLiteDatabase.DatabaseHelper;
import com.sba.sinhalaphotoeditor.UsePreviouslyEditedImageActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>
{

    private ArrayList<Bitmap> images = new ArrayList<>();
    private ArrayList<Integer> ids = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();

    private Context context;

    private DatabaseHelper helper;

    private Bitmap selectedImage = null;

    private ProgressDialog pdLoading;





    public RecyclerViewAdapter(Context context, ArrayList<Bitmap> images, ArrayList<Integer> ids, ArrayList<String> dates)
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
        holder.image.setImageBitmap(images.get(position));
        holder.date.setText(dates.get(position));





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
                            Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        }
                        else
                        {
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
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
                    MainActivity.images.clear();
                    MainActivity.filePaths.clear();
                    MainActivity.imagePosition = 0;

                    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                    Display display = wm.getDefaultDisplay();
                    DisplayMetrics metrics = new DisplayMetrics();
                    display.getMetrics(metrics);



                    //selectedImage = getResizedBitmap(selectedImage,1500);

                    //selectedImage = decodeSampledBitmapFromResource(getRealPathFromDocumentUri(UsePreviouslyEditedImageActivity.this,getImageUri(UsePreviouslyEditedImageActivity.this,selectedImage)),metrics.widthPixels,metrics.heightPixels);
                    MainActivity.filePaths.add(getImageUri(context,selectedImage));




                    MainActivity.images.add(selectedImage);
                    MainActivity.filePaths.add(getImageUri(context,selectedImage));
                    MainActivity.CurrentWorkingFilePath = getImageUri(context,selectedImage);

                    context.startActivity(new Intent(context,EditorActivity.class));
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
    public Bitmap getResizedBitmap(Bitmap image, int maxSize)
    {

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    static Bitmap decodeSampledBitmapFromResource(String path,int reqWidth,
                                                  int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path,options);
    }
    //Given the bitmap size and View size calculate a subsampling size (powers of 2)
    static int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int inSampleSize = 1;   //Default subsampling size
        // See if image raw height and width is bigger than that of required view
        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            //bigger
            final int halfHeight = options.outHeight / 2;
            final int halfWidth = options.outWidth / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
    public Uri getImageUri(Context inContext, Bitmap inImage)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);

/*
        String realPath = getRealPathFromDocumentUri(getApplicationContext(),Uri.parse(path));
        Log.d("image",realPath);

        File file = new File(realPath);
        if(file.exists())
        {
            file.delete();
            Log.d("image","deleted");
        }*/

        return Uri.parse(path);
    }
    public static String getRealPathFromDocumentUri(Context context, Uri uri){
        String filePath = "";

        Pattern p = Pattern.compile("(\\d+)$");
        Matcher m = p.matcher(uri.toString());
        if (!m.find()) {
            //Log.e(ImageConverter.class.getSimpleName(), "ID for requested image not found: " + uri.toString());
            return filePath;
        }
        String imgId = m.group();

        String[] column = { MediaStore.Images.Media.DATA };
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ imgId }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();

        return filePath;
    }
}
