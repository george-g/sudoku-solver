package ru.ggershevich;

import java.util.*;

public class SudokuSolver {

    static final int NUM_OF_NODES = 81;
    // Кол-во клеток в стороне квадрата
    private static final int NODES_IN_ROW = 9;
    private static final int NODES_IN_COLUMN = 9;
    private static final int BOXES_IN_ROW = 3;
    private static final int BOXES_IN_COLUMN = 3;

    private static final BitSet[] adjacency = new BitSet[NUM_OF_NODES];
    private static final BitSet[] adjacencyRow = new BitSet[NUM_OF_NODES];
    private static final BitSet[] adjacencyCol = new BitSet[NUM_OF_NODES];
    private static final BitSet[] adjacencyBox = new BitSet[NUM_OF_NODES];

    static {
        // Подготовка списков смежности. Списки смежности записанные в индексах, могут быть подготовлены заранее и захардкожены.
        // Однако в этой версии этого решено не делать для экономии времени на разработку
        for (int nodeIndex = 0; nodeIndex < NUM_OF_NODES; nodeIndex++) {
            int adjacent = 0;
            adjacency[nodeIndex] = new BitSet(NUM_OF_NODES);
            adjacencyRow[nodeIndex] = new BitSet(NUM_OF_NODES);
            adjacencyCol[nodeIndex] = new BitSet(NUM_OF_NODES);
            adjacencyBox[nodeIndex] = new BitSet(NUM_OF_NODES);
            for (int rowIndex = 0; rowIndex < NODES_IN_ROW; rowIndex++) {
                for (int colIndex = 0; colIndex < NODES_IN_COLUMN; colIndex++) {
                    final int nodeRow = nodeIndex / NODES_IN_ROW;
                    final int nodeColumn = nodeIndex - nodeRow * NODES_IN_ROW;
                    if (nodeRow == rowIndex && nodeColumn == colIndex) {
                        continue;
                    }
                    if (nodeRow == rowIndex) {
                        adjacency[nodeIndex].set(NODES_IN_ROW * rowIndex + colIndex);
                        adjacencyRow[nodeIndex].set(NODES_IN_ROW * rowIndex + colIndex);
                    }
                    if (nodeColumn == colIndex) {
                        adjacency[nodeIndex].set(NODES_IN_ROW * rowIndex + colIndex);
                        adjacencyCol[nodeIndex].set(NODES_IN_ROW * rowIndex + colIndex);
                    }
                    if (boxIndex(nodeRow, nodeColumn) == boxIndex(rowIndex, colIndex)) {
                        adjacency[nodeIndex].set(NODES_IN_ROW * rowIndex + colIndex);
                        adjacencyBox[nodeIndex].set(NODES_IN_ROW * rowIndex + colIndex);
                    }
                }
            }
        }
    }

    private static long usedCombination = 0;

    private static final int[] colorBitMap = {
            0b000000000,
            0b000000001,
            0b000000010,
            0b000000100,
            0b000001000,
            0b000010000,
            0b000100000,
            0b001000000,
            0b010000000,
            0b100000000,
    };
    private static boolean nakedSinglesOptimizationEnabled = true;

    private static int boxIndex(int row, int column) {
        return BOXES_IN_ROW * (row / BOXES_IN_ROW) + (column / BOXES_IN_COLUMN);
    }

    private SudokuSolver() {
    }

    public static int[] prepare(String exampleStr) {
        int [] exampleNodes = new int[NUM_OF_NODES];
        for (int i = 0; i < NUM_OF_NODES; i++) {
            final int color = Character.getNumericValue(exampleStr.charAt(i));
            exampleNodes[i] = color;
        }

        return exampleNodes;
    }

    private static boolean checkExample(String example) {
        if (example == null || example.length() != NUM_OF_NODES) {
            return false;
        }

        // Разрешенные символы 0-9
        return example.chars().noneMatch(c -> c < 0x30 || c > 0x39);
    }

    public static int[] solve(int[] nodes) {
        int[] possibleColors = new int[NUM_OF_NODES];

        int[] colorFrequencies;
        boolean repeat;
        do {
            repeat = false;
            colorFrequencies = new int[colorBitMap.length];
            for (int i = 0; i < nodes.length; i++) {
                if (nodes[i] == 0) {
                    possibleColors[i] = adjacentColorsBitsFor(i, nodes) ^ 0b111111111;
                    // Если для неокрашенной вершины нет доступного цвета, значит задача не решаема
                    if (possibleColors[i] == 0) {
                        return null;
                    }
                    // Улучшение перебора. Если у вершины нет другого доступного цвета - окрасить ее сразу
                    if (nakedSinglesOptimization(i, possibleColors, nodes)) {
                        repeat = true;
                        break;
                    }

                    // Сразу подкоректируем частоту с которой данный цвет встречается
                    for (int color = 0; color < colorBitMap.length; color++) {
                        if ((possibleColors[i] & colorBitMap[color]) > 0) {
                            colorFrequencies[color]++;
                        }
                    }
                }
            }

        } while (repeat);

        boolean allColored = true;
        for (int node : nodes) {
            if (node == 0) {
                allColored = false;
            }
        }
        if (allColored) {
            if (checkNodes(nodes)) {
                return nodes;
            } else {
                return null;
            }
        }

        // Выбираем самый редкий цввет
        int minFrequency = 81;
        int selectedColor = 0;
        for (int i = 1; i < colorFrequencies.length; i++) {
            // colorFrequencies[i] > 0 - цвета для которых нет доступного места уже расставлены и нас не интересуют
            if (colorFrequencies[i] > 0 && colorFrequencies[i] < minFrequency) {
                minFrequency = colorFrequencies[i];
                selectedColor = i;
            }
        }


        // Найдем все вершины где этот цвет допустим
        int selectedColorBit = colorBitMap[selectedColor];
        BitSet nodesWhereSelectedColorPossible = new BitSet(NUM_OF_NODES);
        for (int i = 0; i < possibleColors.length; i++) {
            if ((possibleColors[i] & selectedColorBit) > 0) {
                nodesWhereSelectedColorPossible.set(i);
            }
        }


        // Найдем все вершины где этот цвет предустановлен
        int powerOfCombination = 9;
        for (int node : nodes) {
            if (node == selectedColor) {
                powerOfCombination--;
            }
        }

        BitSet[] independentNodes = Utilites.getIndependentNodes(nodesWhereSelectedColorPossible, adjacency);
        int[] combination = null;
        do {
            combination = Utilites.nextCombination(combination, independentNodes, powerOfCombination);
            if (combination != null) {
                int[] next = Arrays.copyOf(nodes, nodes.length);
                for (int index : combination) {
                    next[index] = selectedColor;
                }

                usedCombination++;

                int[] solution = solve(next);
                if (solution != null) {
                    System.out.println("Испольщованно комбинаций: " + usedCombination);
                    return solution;
                }
            }
        }
        while (combination != null);

        return null;
    }

    private static boolean nakedSinglesOptimization(int i, int[] possibleColors, int[] nodes) {
        if (nakedSinglesOptimizationEnabled && Integer.bitCount(possibleColors[i]) == 1) {
            for (int color = 0; color < colorBitMap.length; color++) {
                if ((possibleColors[i] & colorBitMap[color]) > 0) {
                    nodes[i] = color;
                }
            }
            return true;
        }
        return false;
    }

    private static boolean checkNodes(int[] nodes) {
        for (int i = 0; i < nodes.length; i++) {

            if (!checkNodeByAdjacency(nodes, i, adjacencyRow)
                    || !checkNodeByAdjacency(nodes, i, adjacencyCol)
                    || !checkNodeByAdjacency(nodes, i, adjacencyBox)
            ) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkNodeByAdjacency(int[] nodes, int i, BitSet bitSet[]) {

        int sum = nodes[i];
        int adjacentIndex = bitSet[i].nextSetBit(0);
        while (adjacentIndex >= 0) {
            sum += nodes[adjacentIndex];
            adjacentIndex = bitSet[i].nextSetBit(++adjacentIndex);
        }
        if (sum == 45) {
            return true;
        }
        return false;
    }

    /**
     * @param example строка из 81 символа 0-9. 0 означает что соответствующая ячейка не окрашена
     */
    public static String solve(String example) {
        if (!checkExample(example)) {
            throw new IllegalArgumentException("Example must be string of " + NUM_OF_NODES + " digits");
        }
        int[] solution = solve(prepare(example));

        String result = "";
        if (solution != null) {
            for (int color : solution) {
                result = result + color;
            }
        }

        return result;
    }

    /**
     * Определяет массив цветов использованных соседями для выбранной вершины
     * @param nodeIndex индекс вершины
     * @return массив использованных цветов
     */
    private static int adjacentColorsBitsFor(int nodeIndex, int[] nodes) {
        int adjacentColors = 0;
        int adjacentIndex = adjacency[nodeIndex].nextSetBit(0);
        while (adjacentIndex >= 0) {
            adjacentColors |= colorBitMap[nodes[adjacentIndex]];
            adjacentIndex = adjacency[nodeIndex].nextSetBit(++adjacentIndex);
        }

        return  adjacentColors;
    }

}
