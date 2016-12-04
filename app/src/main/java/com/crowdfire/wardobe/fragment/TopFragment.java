package com.crowdfire.wardobe.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.crowdfire.wardobe.Adapter.CustomTopPagerAdapter;
import com.crowdfire.wardobe.MainActivity;
import com.crowdfire.wardobe.R;
import com.crowdfire.wardobe.database.DatabaseHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Anuja on 12/3/16.
 */
public class TopFragment extends Fragment {

    private static final String STATE_LIST = "State Adapter Data";
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_TOP = 123;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA_TOP = 124;
    public static int REQUEST_CAMERA_TOP = 12345;
    public static int SELECT_FILE_TOP = 12346;
    ViewPager vpTopPager;
    final String[] userChoosenTask = {""};
    CustomTopPagerAdapter mTopPagerAdapter;
    FloatingActionButton mAddTop;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = null;
        if (mView != null) {
            ((ViewGroup) mView.getParent()).removeView(mView);
            return mView;
        }
        mView = inflater.inflate(R.layout.top_fragment, container, false);
        return mView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }
    public static Bitmap getBitmapFromView(View view) {

        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }


    public void setBitmap(Bitmap bitmap){
        int pos = mTopPagerAdapter.setBitmap(bitmap);
        getRandomImage(pos);
    }

    public Bitmap getImage(){
        return vpTopPager.getChildAt(vpTopPager.getCurrentItem()) == null ? null : getBitmapFromView(vpTopPager.getChildAt(vpTopPager.getCurrentItem()).getRootView());
    }

    public void getRandomImage(int position) {
        if(vpTopPager != null && vpTopPager.getChildCount() != 0) {
            vpTopPager.setCurrentItem(position % vpTopPager.getChildCount());
            mTopPagerAdapter.notifyDataSetChanged();
        }
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context, String userChoosenTask) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {

            switch (userChoosenTask) {

                case "Choose from Gallery":
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_TOP);


                        return false;

                    }
                case "Take Photo":
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA_TOP);


                        return false;
                    }

                default: {
                    return true;
                }

            }
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // In fragment class callback
        if (requestCode == REQUEST_CAMERA_TOP) {
            // Make sure the request was successful
            if (resultCode == -1) {
                Toast.makeText(getActivity(), "Select camera", Toast.LENGTH_LONG).show();
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");


                if (bitmap != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "Title", null);
                    Uri tempUri = Uri.parse(path);
                    addEntry("top", byteArray);
                    mTopPagerAdapter.addItem(bitmap);
                    mTopPagerAdapter.notifyDataSetChanged();
                    getData();
                } else {
                    Toast.makeText(getActivity(), "Error while capturing Image", Toast.LENGTH_LONG).show();
                }
            }
        }

        if (requestCode == SELECT_FILE_TOP) {
            if (resultCode == -1) {
                Toast.makeText(getActivity(), "Select file", Toast.LENGTH_LONG).show();
                Uri imageToUploadUri = data.getData();
                if (imageToUploadUri != null) {
                    Uri selectedImage = imageToUploadUri;
                    getActivity().getContentResolver().notifyChange(selectedImage, null);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageToUploadUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (bitmap != null) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        addEntry("top", byteArray);
                        mTopPagerAdapter.addItem(bitmap);
                        mTopPagerAdapter.notifyDataSetChanged();
                        getData();

                    }

                    //Button uploadImageButton = (Button) findViewById(R.id.uploadUserImageButton);
                    //uploadImageButton.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getActivity(), "Error while capturing Image", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    public void addEntry(String name, byte[] image) throws SQLiteException {
        DatabaseHelper db = new DatabaseHelper(getActivity());

        SQLiteDatabase database = db.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("image_type", name);
        cv.put("image_data", image);
        database.insert("wardobe", null, cv);
        //mTopPagerAdapter.addItem(image);
        database.close();
    }

    public void getData() {

        String Table_Name = "wardobe";

        String selectQuery = "SELECT  * FROM " + Table_Name;
        DatabaseHelper db = new DatabaseHelper(getActivity());

        SQLiteDatabase database = db.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        String[] data = null;
        if (cursor.moveToFirst()) {
            do {
                Log.d("Data is", cursor.getCount() + " ");
                // get  the  data into array,or class variable
            } while (cursor.moveToNext());
        }
        db.close();
        //return data;


    }


    private void selectImage() {

        final CharSequence[] items = {"Take Photo", "Choose from Gallery",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = false;

                if (items[item].equals("Take Photo")) {
                    userChoosenTask[0] = "Take Photo";
                    result = checkPermission(getActivity(), userChoosenTask[0]);
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Gallery")) {
                    userChoosenTask[0] = "Choose from Gallery";
                    result = checkPermission(getActivity(), userChoosenTask[0]);

                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE_TOP);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA_TOP);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
      //  outState.putParcelableArrayList(STATE_LIST, mTopPagerAdapter.getItem());

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vpTopPager = (ViewPager) view.findViewById(R.id.viewpagerTop);
        DatabaseHelper db = new DatabaseHelper(getActivity());

        SQLiteDatabase database = db.getWritableDatabase();

        String Table_Name = "wardobe";
        ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
        String selectQuery = "SELECT  image_data FROM " + Table_Name + " where image_type = 'top' ";
        Cursor cursor = database.rawQuery(selectQuery, null);
        String[] data = null;
        if (cursor.moveToFirst()) {
            do {
                byte[] byteImage = cursor.getBlob(cursor.getColumnIndex("image_data"));
                ByteArrayInputStream imageStream = new ByteArrayInputStream(byteImage);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                bitmaps.add(bitmap);
            } while (cursor.moveToNext());
        }
        db.close();
        if (bitmaps != null)
            mTopPagerAdapter = new CustomTopPagerAdapter(getActivity(), bitmaps);
        else
            mTopPagerAdapter = new CustomTopPagerAdapter(getActivity());
        vpTopPager.setAdapter(mTopPagerAdapter);
        mTopPagerAdapter.notifyDataSetChanged();
        mAddTop = (FloatingActionButton) view.findViewById(R.id.addTops);
        mAddTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        vpTopPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                ImageView view =(ImageView) getActivity().findViewById(R.id.imgLike);
                view.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.like));
            }
        });

    }


}


