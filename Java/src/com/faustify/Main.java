package com.faustify;

import java.util.Scanner;

public class Main {
    private static boolean display = true;
    public static void main(String[] args) {
	    if(args.length>=3){
	        if(args[1].equals("-d")){
	            if(args[2].equals("on")||args[2].equals("1")){
                    display = true;
                }else if(args[2].equals("off")||args[2].equals("0")){
                    display = false;
                }else{
	                System.out.println("display usage: -d on/off or -d 1/0 [DEFAULT IS ON]");
                }

            }
        }
	    System.out.println("Do I start first? [write y if bot starts first, n if player starts first]");
        Scanner sc = new Scanner(System.in);
        String inp = sc.nextLine().toLowerCase();
        boolean starF = false;
        if(inp.equals("n")){
            starF = true;
        }
        ConnectFourBot conFourBot = new SmartBot(starF);

        while(!conFourBot.isGameOver()){
            System.out.println("Choose your move 1 - 7:");
            int move = sc.nextInt();
            move--;
            try {
                System.out.println("BOT CHOSE "+(conFourBot.chooseNextMove(move)+1));
                if(display) {
                    conFourBot.displayBoard();
                }
            } catch (InvalidMove  invalidMove) {
                invalidMove.printStackTrace();
            } catch ( GameOverException e){
                System.out.println("Game finished!");
            }
        }
        System.out.print("GAME CONCLUSION: ");
        switch (conFourBot.getGameConclusion()){
            case -1:
                System.out.println("PLAYER WON!!!");
                break;
            case 1:
                System.out.println("BOT WON!!!");
                break;
            case 0:
                System.out.println("IT IS A TIE!!!");
                break;

        }
    }
}
