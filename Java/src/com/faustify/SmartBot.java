package com.faustify;

public class SmartBot implements ConnectFourBot{
    private int board[][]= new int[6][7];
    private int turns = 0;
    private boolean GameOver = false;
    private int gameConclusion = -2;
    public SmartBot(boolean startFist){
            for(int x=0; x<6; x++){
                board[x]=new int[7];
            }
            if(startFist){
                board[0][3] = 2;
                turns ++;
            }
    }
    private int validY (int x){
        if(board[5][x]!=0) return -1;
        for(int i = 0; i< 6; i++){
            if(board[i][x]==0){
                return i;
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
        if(r2.getDisc()==r3.getDisc()){
            if(r2.getCount()+r3.getCount() >= 4){
                return true;
            }
            if(r2.getCount()+r3.getCount() == 3 && r2.getDisc()==disc){
                return true;
            }
        }else{
            if(r2.getCount()>=4||r3.getCount() >= 4){
                return true;
            }

            if((r2.getCount()==3&&r2.getDisc()==disc)||(r3.getCount()==3&&r3.getDisc()==disc)){
                return true;
            }
        }
        // Diagonal 2 check
        r2 = getCount(+1, -1, y, x);
        r3 = getCount(-1, +1, y, x);
        if(r2.getDisc()==r3.getDisc()){
            if(r2.getCount()+r3.getCount() >= 4){
                return true;
            }
            if(r2.getCount()+r3.getCount() == 3 && r2.getDisc()==disc){
                return true;
            }
        }else{
            if(r2.getCount()>=4||r3.getCount() >= 4){
                return true;
            }

            if((r2.getCount()==3&&r2.getDisc()==disc)||(r3.getCount()==3&&r3.getDisc()==disc)){
                return true;
            }
        }
        // Left Right check
        r2 = getCount(0, -1, y, x);
        r3 = getCount(0, +1, y, x);
        if(r2.getDisc()==r3.getDisc()){
            if(r2.getCount()+r3.getCount() >= 4){
                return true;
            }
            if(r2.getCount()+r3.getCount() == 3 && r2.getDisc()==disc){
                return true;
            }
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
            gameConclusion=0;
            throw new GameOverException("GAME FINISHED");
        }
        for(int i = 0; i < 7; i++){
            putY = validY(i);
            if(putY!=-1){
                Response r = getScore(putY, i, 2, 1);
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
        return bestMove;
    }
    private boolean boundsCheck(int y, int x){
        return (y>-1) && (x>-1) && (y<6) && (x<7);
    }

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
    private Response getScoreHelper(ResponseCountDiscs r, int disc, int x){
        if(r.getCount()>=3 && r.getDisc() == disc) {
            return new Response(true, false, false, x, -1, Integer.MAX_VALUE);
        } else if(r.getCount()>=3 && r.getDisc() != disc) {
            return new Response(false, true, false, x, -1, 10000);
        } else if(r.getCount()==2 && r.getDisc() == disc) {
            return new Response(false, false, false, 1, -1, 5);
        } else if(r.getCount()==2 && r.getDisc() != disc) {
            return new Response(false, false, false, 1, -1, 6);
        } else if(r.getCount()==1 && r.getDisc() == disc) {
            return new Response(false, false, false, -1, -1, 1);
        } else if(r.getCount()==1 && r.getDisc() != disc) {
            return new Response(false, false, false, -1, -1, 2);
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
            if(r2.isCanWin()) return r.setScore(r.getScore()-1000);
            return r;
        } else{
            return r;
        }
    }
    private Response getScoreRec(int y, int x, int disc, int otherDisc){
        int score = 0;
        //down check
        Response r = getScoreHelper(getCount(-1, 0, y, x), disc, x);
        if(r.isCanWin()) return r;
        score += r.getScore();

        // Diagonal 1 check
        ResponseCountDiscs r2 = getCount(-1, -1, y, x);
        ResponseCountDiscs r3 = getCount(+1, +1, y, x);
        if(r2.getDisc()==r3.getDisc()){
            r = getScoreHelper(new ResponseCountDiscs(r2.getDisc(), r2.getCount()+r3.getCount()), disc, x);
            if(r.isCanWin()) return r;
            score += r.getScore();
        }else{
            r = getScoreHelper(r2, disc, x);
            if(r.isCanWin()) return r;
            score += r.getScore();

            r = getScoreHelper(r3, disc, x);
            if(r.isCanWin()) return r;
            score += r.getScore();
        }
        // Diagonal 2 check
        r2 = getCount(+1, -1, y, x);
        r3 = getCount(-1, +1, y, x);
        if(r2.getDisc()==r3.getDisc()){
            r = getScoreHelper(new ResponseCountDiscs(r2.getDisc(), r2.getCount()+r3.getCount()), disc, x);
            if(r.isCanWin()) return r;
            score += r.getScore();
        }else{
            r = getScoreHelper(r2, disc, x);
            if(r.isCanWin()) return r;
            score += r.getScore();

            r = getScoreHelper(r3, disc, x);
            if(r.isCanWin()) return r;
            score += r.getScore();
        }
        // Left Right check
        r2 = getCount(0, -1, y, x);
        r3 = getCount(0, +1, y, x);
        if(r2.getDisc()==r3.getDisc()){
            r = getScoreHelper(new ResponseCountDiscs(r2.getDisc(), r2.getCount()+r3.getCount()), disc, x);
            if(r.isCanLose()||r.isCanWin()) return r;
            score += r.getScore();
        }else{
            r = getScoreHelper(r2, disc, x);
            if(r.isCanLose()||r.isCanWin()) return r;
            score += r.getScore();

            r = getScoreHelper(r3, disc, x);
            if(r.isCanLose()||r.isCanWin()) return r;
            score += r.getScore();
        }
        if(x==3) score+=6;
        if(x==2||x==4) score+=2;
        return new Response(false, false, false, -1, -1, score);
    }
    public void displayBoard(){
        for(int y=5; y>-1; y--){
            for (int x =0; x<7; x++){
                System.out.print(board[y][x]);
            }
            System.out.println();
        }
    }

    public int getGameConclusion() {
        return gameConclusion;
    }

    public boolean isGameOver() {
        return GameOver;
    }
}
