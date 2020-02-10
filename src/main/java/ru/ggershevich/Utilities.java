package ru.ggershevich;

import java.util.Arrays;
import java.util.BitSet;

import static ru.ggershevich.SudokuSolver.NUM_OF_NODES;

/**
 * Created by George on 08.02.2020.
 */
class Utilities {
    static BitSet[] getIndependentNodes(BitSet nodesWhereSelectedColorPossible, BitSet[] adjacency) {
        BitSet[] result = new BitSet[NUM_OF_NODES];


        int nodeIndex = nodesWhereSelectedColorPossible.nextSetBit(0);
        while (nodeIndex >= 0) {
            result[nodeIndex] = new BitSet(NUM_OF_NODES);
            nodeIndex = nodesWhereSelectedColorPossible.nextSetBit(++nodeIndex);
        }

        int first = nodesWhereSelectedColorPossible.nextSetBit(0);
        while (first >= 0) {
            int second = nodesWhereSelectedColorPossible.nextSetBit(first + 1);
            while (second >= 0) {
                if (!adjacency[first].get(second)) {
                    result[first].set(second);
                }
                second = nodesWhereSelectedColorPossible.nextSetBit(++second);
            }
            first = nodesWhereSelectedColorPossible.nextSetBit(++first);
        }

        return result;
    }


    static int[] nextCombination(int[] previousCombination, BitSet[] independentNodes, int power) {
        int placeForChange;
        int[] result;
        if (previousCombination == null) {
            result = new int[power];
            result[0] = -1;
            placeForChange = 0;
        } else {
            result = Arrays.copyOf(previousCombination, previousCombination.length);
            placeForChange = result.length - 1;
        }

        while (true) {
            if (placeForChange == 0) {
                int i;
                for (i = result[0] + 1; i < independentNodes.length; i++) {
                    if (independentNodes[i] != null) {
                        result[0] = i;
                        placeForChange++;
                        if (placeForChange == result.length) {
                            return result;
                        }
                        break;
                    }
                }
                // Все комбинации закончились
                if (i == independentNodes.length) {
                    return null;
                }
            }
            int nextValueForPlaceForChange = independentNodes[result[placeForChange - 1]]
                    .nextSetBit(result[placeForChange] + 1);
            if (nextValueForPlaceForChange < 0) {
                result[placeForChange] = 0;
                placeForChange--;
            } else {
                result[placeForChange] = nextValueForPlaceForChange;
                if (placeForChange < result.length - 1) {
                    placeForChange++;
                } else {
                    break;
                }
            }
        }

        return result;
    }
}
