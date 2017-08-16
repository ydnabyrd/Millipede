package com.robinson.andrew.arobinsonproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Game gameBoard = (Game) findViewById(R.id.gameBoard);
        ImageView rightArrow = (ImageView) findViewById(R.id.rightArrow);
        rightArrow.setOnTouchListener(gameBoard);
        ImageView leftArrow = (ImageView) findViewById(R.id.leftArrow);
        leftArrow.setOnTouchListener(gameBoard);
        ImageView crosshair = (ImageView) findViewById(R.id.crosshair);
        crosshair.setOnClickListener(gameBoard);
    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            Toast.makeText(this,
                    "Quarter Project, Spring 2017, Andrew Robinson",
                    Toast.LENGTH_SHORT)
                    .show();
            return true;
        }

        if(id == R.id.action_settings){
            Intent intent = new Intent(this, SettingsActivity.class) ;
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int numSegs = Integer.parseInt(sharedPreferences.getString("numSegs", "10"));
        int numRocks = Integer.parseInt(sharedPreferences.getString("numRocks", "15"));
        int numLives = Integer.parseInt(sharedPreferences.getString("numLives", "3"));
        Game game = (Game) findViewById(R.id.gameBoard);
        game.setPreferences(numSegs, numRocks, numLives);
        game.setup();
    }

    @Override
    public void onPause(){
        super.onPause();
        Game gameBoard = (Game) findViewById(R.id.gameBoard);
        gameBoard.pause();
    }

    public void onGameBoard_Click(View v){
        Game gameBoard = (Game) v;
        gameBoard.pause_Play();
    }
}
