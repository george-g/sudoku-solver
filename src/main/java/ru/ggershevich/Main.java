package ru.ggershevich;


public class Main {
    public static void main(String[] args) {
        if (args == null || args[0] == null || args[0].isEmpty()) {
            System.out.println("Give me example as argument. Example is sting of 81 character there unknown cells are 0, and known cells are 1-9:");
            System.out.println("java -jar sudoku-solver 013800405024605000087000930490306000001000500000701093069000740000207680102008350");
            return;
        }

        final String solution = SudokuSolver.solve(args[0]);
        if (solution == null) {
            System.out.println("Solution not found");
        } else {
            System.out.println(solution);
        }
    }
}
