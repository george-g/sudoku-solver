package ru.ggershevich;


// 100000089000009002000000450007600000030040000900002005004070000500008010060300000

import java.util.Arrays;

public class SudokuSolver {

    private static final int NUM_OF_NODES = 81;
    // Судоку можно представить в виде регулярного графа со степенью каждой вершины = 20
    private static final int DEGREE_OF_NODE = 20;
    // Кол-во клеток в стороне квадрата
    private static final int NODES_IN_ROW = 9;
    private static final int NODES_IN_COLUMN = 9;
    private static final int BOXES_IN_ROW = 3;
    private static final int BOXES_IN_COLUMN = 3;

    private final String example;

    private static class Node {
        private final int id;  // 0-80
        private final int row;
        private final int column;
        private int color = 0; // 0 means not colored yet

        public Node(int id, int color) {
            this.id = id;
            this.color = color;
            this.row = id / NODES_IN_ROW;
            this.column = id - row * NODES_IN_ROW;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }

        public boolean isColored() {
            return color > 0;
        }

        public int getId() {
            return id;
        }
    }

    private final Node[] nodes = new Node[NUM_OF_NODES];
    private final Node[][] adjacency = new Node[NUM_OF_NODES][DEGREE_OF_NODE];

    /**
     * @param example стока из 81 символа 0-9. 0 означает что соответствующая ячейка не окрашена
     */
    public SudokuSolver(String example) {
        if (!checkExample(example)) {
            throw new IllegalArgumentException("Example must be string of " + NUM_OF_NODES + " digits");
        }
        this.example = example;
    }

    private static int boxIndex(int row, int column) {
        return BOXES_IN_ROW * (row / BOXES_IN_ROW) + (column / BOXES_IN_COLUMN);
    }

    private void prepare() {
        for (int i = 0; i < NUM_OF_NODES; i++) {
            final int color = Character.getNumericValue(example.charAt(i));
            nodes[i] = new Node(i, color);
        }

        // Подготовка списков смежности. Таблица смежности записанная в индексах, может быть подготовлена заранее и захардкожена.
        // Однако в этой версии этого решено не делать для экономии времени на разработку
        for (int nodeIndex = 0; nodeIndex < NUM_OF_NODES; nodeIndex++) {
            int adjacent = 0;
            final Node node = nodes[nodeIndex];
            final int boxIndexOfNode = boxIndex(node.getRow(), node.getColumn());
            for (int rowIndex = 0; rowIndex < NODES_IN_ROW; rowIndex++) {
                for (int colIndex = 0; colIndex < NODES_IN_COLUMN; colIndex++) {
                    if (node.getRow() == rowIndex
                            && node.getColumn() == colIndex
                    ) {
                        continue;
                    }
                    if (node.getRow() == rowIndex
                            || node.getColumn() == colIndex
                            || boxIndexOfNode == boxIndex(rowIndex, colIndex)
                    ) {
                        adjacency[nodeIndex][adjacent++] = nodes[NODES_IN_ROW * rowIndex + colIndex];
                    }
                }
            }
        }
        //System.out.println(Arrays.toString(Arrays.stream(adjacency[16]).mapToInt(Node::getId).toArray()));
    }

    private boolean checkExample(String example) {
        if (example == null || example.length() != NUM_OF_NODES) {
            return false;
        }

        // Разрешенные символы 0-9
        return example.chars().noneMatch(c -> c < 0x30 || c > 0x39);
    }

    public int[] solve() {
        prepare();

        long countOfColoredNodes = Arrays.stream(nodes).filter(Node::isColored).count();
        System.out.println("Started with countOfColoredNodes = " + countOfColoredNodes);
        while (countOfColoredNodes < NUM_OF_NODES) {
            int max = -1;
            int index = -1;
            for  (int i = 0; i < NUM_OF_NODES; i++) {
                if (!nodes[i].isColored()) {
                    int d = saturatedDegreeFor(i);

                    if(d > max) {
                        max = d;
                        index = i;
                    }
//                  Эта часть алгоритма опущена, так как для регуляного графа степень вершины всегда одна
//                    if (d == max) {
//                        if (Degree(n[i]) > Degree(n[index])) {
//                            index = i
//                        }
//                    }
                }
            }
            pickColorFor(index);
            countOfColoredNodes++;

            //System.out.println("Now countOfColoredNodes = " + countOfColoredNodes + " and index = " + index);
        }

        return Arrays.stream(nodes).mapToInt(Node::getColor).toArray();
    }

    /**
     * Подбирает для вершины наименьший возможный цвет (цвет еще не использованный соседями).
     * @param nodeIndex индекс вершины
     */
    private void pickColorFor(int nodeIndex) {
        final int[] usedColors = adjacentColorsFor(nodeIndex);
        Arrays.sort(usedColors);

        int pickedColor = 1;
        for (int i = 0; i < usedColors.length; i++) {
            if (pickedColor < usedColors[i]) {
                break;
            }
            pickedColor++;
        }

        nodes[nodeIndex].setColor(pickedColor);
    }

    /**
     * Определяет число окрашенных соседей для выбранной вершины
     * @param nodeIndex индекс вершины
     * @return число окрашенных соседей
     */
    private int saturatedDegreeFor(int nodeIndex) {
        return adjacentColorsFor(nodeIndex).length;
    }

    /**
     * Определяет массив цветов использованных соседями для выбранной вершины
     * @param nodeIndex индекс вершины
     * @return массив использованных цветов
     */
    private int[] adjacentColorsFor(int nodeIndex) {
        final int[] adjacentColors = new int[DEGREE_OF_NODE];
        int count = 0;
        for (Node node : adjacency[nodeIndex]) {
            if (node.isColored() && !contains(adjacentColors, node.getColor())) {
                adjacentColors[count] = node.getColor();
                count++;
            }
        }

        return Arrays.copyOf(adjacentColors, count);
    }

    private boolean contains(final int[] array, final int v) {
        for(int i : array){
            if(i == v){
                return true;
            }
        }

        return false;
    }
}
