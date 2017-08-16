package com.robinson.andrew.arobinsonproject;

import java.io.Serializable;

/**
 * Created by Andy on 6/3/2017.
 */

public class GamePiece implements Serializable{
    private int row;
    private int col;

    public GamePiece(int row, int col){
        this.row = row;
        this.col = col;
    }

    public void moveLeft(int distance){
        col -= distance;
    }

    public void moveRight(int distance){
        col += distance;
    }

    public void moveDown(int distance){
        row += distance;
    }

    public void moveUp(int distance){
        row -= distance;
    }

    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }

    public void setRow(int row){
        this.row = row;
    }

    public void setCol(int col){
        this.col = col;
    }
    public boolean collidesWith(GamePiece gamePiece){
        return (this.col == gamePiece.getCol() && this.row == gamePiece.getRow());
    }
}
