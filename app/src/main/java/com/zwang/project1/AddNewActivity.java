package com.zwang.project1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

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
    Retrofit retrofitLogo;
    Retrofit retrofitStockPrice;
    StockApis stockApis;
    LogoApi logoApi;
    Switch customizedNameSwitch;
    String companyNameString = "";
    TextView switchText;

    Stock stock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        switchText = findViewById(R.id.switchText);
        customizedNameSwitch = findViewById(R.id.customizedNameSwitch);
        customizedNameSwitch.setEnabled(false);
        buttonCheckNew = findViewById(R.id.buttonCheckNew);
        buttonConfirmNew = findViewById(R.id.buttonConfirmNew);
        buttonConfirmNew.setEnabled(false);
        companyCode = findViewById(R.id.companyCode);
        companyName = findViewById(R.id.companyName);
        companyName.setEnabled(false);
        currentPrice = findViewById(R.id.priceNow);
        currentPrice.setEnabled(false);
        change = findViewById(R.id.priceChange);
        change.setEnabled(false);
        companyCode.setText("e.g. AMZN for Amazon");
        companyCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    companyCode.setText("");
                }
            }
        });
        //Initialize API classes
        retrofitStockPrice = new Retrofit.Builder()
                .baseUrl("https://twelve-data1.p.rapidapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        stockApis = retrofitStockPrice.create(StockApis.class);
        retrofitLogo = new Retrofit.Builder()
                .baseUrl("https://autocomplete.clearbit.com/v1/companies/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        logoApi = retrofitLogo.create(LogoApi.class);

        //Allows for customizing company name if switch is on
        customizedNameSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    companyName.setEnabled(true);
                }else{
                    companyName.setEnabled(false);
                    companyName.setText(companyNameString);
                }
            }
        });


    }

    public void onButtonReturn(View view) {
        finish();
    }
    //Two APIs, getRealTime() and getDetail() are called
    //getDetail() is nested within the onResponse() of getRealTime() and then the change% is
    //calculated using the currentPrice from getRealTime() and previousClosingPrice from getDetail()
    //ToDo: Find a better way to coordinate asynchronous API calls other than nesting
    public void queryData(View view)  {
        Call<ResponseBody> callPrice = stockApis.getRealTime(companyCode.getText().toString());
        callPrice.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(AddNewActivity.this,
                            "Code" + response.code() + " " + response.message(),Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONObject jsonObj;
                try {
                    jsonObj = new JSONObject(response.body().string());
                    //stock.setPrice_(jsonObj.getString("price"));
                    try{
                        if(jsonObj.getString("code").equals("400")){
                            Toast.makeText(AddNewActivity.this,
                                    jsonObj.getString("message"),Toast.LENGTH_LONG).show();
                            return;
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    currentPrice.setText(jsonObj.getString("price"));
                    //Calling the second API for details
                    Call<Stock> callDetails = stockApis.getDetail(companyCode.getText().toString());
                    callDetails.enqueue(new Callback<Stock>() {
                        @Override
                        public void onResponse(Call<Stock> callDetail, Response<Stock> responseDetail)  {
                            if(!response.isSuccessful()){
                                Toast.makeText(AddNewActivity.this,
                                        "Code" + responseDetail.code() + " " + responseDetail.message(),Toast.LENGTH_SHORT).show();
                                return;
                            }
                            stock = responseDetail.body();
                            companyName.setText(stock.getName_());
                            companyNameString = stock.getName_();
                            //Calculates for the change
                            double changePercent = (Double.parseDouble(currentPrice.getText().toString()) - Double.parseDouble(stock.getPreviousClose()))/Double.parseDouble(stock.getPreviousClose());
                            DecimalFormat df = new DecimalFormat("###0.00%");
                            change.setText(df.format(changePercent));
                            stock.setChange_(df.format(changePercent));
                            Toast.makeText(AddNewActivity.this,
                                    stock.getName_() + " found, press the checkmark button to add it to your list",Toast.LENGTH_LONG).show();
                            buttonConfirmNew.setEnabled(true);
                            customizedNameSwitch.setEnabled(true);
                            switchText.setTextColor(getResources().getColor(R.color.black, getResources().newTheme()));
                        }
                        @Override
                        public void onFailure(Call<Stock> callDetail, Throwable t2) {
                            Toast.makeText(AddNewActivity.this,
                                    t2.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AddNewActivity.this,
                        t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Calls the logoAPI for the company logo url. Saves the information in shared preferences
    //and then return back to the main activity
    public void onClickConfirmNew(View view) {
        //Remove the "Inc" in the name before querying the logoApi to ensure a more accurate result
        //String companyNameString = companyName.getText().toString();
        int indexOfInc = companyNameString.indexOf("Inc");
        if(indexOfInc != -1){
            companyNameString = companyNameString.substring(0, indexOfInc);
        }
        Call<List<LogoUrl>> call = logoApi.getLogoUrl(companyNameString);
        call.enqueue(new Callback<List<LogoUrl>>() {
            @Override
            public void onResponse(Call<List<LogoUrl>> call, Response<List<LogoUrl>> response) {
                if(!response.isSuccessful()){
                    stock.setPictureUrl_(" ");
                    return;
                }
                List<LogoUrl> logos = response.body();
                String logoUrlString;
                logoUrlString = logos != null && logos.size() > 0? logos.get(0).getLogoUrl() : " ";
                stock.setPictureUrl_(logoUrlString);
                stock.setPrice_(currentPrice.getText().toString());
                stock.setName_(companyName.getText().toString());
                Gson gson = new Gson();
                String jsonString = gson.toJson(stock);
                SharedPreferences settings = getSharedPreferences("newStock", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("stockObj", jsonString);
                editor.putBoolean("hasAdded", false);
                editor.apply();
                finish();
            }

            @Override
            public void onFailure(Call<List<LogoUrl>> call, Throwable t) {
                stock.setPictureUrl_(" ");
                stock.setPrice_(currentPrice.getText().toString());
                stock.setName_(companyName.getText().toString());
                Gson gson = new Gson();
                String jsonString = gson.toJson(stock);
                SharedPreferences settings = getSharedPreferences("newStock", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("stockObj", jsonString);
                editor.putBoolean("hasAdded", false);
                editor.apply();
                finish();

            }
        });

    }
}