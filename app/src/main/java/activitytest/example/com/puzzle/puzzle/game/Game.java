package activitytest.example.com.puzzle.puzzle.game;

import android.graphics.Bitmap;

public interface Game {

    /**
     * 增加难度
     */
    public void addLevel();

    /**
     * 减少难度
     */
    public void reduceLevel();

    /**
     * 修改游戏模式
     */
    public void changeMode(String gameMode);

//    /**
//     * 修改图片
//     */
//    public void changeImage(String imagePath);


    void changeByAlbum(Bitmap bitmap);
}
