package sk.martin.tictactoe.backend;

/**
 * Created by Martin on 25. 9. 2015.
 */
public class Move {

    private int x, y, z, turn;

    public Move(){

    }

    public Move(int x, int y, int z, int turn){
        this.x = x;
        this.y = y;
        this.z = z;
        this.turn = turn;
    }

    public void setX(int x) {this.x  = x;}

    public int getX(){
        return x;
    }

    public void setY(int y) {this.y  = y;}

    public int getY(){
        return y;
    }

    public void setZ(int z) {this.z  = z;}

    public int getZ(){
        return z;
    }

    public int getTurn(){
        return turn;
    }
}
