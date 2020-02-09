package ru.ggershevich;


// 100000089000009002000000450007600000030040000900002005004070000500008010060300000

import java.util.*;


public class SudokuSolver {

    static final int NUM_OF_NODES = 81;
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
    private final BitSet[] adjacency = new BitSet[NUM_OF_NODES];
    private final BitSet[] adjacencyRow = new BitSet[NUM_OF_NODES];
    private final BitSet[] adjacencyCol = new BitSet[NUM_OF_NODES];
    private final BitSet[] adjacencyBox = new BitSet[NUM_OF_NODES];

    private final Set<int[]> usedCombination = new HashSet<>();

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

    private int[] solve(int[] nodes) {
        int[] possibleColors = new int[NUM_OF_NODES];

        int[] colorFrequencies;
        boolean cellWithOneColorExists;
        do {
            cellWithOneColorExists = false;
            colorFrequencies = new int[colorBitMap.length];
            for (int i = 0; i < nodes.length; i++) {
                if (nodes[i] == 0) {
                    possibleColors[i] = adjacentColorsBitsFor(i, nodes) ^ 0b111111111;
                    // Если для неокрашенной вершины нет доступного цвета, значит задача не решаема
                    if (possibleColors[i] == 0) {
                        return null;
                    }
                    if (Integer.bitCount(possibleColors[i]) == 1) {
                        cellWithOneColorExists = true;
                        for (int color = 0; color < colorBitMap.length; color++) {
                            if ((possibleColors[i] & colorBitMap[color]) > 0) {
                                nodes[i] = color;
                            }
                        }
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

        } while (cellWithOneColorExists);

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
                int[] vodes = Arrays.copyOf(nodes, nodes.length);
                for (int index : combination) {
                    vodes[index] = selectedColor;
                }

                if (usedCombination.contains(vodes)) {
                    System.out.println("combination recheck " + Arrays.toString(vodes));
                }

                int[] solution = solve(vodes);
                if (solution != null) {
                    return solution;
                }
            }
        }
        while (combination != null);

        return null;
    }

    private boolean checkNodes(int[] nodes) {
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

    private boolean checkNodeByAdjacency(int[] nodes, int i, BitSet bitSet[]) {

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

    public int[] solve() {
        int[] nodes = Arrays.copyOf(example, example.length);
        return  solve(nodes);
//        List<Integer> bruteForceIdexes = new ArrayList();
//        long bruteForcedValue = 0;
//
//        final long countOfColoredNodesForExample = Arrays.stream(nodes).filter(c -> c != 0).count();
//        long countOfColoredNodes = countOfColoredNodesForExample;
////        System.out.println("Started with countOfColoredNodes = " + countOfColoredNodes);
//        while (countOfColoredNodes < NUM_OF_NODES) {
//            int[] possibleColors = new int[NUM_OF_NODES];
//
//            int max = -1;
//            int index = -1;
//            for  (int i = 0; i < NUM_OF_NODES; i++) {
//                // nodes[i] == 0 - Вершина еще не окрашена
//                if (nodes[i] == 0) {
//                    int d = saturatedDegreeFor(i, nodes);
//
//                    if(d > max) {
//                        max = d;
//                        index = i;
//                    }
////                  Эта часть алгоритма опущена, так как для регуляного графа степень вершины всегда одна
//                    if (d == max) {
//                        if (Integer.bitCount(possibleColors[i]) < Integer.bitCount(possibleColors[index])) {
//                            index = i;
//                        }
//                    }
//                }
//            }
//            pickColorFor(index, nodes);
//            countOfColoredNodes++;
//        }
//
//        return nodes;
    }

    /**
     * Подбирает для вершины наименьший возможный цвет (цвет еще не использованный соседями).
     * @param nodeIndex индекс вершины
     */
    private void pickColorFor(int nodeIndex, int[] nodes) {
        int unusedColors = adjacentColorsBitsFor(nodeIndex, nodes) ^ 0b111111111;

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
        final int colorsBits = adjacentColorsBitsFor(nodeIndex, nodes);

        return Integer.bitCount(colorsBits);
    }

    /**
     * Определяет массив цветов использованных соседями для выбранной вершины
     * @param nodeIndex индекс вершины
     * @return массив использованных цветов
     */
    private int adjacentColorsBitsFor(int nodeIndex, int[] nodes) {
        int adjacentColors = 0;
        int adjacentIndex = adjacency[nodeIndex].nextSetBit(0);
        while (adjacentIndex >= 0) {
            adjacentColors |= colorBitMap[nodes[adjacentIndex]];
            adjacentIndex = adjacency[nodeIndex].nextSetBit(++adjacentIndex);
        }

        return  adjacentColors;
    }

}
