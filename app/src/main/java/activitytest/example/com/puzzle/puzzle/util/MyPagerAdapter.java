package activitytest.example.com.puzzle.puzzle.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import activitytest.example.com.puzzle.R;

public class MyPagerAdapter extends PagerAdapter {

    private int[] mBitmapIds;
    private Context mContext;
    private LruCache<Integer,Bitmap> mCache;

    public MyPagerAdapter(int[] data,Context context){
        mBitmapIds = data;
        mContext = context;

        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory * 3 / 8;  //缓存区的大小
        mCache = new LruCache<Integer, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(Integer key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    @Override
    public int getCount() {
        return mBitmapIds.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_main,container,false);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv);
        loadBitmapIntoTarget(mBitmapIds[position],imageView);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void loadBitmapIntoTarget(Integer id,ImageView imageView){
        //首先尝试从内存缓存中获取是否有对应id的Bitmap
        Bitmap bitmap = mCache.get(id);
        if (bitmap != null){
            imageView.setImageBitmap(bitmap);
        }else {
            //如果没有则开启异步任务去加载
            new LoadBitmapTask(imageView).execute(id);
        }

    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height >= reqHeight || width > reqWidth){
            while ((height / (2 * inSampleSize)) >= reqHeight
                    && (width / (2 * inSampleSize)) >= reqWidth){
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private class LoadBitmapTask extends AsyncTask<Integer,Void,Bitmap> {

        private ImageView imageView;

        public LoadBitmapTask(ImageView imageView){
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;     //1、inJustDecodeBounds置为true，此时只加载图片的宽高信息
            BitmapFactory.decodeResource(mContext.getResources(),params[0],options);
            options.inSampleSize = calculateInSampleSize(options,
                    dp2px(mContext,300),
                    dp2px(mContext,380));          //2、根据ImageView的宽高计算所需要的采样率
            options.inJustDecodeBounds = false;    //3、inJustDecodeBounds置为false，正常加载图片
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),params[0],options);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }

    }
}