package com.faustify;

public class SmartBot implements ConnectFourBot{
    private int board[][]= new int[6][7];
    private int turns = 0;
    private boolean GameOver = false;
    private int gameConclusion = -2;
    private final int SCORE_FOR_2_YOUR_OWN = 1;
    private final int SCORE_FOR_3_YOUR_OWN = 7;
    private final int SCORE_FOR_2_OPPONENT = 4;
    private final int SCORE_FOR_3_OPPONENT = 15;
    private final int SCORE_FOR_WINNING = Integer.MAX_VALUE;
    private final int SCORE_FOR_PREVENTING_LOSS = 10000;
    private final int SCORE_FOR_CAUSING_LOSS_NEXT_TURN = -1000;
    private final int SCORE_FOR_MIDDLE = 10;
    private final int SCORE_FOR_SECOND_TO_MIDDLE = 2;

    /**
     * Constructor
     * @param startFist does the bot start first
     */
    public SmartBot(boolean startFist){
            for(int x=0; x<6; x++){
                board[x]=new int[7];
            }
            if(startFist){
                board[0][3] = 2;
                turns ++;
            }
    }

    /**
     * Method  that finds the position where the disc will fall to when placed in given column
     * Returns -1 in case there are no empty cells.
     * @param x column for which you want to find where the disc goes
     * @return the y (row) where the disc will fall (-1 if no valid cells in column)
     */
    private int validY (int x){
        if(!boundsCheck(4,x)||board[5][x]!=0) {
           // System.out.println("Invalid move");
            return -1;
        }
        for(int y = 0; y< 6; y++){
            if(board[y][x]==0){
                return y;
            }
        }
        return -1;
    }


    public boolean checkVictoryCondition(int y, int x){
        int disc = board[y][x];
        //down check
        ResponseCountDiscs r2 = getCount(-1, 0, y, x);
        if(r2.getCount()>=4) return true;
        if(r2.getCount()==3&&r2.getDisc()==disc) return true;
        //System.out.println(" r2 : "+r2.getDisc()+ " disc "+disc);

        // Diagonal 1 check
        r2 = getCount(-1, -1, y, x);
        ResponseCountDiscs r3 = getCount(+1, +1, y, x);
        if(victoryCheckerHelper(r2,r3,disc)) return true;
        // Diagonal 2 check
        r2 = getCount(+1, -1, y, x);
        r3 = getCount(-1, +1, y, x);
        if(victoryCheckerHelper(r2,r3,disc)) return true;

        // Left Right check
        r2 = getCount(0, -1, y, x);
        r3 = getCount(0, +1, y, x);
        return victoryCheckerHelper(r2, r3, disc);
    }

    private boolean victoryCheckerHelper(ResponseCountDiscs r2, ResponseCountDiscs r3, int disc){
        if(r2.getDisc()==r3.getDisc() && r2.getDisc()==disc){
            return r2.getCount() + r3.getCount() >= 4 || r2.getCount() + r3.getCount() == 3;

        }else{
            if(r2.getCount()>=4||r3.getCount() >= 4){
                return true;
            }

            if((r2.getCount()==3&&r2.getDisc()==disc)||(r3.getCount()==3&&r3.getDisc()==disc)){
                return true;
            }
        }
        return false;
    }

    public int chooseNextMove(int playerColumn) throws InvalidMove, GameOverException {
        if(GameOver){
            throw new GameOverException("GAME FINISHED");
        }
        if(playerColumn>6||playerColumn<0) throw new InvalidMove("invalid player move");
        int bestMove = -1;
        int bestY=-1;
        int bestScore = Integer.MIN_VALUE;
        int putY = validY(playerColumn);
        if(putY==-1) throw new InvalidMove("invalid player move");
        board[putY][playerColumn]=1;
        if(checkVictoryCondition(putY,playerColumn)){
            GameOver = true;
            gameConclusion=-1;
            throw new GameOverException("GAME FINISHED");
        }
        if(turns+1 >= 42){
            GameOver = true;
            gameConclusion=0;
            throw new GameOverException("GAME FINISHED");
        }
        for(int i = 0; i < 7; i++){
            putY = validY(i);
            if(putY!=-1){
                System.out.println("--------------- "+ (i+1)+" --------------");
                Response r = getScore(putY, i, 2, 1);
                System.out.println((i+1)+" has score "+r.getScore());
                if(r.isCanWin()){
                    board[putY][i]=2;
                    GameOver = true;
                    gameConclusion=1;
                    return i;
                }
                if(bestScore<r.getScore()){
                    bestMove = i;
                    bestScore = r.getScore();
                    bestY = putY;
                }
            }
        }
        turns+=2;

        board[bestY][bestMove]=2;
       // System.out.println("----- CHECKING VICTORY CONDITION ------");
        if(checkVictoryCondition(bestY,bestMove)){
            GameOver = true;
            gameConclusion=1;
            throw new GameOverException("GAME FINISHED");
        }
        if(turns>=42){
            GameOver = true;
            gameConclusion=0;
            throw new GameOverException("GAME FINISHED");
        }
        return bestMove;
    }

    /**
     * Bounds check on the board
     * @param y value for the board
     * @param x value for the board
     * @return true if the x,y cell is in the board, false otherwise
     */
    private boolean boundsCheck(int y, int x){
        return (y>-1) && (x>-1) && (y<6) && (x<7);
    }

    /**
     * Counts discs of same type in a given direction (excluding empty cells)
     * @param dy change in y
     * @param dx change in x
     * @param y initial y
     * @param x initial x
     * @return ResponseCountDiscs, which contains number of discs and second parameter, type of discs
     */
    private ResponseCountDiscs getCount(int dy, int dx, int y, int x){
        int count = 1;
        int disc;
        if(boundsCheck(y+dy, x+dx)){
            disc=board[y+dy][x+dx];
            if(disc == 0) return new ResponseCountDiscs(0,0);
        }else{
            return new ResponseCountDiscs(0,0);
        }
        y=y+dy+dy;
        x=x+dx+dx;

        while(boundsCheck(y,x) && board[y][x]==disc && count<4){
            count++;
            y=y+dy;
            x=x+dx;
           // System.out.print (count+" ");
        }
        //System.out.println("COUNTED: "+count+" FOR DICRECTION Y : "+dy +" X: "+dx);
        return new ResponseCountDiscs(disc, count);
    }

    /**
     * Method, which given certain ResponseCountDiscs calculates the score if we put a disc in the given column
     * @param r ResponseCountDiscs
     * @param disc disc type which we put
     * @param x the column where we place it
     * @return Response object, which contains the scores and flags whether we lose/win
     */
    private Response getScoreHelper(ResponseCountDiscs r, int disc, int x){
        if(r.getCount()>=3 && r.getDisc() == disc) {
            return new Response(true, false, false, x, -1, SCORE_FOR_WINNING);
        } else if(r.getCount()>=3 && r.getDisc() != disc) {
            return new Response(false, true, false, x, -1, SCORE_FOR_PREVENTING_LOSS);
        } else if(r.getCount()==2 && r.getDisc() == disc) {
            return new Response(false, false, false, 1, -1, SCORE_FOR_3_YOUR_OWN);
        } else if(r.getCount()==2 && r.getDisc() != disc) {
            return new Response(false, false, false, 1, -1, SCORE_FOR_3_OPPONENT);
        } else if(r.getCount()==1 && r.getDisc() == disc) {
            return new Response(false, false, false, -1, -1, SCORE_FOR_2_YOUR_OWN);
        } else if(r.getCount()==1 && r.getDisc() != disc) {
            return new Response(false, false, false, -1, -1, SCORE_FOR_2_OPPONENT);
        }else if(r.getCount()<=0) {
            return new Response(false, false, false, -1, -1, 0);
        }
        return null;
    }


    public Response getScore(int y, int x, int disc, int otherDisc){
        Response r = getScoreRec(y,x,disc,otherDisc);
        if(r.isCanWin()) return r;
        if(y<5) {
            Response r2 = getScoreRec(y + 1, x, otherDisc, disc);
            if(r2.isCanWin()) return r.setScore(r.getScore()+SCORE_FOR_CAUSING_LOSS_NEXT_TURN);
            return r;
        } else {
            return r;
        }
    }


    private Response getScoreRec(int y, int x, int disc, int otherDisc){
        int score = 0;
        //down check
        Response r = getScoreHelper(getCount(-1, 0, y, x), disc, x);
        if(r.isCanWin()) return r;
        score += r.getScore();
        System.out.println("DOWN SCORE "+r.getScore());
        // Diagonal 1 check
        ResponseCountDiscs r2 = getCount(-1, -1, y, x);
        ResponseCountDiscs r3 = getCount(+1, +1, y, x);
        if(r2.getDisc()==r3.getDisc()){
            r = getScoreHelper(new ResponseCountDiscs(r2.getDisc(), r2.getCount()+r3.getCount()), disc, x);
            if(r.isCanWin()) return r;
            score += r.getScore();
            System.out.println("DIAGONAL 1 "+r.getScore());
        }else{
            r = getScoreHelper(r2, disc, x);
            if(r.isCanWin()) return r;
            score += r.getScore();
            int buff = r.getScore();

            r = getScoreHelper(r3, disc, x);
            if(r.isCanWin()) return r;
            score += r.getScore();
            System.out.println("DIAGONAL 1 "+(buff+r.getScore()));
        }
        // Diagonal 2 check
        r2 = getCount(+1, -1, y, x);
        r3 = getCount(-1, +1, y, x);
        if(r2.getDisc()==r3.getDisc()){
            r = getScoreHelper(new ResponseCountDiscs(r2.getDisc(), r2.getCount()+r3.getCount()), disc, x);
            if(r.isCanWin()) return r;
            score += r.getScore();
            System.out.println("DIAGONAL 2 "+r.getScore());
        }else{
            r = getScoreHelper(r2, disc, x);
            if(r.isCanWin()) return r;
            score += r.getScore();
            int buff = r.getScore();
            r = getScoreHelper(r3, disc, x);
            if(r.isCanWin()) return r;
            score += r.getScore();
            System.out.println("DIAGONAL 2 "+(buff+r.getScore()));
        }
        // Left Right check
        r2 = getCount(0, -1, y, x);
        r3 = getCount(0, +1, y, x);
        if(r2.getDisc()==r3.getDisc()){
            r = getScoreHelper(new ResponseCountDiscs(r2.getDisc(), r2.getCount()+r3.getCount()), disc, x);
            if(r.isCanWin()) return r;
            score += r.getScore();
            System.out.println("LR "+r.getScore());
        }else{
            r = getScoreHelper(r2, disc, x);
            if(r.isCanWin()) return r;
            score += r.getScore();
            int buff = r.getScore();
            r = getScoreHelper(r3, disc, x);
            if(r.isCanWin()) return r;
            score += r.getScore();
            System.out.println("LR "+(buff+r.getScore()));
        }
        if(x==3) score+=SCORE_FOR_MIDDLE;
        if(x==2||x==4) score+=SCORE_FOR_SECOND_TO_MIDDLE;
        return new Response(false, false, false, -1, -1, score);
    }


    public void displayBoard(){
        for(int y=12; y>-1; y--){
            for (int x =0; x<=21; x++){
                if(y%2==0){
                    System.out.print(ANSI_WHITE+"█");

                }else {
                    if(x%3==0){
                        System.out.print(ANSI_WHITE+"█");
                    }else{
                        if(board[y/2][x/3]==0){
                            System.out.print(ANSI_WHITE+" ");
                        }else if(board[y/2][x/3]==1){
                            System.out.print(ANSI_BLUE+"█");
                        }else{
                            System.out.print(ANSI_RED+"█");
                        }

                    }

                }
            }
            System.out.println();
        }
        System.out.println(ANSI_WHITE+" 1  2  3  4   5  6  7");
    }

    public int getGameConclusion() {
        return gameConclusion;
    }

    public boolean isGameOver() {
        return GameOver;
    }


    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_WHITE = "\u001B[37m";
}
