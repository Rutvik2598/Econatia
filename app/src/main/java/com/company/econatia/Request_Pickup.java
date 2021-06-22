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
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.company.econatia.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Request_Pickup extends AppCompatActivity {

    EditText city1,address1,number1;
    Button submit;

    String email1 = "" , fullname , username , uid , date;

    CalendarView calendar;

    FirebaseUser firebaseUser;

    DatabaseReference rootRef;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request__pickup);

        city1 = findViewById(R.id.city);

        address1 = findViewById(R.id.address);
        submit = findViewById(R.id.submit);
        number1 = findViewById(R.id.number);

        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = formatter.format(todayDate);

        calendar = findViewById(R.id.calender2);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                i1++;
                String day=null,month=null;
                day = String.valueOf(i2);
                month = String.valueOf(i1);
                if(i1 < 10)
                {
                    month = String.format("%01d" , i1);
                }

                if(i2 < 10)
                {
                    day = String.format("%01d" , i2);
                }

                date =(day + "-"+ month + "-" + i);
            }
        });

        rootRef = FirebaseDatabase.getInstance().getReference("Requests");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();
        email1 = firebaseUser.getEmail();

        userInfo();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(Request_Pickup.this);
                pd.setMessage("Please wait..");
                pd.show();


                String city = city1.getText().toString();
                String address = address1.getText().toString();
                String number = number1.getText().toString();

                if(TextUtils.isEmpty(address) || TextUtils.isEmpty(city)){
                    Toast.makeText(Request_Pickup.this , "All fields are necessary" , Toast.LENGTH_SHORT).show();
                }

                else{

                    String email = "You have new pick-up request from:" + fullname + "\n" +
                            "City:" + city + "\n" +
                            "Address:" + address + "\n" +
                            "Email:" + email1 + "\n" +
                            "Phone Number:" + number + "\n" +
                            "On date:" + date + "\n";

                    String requestid = rootRef.push().getKey();

                    HashMap<String , Object> hashMap = new HashMap<>();

                    hashMap.put("requestId" , requestid);
                    hashMap.put("Full Name" , fullname);
                    hashMap.put("Email" , email);
                    hashMap.put("Phone" , number);
                    hashMap.put("City" , city);
                    hashMap.put("Address" , address);
                    hashMap.put("Date" , date);

                    rootRef.child(requestid).setValue(hashMap);

                    SendMail sm = new SendMail(Request_Pickup.this , "econatia@gmail.com" , "New Pick-up request" , email);

                    sm.execute();

                    Toast.makeText(Request_Pickup.this , "Booking Confirmed" , Toast.LENGTH_SHORT).show();

                    pd.dismiss();

                    Intent intent = new Intent(Request_Pickup.this, MainActivity.class);
                    ContextCompat.startForegroundService(Request_Pickup.this , intent);
                    startActivity(intent);

                }
            }
        });
    }

    private void userInfo(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(this == null){
                    return;
                }

                User user = dataSnapshot.getValue(User.class);
                username = user.getUsername();
                fullname = user.getFullname();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
