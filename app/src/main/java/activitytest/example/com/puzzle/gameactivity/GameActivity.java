package activitytest.example.com.puzzle.gameactivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import activitytest.example.com.puzzle.R;
import activitytest.example.com.puzzle.puzzle.dialog.SuccessDialog;
import activitytest.example.com.puzzle.puzzle.game.PuzzleGame;
import activitytest.example.com.puzzle.puzzle.ui.PuzzleLayout;
import activitytest.example.com.puzzle.puzzle.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;


public class GameActivity extends AppCompatActivity implements PuzzleGame.GameStateListener,View.OnClickListener{

    private ResideMenu resideMenu;
    private ResideMenu.OnMenuListener menuListener;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemPicture;
    private ResideMenuItem itemCamera;

    private PuzzleLayout puzzleLayout;
    private PuzzleGame puzzleGame;
    private ImageView srcImg;
    private Spinner spinner1;
    private TextView tvlevel;
    private TextView tvleve2;
    private Uri imageUri;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity_layout);
        initView();
        initListener();
    }

    private void initView() {
        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.background);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        // create menu items;
        itemHome = new ResideMenuItem(this,R.drawable.home,"主页");
        itemPicture = new ResideMenuItem(this,R.drawable.picture6,"相册");
        itemCamera = new ResideMenuItem(this,R.drawable.camera6, "相机");

        itemHome.setOnClickListener(this);
        itemPicture.setOnClickListener(this);
        itemCamera.setOnClickListener(this);

        resideMenu.addMenuItem(itemHome,  ResideMenu.DIRECTION_LEFT); // or  ResideMenu.DIRECTION_RIGHT
        resideMenu.addMenuItem(itemPicture,  ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemCamera,  ResideMenu.DIRECTION_LEFT);

        menuListener = new ResideMenu.OnMenuListener() {
            @Override
            public void openMenu() {
                Toast.makeText(GameActivity.this, "菜单打开", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void closeMenu() {
                Toast.makeText(GameActivity.this, "菜单关闭", Toast.LENGTH_SHORT).show();
            }
        };
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
    }

        puzzleLayout = (PuzzleLayout)findViewById(R.id.puzzleLayout);
        puzzleGame = new PuzzleGame(this,puzzleLayout);
        srcImg = (ImageView)findViewById(R.id.imageView);
        spinner1 = (Spinner)findViewById(R.id.modeSpinner);
        tvlevel = (TextView)findViewById(R.id.textView3);
        tvleve2 = (TextView)findViewById(R.id.textView2);
        tvlevel.setText("  难度等级" + puzzleGame.getLevel());
        tvleve2.setText("原图");
        srcImg.setImageBitmap(Utils.readBitmap(getApplicationContext(),puzzleLayout.getRes(),4));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    private void initListener() {
        puzzleGame.addGameStateListener(this);
        mode();
    }

    //相册选择图片
    public  void photo(){
         if (ContextCompat.checkSelfPermission(GameActivity.this,
                   Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.
                       PERMISSION_GRANTED) {
              ActivityCompat.requestPermissions(this,
              new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
         } else {
               openAlbum();
         }
    }

    //相机拍照得到图片
    public void camera(){
        //创建File对象，用于储存拍照后的图片
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
                }
                outputImage.createNewFile();
            } catch (IOException e) {
                 e.printStackTrace();
                }
            if (Build.VERSION.SDK_INT >= 24) {
                  imageUri = FileProvider.getUriForFile(GameActivity.this,
                    "in_time", outputImage);
             } else {
                    imageUri = Uri.fromFile(outputImage);
                    }
                    //启动相机
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, TAKE_PHOTO);
    }

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);  //打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch(requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this,"打开相册失败",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    try{
                        Bitmap bitmap = null;
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);

                        bitmap = BitmapFactory.decodeStream(inputStream);
                        puzzleGame.changeByAlbum(bitmap);

                        //将拍摄的图片已小图显示出来
                        displayImageByCamera(bitmap);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    //判断手机系统版本号
                    if(Build.VERSION.SDK_INT >= 19){
                        //4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    }else{
                        //4.4及以下系统使用这个方法处理图片
                        handleImageBeforKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        Bitmap bitmap;
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            //如果是document类型的Uri,则通过document id处理
            String docid = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docid.split(":")[1];          //解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contenUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docid));
                imagePath = getImagePath(contenUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        bitmap = BitmapFactory.decodeFile(imagePath);
        displayImage(bitmap);
        puzzleGame.changeByAlbum(bitmap);
    }

    private void handleImageBeforKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        displayImage(bitmap);
        new GameActivity().puzzleGame.changeByAlbum(bitmap);
    }

    private String getImagePath(Uri uri,String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri,null,selection,
                null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    //将相册选择的图片已小图的形式显示在界面上
    private void displayImage(Bitmap bitmap) {
        if(bitmap != null){
            srcImg.setImageBitmap(Utils.readInAlbum(bitmap,4));
        }else{
            Toast.makeText(GameActivity.this,"读取图片失败",Toast.LENGTH_SHORT).show();
        }
    }

    //将相机拍照的图片已小图的形式显示在界面上
    private void displayImageByCamera(Bitmap bitmap){
        if(bitmap != null){
            srcImg.setImageBitmap(Utils.readInAlbum(bitmap,4));
        }
    }

    private void mode(){
        //切换拼图模式，平移或交换
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    puzzleGame.changeMode(PuzzleLayout.GAME_MODE_NORMAL);
                }else{
                    puzzleGame.changeMode(PuzzleLayout.GAME_MODE_EXCHANGE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void setLevel(int level) {
        tvlevel.setText("难度等级" + level);
    }

    @Override
    public void gameSuccess(int level) {
        final SuccessDialog successDialog = new SuccessDialog();
        successDialog.show(getFragmentManager(),"successDialog");
        successDialog.addButtonClickListener(new SuccessDialog.OnButtonClickListener() {
            @Override
            public void nextLevelClick() {
                puzzleGame.addLevel();
                successDialog.dismiss();
            }

            @Override
            public void cancelClick() {
                successDialog.dismiss();
            }
        });
    }

    //设置toolbar
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.take_photo:
                Toast.makeText(this,"正在打开相机",Toast.LENGTH_SHORT).show();
                camera();
                break;
            case R.id.choose_photo:
                Toast.makeText(this,"正在打开相册",Toast.LENGTH_SHORT).show();
                photo();
                break;
            case R.id.up_level:
                if(puzzleGame.getLevel() == 5) {
                    Toast.makeText(this,"已经是最高等级",Toast.LENGTH_SHORT).show();
                    break;
                }else{
                    Toast.makeText(this,"游戏难度等级增加",Toast.LENGTH_SHORT).show();
                    puzzleGame.addLevel();
                    break;
                }
            case R.id.down_level:
                if(puzzleGame.getLevel() == 1) {
                    Toast.makeText(this,"已经是最低等级",Toast.LENGTH_SHORT).show();
                    break;
                }else{
                    Toast.makeText(this,"游戏难度等级降低",Toast.LENGTH_SHORT).show();
                    puzzleGame.reduceLevel();
                    break;
                }
            default:
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if(view == itemHome){

        }else if(view == itemPicture){
            photo();
        }else if(view == itemCamera){
            camera();
        }
            resideMenu.closeMenu();
    }

}
