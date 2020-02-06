package ru.ggershevich;

import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class SudokuSolverTest {
    private static final int ROWS_COUNT = 9;
    private static final int NODES_IN_ROW = 9;
    private static final int СOLUMNS_COUNT = 9;
    private static final int EXPECTED_SUM = 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9;


    @Test
    public void testSolve() {
        final SudokuSolver sudoku = new SudokuSolver("081790304000040016006103050000008640008904100049200000090605200870020000205017490");

        checkAnswer(sudoku.solve());
    }

    @Test
    public void testSolve2() {
        final SudokuSolver sudoku = new SudokuSolver("300000000050703008000028070700000043000000000003904105400300800100040000968000200");

        checkAnswer(sudoku.solve());
    }

    private void checkAnswer(int[] answer) {
        assertTrue(Arrays.stream(answer).noneMatch(c -> c < 1 || c > 9));

        // проверка ссумы в рядах
        for (int i = 0; i < ROWS_COUNT; i++) {
            int summ = 0;
            for (int j = 0; j < NODES_IN_ROW; j++) {
                summ += answer[i * NODES_IN_ROW + j];
            }
            assertEquals(summ, EXPECTED_SUM);
        }

        // проверка ссумы колонках
        for (int i = 0; i < СOLUMNS_COUNT; i++) {
            int summ = 0;
            for (int j = 0; j < NODES_IN_ROW; j++) {
                summ += answer[i + j * NODES_IN_ROW];
            }
            assertEquals(summ, EXPECTED_SUM);
        }

        // Проверка суммы в боксах
    }
}