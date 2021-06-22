package com.company.econatia;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteAccountActivity extends AppCompatActivity {

    EditText email , password;
    Button delete_account;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        delete_account = findViewById(R.id.delete);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                AuthCredential credential = EmailAuthProvider
                        .getCredential(str_email, str_password);

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(DeleteAccountActivity.this ,
                                                            "Deleted your account successfully" , Toast.LENGTH_SHORT).show();
                                                    //FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).removeValue();
                                                    FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid()).removeValue();
                                                    FirebaseDatabase.getInstance().getReference("Posts").orderByChild("publisher").equalTo(firebaseUser.getUid())
                                                            .getRef().removeValue();
                                                    FirebaseDatabase.getInstance().getReference("Comments").orderByChild("publisher")
                                                            .equalTo(firebaseUser.getUid()).getRef().removeValue();
                                                    FirebaseDatabase.getInstance().getReference("Likes").child(firebaseUser.getUid()).removeValue();
                                                    FirebaseDatabase.getInstance().getReference("Follow").child(firebaseUser.getUid()).removeValue();

                                                    Intent intent = new Intent(DeleteAccountActivity.this , StartActivity.class);
                                                    ContextCompat.startForegroundService(DeleteAccountActivity.this , intent);
                                                    startActivity(intent);
                                                }
                                                else{
                                                    Toast.makeText(DeleteAccountActivity.this ,
                                                            "Enter correct details" , Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                            }
                        });

            }
        });

    }
}
