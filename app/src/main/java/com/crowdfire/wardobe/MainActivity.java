package com.crowdfire.wardobe;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.crowdfire.wardobe.Adapter.CustomTopPagerAdapter;
import com.crowdfire.wardobe.database.DatabaseHelper;
import com.crowdfire.wardobe.fragment.BottomFragment;
import com.crowdfire.wardobe.fragment.TopFragment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_TOP = 123;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA_TOP = 124;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_BOTTOM = 125;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA_BOTTOM = 126;
    private static final String DB_TABLE = "wardobe";
    public static int REQUEST_CAMERA_TOP = 12345;
    public static int SELECT_FILE_TOP = 12346;
    public static int REQUEST_CAMERA_BOTTOM = 12347;
    public static int SELECT_FILE_BOTTOM = 12348;
    ViewPager mViewPager;
    CustomTopPagerAdapter mCustomTopPagerAdapter;
    ImageView mLikeBtn, mShuffle;
    TopFragment topFragment;
    BottomFragment bottomFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "Please add items in the list", Toast.LENGTH_LONG).show();
        mViewPager = (ViewPager) findViewById(R.id.viewpagerTop);
        mLikeBtn = (ImageView) findViewById(R.id.imgLike);
        mLikeBtn.setTag(R.id.imgLike);
        mShuffle = (ImageView) findViewById(R.id.imgShuffle);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();


        if (savedInstanceState == null) {
            topFragment = new TopFragment();
            bottomFragment = new BottomFragment();
        } else {
            topFragment = (TopFragment) getFragmentManager().findFragmentByTag("TOP_FRAGMENT");
            bottomFragment = (BottomFragment) getFragmentManager().findFragmentByTag("BOTTOM_FRAGMENT");

        }
        ft.replace(R.id.top_frame_layout, topFragment, "TOP_FRAGMENT");
        ft.replace(R.id.bottom_frame_layout, bottomFragment, "BOTTOM_FRAGMENT").commit();

        mLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLikeBtn.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.like).getConstantState())) {

                    mLikeBtn.setImageDrawable(getResources().getDrawable(R.drawable.liked));
                    TopFragment tFragment = (TopFragment) getFragmentManager().findFragmentByTag("TOP_FRAGMENT");
                    BottomFragment bFragment = (BottomFragment) getFragmentManager().findFragmentByTag("BOTTOM_FRAGMENT");
                    Bitmap imgTopView = tFragment.getImage();
                    Bitmap imgBottomView = bFragment.getImage();
                    if (imgBottomView != null && imgTopView != null) {
                        new SaveData(imgTopView, imgBottomView).execute();
                    }

                } else {
                    mLikeBtn.setImageDrawable(getResources().getDrawable(R.drawable.like));
                }
            }
        });


        mShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Shuffled" , Toast.LENGTH_SHORT).show();
                mLikeBtn.setImageDrawable(getResources().getDrawable(R.drawable.like));
                TopFragment tFragment = (TopFragment) getFragmentManager().findFragmentByTag("TOP_FRAGMENT");
                BottomFragment bFragment = null;
                Random randomGenerator = new Random();
                if (randomGenerator.nextInt(100) % 2 == 0) {
                    tFragment.getRandomImage(randomGenerator.nextInt(100));
                    bFragment = (BottomFragment) getFragmentManager().findFragmentByTag("BOTTOM_FRAGMENT");
                    bFragment.getRandomImage(randomGenerator.nextInt(100));
                } else {

                    new GetData().execute();


                }
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mLikeBtn.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.liked).getConstantState()))
            outState.putString("like_value", "liked");
        else
            outState.putString("like_value", "unliked");

    }

    public void setButton() {
        mLikeBtn.setImageDrawable(getResources().getDrawable(R.drawable.like));
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String btnStatus = savedInstanceState.getString("like_value");
        Log.d("Status is", btnStatus + " ");
        if (btnStatus.equals("liked")) {
            mLikeBtn.setImageDrawable(null);
            mLikeBtn.setImageDrawable(getResources().getDrawable(R.drawable.liked));
        } else {
            mLikeBtn.setImageDrawable(getResources().getDrawable(R.drawable.like));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA_TOP) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityFromFragment(topFragment, intent, REQUEST_CAMERA_TOP);
            } else {
                Toast.makeText(this, "Unable to open camera .Please Grant Permission first!!", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA_BOTTOM) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityFromFragment(bottomFragment, intent, REQUEST_CAMERA_BOTTOM);
            } else {
                Toast.makeText(this, "Unable to open camera .Please Grant Permission first!!", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_TOP) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityFromFragment(topFragment, Intent.createChooser(intent, "Select File"), SELECT_FILE_TOP);
            } else {
                Toast.makeText(this, "Unable to open gallery .Please Grant Permission first!!", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_BOTTOM) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityFromFragment(bottomFragment, Intent.createChooser(intent, "Select File"), SELECT_FILE_BOTTOM);
            } else {
                Toast.makeText(this, "Unable to open gallery .Please Grant Permission first!!", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    void insertData(Bitmap topImg, Bitmap bottomImg) {
        Log.d("Inserting", "Ture");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        topImg.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArrayTopImg = stream.toByteArray();
        bottomImg.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArrayBottomImg = stream.toByteArray();

        DatabaseHelper db = new DatabaseHelper(this);

        SQLiteDatabase database = db.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("fav_top_data", byteArrayTopImg);
        cv.put("fav_bottom_data", byteArrayBottomImg);
        //database.insert("wardobe_fav", null, cv);
        database.insertWithOnConflict("wardobe_fav", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        database.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getData() {

        String Table_Name = "wardobe_fav";

        String selectQuery = "SELECT  * FROM " + Table_Name;
        DatabaseHelper db = new DatabaseHelper(this);

        SQLiteDatabase database = db.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        String[] data = null;
        if (cursor.moveToFirst()) {
            do {
                Log.d("Data in fav", cursor.getCount() + " ");
                // get  the  data into array,or class variable
            } while (cursor.moveToNext());
        }
        db.close();
        //return data;


    }

    private class SaveData extends AsyncTask<String, Void, String> {

        Bitmap imgTopView, imgBottomView;

        public SaveData(Bitmap imgTopView, Bitmap imgBottomView) {
            this.imgTopView = imgTopView;
            this.imgBottomView = imgBottomView;
        }

        @Override
        protected String doInBackground(String... params) {
            insertData(imgTopView, imgBottomView);
            getData();
            /*for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }*/
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            //  TextView txt = (TextView) findViewById(R.id.output);
            //  txt.setText("Executed"); // txt.setText(result);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private class GetData extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            String Table_Name = "wardobe_fav";
            final TopFragment tFragment = (TopFragment) getFragmentManager().findFragmentByTag("TOP_FRAGMENT");
            final BottomFragment bFragment = (BottomFragment) getFragmentManager().findFragmentByTag("BOTTOM_FRAGMENT");

            String selectQuery = "SELECT  * FROM " + Table_Name;
            final DatabaseHelper db = new DatabaseHelper(MainActivity.this);

            SQLiteDatabase database = db.getWritableDatabase();
            final Cursor cursor = database.rawQuery(selectQuery, null);
            String[] data = null;
            int i = 0;
            Random randomGenerator = new Random();
            if (cursor.moveToFirst()) {
                do {

                    if (i == randomGenerator.nextInt(10) % cursor.getCount())
                        break;
                    //mCustomTopPagerAdapter.setBitmap(bitmap);
                    //mCustomTopPagerAdapter.setBitmap(bitmap);
                    //mCustomTopPagerAdapter.setmCustomTopPagerAdapter.getItemPosition(bitmap);
                    i++;

                    //Log.d("Data in fav", cursor.getCount() + " ");
                    // get  the  data into array,or class variable
                } while (cursor.moveToNext());
            }

            Log.d("The pos is ", i + " : " + cursor.getCount());
            final int finalI = i;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (cursor != null && finalI != cursor.getCount() && cursor.getCount() > 0) {
                            byte[] byteImageTop = cursor.getBlob(cursor.getColumnIndex("fav_top_data"));
                            ByteArrayInputStream imageStream = new ByteArrayInputStream(byteImageTop);
                            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                            tFragment.setBitmap(bitmap);

                            byte[] byteImageBottom = cursor.getBlob(cursor.getColumnIndex("fav_bottom_data"));
                            imageStream = new ByteArrayInputStream(byteImageBottom);
                            bitmap = BitmapFactory.decodeStream(imageStream);
                            bFragment.setBitmap(bitmap);
                            db.close();
                        }
                    }catch (IllegalStateException e){

                    }
//stuff that updates ui

                }
            });

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {


        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
