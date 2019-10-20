package com.example.bazarbaba;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.ceil;

public class conformation extends AppCompatActivity {
    public TextView textstatus,otp;
    private List<Product_details> itemList,cartList;
    private ListView list;
    private List ans;
    private ProgressDialog progressDialog;
    private EditText pri,dpri;
    private conform_adapter adapter2;
    private double s,ds;
    private boolean dib=false;
    private String dil="";

    private String uid,ot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation);
        pri=findViewById(R.id.conf_price_editable);
//        dpri=findViewById(R.id.conf_delivery_charge_editable);
        ans= new ArrayList<String>();
        textstatus=findViewById(R.id.confirmation_processing_text);
        Intent intent=getIntent();
        cartList=intent.getParcelableArrayListExtra("List");
        uid=intent.getStringExtra("uid");
        dil=intent.getStringExtra("dil");
        dib=intent.getBooleanExtra("dib",false);
        ot=intent.getStringExtra("orderid");
        otp=findViewById(R.id.confirmation_otp);
        for(Product_details item:cartList)
        {
            ans.add(item.getName());
        }
        setUpRecyclerViewcart();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final DocumentReference docRef = db.collection("Orders").document(ot);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("abc", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("abc", "Current data: " + snapshot.getData());
                    textstatus.setText((String)snapshot.get("status"));
                    if((Long)snapshot.get("curstatus")==1) {
                        otp.setText(ot);
                        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, -1);
                    }
                } else {
                    Log.d("abc", "Current data: null");
                }
            }
        });
    }


    private void setUpRecyclerViewcart(){

        s=0;
    ds=0;
        for(Product_details item:cartList)
        {
            s += item.getPrice();
//            ds += getDeliveryCharge(item);
        }
        if (s <=40)
            ds = 5;
        if(dib==true)
            s=ds+s;
        pri.setText(Double.toString(s));
//        dpri.setText(Double.toString(ds));
        RecyclerView recyclerView = findViewById(R.id.confirmation_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter2=new conform_adapter(cartList,this);
        recyclerView.setAdapter(adapter2);
//        itemList.clear();
        adapter2.filterList(cartList);

    }

    private static boolean contains (String[] array, String value)
    {
        for (String s : array)
        {
            if (s.equals(value))
                return true;
        }
        return false;
    }

    private static double getDeliveryCharge(Product_details item) {
        long itemprice = item.getPrice();
        String itemshop = item.getShop();

        String[] hostels = {"Umiam", "Brahmaputra", "Siang", "Barak", "Kameng", "Manas", "Lohit", "Dihing", "Lohit Pure Veg", "Kapili"};

        if (contains(hostels, itemshop))
        {
            if (itemprice < 30)
                return 3;
            else if (itemprice < 100)
                return ceil(itemprice/10);
            else
                return ceil(itemprice/10);
        }
        else if (itemshop.equals("Khokha"))
        {
            return 20;
        }
        else
            return 0;
    }
}
