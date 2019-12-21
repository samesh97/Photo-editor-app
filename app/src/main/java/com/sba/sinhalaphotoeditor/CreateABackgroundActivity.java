package com.sba.sinhalaphotoeditor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sba.sinhalaphotoeditor.MainActivity.decodeSampledBitmapFromResource;
import static com.sba.sinhalaphotoeditor.UsePreviouslyEditedImageActivity.getRealPathFromDocumentUri;

public class CreateABackgroundActivity extends AppCompatActivity {

    ImageView userCreatedImage;
    Button create;
    EditText width,height;
    int color = -99;
    Bitmap createdBitmap = null;
    ImageView pickColor;
    Button useImage;
    Boolean isImageCreating = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_abackground);

        WindowManager wm = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int widtha = metrics.widthPixels;



        create = (Button) findViewById(R.id.create);
        width = (EditText) findViewById(R.id.width);
        height = (EditText) findViewById(R.id.height);
        userCreatedImage = (ImageView) findViewById(R.id.userCreatedImage);
        pickColor = (ImageView) findViewById(R.id.pickColor);
        useImage = (Button) findViewById(R.id.useImage);

        userCreatedImage.setMaxWidth(widtha);

        useImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                useImage.setEnabled(false);
                if(createdBitmap != null)
                {

                    EditorActivity.imageWidth = Integer.parseInt(width.getText().toString());
                    EditorActivity.imageHeight = Integer.parseInt(height.getText().toString());

                    MainActivity.images.clear();
                    MainActivity.filePaths.clear();
                    MainActivity.imagePosition = 0;
                    MainActivity.images.add(createdBitmap);
                    MainActivity.filePaths.add(getImageUri(getApplicationContext(),createdBitmap));
                    MainActivity.CurrentWorkingFilePath = getImageUri(getApplicationContext(),createdBitmap);

                    startActivity(new Intent(getApplicationContext(),EditorActivity.class));
                    finish();
                }
            }
        });

        pickColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                chooseColor();
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(isImageCreating)
                {
                    return;
                }
                if(width.getText().toString().equals(""))
                {
                    Toast.makeText(CreateABackgroundActivity.this, "Enter a valid width", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(height.getText().toString().equals(""))
                {
                    Toast.makeText(CreateABackgroundActivity.this, "Enter a valid height", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(color == -99)
                {
                    Toast.makeText(CreateABackgroundActivity.this, "Pick a color", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Integer.parseInt(width.getText().toString()) > 1000)
                {
                    Toast.makeText(CreateABackgroundActivity.this, "You can only create of maximum width of 1000", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Integer.parseInt(height.getText().toString()) > 1000)
                {
                    Toast.makeText(CreateABackgroundActivity.this, "You can only create of maximum height of 1000", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Integer.parseInt(width.getText().toString()) < 400)
                {
                    Toast.makeText(CreateABackgroundActivity.this, "You can only create of minimum width of 400", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Integer.parseInt(height.getText().toString()) < 400)
                {
                    Toast.makeText(CreateABackgroundActivity.this, "You can only create of minimum height of 400", Toast.LENGTH_SHORT).show();
                    return;
                }


                String hex = "#" + Integer.toHexString(color);
                createdBitmap = createImage(Integer.parseInt(width.getText().toString()),Integer.parseInt(height.getText().toString()),hex);
                userCreatedImage.setImageBitmap(createdBitmap);


            }
        });


        width.setText("1000");
        height.setText("1000");
        color = Color.parseColor("#2dcb70");
        createdBitmap = createImage(1000,1000,"#2dcb70");
        userCreatedImage.setImageBitmap(createdBitmap);


    }
    public  Bitmap createImage(int width, int height, String color)
    {
        isImageCreating = true;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor(color));
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint);

        isImageCreating = false;
        return bitmap;
    }
    public void chooseColor()
    {
        ColorPickerDialogBuilder.with(CreateABackgroundActivity.this)
                .setTitle("Choose Color")
                .initialColor(Color.parseColor("#2dcb70"))
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(20)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int i)
                    {
                       color = i;
                    }
                })
                .setPositiveButton("Ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, Integer[] integers)
                    {
                        color = i;
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                    }
                }).build().show();
    }
    public Uri getImageUri(Context inContext, Bitmap inImage)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);


        String realPath = getRealPathFromDocumentUri(getApplicationContext(),Uri.parse(path));
        Log.d("image",realPath);

        File file = new File(realPath);
        if(file.exists())
        {
            file.delete();
            Log.d("image","deleted");
        }

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
