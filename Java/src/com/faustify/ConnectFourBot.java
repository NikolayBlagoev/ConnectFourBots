package com.faustify;

public interface ConnectFourBot {
    public boolean checkVictoryCondition(int y, int x);
    public int chooseNextMove(int playerColumn) throws InvalidMove, GameOverException;
    public int getGameConclusion();
    public boolean isGameOver();
    public void displayBoard();
}
