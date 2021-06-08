package com.zwang.project1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.logging.Logger;
import android.util.TypedValue;



public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewAdapter;
    ArrayList<Stock> stocks;
    private static final String TAG = "Main";
    private DBAdapter db;
    private Stock deletedStock;
    private Paint p = new Paint();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stocks = new ArrayList<>();
        // database
        db = new DBAdapter(this);
        // get the existing database file or from the assets folder if doesn't exist
        try
        {
            String destPath = "data/data/"+getPackageName()+"/databases";
            File f = new File(destPath);
            if(!f.exists()){
                f.mkdirs();
                //copy db from assets folder
                CopyDB(getBaseContext().getAssets().open("mydb"),
                        new FileOutputStream(destPath + "/MyDB"));
            }
        }catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        db.open();
        Cursor c;
        c = db.getAllStocks();
        if(c.moveToFirst())
        {
            do{
                // put database item into an ArrayList for the RecyclerView
                stocks.add(new Stock(c.getInt(0),c.getString(1),
                        c.getString(2),c.getString(3),
                        c.getString(4),c.getString(5),
                        c.getString(6)));
            } while(c.moveToNext());
        }
        //db.insertStock("Amazon","AMZN","amazon.com",
        //        "https://logo.clearbit.com/amazon.com","3516.25","3.51");
        //db.close();





        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewAdapter = new RecyclerViewAdapter(stocks, getApplicationContext());
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(recyclerViewAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Add divider between items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        //Attach swipe callback method to the recycler view
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    // copy database from assets to phone
    // items in the assets folder preserve filename
    public void CopyDB(InputStream inputStream, OutputStream outputStream)
            throws IOException {
        //Copy one byte at a time
        byte [] buffer = new byte[1024];
        int length;
        while((length = inputStream.read(buffer)) > 0)
        {
            outputStream.write(buffer,0,length);
        }
        inputStream.close();
        outputStream.close();
    }
    //ItemTouchHelper that handles swiping action on recycler view handlers
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            deletedStock = stocks.get(position);
            int rowID = deletedStock.getId_();
            stocks.remove(position);
            recyclerViewAdapter.notifyItemRemoved(position);
            Log.d(TAG, String.valueOf(rowID));
            String snackbarMessage = deletedStock.getName_() + " deleted from your list";
            Snackbar snackbar = Snackbar.make(recyclerView, snackbarMessage, Snackbar.LENGTH_LONG);
            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stocks.add(position, deletedStock);
                    recyclerViewAdapter.notifyItemInserted(position);
                }
            });
            snackbar.show();
            //Only deletes the record from bd if undo was not clicked
            snackbar.addCallback(new Snackbar.Callback(){
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    if(event != DISMISS_EVENT_ACTION){
                        db.deleteStock(rowID);
                    }
                }
            });
        }

        @Override
        public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;


            if(dX < 0){
                p.setColor(Color.parseColor("#ff0000"));
                RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                c.drawRect(background,p);
                Drawable d = getResources().getDrawable(R.drawable.ic_baseline_delete_forever_24_white, getTheme());
                RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                c.drawBitmap(drawableToBitmap(d),null,icon_dest,p);

            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public void onButtonNew(View view) {
        Intent intent = new Intent(this,AddNewActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = getSharedPreferences("newStock", Context.MODE_PRIVATE);
        boolean hasAdded = settings.getBoolean("hasAdded", true);
        if(!hasAdded){
            String jsonString = settings.getString("stockObj", "");
            Gson gson = new Gson();
            Stock stock = gson.fromJson(jsonString,Stock.class);
            //stock.setPictureUrl_(" ");
            stocks.add(stock);
            recyclerViewAdapter.notifyItemInserted(stocks.size()-1);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("hasAdded", true);
            editor.apply();
            db.open();
            long status = db.insertStock(stock.getName_(),stock.getSymbol_()," ", stock.getPictureUrl_(),
                    stock.getPrice_(),stock.getChange_());
        }
    }
}