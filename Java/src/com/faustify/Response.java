package com.faustify;

public class Response {
    private boolean canWin = false;
    private boolean canLose = false;
    private boolean canLoseNext = false;
    private int winnable = -1;
    private int losable = -1;
    private int score = -1;
    public Response(){

    }
    public Response(boolean canWin, boolean canLose, boolean canLoseNext, int winnable, int losable, int score) {
        this.canWin = canWin;
        this.canLose = canLose;
        this.canLoseNext = canLoseNext;
        this.winnable = winnable;
        this.losable = losable;
        this.score = score;
    }

    public boolean isCanWin() {
        return canWin;
    }

    public boolean isCanLose() {
        return canLose;
    }

    public boolean isCanLoseNext() {
        return canLoseNext;
    }

    public int getWinnable() {
        return winnable;
    }

    public int getLosable() {
        return losable;
    }

    public int getScore() {
        return score;
    }

    public Response setCanWin(boolean canWin) {
        this.canWin = canWin;
        return this;
    }

    public Response setCanLose(boolean canLose) {
        this.canLose = canLose;
        return this;
    }

    public Response setWinnable(int winnable) {
        this.winnable = winnable;
        return this;
    }

    public Response setLosable(int losable) {
        this.losable = losable;
        return this;
    }

    public Response setScore(int score) {
        this.score = score;
        return this;
    }
}
