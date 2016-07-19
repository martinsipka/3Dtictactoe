package com.example.dtictactoe.AI;

public class ScoreCheck {

    private int[][][] a;
    private int[][] winningCombination = new int[4][3];

    /*Check for winning. Players have their values: 1 or 5 so when 4 or 20 is sum of near fields
    * then we have a winner. Checking only neighbour fields with fastest possible loop.
    */
    int s1 = 0, s2 = 0, s3 = 0, s4 = 0, s5 = 0, s6 = 0, s7 = 0;


    public ScoreCheck(int[][][] a){
        this.a = a;
    }

    public int check(int x, int y, int z) {
        int result = 0;
        // seven possible win lines.
        s1 = 0;
        s2 = 0;
        s3 = 0;
        s4 = 0;
        s5 = 0;
        s6 = 0;
        s7 = 0;

        for (int k = 0; k < 4; k++) {
            s1 = s1 + a[k][y][z];
            s2 = s2 + a[x][k][z];
            s3 = s3 + a[x][y][k];
            if (x == y) {
                s4 = s4 + a[k][k][z];
            } else if (x == 3 - y) {
                s4 = s4 + a[k][3 - k][z];
            }
            if (y == z) {
                s5 = s5 + a[x][k][k];
            } else if (y == 3 - z) {
                s5 = s5 + a[x][k][3 - k];
            }
            if (x == z) {
                s6 = s6 + a[k][y][k];
            } else if (x == 3 - z) {
                s6 = s6 + a[k][y][3 - k];
            }
            if (x == y && x == z) {
                s7 = s7 + a[k][k][k];
            } else if (x == y && z == 3 - x) {
                s7 = s7 + a[k][k][3 - k];
            } else if (x == z && y == 3 - x) {
                s7 = s7 + a[k][3 - k][k];
            } else if (y == z && x == 3 - y) {
                s7 = s7 + a[3 - k][k][k];
            }

        }
        if (s1 == 4 | s2 == 4 | s3 == 4 | s4 == 4 | s5 == 4 | s6 == 4 | s7 == 4) {
            result = 1;
            findWinner(x, y, z, 4);
        } else if (s1 == 20 | s2 == 20 | s3 == 20 | s4 == 20 | s5 == 20 | s6 == 20 | s7 == 20) {
            result = 2;
            findWinner(x, y, z, 20);
        }

        return result;

    }

    public void findWinner(int x, int y, int z, int result){
        if(s1 == result) {
            for(int i = 0; i < 4; i++){
                winningCombination[i][0] = i;
                winningCombination[i][1] = y;
                winningCombination[i][2] = z;
            }
        } else if(s2 == result) {
            for(int i = 0; i < 4; i++){
                winningCombination[i][0] = x;
                winningCombination[i][1] = i;
                winningCombination[i][2] = z;
            }
        } else if(s3 == result) {
            for(int i = 0; i < 4; i++){
                winningCombination[i][0] = x;
                winningCombination[i][1] = y;
                winningCombination[i][2] = i;
            }
        } else if(s4 == result) {
            if (x == y) {
                for(int i = 0; i < 4; i++){
                    winningCombination[i][0] = i;
                    winningCombination[i][1] = i;
                    winningCombination[i][2] = z;
                }
            } else if (x == 3 - y) {
                for(int i = 0; i < 4; i++){
                    winningCombination[i][0] = i;
                    winningCombination[i][1] = 3-i;
                    winningCombination[i][2] = z;
                }
            }
        } else if(s5 == result) {
            if (y == z) {
                for(int i = 0; i < 4; i++){
                    winningCombination[i][0] = x;
                    winningCombination[i][1] = i;
                    winningCombination[i][2] = i;
                }
            } else if (y == 3 - z) {
                for(int i = 0; i < 4; i++){
                    winningCombination[i][0] = x;
                    winningCombination[i][1] = i;
                    winningCombination[i][2] = 3-i;
                }
            }
        } else if(s6 == result) {
            if (x == z) {
                for(int i = 0; i < 4; i++){
                    winningCombination[i][0] = i;
                    winningCombination[i][1] = y;
                    winningCombination[i][2] = i;
                }
            } else if (x == 3 - z) {
                for(int i = 0; i < 4; i++){
                    winningCombination[i][0] = i;
                    winningCombination[i][1] = y;
                    winningCombination[i][2] = 3-i;
                }
            }
        } else if(s7 == result) {
            if (x == y && x == z) {
                for (int i = 0; i < 4; i++) {
                    winningCombination[i][0] = i;
                    winningCombination[i][1] = i;
                    winningCombination[i][2] = i;
                }
            } else if (x == y && z == 3 - x) {
                for (int i = 0; i < 4; i++) {
                    winningCombination[i][0] = i;
                    winningCombination[i][1] = i;
                    winningCombination[i][2] = 3 - i;
                }
            } else if (x == z && y == 3 - x) {
                for (int i = 0; i < 4; i++) {
                    winningCombination[i][0] = i;
                    winningCombination[i][1] = 3 - i;
                    winningCombination[i][2] = i;
                }
            } else if (y == z && x == 3 - y) {
                for (int i = 0; i < 4; i++) {
                    winningCombination[i][0] = 3 - i;
                    winningCombination[i][1] = i;
                    winningCombination[i][2] = i;
                }
            }
        }
    }

    public int[][] getWinningCombination(){
        return winningCombination;
    }

    public void updateTable(int[][][] playBoard){
        a = playBoard;
    }

}
