package com.sba.sinhalaphotoeditor;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;


    private static final int PICK_IMAGE_REQUEST = 234;
    static Bitmap bitmap;
    static Uri CurrentWorkingFilePath;
    static int imagePosition = 0;
    TextView selectPictureText,fromGalery,createOne;

    boolean isReadGranted = false;
    boolean isWriteGranted = false;

    boolean isExit = false;

    ImageView createBitmap;


    static ArrayList<Bitmap> images = new ArrayList<>();
    static ArrayList<Uri> filePaths = new ArrayList<>();

    ProgressDialog dialog;

    ImageView usePreviousBitmap;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            dialog.show();
            CurrentWorkingFilePath = data.getData();
            try
            {

                images.clear();
                filePaths.clear();
                imagePosition = 0;



               bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), CurrentWorkingFilePath);

                //bitmap = GlideBitmapFactory.decodeFile(getRealPathFromDocumentUri(MainActivity.this,CurrentWorkingFilePath));





                Uri selectedPicture = data.getData();
                // Get and resize profile image
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = MainActivity.this.getContentResolver().query(selectedPicture, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                ExifInterface exif = new ExifInterface(picturePath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                bitmap = rotateBitmap(bitmap, orientation);



                /*

                bitmap = getResizedBitmap(bitmap,1500);*/

                WindowManager wm = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                DisplayMetrics metrics = new DisplayMetrics();
                display.getMetrics(metrics);

               // bitmap = scale(bitmap,metrics.widthPixels,metrics.heightPixels);

                //bitmap = getResizedBitmap(bitmap,1500);*/
                bitmap = decodeSampledBitmapFromResource(getRealPathFromDocumentUri(MainActivity.this,getImageUri(MainActivity.this,bitmap)),metrics.widthPixels,metrics.heightPixels);
                CurrentWorkingFilePath = getImageUri(MainActivity.this,bitmap);









                images.add(bitmap);
                filePaths.add(CurrentWorkingFilePath);
                startActivity(new Intent(getApplicationContext(),EditorActivity.class));
                dialog.dismiss();

            }
            catch (Exception e)
            {

            }

            {

                try {

                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), CurrentWorkingFilePath);
                   // bitmap = GlideBitmapFactory.decodeFile(getRealPathFromDocumentUri(MainActivity.this,CurrentWorkingFilePath));



                    WindowManager wm = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
                    Display display = wm.getDefaultDisplay();
                    DisplayMetrics metrics = new DisplayMetrics();
                    display.getMetrics(metrics);

                    //bitmap = scale(bitmap,metrics.widthPixels,metrics.heightPixels);
//
                    //bitmap = getResizedBitmap(bitmap,1500);

                    bitmap = decodeSampledBitmapFromResource(getRealPathFromDocumentUri(MainActivity.this,data.getData()),metrics.widthPixels,metrics.heightPixels);

                    images.add(bitmap);
                    CurrentWorkingFilePath = data.getData();
                    filePaths.add(CurrentWorkingFilePath);
                    startActivity(new Intent(getApplicationContext(),EditorActivity.class));
                    dialog.dismiss();
                }
                catch (Exception es)
                {
                    Toast.makeText(this, "" + es.getMessage(), Toast.LENGTH_SHORT).show();
                    es.printStackTrace();
                }


            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Runtime rt = Runtime.getRuntime();
        int maxMemory = (int)rt.freeMemory();
        //GlideBitmapPool.initialize(maxMemory); // 10mb max memory size
        Toast.makeText(this, "" + maxMemory, Toast.LENGTH_LONG).show();



        imageView = (ImageView) findViewById(R.id.imageView);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        imageView.startAnimation(pulse);




        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Loading");



        createBitmap = (ImageView) findViewById(R.id.createBitmap);


        createBitmap.startAnimation(pulse);
        createOne = (TextView) findViewById(R.id.createOne);
        fromGalery = (TextView) findViewById(R.id.fromGalery);

        createBitmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {

                    if(!isPermissonGranted())
                    {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                    if(!isPermissonGranted2())
                    {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    }
                    if(isPermissonGranted() && isPermissonGranted2())
                    {
                        startActivity(new Intent(getApplicationContext(),CreateABackgroundActivity.class));
                    }
                }
                else
                {
                    startActivity(new Intent(getApplicationContext(),CreateABackgroundActivity.class));
                }


            }
        });

        selectPictureText = findViewById(R.id.selectPictureText);

        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.kaputaunicode);
        selectPictureText.setTypeface(typeface);
        createOne.setTypeface(typeface);
        fromGalery.setTypeface(typeface);

        usePreviousBitmap = (ImageView) findViewById(R.id.usePreviousBitmap);
        usePreviousBitmap.startAnimation(pulse);

        usePreviousBitmap.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {

                    if(!isPermissonGranted())
                    {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                    if(!isPermissonGranted2())
                    {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    }
                    if(isPermissonGranted() && isPermissonGranted2())
                    {
                        startActivity(new Intent(MainActivity.this,UsePreviouslyEditedImageActivity.class));
                    }
                }
                else
                {
                    startActivity(new Intent(MainActivity.this,UsePreviouslyEditedImageActivity.class));
                }

            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    isReadGranted = true;
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                }
                else
                {
                    isReadGranted = false;
                }
                break;

            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    isWriteGranted = true;

                }
                else
                {
                    isWriteGranted = false;
                }
                break;
        }
    }

    public void showFileChooser(View view)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {

            if(!isPermissonGranted())
            {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            if(!isPermissonGranted2())
            {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            }
            if(isPermissonGranted2() && isPermissonGranted())
            {
                showFileChooser();
            }

        }
        else
        {
            showFileChooser();
        }


    }
    private void showFileChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    public boolean isPermissonGranted()
    {
        if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        return false;

    }
    public boolean isPermissonGranted2()
    {

        if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        return false;

    }
    public boolean isUserWantToExitTheApp()
    {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.setTitle("Exit");
        dialog.setIcon(R.drawable.ic_delete);
        dialog.setMessage("Are you sure you want to exit?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                isExit = true;
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isExit = false;
            }
        });

        dialog.show();

        return isExit;
    }
    private Bitmap getDownsampledBitmap(Context ctx, Uri uri, int targetWidth, int targetHeight) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options outDimens = getBitmapDimensions(uri);

            int sampleSize = calculateSampleSize(outDimens.outWidth, outDimens.outHeight, targetWidth, targetHeight);

            bitmap = downsampleBitmap(uri, sampleSize);

        } catch (Exception e) {
            //handle the exception(s)
        }

        return bitmap;
    }

    private BitmapFactory.Options getBitmapDimensions(Uri uri) throws FileNotFoundException, IOException {
        BitmapFactory.Options outDimens = new BitmapFactory.Options();
        outDimens.inJustDecodeBounds = true; // the decoder will return null (no bitmap)

        InputStream is= getContentResolver().openInputStream(uri);
        // if Options requested only the size will be returned
        BitmapFactory.decodeStream(is, null, outDimens);
        is.close();

        return outDimens;
    }

    private int calculateSampleSize(int width, int height, int targetWidth, int targetHeight) {
        int inSampleSize = 1;

        if (height > targetHeight || width > targetWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) targetHeight);
            final int widthRatio = Math.round((float) width / (float) targetWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    private Bitmap downsampleBitmap(Uri uri, int sampleSize) throws FileNotFoundException, IOException {
        Bitmap resizedBitmap;
        BitmapFactory.Options outBitmap = new BitmapFactory.Options();
        outBitmap.inJustDecodeBounds = false; // the decoder will return a bitmap
        outBitmap.inSampleSize = sampleSize;

        InputStream is = getContentResolver().openInputStream(uri);
        resizedBitmap = BitmapFactory.decodeStream(is, null, outBitmap);
        is.close();

        return resizedBitmap;
    }
    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0;
        float pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
        ///return bitmap;
    }
    public Uri getImageUri(Context inContext, Bitmap inImage)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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
    public static void deleteUndoRedoImages()
    {


        if(images.size() > 6)
        {
                int size = images.size() - 6;
                 MainActivity.imagePosition = MainActivity.imagePosition - size;
                for(int i = 1; i <= size; i++)
                {
                    if(i == imagePosition)
                    {
                        size++;
                    }
                    else
                    {

                        images.remove(i);
                        //GlideBitmapPool.clearMemory();
                    }
                }


        }
    }
    private Bitmap scale(Bitmap bitmap, int maxWidth, int maxHeight) {
        // Determine the constrained dimension, which determines both dimensions.
        int width;
        int height;
        float widthRatio = (float)bitmap.getWidth() / maxWidth;
        float heightRatio = (float)bitmap.getHeight() / maxHeight;
        // Width constrained.
        if (widthRatio >= heightRatio) {
            width = maxWidth;
            height = (int)(((float)width / bitmap.getWidth()) * bitmap.getHeight());
        }
        // Height constrained.
        else {
            height = maxHeight;
            width = (int)(((float)height / bitmap.getHeight()) * bitmap.getWidth());
        }
        Bitmap scaledBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        float ratioX = (float)width / bitmap.getWidth();
        float ratioY = (float)height / bitmap.getHeight();
        float middleX = width / 2.0f;
        float middleY = height / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
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


}







