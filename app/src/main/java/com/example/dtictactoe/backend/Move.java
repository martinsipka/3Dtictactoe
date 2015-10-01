package com.example.dtictactoe.backend;

/**
 * Created by Martin on 25. 9. 2015.
 */
public class Move {

    private int x, y, z, turn;

    public Move(int x, int y, int z, int turn){
        this.x = x;
        this.y = y;
        this.z = z;
        this.turn = turn;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getZ(){
        return z;
    }

    public int getTurn(){
        return turn;
    }
}
