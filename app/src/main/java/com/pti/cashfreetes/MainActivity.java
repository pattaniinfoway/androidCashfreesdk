package com.pti.cashfreetes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gocashfree.cashfreesdk.CFPaymentService;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_APP_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_EMAIL;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_NAME;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_PHONE;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_AMOUNT;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_NOTE;
import static javax.crypto.Cipher.SECRET_KEY;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ApiInterface apiInterface;


    public static final String CLIENT_ID = "xxxxxxxxxxxxxxx";
     public static final String SECRET_KEY = "xxxxxxxxxxxxxxxxxx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiInterface = ApiClient.createService(ApiInterface.class);
    }

    @Override
    public void onClick(View v) {
        sendAndRequestResponseN("100", "1213");
    }


    private void sendAndRequestResponseN(final String amount, final String orderid) {
         String url = "https://test.cashfree.com/api/v2/cftoken/order";
      //  String url = "https://api.cashfree.com/api/v2/cftoken/order";
        String orderNote = "Test Order";
        String customerName = "Gautam Pattani";
        String customerPhone =  "9999999999";
        String customerEmail = "info@pattaniinfotech.com";
        Map<String, String> data = new HashMap<>();
        data.put("orderCurrency", "INR");
        data.put(PARAM_APP_ID, CLIENT_ID);
        data.put(PARAM_ORDER_ID, orderid);
        data.put(PARAM_ORDER_AMOUNT, amount);
        data.put(PARAM_ORDER_NOTE, orderNote);
        data.put(PARAM_CUSTOMER_NAME, customerName);
        data.put(PARAM_CUSTOMER_PHONE, customerPhone);
        data.put(PARAM_CUSTOMER_EMAIL, customerEmail);

        Call<CheckSumContainer> call = apiInterface.getChecksum(url, CLIENT_ID, SECRET_KEY,getRequestBody(data));

        call.enqueue(new Callback<CheckSumContainer>() {

            @Override
            public void onResponse(Call<CheckSumContainer> call, Response<CheckSumContainer> response) {
                // Log.e("onResponse",new Gson().toJson(response));
               if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().getStatus().equals("OK")) {
                       getprocess(response.body().getCftoken(), amount, orderid);
                    } else {
                        Toast.makeText(MainActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();

                    }
                }
            }

            @Override
            public void onFailure(Call<CheckSumContainer> call, Throwable t) {
                Log.e("onFailure",t.toString());
            }
        });
    }


    private void getprocess(String cftoken, String amount, String orderid) {
      //  String stage = "PROD";
         String stage = "TEST";
        String orderNote = "Test Order";

        String customerName = "Gautam Pattani";
        String customerPhone =  "9999999999";
        String customerEmail = "info@pattaniinfotech.com";

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_APP_ID, CLIENT_ID);
        params.put(PARAM_ORDER_ID, orderid);
        params.put(PARAM_ORDER_AMOUNT, amount);
        params.put(PARAM_ORDER_NOTE, orderNote);
        params.put(PARAM_CUSTOMER_NAME, customerName);
        params.put(PARAM_CUSTOMER_PHONE, customerPhone);
        params.put(PARAM_CUSTOMER_EMAIL, customerEmail);

        CFPaymentService cfPaymentService = CFPaymentService.getCFPaymentServiceInstance();
        cfPaymentService.setOrientation( 0);
        cfPaymentService.doPayment(this, params, cftoken, stage);
    }
    @Override
    protected  void  onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Same request code for all payment APIs.
        Log.d("onActivityResult", "ReqCode : " + CFPaymentService.REQ_CODE);
        Log.d("onActivityResult", "API Response : ");
        //Prints all extras. Replace with app logic.
        if (data != null) {
            Bundle  bundle = data.getExtras();
            if (bundle != null)
                for (String  key  :  bundle.keySet()) {
                    if (bundle.getString(key) != null) {
                        Log.d("onActivityResult", key + " : " + bundle.getString(key));
                    }
                }
        }
    }

    public static RequestBody getRequestBody(Map<String, String> data) {
        RequestBody body = RequestBody.create(getMediaType(), getJson(data));
        return body;
    }
    public static String getJson(Map<String, String> data) {
        return (new JSONObject(data)).toString();
    }

    public static MediaType getMediaType() {
        return okhttp3.MediaType.parse("application/json");
    }
}
