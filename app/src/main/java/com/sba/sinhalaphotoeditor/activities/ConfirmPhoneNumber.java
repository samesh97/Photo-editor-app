package com.sba.sinhalaphotoeditor.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.firebase.SihalaUser;

import java.util.concurrent.TimeUnit;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.concurrent.TimeUnit;

public class ConfirmPhoneNumber extends AppCompatActivity {

    private Button varify;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private String name;
    private String phone;
   // private String password;
    private String uri;
    private Uri filePath;
    private StorageReference storageReference;
    private String code = "null";
    private Pinview pin;
    private ProgressDialog pd;
    private TextView textView;
    private String CountryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_phone_number);


        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
      //  password = intent.getStringExtra("password");
        uri = intent.getStringExtra("uri");
        CountryCode = intent.getStringExtra("CountryCode");

        pd = new ProgressDialog(ConfirmPhoneNumber.this);
        pd.setCancelable(false);
        pd.setMessage("Verifying..");


        textView = (TextView) findViewById(R.id.textView);
        textView.setText("Enter the code We've sent \nto " + CountryCode + phone);

        filePath = Uri.parse(uri);
        pin = (Pinview) findViewById(R.id.pinview);

        pin.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser)
            {
                if(fromUser)
                {
                    code = pinview.getValue();
                }

            }
        });


        varify = (Button) findViewById(R.id.varify);

        sendVerificationCode(phone);

        varify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {


                if (!code.equals("null") && code.length() == 6)
                {
                    pd.show();
                    verifyVerificationCode(code);
                }
                else
                {
                    Toast.makeText(ConfirmPhoneNumber.this, "Please Enter the Valid Code", Toast.LENGTH_SHORT).show();
                }


            }
        });




    }
    private void sendVerificationCode(String no)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                CountryCode + Integer.parseInt(no),
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);

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
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(ConfirmPhoneNumber.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
            // Toast.makeText(ConfirmPhoneNumber.this,s, Toast.LENGTH_LONG).show();
        }
    };

    private void verifyVerificationCode(String code)
    {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(ConfirmPhoneNumber.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {

                            registerUser();
                        }
                        else
                        {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                            {
                                Toast.makeText(ConfirmPhoneNumber.this, "Code is incorrect!", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                            else
                            {
                                Toast.makeText(ConfirmPhoneNumber.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }


                        }
                    }
                });
    }
    private void registerUser()
    {
        try
        {
            if (uri != null)
            {

                final StorageReference sRef = storageReference.child("ProfilePictures/" + mAuth.getUid() + "." + getFileExtension(filePath));



                //adding the file to reference
                sRef.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                        {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                            {
//                                String profilePicId = taskSnapshot.getDownloadUrl().toString();

                                sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri)
                                    {
                                        String profilePicId =  uri.toString();


                                        SihalaUser user = new SihalaUser();
                                        user.setPhoneNumber(CountryCode + phone);
                                        user.setUserProfilePic(profilePicId);
                                        user.setUserName(name);
                                        user.setUserId(mAuth.getCurrentUser().getUid());

                                        user.saveUser(ConfirmPhoneNumber.this,user);



//                                UserDetails user = new UserDetails(mAuth.getCurrentUser().getUid(),name,phone,profilePicId,password);


                                        FirebaseDatabase.getInstance().getReference("Users")
                                                .child(CountryCode + phone)
                                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {

                                                if(task.isSuccessful())
                                                {
                                                    Intent intent = new Intent(ConfirmPhoneNumber.this,MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);


                                                    Toast.makeText(ConfirmPhoneNumber.this,"Successfully Registered!",Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    Toast.makeText(ConfirmPhoneNumber.this,"Failed to Register, Check Your Connection!",Toast.LENGTH_SHORT).show();
                                                }
                                                pd.dismiss();
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {


                                    }
                                });





                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {

                                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                            {

                            }
                        });
            }
            else
            {
                //display an error if no file is selected
            }


            //Toast.makeText(RegisterScreen.this,"Successfully Registered!",Toast.LENGTH_SHORT).show();

        }
        catch (Exception e)
        {
            Toast.makeText(ConfirmPhoneNumber.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    public String getFileExtension(Uri uri)
    {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
