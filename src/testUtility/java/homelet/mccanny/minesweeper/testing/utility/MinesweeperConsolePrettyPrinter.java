package homelet.mccanny.minesweeper.testing.utility;

import homelet.mccanny.minesweeper.model.game.Block;
import homelet.mccanny.minesweeper.model.game.MinesweeperGame;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.BiFunction;

public class MinesweeperConsolePrettyPrinter {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_WHITE_BRIGHT = "\033[0;97m";
    private static final String ANSI_WHITE_BOLD = "\033[1;37m";

    private static final String HIDDEN_CHAR = " ";
    private static final String UNREVEALED_CHAR = ANSI_WHITE_BRIGHT + "?" + ANSI_RESET;
    private static final String UNREVEALED_MINE_CHAR = ANSI_WHITE_BRIGHT + "*" + ANSI_RESET;
    private static final String UNREVEALED_FLAG_CHAR = ANSI_WHITE_BRIGHT + "#" + ANSI_RESET;
    private static final String FLAG_CHAR = ANSI_BLUE + "#" + ANSI_RESET;
    private static final String FLAG_CORRECT_CHAR = ANSI_GREEN + "#" + ANSI_RESET;
    private static final String FLAG_INCORRECT_CHAR = ANSI_RED + "#" + ANSI_RESET;
    private static final String MINE_CHAR = ANSI_RED + "*" + ANSI_RESET;

    private final MinesweeperGame game;

    public static MinesweeperConsolePrettyPrinter prettyPrinter(MinesweeperGame game) {
        return new MinesweeperConsolePrettyPrinter(game);
    }

    private MinesweeperConsolePrettyPrinter(MinesweeperGame game) {
        this.game = game;
    }

    public void printBoard(BiFunction<Integer, Integer, String> supplier, int maxLength) {
        int pad = String.valueOf(Collections.max(Arrays.asList(game.getLevel().getBoardWidth(), game.getLevel().getBoardHeight(), maxLength))).length() + 1;
        for (int rowIndex = -1; rowIndex < game.getLevel().getBoardHeight(); rowIndex++) {
            for (int colIndex = -1; colIndex < game.getLevel().getBoardWidth(); colIndex++) {
                if (rowIndex == -1 && colIndex == -1) {
                    System.out.print(" ".repeat(pad));
                } else if (rowIndex == -1) {
                    int realLength = String.valueOf(colIndex).length();
                    if (pad - realLength > 0) {
                        System.out.print(" ".repeat(pad - realLength));
                    }
                    System.out.print(ANSI_WHITE_BOLD + colIndex + ANSI_RESET);
                } else if (colIndex == -1) {
                    int realLength = String.valueOf(rowIndex).length();
                    if (pad - realLength > 0) {
                        System.out.print(" ".repeat(pad - realLength));
                    }
                    System.out.print(ANSI_WHITE_BOLD + rowIndex + ANSI_RESET);
                } else {
                    String s = supplier.apply(rowIndex, colIndex);
                    int realLength = s
                            .replace(ANSI_RESET, "")
                            .replace(ANSI_RED, "")
                            .replace(ANSI_GREEN, "")
                            .replace(ANSI_WHITE_BRIGHT, "")
                            .replace(ANSI_WHITE_BOLD, "")
                            .replace(ANSI_BLUE, "").length();
                    if (pad - realLength > 0) {
                        System.out.print(" ".repeat(pad - realLength));
                    }
                    System.out.print(s);
                }
            }
            System.out.println();
        }
    }

    public void printMineDistribution() {
        System.out.println("Mine distribution graph (mine blocks are labeled " + MINE_CHAR + ")\n");
        printBoard((rowIndex, colIndex) -> {
            if (game.getBlock(rowIndex, colIndex).isMine()) {
                return MINE_CHAR;
            } else {
                return HIDDEN_CHAR;
            }
        }, 1);
        System.out.println();
    }

    public void printFlagged() {
        System.out.println("Flagged blocks graph (flagged blocks are labeled " + FLAG_CHAR + ")\n");
        printBoard((rowIndex, colIndex) -> {
            if (game.getBlock(rowIndex, colIndex).isFlagged()) {
                return FLAG_CHAR;
            } else {
                return HIDDEN_CHAR;
            }
        }, 1);
        System.out.println();
    }

    public void printMinesAround(boolean useData) {
        System.out.println("Number of mines around blocks graph (mine blocks are labeled " + MINE_CHAR + ")\n");
        printBoard((rowIndex, colIndex) -> {
            if (game.getBlock(rowIndex, colIndex).isMine()) {
                return MINE_CHAR;
            } else {
                if (useData) {
                    return String.valueOf(game.getBlock(rowIndex, colIndex).getNumMineAround());
                } else {
                    return String.valueOf(game.countMinesAround(rowIndex, colIndex));
                }
            }
        }, 1);
        System.out.println();
    }

    public void printCorrectAndIncorrectFlagged() {
        System.out.println("Flagged correct/incorrect blocks graph \n" +
                "- flagged mine blocks are labeled " + FLAG_CORRECT_CHAR + "\n" +
                "- flagged regular block are labeled " + FLAG_INCORRECT_CHAR + "\n" +
                "- un-flagged mine block are labeled " + MINE_CHAR + "\n");
        printBoard((rowIndex, colIndex) -> {
            Block block = game.getBlock(rowIndex, colIndex);
            if (block.isFlagged()) {
                if (block.isMine()) {
                    return FLAG_CORRECT_CHAR;
                } else {
                    return FLAG_INCORRECT_CHAR;
                }
            } else if (block.isMine()) {
                return MINE_CHAR;
            } else {
                return HIDDEN_CHAR;
            }
        }, 1);
        System.out.println();
    }

    public void printRevealedCorrectAndIncorrect() {
        System.out.println("Revealed correct/incorrect blocks graph \n" +
                "- unRevealed blocks are labeled " + UNREVEALED_CHAR + "\n" +
                "- revealed regular block are labeled '" + HIDDEN_CHAR + "'\n" +
                "- revealed mine block are labeled " + MINE_CHAR + "\n");
        printBoard((rowIndex, colIndex) -> {
            Block block = game.getBlock(rowIndex, colIndex);
            if (block.isRevealed()) {
                if (block.isMine()) {
                    return MINE_CHAR;
                } else {
                    return HIDDEN_CHAR;
                }
            } else {
                return UNREVEALED_CHAR;
            }
        }, 1);
        System.out.println();
    }

    public void printRevealed() {
        System.out.println("Revealed blocks graph (unRevealed blocks are labeled " + UNREVEALED_CHAR + " or " + UNREVEALED_MINE_CHAR + " or " + UNREVEALED_FLAG_CHAR +
                ", revealed block are labeled '" + HIDDEN_CHAR + "')\n");
        printBoard((rowIndex, colIndex) -> {
            Block block = game.getBlock(rowIndex, colIndex);
            if (block.isRevealed()) {
                return HIDDEN_CHAR;
            } else {
                if (block.isFlagged()) {
                    return UNREVEALED_FLAG_CHAR;
                } else if (block.isMine()) {
                    return UNREVEALED_MINE_CHAR;
                } else {
                    return UNREVEALED_CHAR;
                }
            }
        }, 1);
        System.out.println();
    }
}
