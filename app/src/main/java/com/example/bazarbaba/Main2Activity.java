package com.example.bazarbaba;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class Main2Activity extends AppCompatActivity implements Product_Adapter.MyClickListener , Cart_adapter.MyClickListener{
    private TextView mTextMessage;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference booksref = db.collection("Items");
    private List<Product_details> itemList,cartList;
    private Product_Adapter adapter;
    private Cart_adapter adapter2;
    private EditText search_et;
    private String uid;
    private ProgressDialog progressDialog;
    private Button buttonm2;
    private String token;
    private RecyclerView recyclerView;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setUpRecyclerView();
//                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_notifications:
//                    setUpRecyclerViewcart();
                    Intent intent=new Intent(Main2Activity.this,cart.class);
//                    Bundle bundle=new Bundle();
//                    bundle.putParcelableArray();
                    intent.putExtra("uid",uid);
                    intent.putParcelableArrayListExtra("List",(ArrayList< ? extends  Parcelable >)cartList);
//                intent.putExtra("uemail",uemail);

                        startActivity(intent);
                    finish();


//                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        BottomNavigationView navView = findViewById(R.id.nav_view);
////        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupWithNavController(navView, navController);

        search_et =findViewById(R.id.search_et);
        itemList=new ArrayList<>();
//        uid=getIntent().getStringExtra("uid");
        progressDialog=new ProgressDialog(this);
        uid=getIntent().getStringExtra("uid");
        cartList=new ArrayList<>();
//        uemail=getIntent().getStringExtra("uemail");
        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w("e", "getInstanceId failed", task.getException());
//                            return;
//                        }
//
//                        // Get new Instance ID token
//                        token = task.getResult().getToken();
//                        db.collection("Items").document(uid).update("token",token);
//
//
//                        // Log and toast
////                        String msg = getString(R.string.msg_token_fmt, token);
////                        Log.d(TAG, msg);
////                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//                    }
//                });

        setUpRecyclerView();
    }
    private void setUpRecyclerView(){
        progressDialog.setMessage("Loading Products...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

//        Query query = booksref.orderBy("ISBN",Query.Direction.DESCENDING);
//
//        FirestoreRecyclerOptions<Book_details> options=new FirestoreRecyclerOptions.Builder<Book_details>().setQuery(query,Book_details.class).build();
//
//        adapter =new BookAdapter(options);
//
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter=new Product_Adapter(itemList,this,this);
//        recyclerView.cle
        recyclerView.setAdapter(adapter);
        itemList.clear();

        booksref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        itemList.add(new Product_details(documentSnapshot.get("Name").toString(),documentSnapshot.get("Shop").toString(),(long)documentSnapshot.get("Price")));
                    }
                    adapter.notifyDataSetChanged();
                    adapter.filterList(itemList);
                    progressDialog.hide();
                }
                else{
                    String error=task.getException().getMessage();
                    progressDialog.hide();
                    Toast.makeText(Main2Activity.this, error, Toast.LENGTH_SHORT).show();
                }
//                bar.dismiss();
            }
        });
    }
    private void setUpRecyclerViewcart(){

        RecyclerView recyclerView2 = findViewById(R.id.recycler_view);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));

        adapter2=new Cart_adapter(itemList,this,this);
        recyclerView2.setAdapter(adapter2);
//        itemList.clear();
        adapter2.filterList(cartList);

    }
    private void filter(String text) {
        List<Product_details> filteredList = new ArrayList<>();

        for (Product_details item : itemList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        adapter.filterList(filteredList);
//        adapter.notifyDataSetChanged();

//        recyclerView.setAdapter(adapter);

    }
    @Override
    protected void onStart()
    {
        super.onStart();
//        Toast.makeText(getApplicationContext(),"Hello", Toast.LENGTH_SHORT).show();
        search_et.setText("");

    }
    @Override
    public void onAdd(Product_details it) {
        Toast.makeText(getApplicationContext(),"Added 1 pc Successfully", Toast.LENGTH_SHORT).show();
        cartList.add(it);
    }
    @Override
    public void ondd(Product_details it) {
        Toast.makeText(getApplicationContext(),"Hello", Toast.LENGTH_SHORT).show();
        cartList.remove(it);
        setUpRecyclerViewcart();
    }
}
