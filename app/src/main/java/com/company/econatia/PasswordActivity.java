package com.company.econatia;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordActivity extends AppCompatActivity {

    EditText reset_email1;
    Button send_mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        reset_email1 = findViewById(R.id.reset_email);
        send_mail = findViewById(R.id.send_mail);

        send_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_email_entered = reset_email1.getText().toString();
                final ProgressDialog progressDialog = new ProgressDialog(PasswordActivity.this);
                progressDialog.setMessage("Sending mail");
                progressDialog.show();
                if(TextUtils.isEmpty(str_email_entered)){
                    Toast.makeText(PasswordActivity.this , "Enter email" , Toast.LENGTH_SHORT).show();
                }else{
                    FirebaseAuth.getInstance().sendPasswordResetEmail(str_email_entered)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Toast.makeText(PasswordActivity.this , "Email sent" , Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(PasswordActivity.this , LoginActivity.class);
                                        ContextCompat.startForegroundService(PasswordActivity.this , intent);
                                        startActivity(intent);
                                    }
                                }
                            });
                }
            }
        });


    }
}
