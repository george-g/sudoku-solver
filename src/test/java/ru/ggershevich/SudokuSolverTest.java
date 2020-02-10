package ru.ggershevich;

import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static ru.ggershevich.SudokuSolver.*;
import static ru.ggershevich.SudokuSolver.solve;

public class SudokuSolverTest {
    private static final int ROWS_COUNT = 9;
    private static final int NODES_IN_ROW = 9;
    private static final int СOLUMNS_COUNT = 9;
    private static final int EXPECTED_SUM = 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9;


    @Test
    public void testSolve() {
        // 581796324937542816426183957712358649658974132349261785194635278873429561265817493
        checkAnswer(solve(prepare("081790304000040016006103050000008640008904100049200000090605200870020000205017490")));
    }

    @Test
    public void testSolve2() {
        checkAnswer(solve(prepare("300000000050703008000028070700000043000000000003904105400300800100040000968000200")));
    }


    @Test(description = "Очень сложный пример. Отключен, так как требует много времени на выполнение", enabled = false)
    // Решенеие для данного примера с выключенной оптимизацией nakedSingle вычисляется примерно за 5 минут 30 секунд. Перебирается 98_536_075 комбинаций.
    // С включенной оптимизацией примерно за 3 минуты 40 секунд. Перебирается 4_6623_263 комбинаций
    public void testSolve3() {
        checkAnswer(solve(prepare("100000089000009002000000450007600000030040000900002005004070000500008010060300000")));
    }


    @Test(description = "Простой пример из задания")
    public void testSolve4() {
        checkAnswer(solve(prepare("013800405024605000087000930490306000001000500000701093069000740000207680102008350")));
    }

    @Test(description = "Сложный пример из задания")
    public void testSolve5() {
        checkAnswer(solve(prepare("002000041000082070000040009200079300010000080006810004100090000060430000850000400")));
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
    }
}