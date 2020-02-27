package com.sba.sinhalaphotoeditor.activities;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rilixtech.Country;
import com.rilixtech.CountryCodePicker;
import com.sba.sinhalaphotoeditor.R;

import java.io.IOException;

public class RegisterScreen extends AppCompatActivity {

    EditText fullName,phone;
    Button next;
    //TextView loginLink;
    private Uri filePath;
    CircleImageView profilePicture;
    private static final int PICK_IMAGE_REQUEST = 234;
    private CountryCodePicker ccp;
    private String Countrycode;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profilePicture.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);


        ccp = (CountryCodePicker) findViewById(R.id.ccp);

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



        //confirmPassword = (EditText) findViewById(R.id.confirmPassword);

        fullName = (EditText) findViewById(R.id.fullName);
        profilePicture = (CircleImageView) findViewById(R.id.profilePicture);

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });


        //password = (EditText) findViewById(R.id.password);
        phone = (EditText) findViewById(R.id.phoneNumber);
        next = (Button) findViewById(R.id.loginButton);
      //  loginLink = (TextView) findViewById(R.id.loginLink);

//        loginLink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent =new Intent(RegisterScreen.this,LoginScreen.class);
//                startActivity(intent);
//            }
//        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(!fullName.getText().toString().equals("") && filePath != null && !phone.getText().toString().equals(""))
                {
                    Intent intent = new Intent(RegisterScreen.this,ConfirmPhoneNumber.class);
                    intent.putExtra("name",fullName.getText().toString());
                    intent.putExtra("phone",phone.getText().toString());
                  //  intent.putExtra("password",password.getText().toString());
                    intent.putExtra("uri",filePath.toString());
                    intent.putExtra("CountryCode",Countrycode);
                    startActivity(intent);

                }
                else if(filePath == null && (!fullName.getText().toString().equals("")  && !phone.getText().toString().equals("")))
                {
                    Toast.makeText(RegisterScreen.this, "Please Select an Image for Your Profile Picture!", Toast.LENGTH_SHORT).show();
                }
                else if(fullName.getText().toString().equals("") || phone.getText().toString().equals(""))
                {
                    Toast.makeText(RegisterScreen.this, "Please Fill out the Required Fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void showFileChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    public String getFileExtension(Uri uri)
    {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
