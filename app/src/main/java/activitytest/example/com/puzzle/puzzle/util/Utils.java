package activitytest.example.com.puzzle.puzzle.util;

/**
 * 读取屏幕的宽高，以数组形式返回
 *将一张大图切割成宫格形式
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;
import activitytest.example.com.puzzle.R;

import activitytest.example.com.puzzle.puzzle.module.ImagePiece;
import activitytest.example.com.puzzle.puzzle.ui.PuzzleLayout;

import java.util.ArrayList;
import java.util.List;


public class Utils {

    public static Bitmap imagePieceLast;

    /**
     * 返回屏幕的宽高，用数组返回
     *
     * @param context
     * @return
     */
     public static int[] getPhoneWidth(Context context){
         context = context.getApplicationContext();
         WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
         DisplayMetrics outMetrics = new DisplayMetrics();
         manager.getDefaultDisplay().getMetrics(outMetrics);
         int width = outMetrics.widthPixels;
         int height = outMetrics.heightPixels;
         int[] size = new int[2];
         size[0] = width;
         size[1] = height;
         return size;
     }

    /**
     *切割
     * @param context
     * @param bitmap
     * @param count      每行小图的个数
     * @param gameMode   游戏的模式
     * @return
     */
     public static List<ImagePiece> splitImage(Context context, Bitmap bitmap,int count,String gameMode){
         List<ImagePiece> imagePieces = new ArrayList<>();
         int width = bitmap.getWidth();
         int height = bitmap.getHeight();
         int piceceWidth = Math.min(width,height)/count;

         for(int i = 0 ;i < count; i++){
             for (int j = 0;j< count; j++){
                 ImagePiece imagePiece = new ImagePiece();
                 imagePiece.setIndex(j + i*count);
                //为creatBitmap切割图片获取x,y
                 int x = j*piceceWidth;
                 int y = i*piceceWidth;
                 if(gameMode.equals(PuzzleLayout.GAME_MODE_NORMAL)){
                     if(i == count - 1 && j == count - 1){
                         imagePieceLast = Bitmap.createBitmap(bitmap,x,y,piceceWidth,piceceWidth);
                         imagePiece.setType(ImagePiece.TYPE_EMPTY);
                         Bitmap emptyBitmap = BitmapFactory.decodeResource(context.getResources(),
                                 R.drawable.empty);
                         imagePiece.setBitmap(emptyBitmap);
                     }else{
                         imagePiece.setBitmap(Bitmap.createBitmap(bitmap,x,y,piceceWidth,piceceWidth));
                     }
                 }else{
                     imagePiece.setBitmap(Bitmap.createBitmap(bitmap,x,y,piceceWidth,piceceWidth));
                 }
                 imagePieces.add(imagePiece);
             }
         }
            return imagePieces;
     }

    /**
     *读取图片，按照缩放比保持长宽比例返回bitmap对象
     *
     * @param context
     * @param res
     * @param scale  缩放比例（1到10，为2时，长和宽均缩放至原来的1/2，为3事缩放为1/3，以此类推）
     * @return
     */
     @Nullable
     public synchronized static Bitmap readBitmap(Context context, int res, int scale){
         try{
             BitmapFactory.Options options = new BitmapFactory.Options();

         /*
         inJustDecodeBounds 如果把它设为true，那么BitmapFactory.decodeFile(String path, Options opt)
         并不会真的返回一个Bitmap给你，它仅仅会把它的宽，高取回来给你
         (通过设置inJustDecodeBounds=true，可以避免加载过大的原始图片，从而避免内存溢出。)
          */
             options.inJustDecodeBounds = false;
             options.inSampleSize = scale;
             //已被弃用
             /* 如果inPurgeable设为true的话表示使用BitmapFactory创建的Bitmap
                用于存储Pixel的内存空间在系统内存不足时可以被回收*/
             options.inPurgeable = true;
             // inPreferredConfig和inPurgeable的两个属性必须联合使用才会有效果
             options.inInputShareable = true;
             options.inPreferredConfig = Bitmap.Config.RGB_565;
             return BitmapFactory.decodeResource(context.getResources(),res,options);
         }catch (Exception e){
             return null;
         }
     }

    /**
     * 相册或者拍照的图片缩放
     * @param bitmap
     * @param scale
     * @return
     */
    public static Bitmap readInAlbum(Bitmap bitmap, int scale) {
        try{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            return bitmap;
        }catch (Exception e){
            return null;
        }
    }

     public static int getMinLength(int... params){
         int min = params[0];
         for(int para : params){
             if(para<min){
                 min = para;
             }
         }
         return min;
     }

    /**
     * 标准单位: px (px是安卓系统内部使用的单位, dp是与设备无关的尺寸单位 )
     *
     * @param context
     * @param dpvalue
     * @return
     */
    public static int dpTopx(Context context,int dpvalue){
        context = context.getApplicationContext();
         //getDisplayMetrics用来获取屏幕参数
        /*TypedValue.applyDimension()方法的功能就是把非标准尺寸转换成标准尺寸,如
          dp->px:  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
           context.getResources().getDisplayMetrics());
        */
         return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpvalue,
                 context.getResources().getDisplayMetrics());
    }

}
