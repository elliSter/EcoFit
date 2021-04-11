package com.example.ecofit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class ContactUsActivity extends AppCompatActivity {
    TextInputLayout emailBody,emailSubject;
    Button sendBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        emailSubject=findViewById(R.id.emailSubject);
        emailBody=findViewById(R.id.emailBody);
        sendBtn=findViewById(R.id.sendBtn);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject=emailSubject.getEditText().getText().toString().trim();
                String body=emailBody.getEditText().getText().toString().trim();
                String emailAddress="info@thessalliance.com";

                if(subject.isEmpty()){
                    Toast.makeText(ContactUsActivity.this,"Please add Subject",Toast.LENGTH_SHORT).show();
                }else if(body.isEmpty()){
                    Toast.makeText(ContactUsActivity.this,"Please add a Message",Toast.LENGTH_SHORT).show();
                }else {
                    String mail="mailto:"+emailAddress +
                            "?&subject="+ Uri.encode(subject)+
                            "?&body="+ Uri.encode(body);
                    Intent intent=new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse(mail));

                    try {
                        startActivity(Intent.createChooser(intent,"Send Email.."));
                    }catch (Exception e){
                        Toast.makeText(ContactUsActivity.this,"Exception: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    public void onBackPressed(){
        Intent intentHome = new Intent(ContactUsActivity.this, HomeActivity.class);
        startActivity(intentHome);
        finish();
    }
}