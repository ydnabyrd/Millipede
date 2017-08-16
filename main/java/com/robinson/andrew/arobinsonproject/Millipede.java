package com.robinson.andrew.arobinsonproject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.widget.ArrayAdapter;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by Andy on 6/1/2017.
 */

public class Millipede implements Serializable{
    private int movementIncrement;
    private boolean isDead;
    private ArrayList<GamePiece> segments;
//constructor for starting game
    public Millipede(int numSegs){
        segments = new ArrayList<GamePiece>();
        this.movementIncrement = 1;
        for(int c = 0; c < numSegs; c++)
            segments.add(new GamePiece(0, c));
    }
//constructor for split operation
    private Millipede(ArrayList<GamePiece> segs, int movementIncrement){
        this.movementIncrement = movementIncrement;
        segments = new ArrayList<GamePiece>();
        for(int i = 0; i < segs.size(); i++)
            segments.add(segs.get(i));
    }

    public void moveMillipede(int [][]playField){
        for(int i = 0; i < segments.size(); i++)
            moveSegment(i, playField);
    }

    private void moveSegment(int segIndex, int[][] playField) {
        GamePiece seg = segments.get(segIndex);
        //check if not head
        if (segIndex < segments.size() - 1) {
            seg.setRow(segments.get(segIndex + 1).getRow());
            seg.setCol(segments.get(segIndex + 1).getCol());
        }

        else {//is head
            //check if next space is in playfield
            if (seg.getCol() + movementIncrement < playField[seg.getRow()].length &&
                    seg.getCol() + movementIncrement > -1) {
                //check if next space is clear
                if (playField[seg.getRow()][seg.getCol() + movementIncrement] == 0)
                    seg.setCol(seg.getCol() + movementIncrement);

                else {//next space has rock
                    movementIncrement = -1 * movementIncrement;
                    seg.setRow(seg.getRow() + 1);
                }
            }
            //at edge
            else if (seg.getCol() + movementIncrement == playField[seg.getRow()].length ||
                    seg.getCol() + movementIncrement == -1) {
                movementIncrement = -1 * movementIncrement;
                //another row is left
                if (seg.getRow() + 1 < playField.length) {
                    seg.setRow(seg.getRow() + 1);
                }
                //in bottom corner
                else
                    seg.setCol(seg.getCol() + movementIncrement);
            }
        }
    }

    public int checkCollision(int row, int col){
        for(int i = 0; i < segments.size(); i++)
            if(segments.get(i).getRow() == row && segments.get(i).getCol() == col)
                return i;

        return -1;
    }

    public Millipede split(int splitIndex){
        ArrayList<GamePiece> splitSegs = new ArrayList<GamePiece>();
        //find segment to split on
        int i;
        for(i = 0; i < splitIndex; i++)
            splitSegs.add(segments.get(i));

        segments.remove(splitIndex);

        if(i == segments.size() || i == 0){
            splitSegs = null;
        }

        else{
            segments.removeAll(splitSegs);
        }

        if(segments.size() == 0)
            isDead = true;

        if(splitSegs != null)
            return new Millipede(splitSegs, movementIncrement);
        return null;
    }

    public void draw(Resources resources, Canvas canvas, float dimension){
        Bitmap segment = BitmapFactory.decodeResource(resources, R.drawable.segment);

        for(int i = 0; i < segments.size(); i++){
            GamePiece seg = segments.get(i);
            RectF rect = new RectF(dimension * seg.getCol(), dimension * seg.getRow(),
                    dimension * seg.getCol() + dimension,
                    dimension * seg.getRow() + dimension);

            canvas.drawBitmap(segment, null, rect, null);
        }
    }

    public boolean isDead(){
        return isDead;
    }
}