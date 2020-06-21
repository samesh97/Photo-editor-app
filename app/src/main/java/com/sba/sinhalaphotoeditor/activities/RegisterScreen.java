package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rilixtech.Country;
import com.rilixtech.CountryCodePicker;
import com.sba.sinhalaphotoeditor.Config.Constants;
import com.sba.sinhalaphotoeditor.MostUsedMethods.Methods;
import com.sba.sinhalaphotoeditor.R;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.sba.sinhalaphotoeditor.Config.Constants.LANGUAGE_ENGLISH;
import static com.sba.sinhalaphotoeditor.Config.Constants.LANGUAGE_KEY;
import static com.sba.sinhalaphotoeditor.Config.Constants.LANGUAGE_SINHALA;
import static com.sba.sinhalaphotoeditor.Config.Constants.SHARED_PREF_NAME;

public class RegisterScreen extends AppCompatActivity{

    private EditText fullName,phone;
    private Button next;
    private Uri filePath;
    private CircleImageView profilePicture;
    private static final int PICK_IMAGE_REQUEST = 234;
    private CountryCodePicker ccp;
    private String Countrycode;
    private String mVerificationId;
    private boolean isAlreadyVerified = false;
    private ProgressBar progress_bar;
    private TextView skipButton;
    private Methods methods;
    private long startedTime;





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST)
        {
            if(MyCustomGallery.selectedBitmap != null)
            {
                Bitmap bitmap =  Methods.getResizedProfilePic(getApplicationContext(),MyCustomGallery.selectedBitmap,200);
                profilePicture.setImageBitmap(bitmap);
                profilePicture.setBorderWidth(4);
                profilePicture.setBorderColor(Color.WHITE);


                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                File file = null;
                String path = "userProfilePicResized" + ".PNG";
                file = new File(directory, path);
                filePath = Uri.fromFile(file);

            }
            else
            {
                Methods.showCustomToast(RegisterScreen.this,"No Image Selected");
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);



        methods = new Methods(getApplicationContext());
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        progress_bar = findViewById(R.id.progress_bar);
        skipButton = findViewById(R.id.skip_button);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipRegistraion();
            }
        });

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry)
            {
                Countrycode = "+" + selectedCountry.getPhoneCode();

            }
        });
        if(Countrycode == null)
        {
            Countrycode = "+94";
        }



        fullName = (EditText) findViewById(R.id.fullName);
        profilePicture = (CircleImageView) findViewById(R.id.profilePicture);

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                openCustomGallery();
            }
        });



        phone = (EditText) findViewById(R.id.phoneNumber);
        next = (Button) findViewById(R.id.loginButton);

        changeTypeFace();

    }

    private void openCustomGallery()
    {
        Intent intent = new Intent(RegisterScreen.this,MyCustomGallery.class);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    public String getFileExtension(Uri uri)
    {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private void sendVerificationCode(String no)
    {
        startedTime = System.currentTimeMillis();
        startCounting();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                Countrycode + Integer.parseInt(no),
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,mCallbacks);

    }

    private void startCounting()
    {
        final Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long nowTime = System.currentTimeMillis();
                int seconds = (int) ((nowTime - startedTime) / 1000);
                if(seconds >= 12)
                {
                    mCallbacks.onVerificationFailed(null);
                }
            }
        }, 0, 1000);

    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
        {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null)
            {
                // codd.setText(code);
                //verifying the code
                //verifyVerificationCode(code);
                if(!isAlreadyVerified)
                {
                    progress_bar.setVisibility(View.GONE);
                    startConfirmActivity(code,mVerificationId);
                    isAlreadyVerified = true;
                }

            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e)
        {
            if(e != null)
            {
                Toast.makeText(RegisterScreen.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    next.setText("Retry");
                    next.setVisibility(View.VISIBLE);
                    progress_bar.setVisibility(View.GONE);
                }
            });

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken)
        {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
            // Toast.makeText(ConfirmPhoneNumber.this,s, Toast.LENGTH_LONG).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    if(!isAlreadyVerified)
                    {

                        progress_bar.setVisibility(View.GONE);
                        isAlreadyVerified = true;
                        startConfirmActivity(null,mVerificationId);
                    }

                }
            },5000);

        }
    };
    public void startConfirmActivity(String code,String mVerificationId)
    {
        Intent intent = new Intent(RegisterScreen.this,ConfirmPhoneNumber.class);
        intent.putExtra("name",fullName.getText().toString());
        intent.putExtra("phone",phone.getText().toString());
        intent.putExtra("uri",filePath.toString());
        intent.putExtra("CountryCode",Countrycode);
        intent.putExtra("mVerificationId",mVerificationId);
        intent.putExtra("code",code);

        startActivity(intent);

        next.setText("Next");
        next.setVisibility(View.VISIBLE);

    }
    public void skipRegistraion()
    {
        Constants.isRegistrationSkipped = true;
        Intent intent = new Intent(RegisterScreen.this,MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.activity_start_animation,R.anim.activity_exit_animation);
    }
    public void onNextClicked(View view)
    {
        if(!fullName.getText().toString().equals("") && filePath != null && !phone.getText().toString().equals(""))
        {

            next.setVisibility(View.GONE);
            progress_bar.setVisibility(View.VISIBLE);
            sendVerificationCode(phone.getText().toString());

        }
        else if(filePath == null && (!fullName.getText().toString().equals("")  && !phone.getText().toString().equals("")))
        {
            Methods.showCustomToast(RegisterScreen.this,"Please Select an Image for Your Profile Picture!");
        }
        else if(fullName.getText().toString().equals("") || phone.getText().toString().equals(""))
        {
            Methods.showCustomToast(RegisterScreen.this, "Please Fill out the Required Fields!");
        }
    }
    private void changeTypeFace()
    {
        Typeface typeface = Methods.getDefaultTypeFace(getApplicationContext());

        fullName.setTypeface(typeface);
        phone.setTypeface(typeface);
        next.setTypeface(typeface);
        skipButton.setTypeface(typeface);


    }
}

