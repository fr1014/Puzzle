package activitytest.example.com.puzzle.puzzle.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import activitytest.example.com.puzzle.R;
import activitytest.example.com.puzzle.puzzle.util.Utils;
import activitytest.example.com.puzzle.puzzle.module.ImagePiece;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PuzzleLayout extends FrameLayout implements View.OnClickListener{

    public static final String GAME_MODE_NORMAL = "gameModeNormal";
    public static final String GAME_MODE_EXCHANGE = "gameModeExchange";

    private static final int DEFAULT_MARGIN = 0;

    //游戏模式
    private String mGameMode = GAME_MODE_EXCHANGE;

    private ImagePiece imagePiece = null;

    private ImageView imageView = null;

    //拼图游戏布局为正方形，宽度为屏幕的宽度
    private int mViewWidth = 0;

    //拼图游戏每一行的图片个数（默认为三个）
    private int mcountPieces = 3;

    //每张图片的宽度
    private int mitemWidth;

    //拼图游戏的bitmap集合
    private List<ImagePiece> mimagePieces;

    //用于给每个图片设置大小
    private FrameLayout.LayoutParams layoutParams;

    //大图
    private Bitmap mbitmap;

    //动画图
    private RelativeLayout mAnimLayout;

    //小图之间的margin
    private int mMargin;

    //这个View的padding
    private int mPadding;

    //选中的第一张小切图
    private ImageView mFirst;

    //选中的第二张小切图
    private ImageView mSecond;

    //是否添加了动画层
    private boolean isAddAnimatorLayout = false;

    //是否正在进行动画
    private boolean isAnimation = false;

    //本地自带图片默认一开始的图片
    private int res = R.mipmap.lb;

    private Bitmap bitmap = null;

    public PuzzleLayout(Context context) {
        this(context,null);
    }

    public  PuzzleLayout(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }

    public PuzzleLayout(Context context,AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);
            init(context);
            initBitmaps(bitmap);
            initBitmapsWidth();
    }

    //
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mViewWidth,mViewWidth);
    }

    //
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        for (int i = 0;i < getChildCount();i++){
            if(getChildAt(i)instanceof ImageView){
                ImageView imageView = (ImageView)getChildAt(i);
                imageView.layout(imageView.getLeft(),imageView.getTop(),
                        imageView.getRight(),imageView.getBottom());
            }else{
                RelativeLayout relativeLayout = (RelativeLayout)getChildAt(i);
                relativeLayout.layout(0,0,mViewWidth,mViewWidth);
            }
        }
    }

    /**
     * 初始化初始变量
     */
    private void init(Context context) {
        mMargin = Utils.dpTopx(context,DEFAULT_MARGIN);
        mViewWidth = Utils.getPhoneWidth(context)[0];                               //读取屏幕的宽高
        mPadding = Utils.getMinLength(getPaddingBottom(),getPaddingLeft(),
                getPaddingRight(),getPaddingTop());
        mitemWidth = (mViewWidth - mPadding*2)/mcountPieces;   //每个小图的宽度
    }

    /**
     * 将大图割成多个小图
     */
    private void initBitmaps(Bitmap bitmap) {
            if(mbitmap == null && bitmap == null){
                mbitmap = BitmapFactory.decodeResource(getResources(),res);
            }else if(bitmap != null){
                mbitmap = bitmap;
            }
            mimagePieces = Utils.splitImage(getContext(),mbitmap,mcountPieces,mGameMode);
            sortImagePieces();
    }

    //
    /**
     * 设置图片的大小和layout的属性
     */
    private void initBitmapsWidth() {
        int line = 0;
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;
        for(int i = 0; i < mimagePieces.size();i++){
            imageView = new ImageView(getContext());
            imageView.setImageBitmap(mimagePieces.get(i).getBitmap());
            layoutParams = new LayoutParams(mitemWidth,mitemWidth);
            imageView.setLayoutParams(layoutParams);
            if(i != 0 && i % mcountPieces == 0){
                line++;
            }
            if(i % mcountPieces == 0){
                left = i % mcountPieces*mitemWidth;
            }else{
                left = i % mcountPieces*mitemWidth;
            }
            top = mitemWidth * line;
            right = left + mitemWidth;
            bottom = top + mitemWidth;
            imageView.setRight(right);
            imageView.setLeft(left);
            imageView.setBottom(bottom);
            imageView.setTop(top);
            imageView.setId(i);
            imageView.setOnClickListener(this);
            mimagePieces.get(i).setImageView(imageView);
            addView(imageView);
        }
    }

    /**
     * 对ImagePieces进行排序
     */
    private void sortImagePieces() {
            int index = mimagePieces.size()-1;
            int col = (int) Math.sqrt(mimagePieces.size());     //多少行或多少列
            Random random = new Random();
            for(int i = 0;i < 150; i++){
                int tempIndex = random.nextInt(4);     //随机生成整数范围为[0,4)
                switch (tempIndex){
                    case 0:
                        if (index - col > 0){
                            /**
                             * swap(List<?> list, int i, int j)
                             *在指定列表的指定位置处交换元素。
                             */
                            Collections.swap(mimagePieces,index,index-col);
                            index = index - col;
                        }else {
                            i--;
                        }
                        break;
                    case 1:
                        if (index + col < mimagePieces.size()){
                            Collections.swap(mimagePieces,index,index + col);
                            index = index + col;
                        }else {
                            i--;
                        }
                        break;
                    case 2:
                        if ( (index - 1) / col == index / col && index - 1 > 0){
                            Collections.swap(mimagePieces,index,index - 1);
                            index--;
                        }else {
                            i--;
                        }
                        break;
                    case 3:
                        if ((index + 1) / col == index / col && index + 1 < mimagePieces.size()){
                            Collections.swap(mimagePieces,index,index + 1);
                            index++;
                        }else {
                            i--;
                        }
                        break;
                }
            }
            if(mGameMode.equals(GAME_MODE_NORMAL)){
                ImagePiece tempImagePieces = null;
                int tempIndex = 0;
                for(int i = 0;i < mimagePieces.size();i++){
                    ImagePiece imagePiece = mimagePieces.get(i);
                    if(imagePiece.getType() == ImagePiece.TYPE_EMPTY){
                        tempImagePieces = imagePiece;
                        tempIndex = i;
                        break;
                    }
                }
                if(tempImagePieces == null)
                    return;
                mimagePieces.remove(tempIndex);
                mimagePieces.add(mimagePieces.size(),tempImagePieces);
            }
    }

    /**
     * 改变游戏模式
     * @param gameMode
     */
    public void changeMode(String gameMode){
        if(gameMode.equals(mGameMode)){
            return;
        }
        this.mGameMode = gameMode;
        reset();
    }


    /**
     * 改变模式后或者增减等级时重置
     */
    private void reset() {
        mitemWidth = (mViewWidth - mPadding*2)/mcountPieces;   //被坑
        if(mimagePieces != null){
            mimagePieces.clear();
        }
        isAddAnimatorLayout = false;
        mbitmap = null;
        removeAllViews();
        initBitmaps(bitmap);
        initBitmapsWidth();
    }

    /**
     * 添加count最多每行7个
     * @return
     */
    public void addCount(){
        mcountPieces++;
        if(mcountPieces > 7){
            mcountPieces--;
        }
        reset();
    }

    /**
     * 改变图片（相册或者相机拍照的图片）
     * @param  bitmap
     */
    public void changeByAlbum(Bitmap bitmap) {
        this.bitmap = bitmap;
        reset();
    }

    /**
     * 减少count最少每行三个，否则普通模式无法游戏
     * @return
     */
    public void reduceCount(){
        mcountPieces--;
        if(mcountPieces < 3){
            mcountPieces++;
        }
        reset();
    }

    @Override
    public void onClick(View v) {
        if(isAnimation){
            //还在运行动画时不允许点击
            return;
        }
        //
        if(!(v instanceof ImageView)){
            return;
        }
        if(GAME_MODE_NORMAL.equals(mGameMode)){
            ImageView imageView = (ImageView) v;
            ImagePiece imagePiece = mimagePieces.get(imageView.getId());
            if(imagePiece.getType() == ImagePiece.TYPE_EMPTY){
                //普通模式，点击到空图不做处理
                return;
            }
            if(mFirst == null){
                mFirst = (ImageView) v;
            }
            checkEmptyImage(mFirst);
        }else {
            //点的是同一个View
            if (mFirst == v){
                mFirst.setColorFilter(null);  //
                mFirst = null;
                return;
            }
            if (mFirst == null){
                mFirst = (ImageView) v;
                //选中之后添加一层颜色
                mFirst.setColorFilter(Color.parseColor("#55FF0000"));
            }else{
                mSecond = (ImageView)v;
                exChangeView();
            }
        }
    }
    
    private void checkEmptyImage(ImageView imageView) {
        int index = imageView.getId();      //
        int line = mimagePieces.size()/mcountPieces;
        ImagePiece imagePiece = null;
        if(index < mcountPieces){
            //第一行（需要额外计算，下一行是否有空图）
            imagePiece = checkCurrentLine(index);
            //判断下一行同一列的图片是否为空
            imagePiece = checkOtherLine(index+mcountPieces,imagePiece);
        }else if(index < (line - 1)*mcountPieces){
            //中间部分的行（需要额外计算，上一行和下一行是否有空图）
            imagePiece = checkCurrentLine(index);
            //判断上一行的同一列的图片是否为空
            imagePiece = checkOtherLine(index - mcountPieces,imagePiece);
            //判断下一行的同一列的图片是否为空
            imagePiece = checkOtherLine(index + mcountPieces,imagePiece);
        }else{
            //最后一行（需要额外计算，上一行是否有空图）
            imagePiece = checkCurrentLine(index);
            //判断上一行同一列有没有空图
            imagePiece = checkOtherLine(index - mcountPieces,imagePiece);
        }
        if(imagePiece == null){
            //周围没有空的imageView
            mFirst = null;
            mSecond = null;
        }else{
            //记录第二张ImageView
            mSecond = imagePiece.getImageView();
            //选中第二张图片，开启两张图片的替换动画
            exChangeView();
        }
    }

    /**
     * 检查其它行同一列有没有空图
     * @param index
     * @param imagePiece
     * @return
     */
    private ImagePiece checkOtherLine(int index, ImagePiece imagePiece) {
        if(imagePiece != null){
            return  imagePiece;
        }else{
            return  getCheckEmptyImageView(index);
        }
    }

    /**
     * 检查当前行有没有空图
     * @param index
     * @return
     */
    private ImagePiece checkCurrentLine(int index) {
        ImagePiece imagePiece = null;
        if(index % mcountPieces == 0){                                      //第一列
            imagePiece = getCheckEmptyImageView(index+1);
        }else if (index % mcountPieces == mcountPieces -1){                 //最后一列
            imagePiece = getCheckEmptyImageView(index - 1);
        }else{
            imagePiece = getCheckEmptyImageView(index + 1);
            if(imagePiece == null){
                imagePiece = getCheckEmptyImageView(index - 1);
            }
        }
        return imagePiece;
    }

    private ImagePiece getCheckEmptyImageView(int index) {
        ImagePiece imagePiece = mimagePieces.get(index);
        if(imagePiece.getType() == ImagePiece.TYPE_EMPTY){
            //找到空的imageView
            return imagePiece;
        }
        return  null;
    }

    //
    private ImageView addAnimationImageView(ImageView imageView){
        ImageView getImage = new ImageView(getContext());

        RelativeLayout.LayoutParams firstParams = new RelativeLayout.LayoutParams(mitemWidth,mitemWidth);

        firstParams.leftMargin = imageView.getLeft() - mPadding;

        firstParams.topMargin = imageView.getTop() - mPadding;

        Bitmap firstBitmap = mimagePieces.get(imageView.getId()).getBitmap();

        getImage.setImageBitmap(firstBitmap);

        getImage.setLayoutParams(firstParams);

        mAnimLayout.addView(getImage);

        return getImage;
    }

    /**
     * 添加动画层，并且添加平移的动画
     */
    private void exChangeView() {
        //添加动画层
        setUpAnimaLayout();
        //添加第一个图片
        ImageView first = addAnimationImageView(mFirst);
        //添加另一个图片
        ImageView second = addAnimationImageView(mSecond);
        ObjectAnimator secondXAnimator = ObjectAnimator.ofFloat(second, "TranslationX",
                0f, -(mSecond.getLeft() - mFirst.getLeft()));
        ObjectAnimator secondYAnimator = ObjectAnimator.ofFloat(second, "TranslationY",
                0f, -(mSecond.getTop() - mFirst.getTop()));
        ObjectAnimator firstXAnimator = ObjectAnimator.ofFloat(first, "TranslationX",
                0f, mSecond.getLeft() - mFirst.getLeft());
        ObjectAnimator firstYAnimator = ObjectAnimator.ofFloat(first, "TranslationY",
                0f, mSecond.getTop() - mFirst.getTop());
        AnimatorSet secondAnimator = new AnimatorSet();
        secondAnimator.play(secondXAnimator).with(secondYAnimator).with(firstXAnimator).with(firstYAnimator);
        secondAnimator.setDuration(300);

        final ImagePiece firstPiece = mimagePieces.get(mFirst.getId());

        final ImagePiece secondPiece = mimagePieces.get(mSecond.getId());

        final int firstType = firstPiece.getType();

        final int secondType = secondPiece.getType();

        final Bitmap firstBitmap = mimagePieces.get(mFirst.getId()).getBitmap();

        final Bitmap secondBitmap = mimagePieces.get(mSecond.getId()).getBitmap();

        secondAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                int fristIndex = firstPiece.getIndex();
                int secondeIndex = secondPiece.getIndex();
                if (mFirst != null) {
                    mFirst.setColorFilter(null);
                    mFirst.setVisibility(VISIBLE);
                    mFirst.setImageBitmap(secondBitmap);
                    firstPiece.setBitmap(secondBitmap);
                    firstPiece.setIndex(secondeIndex);
                }
                if (mSecond != null) {
                    mSecond.setVisibility(VISIBLE);
                    mSecond.setImageBitmap(firstBitmap);
                    secondPiece.setBitmap(firstBitmap);
                    secondPiece.setIndex(fristIndex);
                }

                if (mGameMode.equals(GAME_MODE_NORMAL)) {
                    firstPiece.setType(secondType);
                    secondPiece.setType(firstType);
                }
                mAnimLayout.removeAllViews();
                mAnimLayout.setVisibility(GONE);
                mFirst = null;
                mSecond = null;
                isAnimation = false;
                invalidate();

                if (checkSuccess()) {
                    if(mGameMode.equals(GAME_MODE_NORMAL)){
                        imagePiece.setBitmap(Utils.imagePieceLast);
                        mimagePieces.add(imagePiece);
                        initBitmapsWidth();
                    }
                    Toast.makeText(getContext(), "成功!", Toast.LENGTH_SHORT).show();
                    if (mSuccessListener != null) {
                        mSuccessListener.success();
                    }
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimation = true;
                mAnimLayout.setVisibility(VISIBLE);
                mFirst.setVisibility(INVISIBLE);
                mSecond.setVisibility(INVISIBLE);
            }
        });
        secondAnimator.start();
    }

    /**
     * 构造动画层 用于点击之后的动画
     */
    private void setUpAnimaLayout() {

        if (mAnimLayout == null) {
            mAnimLayout = new RelativeLayout(getContext());
        }
        if (!isAddAnimatorLayout) {
            isAddAnimatorLayout = true;
            addView(mAnimLayout);
        }
    }

    /**
     * 检查是否成功
     */
    private boolean checkSuccess(){
        boolean isSuccess = true;
            for(int i = 0; i < mimagePieces.size(); i++){
                imagePiece = mimagePieces.get(i);
                if(i != imagePiece.getIndex()){
                    isSuccess = false;
                    break;
                }
            }
            return  isSuccess;
    }

    public int getCount() {
        return mcountPieces;
    }

    public int getRes() {
        return res;
    }

    private SuccessListener  mSuccessListener;

    public void addSuccessListener(SuccessListener successListener){
        this.mSuccessListener = successListener;
    }

    public interface SuccessListener{
        void success();
    }

}
