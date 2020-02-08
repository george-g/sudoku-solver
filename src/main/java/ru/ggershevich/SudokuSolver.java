package ru.ggershevich;


// 100000089000009002000000450007600000030040000900002005004070000500008010060300000

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SudokuSolver {

    private static final int NUM_OF_NODES = 81;
    // Судоку можно представить в виде регулярного графа со степенью каждой вершины = 20
    private static final int DEGREE_OF_NODE = 20;
    private static final int DEGREE_OF_ROW = 8;
    private static final int DEGREE_OF_COL = 8;
    private static final int DEGREE_OF_BOX = 8;
    // Кол-во клеток в стороне квадрата
    private static final int NODES_IN_ROW = 9;
    private static final int NODES_IN_COLUMN = 9;
    private static final int BOXES_IN_ROW = 3;
    private static final int BOXES_IN_COLUMN = 3;

    private final int[] example;
    private final int[][] adjacencyRow = new int[NUM_OF_NODES][DEGREE_OF_ROW];
    private final int[][] adjacencyCol = new int[NUM_OF_NODES][DEGREE_OF_COL];
    private final int[][] adjacencyBox = new int[NUM_OF_NODES][DEGREE_OF_BOX];

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
    /**
     * @param example стока из 81 символа 0-9. 0 означает что соответствующая ячейка не окрашена
     */
    public SudokuSolver(String example) {
        if (!checkExample(example)) {
            throw new IllegalArgumentException("Example must be string of " + NUM_OF_NODES + " digits");
        }
        this.example = prepare(example);

        // Подготовка списков смежности. Списки смежности записанные в индексах, могут быть подготовлены заранее и захардкожены.
        // Однако в этой версии этого решено не делать для экономии времени на разработку
        for (int nodeIndex = 0; nodeIndex < NUM_OF_NODES; nodeIndex++) {
            int adjacentRow = 0;
            int adjacentCol = 0;
            int adjacentBox = 0;
            for (int rowIndex = 0; rowIndex < NODES_IN_ROW; rowIndex++) {
                for (int colIndex = 0; colIndex < NODES_IN_COLUMN; colIndex++) {
                    final int nodeRow = nodeIndex / NODES_IN_ROW;
                    final int nodeColumn = nodeIndex - nodeRow * NODES_IN_ROW;
                    if (nodeRow == rowIndex && nodeColumn == colIndex) {
                        continue;
                    }
                    if (nodeRow == rowIndex) {
                        adjacencyRow[nodeIndex][adjacentRow++] = NODES_IN_ROW * rowIndex + colIndex;
                    }
                    if (nodeColumn == colIndex) {
                        adjacencyCol[nodeIndex][adjacentCol++] = NODES_IN_ROW * rowIndex + colIndex;
                    }
                    if (boxIndex(nodeRow, nodeColumn) == boxIndex(rowIndex, colIndex)) {
                        adjacencyBox[nodeIndex][adjacentBox++] = NODES_IN_ROW * rowIndex + colIndex;
                    }
                }
            }
        }
    }

    private static int boxIndex(int row, int column) {
        return BOXES_IN_ROW * (row / BOXES_IN_ROW) + (column / BOXES_IN_COLUMN);
    }

    private int[] prepare(String exampleStr) {
        int [] exampleNodes = new int[NUM_OF_NODES];
        for (int i = 0; i < NUM_OF_NODES; i++) {
            final int color = Character.getNumericValue(exampleStr.charAt(i));
            exampleNodes[i] = color;
        }

        return exampleNodes;
    }

    private boolean checkExample(String example) {
        if (example == null || example.length() != NUM_OF_NODES) {
            return false;
        }

        // Разрешенные символы 0-9
        return example.chars().noneMatch(c -> c < 0x30 || c > 0x39);
    }

    public int[] solve() {
        int[] nodes = Arrays.copyOf(example, example.length);
        List<Integer> bruteForceIdexes = new ArrayList();
        long bruteForcedValue = 0;

        final long countOfColoredNodesForExample = Arrays.stream(nodes).filter(c -> c != 0).count();
        long countOfColoredNodes = countOfColoredNodesForExample;
//        System.out.println("Started with countOfColoredNodes = " + countOfColoredNodes);
        while (countOfColoredNodes < NUM_OF_NODES) {
            int[] possibleColors = new int[NUM_OF_NODES];

            int max = -1;
            int index = -1;
            for  (int i = 0; i < NUM_OF_NODES; i++) {
                // nodes[i] == 0 - Вершина еще не окрашена
                if (nodes[i] == 0) {
                    int d = saturatedDegreeFor(i, nodes);

                    if(d > max) {
                        max = d;
                        index = i;
                    }
//                  Эта часть алгоритма опущена, так как для регуляного графа степень вершины всегда одна
                    if (d == max) {
                        if (Integer.bitCount(possibleColors[i]) < Integer.bitCount(possibleColors[index])) {
                            index = i;
                        }
                    }
                }
            }
            pickColorFor(index, nodes);
            countOfColoredNodes++;

            if (nodes[index] > 9) {

            }
//            if (nodes[index] > 9) {
//                // Этот брютфорс не работает, так как не учитывает цвета соседей у изменяемых нод
//                bruteForcedValue++;
//                String bruteForcedValueStr = String.valueOf(bruteForcedValue);
//                bruteForcedValueStr = bruteForcedValueStr.replace('0', '1');
//                bruteForcedValue = Long.parseLong(bruteForcedValueStr);
//
//                if (bruteForcedValueStr.length() > bruteForceIdexes.size()) {
//                    bruteForceIdexes.add(index);
//                }
//
//                nodes = Arrays.copyOf(example, example.length);
//
//                System.out.println("BruteForcedIndexes = " + bruteForceIdexes);
//                System.out.println("bruteForcedValue = " + bruteForcedValue);
//
//                long bruteForcedValueTemp = bruteForcedValue;
//                for (int i = bruteForceIdexes.size() - 1; i >= 0; i--) {
//                    nodes[bruteForceIdexes.get(i)] = (int) (bruteForcedValueTemp % 10);
//                    bruteForcedValueTemp = bruteForcedValueTemp / 10;
//                    System.out.println("Node X = " + nodes[bruteForceIdexes.get(i)]);
//                }
//
//                countOfColoredNodes = countOfColoredNodesForExample + bruteForceIdexes.size();
//            }

            //System.out.println("Now countOfColoredNodes = " + countOfColoredNodes + " and index = " + index);
        }

        return nodes;
    }

    /**
     * Подбирает для вершины наименьший возможный цвет (цвет еще не использованный соседями).
     * @param nodeIndex индекс вершины
     */
    private void pickColorFor(int nodeIndex, int[] nodes) {
        int unusedColors = adjacentColorsBitsUnitedFor(nodeIndex, nodes) ^ 0b111111111;
//
//        if (Integer.bitCount(unusedColors) > 1) {
//            int[] acceptableColorsByType = new int[3];
//
//            acceptableColorsByType[0] = 0;
//            for (int adjacentIndex : adjacencyRow[nodeIndex]) {
//                if (nodes[adjacentIndex] == 0) {
//                    acceptableColorsByType[0] |= adjacentColorsBitsUnitedFor(adjacentIndex, nodes) ^ 0b111111111;
//                }
//            }
//            acceptableColorsByType[1] = 0;
//            for (int adjacentIndex : adjacencyCol[nodeIndex]) {
//                if (nodes[adjacentIndex] == 0) {
//                    acceptableColorsByType[1] |= adjacentColorsBitsUnitedFor(adjacentIndex, nodes) ^ 0b111111111;
//                }
//            }
//            acceptableColorsByType[2] = 0;
//            for (int adjacentIndex : adjacencyBox[nodeIndex]) {
//                if (nodes[adjacentIndex] == 0) {
//                    acceptableColorsByType[2] |= adjacentColorsBitsUnitedFor(adjacentIndex, nodes) ^ 0b111111111;
//                }
//            }
//            acceptableColorsByType[0] = unusedColors & ~acceptableColorsByType[0];
//            acceptableColorsByType[1] = unusedColors & ~acceptableColorsByType[1];
//            acceptableColorsByType[2] = unusedColors & ~acceptableColorsByType[2];
//        }
//        for  (int adjacentByRowIndex : adjacencyRow[nodeIndex]) {
//            adjacentColorsBitsFor(i, nodes) ^ 0b0111111111;
//        }

        // должно быть максимум 9. Но выбранный алгоритм бывает заходит в тупик. 10 нужно чтоб распознать это
        for (int i = 1; i < 10; i++) {
            if ((unusedColors & 0x000000001) == 1) {
                nodes[nodeIndex] = i;
                return;
            }
            unusedColors >>>= 1;
        }
    }

    /**
     * Определяет число окрашенных соседей для выбранной вершины
     * @param nodeIndex индекс вершины
     * @return число окрашенных соседей
     */
    private int saturatedDegreeFor(int nodeIndex, int[] nodes) {
        final int[] colorsBits = adjacentColorsBitsFor(nodeIndex, nodes);

        return Integer.bitCount(colorsBits[0] | colorsBits[1] | colorsBits[2]);
    }

    /**
     * Определяет массив цветов использованных соседями для выбранной вершины
     * @param nodeIndex индекс вершины
     * @return массив использованных цветов
     */
    private int[] adjacentColorsBitsFor(int nodeIndex, int[] nodes) {
        int adjacentByRowColors = 0;
        int adjacentByColColors = 0;
        int adjacentByBoxColors = 0;
        int count = 0;
        for (int adjacentIndex : adjacencyRow[nodeIndex]) {
            adjacentByRowColors |= colorBitMap[nodes[adjacentIndex]];
        }
        for (int adjacentIndex : adjacencyCol[nodeIndex]) {
            adjacentByColColors |= colorBitMap[nodes[adjacentIndex]];
        }
        for (int adjacentIndex : adjacencyBox[nodeIndex]) {
            adjacentByBoxColors |= colorBitMap[nodes[adjacentIndex]];
        }

        return new int[] {adjacentByBoxColors, adjacentByColColors, adjacentByRowColors};
    }

    /**
     * Определяет массив цветов использованных соседями для выбранной вершины
     * @param nodeIndex индекс вершины
     * @return массив использованных цветов
     */
    private int adjacentColorsBitsUnitedFor(int nodeIndex, int[] nodes) {
        final int[] colorsBitsFor = adjacentColorsBitsFor(nodeIndex, nodes);
        return colorsBitsFor[0] | colorsBitsFor[1] | colorsBitsFor[2];
    }

}
