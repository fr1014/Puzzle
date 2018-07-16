package activitytest.example.com.puzzle;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

import activitytest.example.com.puzzle.gameactivity.GameActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ResideMenu resideMenu;
    private ResideMenu.OnMenuListener menuListener;
    private ResideMenuItem itemHome1;
    private ResideMenuItem itemGame;
    private ResideMenuItem itemRanking;
    private ResideMenuItem itemHome2;
    private ImageView imageView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        initview1();
        initview2();
    }

    private void initview1() {
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.fly);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        itemHome1 = new ResideMenuItem(this,R.drawable.home2,"主页");
        itemHome2 = new ResideMenuItem(this,R.drawable.home2,"主页");
        itemGame = new ResideMenuItem(this,R.drawable.play,"进行游戏");
        itemRanking = new ResideMenuItem(this,R.drawable.ranking, "排行榜（当然还没做好）");

        itemHome1.setOnClickListener(this);
        itemGame.setOnClickListener(this);
        itemRanking.setOnClickListener(this);
        itemHome2.setOnClickListener(this);

        resideMenu.addMenuItem(itemHome1,  ResideMenu.DIRECTION_LEFT); // or  ResideMenu.DIRECTION_RIGHT
        resideMenu.addMenuItem(itemGame,  ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemRanking,  ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemHome2,  ResideMenu.DIRECTION_RIGHT);

        menuListener = new ResideMenu.OnMenuListener() {
            @Override
            public void openMenu() {
                Toast.makeText(MainActivity.this, "菜单打开", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void closeMenu() {
                Toast.makeText(MainActivity.this, "菜单关闭", Toast.LENGTH_SHORT).show();
            }
        };
    }

    //使侧滑栏支持手势滑动
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }


    private void initview2() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        ImageView imageView = (ImageView)findViewById(R.id.image_layout);
        imageView1 = (ImageView)findViewById(R.id.imageView3);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.zhizhu));
        imageView1.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        if(view == imageView1 || view == itemGame){
            Intent intent = new Intent(MainActivity.this,GameActivity.class);
            startActivity(intent);
        }else if(view == itemRanking){

        }
        resideMenu.closeMenu();
    }

}
