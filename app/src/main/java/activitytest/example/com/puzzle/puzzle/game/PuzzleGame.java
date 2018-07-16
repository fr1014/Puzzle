package activitytest.example.com.puzzle.puzzle.game;

import android.content.Context;
import android.graphics.Bitmap;

import activitytest.example.com.puzzle.puzzle.ui.PuzzleLayout;

public class PuzzleGame implements Game,PuzzleLayout.SuccessListener {

    private PuzzleLayout puzzleLayout;
    private  GameStateListener stateListener;
    private Context context;

    public void addGameStateListener(GameStateListener stateListener){
        this.stateListener = stateListener;
    }

    public PuzzleGame(Context context, PuzzleLayout puzzleLayout){
        this.context = context.getApplicationContext();
        this.puzzleLayout = puzzleLayout;
        puzzleLayout.addSuccessListener(this);
    }

    private boolean checkNull(){
        return puzzleLayout == null;
    }

    @Override
    public void addLevel() {
        if(checkNull()){
            return;
        }
        puzzleLayout.addCount();
        if(stateListener != null){
            stateListener.setLevel(getLevel());
        }
    }

    @Override
    public void reduceLevel() {
        if(checkNull()){
            return;
        }
        puzzleLayout.reduceCount();
        if(stateListener != null){
            stateListener.setLevel(getLevel());
        }
    }

    @Override
    public void changeMode(String gameMode) {
        puzzleLayout.changeMode(gameMode);
    }

    @Override
    public void changeByAlbum(Bitmap bitmap) {
        puzzleLayout.changeByAlbum(bitmap);
    }

    public int getLevel() {
        if(checkNull()){
            return 0;
        }
        int count = puzzleLayout.getCount();
        return count - 3 + 1;
    }

    @Override
    public void success() {
        if(stateListener != null){
            stateListener.gameSuccess(getLevel());
        }
    }

    public interface GameStateListener{
        void setLevel(int level);

        void gameSuccess(int level);
    }
}
