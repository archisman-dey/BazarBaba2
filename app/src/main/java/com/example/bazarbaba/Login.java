package com.example.bazarbaba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class Login extends AppCompatActivity {
    private Button login_button;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    public String TAG="abc";
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String phoneNumber="";
    private FirebaseAuth mAuth;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private TextView phonenumbertext;
    private ProgressDialog progressDialog;
    private TextView nametext;
    private TextView addresstext;
    private String uid;
    private TextView rollnumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_button=findViewById(R.id.material_button);
        mAuth = FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        phonenumbertext=findViewById(R.id.login_phone_number);
        nametext=findViewById(R.id.login_name);
        addresstext=findViewById(R.id.login_address);
        rollnumber=findViewById(R.id.roll_number);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }
        };
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber=phonenumbertext.getText().toString();
                if(TextUtils.isEmpty(nametext.getText())){
                    nametext.setError( "First name is required!" );
                }
                else if(TextUtils.isEmpty(addresstext.getText())){
                    addresstext.setError( "Address is required!" );
                }
                else {
                    progressDialog.setMessage("Verifying Phone Number...");
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);

                    startPhoneNumberVerification(phoneNumber);
                }
            }

        });


    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            uid=user.getUid();
                            Map<String, Object> city = new HashMap<>();
                            city.put("uid", uid);
                            city.put("address", addresstext.getText().toString());
                            city.put("roll number",rollnumber.getText().toString());
                            city.put("name",nametext.getText().toString());
                            db.collection("Users").document(uid)
                                    .set(city)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("abc", "DocumentSnapshot successfully written!");
//                                            Intent intent=new Intent(Login.this,Main2Activity.class);
//                    Bundle bundle=new Bundle();
//                    bundle.putParcelableArray();
                                            Toast.makeText(getApplicationContext(),"Success", Toast.LENGTH_SHORT).show();
                                            progressDialog.hide();
                                            Intent intent=new Intent(Login.this,Main2Activity.class);
                                            intent.putExtra("uid",uid);
//                    intent.putExtra("uemail",user.getEmail());
                                            Intent i = new Intent(getApplicationContext(), Login.class);
                                            startActivity(intent);
                                            finish();
//                                            intent.putExtra("orderid",orderId);
//                                            intent.putParcelableArrayListExtra("List",(ArrayList< ? extends Parcelable>)cartList);

//        intent.putParcelableArrayListExtra("List",(ArrayList< ? extends Parcelable>)cartList);
//                intent.putExtra("uemail",uemail);

                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("abc", "Error writing document", e);
                                        }
                                    });


                            }
                            // ...
                         else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }



}
