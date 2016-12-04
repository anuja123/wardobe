package com.crowdfire.wardobe.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.crowdfire.wardobe.R;

import java.util.ArrayList;

/**
 * Created by Anuja on 12/3/16.
 */
public class CustomTopPagerAdapter extends PagerAdapter {


    Context mContext;
    Bitmap bitmap;
    LayoutInflater mLayoutInflater;
    int position;
    ArrayList<Bitmap> mResources = new ArrayList<>();
   /* int[] mResources = {
            R.drawable.top1,
            R.drawable.top2,
            R.drawable.top3,
            R.drawable.top4,
            R.drawable.top1,
            R.drawable.top2,
            R.drawable.top3,
            R.drawable.top4,
            R.drawable.top1,
            R.drawable.top2,
            R.drawable.top3,
            R.drawable.top4
    };*/

    public CustomTopPagerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public CustomTopPagerAdapter(Context context , ArrayList<Bitmap> bitmaps) {
        mContext = context;
        mResources.addAll(bitmaps);
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(Bitmap bitmap){
        mResources.add(bitmap);
        notifyDataSetChanged();
    }

    public void addAllItems(ArrayList<Bitmap> bitmaps){
        mResources.addAll(bitmaps);
    }


  /*  public ArrayList<Bitmap> getItem(){
        return mResources;
    }
*/
    @Override
    public int getCount() {
        return mResources.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        imageView.setImageBitmap(mResources.get(position));

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View)object;
        ((ViewPager) container).removeView(view);
        view = null;
        /*container.removeView((LinearLayout) object);
        container = null ;*/
    }

    public int setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.position = mResources.indexOf(bitmap);
        return position;
    }
}
