package com.robinson.andrew.arobinsonproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Andy on 5/24/2017.
 */

public class Game extends View
implements View.OnClickListener, View.OnTouchListener, MediaPlayer.OnCompletionListener{
    private int[][] playField;
    private int numSegs;
    private int numRocks;
    private int numLives;
    private int livesRemaining;
    private int dimension;
    private int timerTickNum;
    private int points;
    private int clipID;
    private boolean gameInProgress;
    private boolean hasBeenSetup;
    private boolean moveBlasterRight;
    private boolean moveBlasterLeft;
    private ArrayList<Millipede> millipedes;
    private GamePiece blaster;
    private GamePiece projectile;
    private MediaPlayer mp;
    private final Handler handler = new Handler();
    private final Runnable timer = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, 15);
            move();
            invalidate();

        }
    };

    public Game(Context context) {
        super(context);
        initVals();
    }

    public Game(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initVals();
    }

    public Game(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVals();
    }

    private void initVals(){
        playField = new int[24][24];
        millipedes = new ArrayList<Millipede>();
        blaster = new GamePiece(23, 12);
        mp = new MediaPlayer();
        mp.setOnCompletionListener(this);
    }

    @Override
    public Parcelable onSaveInstanceState(){
        super.onSaveInstanceState();
        Bundle b = new Bundle();

        for(int i = 0; i < 24; i++){
            b.putIntArray("playfield" + i, playField[i]);
        }
        b.putInt("numlives", numLives);
        b.putInt("points", points);
        b.putSerializable("millipedes", millipedes);
        b.putSerializable("blaster", blaster);
        b.putSerializable("projectile", projectile);
        b.putBoolean("setup", hasBeenSetup);
        return b;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state){
        super.onRestoreInstanceState(state);
        Bundle b = (Bundle) state;
        if(playField == null)
            playField = new int[24][24];
        for(int i = 0; i < 24; i++)
            playField[i] = b.getIntArray("playfield" + i);
        numLives = b.getInt("numlives");
        points = b.getInt("points");
        millipedes = (ArrayList<Millipede>) b.getSerializable("millipedes");
        blaster = (GamePiece) b.getSerializable("blaster");
        projectile = (GamePiece) b.getSerializable("projectile");
        hasBeenSetup = b.getBoolean("setup");
    }

    public void setPreferences(int numSegs, int numRocks, int numLives){
        if(this.numSegs != numSegs || this.numRocks != numRocks ||
                this.numLives != numLives) {
            this.numSegs = numSegs;
            this.numRocks = numRocks;
            this.numLives = livesRemaining = numLives;
            reset(true);
            invalidate();
        }
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        this.dimension = w;
    }

    @Override
    public void onMeasure (int widthMeasureSpec, int heightMeasureSpec){
        int actWidth = MeasureSpec.getSize(widthMeasureSpec);
        int actHeight = MeasureSpec.getSize(heightMeasureSpec);
        float actAspectRatio = ((float) actWidth) / actHeight;
        if(actAspectRatio > 1)
            setMeasuredDimension(actHeight, actHeight);
        else
            setMeasuredDimension(actWidth, actWidth);
    }

    @Override
    public void onDraw(Canvas canvas){
        drawRocks(canvas);
        for(Millipede mil : millipedes)
            mil.draw(getResources(), canvas, dimension / 24f);
        drawBlaster(canvas);
        drawProjectile(canvas);
    }

    private void drawRocks(Canvas canvas){
        float spaceDimension = dimension / 24f;
        for(int r = 1; r < playField.length; r++) {
            for (int c = 0; c < playField[r].length; c++) {
                Bitmap bitmap = null;
                if (playField[r][c] == 4)
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rock4);
                else if (playField[r][c] == 3)
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rock3);
                else if (playField[r][c] == 2)
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rock2);
                else if (playField[r][c] == 1)
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rock1);
                if (bitmap != null) {
                    RectF rect = new RectF(spaceDimension * c, spaceDimension * r,
                            spaceDimension * c + spaceDimension,
                            spaceDimension * r + spaceDimension);
                    canvas.drawBitmap(bitmap, null, rect, null);
                }
            }
        }
    }

    private void drawBlaster(Canvas canvas){
        float spaceDimension = dimension / 24f;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.launcher2);
        RectF rect = new RectF(spaceDimension * blaster.getCol(), spaceDimension * blaster.getRow(),
                spaceDimension * blaster.getCol() + spaceDimension,
                spaceDimension * blaster.getRow() + spaceDimension);
        canvas.drawBitmap(bitmap, null, rect, null);
    }

    private void drawProjectile(Canvas canvas){
        if(projectile != null){
            float spaceDimension = dimension / 24f;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bullet);
            RectF rect = new RectF(spaceDimension * projectile.getCol(),
                    spaceDimension * projectile.getRow(),
                    spaceDimension * projectile.getCol() + spaceDimension,
                    spaceDimension * projectile.getRow() + spaceDimension);
            canvas.drawBitmap(bitmap, null, rect, null);
        }
    }

    public void setup(){
        if(hasBeenSetup)
            return;
        playField = new int[24][24];
        int rocksPlaced = 0;
        while(rocksPlaced < numRocks) {
            for (int r = 1; r < playField.length - 1; r++)
                for (int c = 0; c < playField[r].length; c++) {
                    if (Math.random() < .01 && numRocks > rocksPlaced &&
                            playField[r][c] == 0) {
                        playField[r][c] = 4;
                        rocksPlaced++;
                    }
                }
        }
        millipedes.clear();
        millipedes.add(new Millipede(numSegs));
        hasBeenSetup = true;
    }

    @Override
    public void onClick(View v) {
        if(gameInProgress && v.getId() == R.id.crosshair){
            if(projectile == null){
                projectile = new GamePiece(blaster.getRow() - 1, blaster.getCol());
                checkCollisions();
            }
        }
    }

    private void move(){
        timerTickNum = (timerTickNum + 1) % 2;
        if(timerTickNum == 0) {
            playClip(R.raw.walk);
            for (Millipede milli : millipedes)
                milli.moveMillipede(playField);
        }

        if(projectile != null) {
            projectile.moveUp(1);
            if (projectile.getRow() < 0)
                projectile = null;
        }
        if(moveBlasterLeft && blaster.getCol() > 0)
            blaster.moveLeft(1);
        if(moveBlasterRight && blaster.getCol() < playField[0].length - 1)
            blaster.moveRight(1);
        checkCollisions();
    }

    private void checkCollisions(){
        if(projectile != null) {
            if(playField[projectile.getRow()][projectile.getCol()] > 0) {
                playField[projectile.getRow()][projectile.getCol()]--;
                projectile = null;
                points += 5;
                TextView scoreText = (TextView) ((MainActivity) getContext()).findViewById(R.id.scoreText);
                scoreText.setText("Score: " + points);
                playClip(R.raw.rockhit);
            }

            else {
                for (int i = millipedes.size() -1; i > -1; i--) {
                    Millipede milli = millipedes.get(i);
                    int splitIndex = milli.checkCollision(projectile.getRow(), projectile.getCol());
                    if (splitIndex != -1) {
                        playClip(R.raw.seghit);
                        playField[projectile.getRow()][projectile.getCol()] = 4;
                        Millipede newMil = milli.split(splitIndex);
                        if (milli.isDead()) {
                            millipedes.remove(milli);
                            playClip(R.raw.snakedie);
                        }
                        if (newMil != null)
                            millipedes.add(newMil);
                        projectile = null;
                        points += 10;
                        TextView scoreText = (TextView) ((MainActivity) getContext()).findViewById(R.id.scoreText);
                        scoreText.setText("Score: " + points);
                        if(millipedes.size() == 0)
                            reset(false);
                        i = -1;
                    }
                }
            }
        }

        for(int i = millipedes.size() - 1; i > -1; i--){
            if(millipedes.get(i).checkCollision(blaster.getRow(), blaster.getCol()) != -1){
                livesRemaining--;
                playClip(R.raw.fatality);
                reset(livesRemaining == 0);
                return;
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(gameInProgress) {
            if (v.getId() == R.id.rightArrow) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    moveBlasterRight = true;
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    moveBlasterRight = false;
            }

            else if (v.getId() == R.id.leftArrow) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    moveBlasterLeft = true;
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    moveBlasterLeft = false;
            }
            return true;
        }
        return false;
    }

    private void reset(boolean gameOver){
        MainActivity mainActivity = (MainActivity) getContext();
        if(gameOver){
            Toast.makeText(mainActivity, "Game Over", Toast.LENGTH_SHORT).show();
            points = 0;
            TextView scoreText = (TextView) mainActivity.findViewById(R.id.scoreText);
            scoreText.setText("Score: " + points);
            livesRemaining = numLives;
        }

        TextView livesText = (TextView) mainActivity.findViewById(R.id.livesText);
        livesText.setText("Lives: " + livesRemaining);
        hasBeenSetup = false;
        gameInProgress = true;
        pause_Play();
    }

    public void pause_Play(){
        gameInProgress = !gameInProgress;
        if(!hasBeenSetup)
            setup();
        if(gameInProgress)
            timer.run();
        else
            handler.removeCallbacks(timer);
    }

    public void pause(){
        gameInProgress = false;
        handler.removeCallbacks(timer);
    }

    private void playClip(int id) {
        if (mp!=null && id==clipID) {
            mp.pause();
            mp.seekTo(0);
            mp.start();
        }

        else {
            if (mp!=null) mp.release() ;
            clipID = id ;
            mp = MediaPlayer.create(getContext(), id) ;
            mp.setOnCompletionListener(this) ;
            mp.setVolume(0.6f,0.6f) ;
            mp.start() ;
        }
    }

    @Override
    public void onCompletion(MediaPlayer amp) {
        amp.release();
        mp = null;
    }
}
