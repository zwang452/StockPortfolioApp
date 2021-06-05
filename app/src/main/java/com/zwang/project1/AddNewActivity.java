package com.zwang.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddNewActivity extends AppCompatActivity {
    FloatingActionButton buttonCheckNew;
    FloatingActionButton buttonConfirmNew;
    TextInputEditText companyName;
    TextInputEditText companyCode;
    TextInputEditText currentPrice;
    TextInputEditText change;
    Retrofit retrofitStockDetails;
    Retrofit retrofitStockPrice;
    RealTimePriceApi realTimePriceApi;
    Stock stock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        buttonCheckNew = findViewById(R.id.buttonCheckNew);
        buttonConfirmNew = findViewById(R.id.buttonConfirmNew);
        companyCode = findViewById(R.id.companyCode);
        companyName = findViewById(R.id.companyName);
        currentPrice = findViewById(R.id.priceNow);
        change = findViewById(R.id.priceChange);
        companyCode.setText("e.g. AMZN for Amazon");
        companyCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    companyCode.setText("");
                }
            }
        });

        retrofitStockPrice = new Retrofit.Builder()
                .baseUrl("https://twelve-data1.p.rapidapi.com/")
                .build();
        realTimePriceApi = retrofitStockPrice.create(RealTimePriceApi.class);

    }

    public void onButtonReturn(View view) {
        finish();
    }

    public void queryData(View view) {
        Call<ResponseBody> call = realTimePriceApi.getRealTime(companyCode.getText().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(AddNewActivity.this,
                            "Code" + response.code() + " " + response.message(),Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONObject jsonObj = new JSONObject();
                try {
                    jsonObj = new JSONObject(response.body().string());
                    currentPrice.setText(jsonObj.getString("price"));
                    if(jsonObj.getString("code").equals("400")){
                        Toast.makeText(AddNewActivity.this,
                                jsonObj.getString("message"),Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(AddNewActivity.this,
                        "Code" + response.code() + " " +response.message(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AddNewActivity.this,
                        t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}