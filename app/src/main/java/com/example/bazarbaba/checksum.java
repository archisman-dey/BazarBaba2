package com.example.bazarbaba;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Parcelable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by kamal_bunkar on 15-01-2019.
 */
public class checksum extends AppCompatActivity implements PaytmPaymentTransactionCallback {
    String custid="", orderId="", mid="";
    private String uid,dil="";
    private boolean dib=false;
    private Double s;
    private List<Product_details> itemList,cartList;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(checksum.this);
        //initOrderId();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Intent intent = getIntent();
        uid=intent.getStringExtra("uid");
//        int n = 100000 + random_float() * 900000;
        int n = 10000 + new Random().nextInt(90000);
        orderId =String.valueOf(n) ;
        cartList=intent.getParcelableArrayListExtra("List");
        s=intent.getDoubleExtra("cost",0);
        dil=intent.getStringExtra("dil");
        dib=intent.getBooleanExtra("dib",false);
        //s=(double)1;
        Log.e("sum ", Double.toString(s));

        custid = intent.getStringExtra("uid");
        mid = "ScXwUW43791690584123"; /// your marchant key
        sendUserDetailTOServerdd dl = new sendUserDetailTOServerdd();
        dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
// vollye , retrofit, asynch
    }
    public class sendUserDetailTOServerdd extends AsyncTask<ArrayList<String>, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(checksum.this);
        //private String orderId , mid, custid, amt;
        String url ="https://bazarbaba.000webhostapp.com/generateChecksum.php";
        String varifyurl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
        // "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID"+orderId;
        String CHECKSUMHASH ="";
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }
        protected String doInBackground(ArrayList<String>... alldata) {
            JSONParser jsonParser = new JSONParser(checksum.this);
            String param=
                    "MID="+mid+
                            "&ORDER_ID=" + orderId+
                            "&CUST_ID="+custid+
                            "&CHANNEL_ID=WAP&TXN_AMOUNT="+Double.toString(s)+"&WEBSITE=DEFAULT"+
                            "&CALLBACK_URL="+ varifyurl+"&INDUSTRY_TYPE_ID=Retail";
            JSONObject jsonObject = jsonParser.makeHttpRequest(url,"POST",param);
            // yaha per checksum ke saht order id or status receive hoga..
            Log.e("CheckSum result >>",jsonObject.toString());
            if(jsonObject != null){
                Log.e("CheckSum result >>",jsonObject.toString());
                try {
                    CHECKSUMHASH=jsonObject.has("CHECKSUMHASH")?jsonObject.getString("CHECKSUMHASH"):"";
                    Log.e("CheckSum result >>",CHECKSUMHASH);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ","  signup result  " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            PaytmPGService Service = PaytmPGService.getProductionService();
            // when app is ready to publish use production service
            // PaytmPGService  Service = PaytmPGService.getProductionService();
            // now call paytm service here
            //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values
            HashMap<String, String> paramMap = new HashMap<String, String>();
            //these are mandatory parameters
            paramMap.put("MID", mid); //MID provided by paytm
            paramMap.put("ORDER_ID", orderId);
            paramMap.put("CUST_ID", custid);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", Double.toString(s));
            paramMap.put("WEBSITE", "DEFAULT");
            paramMap.put("CALLBACK_URL" ,varifyurl);
            //paramMap.put( "EMAIL" , "abc@gmail.com");   // no need
            // paramMap.put( "MOBILE_NO" , "9144040888");  // no need
            paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);
            //paramMap.put("PAYMENT_TYPE_ID" ,"CC");    // no need
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");
            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param "+ paramMap.toString());
            Service.initialize(Order,null);
            // start payment service call here
            Service.startPaymentTransaction(checksum.this, true, true,
                    checksum.this  );
        }
    }
    @Override
    public void onTransactionResponse(Bundle bundle) {
        Log.e("checksum ", " respon true " + bundle.toString());
//        progressDialog=new ProgressDialog(this);
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();
//        progressDialog.setCancelable(false);
//        progressDialog.setCanceledOnTouchOutside(false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> city = new HashMap<>();
        city.put("uid", uid);
        city.put("orderlist", cartList);
        city.put("curstatus",0);
        city.put("status","Your order is being processed...");
        city.put("delivery address",dil);
//        city.put("")
        db.collection("Orders").document(orderId)
                .set(city)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("abc", "DocumentSnapshot successfully written!");
                        Intent intent=new Intent(checksum.this,conformation.class);
//                    Bundle bundle=new Bundle();
//                    bundle.putParcelableArray();
//                        progressDialog.hide();
                        intent.putExtra("uid",uid);
                        intent.putExtra("orderid",orderId);
                        intent.putExtra("dil",dil);
                        intent.putExtra("dib",dib);
                        intent.putParcelableArrayListExtra("List",(ArrayList< ? extends  Parcelable >)cartList);

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
    @Override
    public void networkNotAvailable() {
        Toast.makeText(getApplicationContext(),"Transaction Failed Please Retry", Toast.LENGTH_SHORT).show();
        finish();
    }
    @Override
    public void clientAuthenticationFailed(String s) {
        Toast.makeText(getApplicationContext(),"Transaction Failed Please Retry", Toast.LENGTH_SHORT).show();
        finish();
    }
    @Override
    public void someUIErrorOccurred(String s) {
        Log.e("checksum ", " ui fail respon  "+ s );
        Toast.makeText(getApplicationContext(),"Transaction Failed Please Retry", Toast.LENGTH_SHORT).show();
        finish();
    }
    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e("checksum ", " error loading pagerespon true "+ s + "  s1 " + s1);
        Toast.makeText(getApplicationContext(),"Transaction Failed Please Retry", Toast.LENGTH_SHORT).show();
        finish();
    }
    @Override
    public void onBackPressedCancelTransaction() {
        Log.e("checksum ", " cancel call back respon  " );
        Toast.makeText(getApplicationContext(),"Transaction Failed Please Retry", Toast.LENGTH_SHORT).show();
        finish();
    }
    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.e("checksum ", "  transaction cancel " );
        Toast.makeText(getApplicationContext(),"Transaction Failed Please Retry", Toast.LENGTH_SHORT).show();
        finish();
    }
}