package ru.ggershevich;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.BitSet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Created by George on 08.02.2020.
 */
public class UtilitiesTest {
    BitSet[] adjacency = new BitSet[7];
    BitSet nodes;

    @BeforeMethod
    public void setUp() throws Exception {
        // +---+         +---+      +---+      +---+
        // | 0 |         | 1 |------| 2 |------| 3 |
        // +---+         +---+      +---+     /+-|-+
        //                |                 /   |
        //                |                /    |
        //                |               /     |
        //                |              /      |
        //              +---+         +---+   +---+
        //              | 6 |         | 4 |   | 5 |
        //              +---+         +---+   +---+

        adjacency[0] = new BitSet(6);
        adjacency[1] = new BitSet(6);
        adjacency[2] = new BitSet(6);
        adjacency[3] = new BitSet(6);
        adjacency[4] = new BitSet(6);
        adjacency[5] = new BitSet(6);
        adjacency[6] = new BitSet(6);
        adjacency[1].set(2);
        adjacency[1].set(6);
        adjacency[2].set(1);
        adjacency[2].set(3);
        adjacency[3].set(2);
        adjacency[3].set(4);
        adjacency[3].set(5);
        adjacency[4].set(3);
        adjacency[5].set(3);
        adjacency[6].set(1);

        nodes = new BitSet();
        nodes.set(0);
        nodes.set(1);
        nodes.set(2);
        nodes.set(3);
        nodes.set(4);
        nodes.set(5);
        nodes.set(6);
    }

    @Test
    public void testGetIndependentNodes() {
        BitSet[] independentNodes = Utilities.getIndependentNodes(nodes, adjacency);
        assertEquals(independentNodes[0].toString(), "{1, 2, 3, 4, 5, 6}");
        assertEquals(independentNodes[1].toString(), "{3, 4, 5}");
        assertEquals(independentNodes[2].toString(), "{4, 5, 6}");
        assertEquals(independentNodes[3].toString(), "{6}");
        assertEquals(independentNodes[4].toString(), "{5, 6}");
        assertEquals(independentNodes[5].toString(), "{6}");
        assertEquals(independentNodes[6].toString(), "{}");
    }

    @Test
    public void nextCombinationPower4() {
        BitSet[] independentNodes = Utilities.getIndependentNodes(nodes, adjacency);
        int[] combination;
        combination = Utilities.nextCombination(null, independentNodes, 4);
        assertEquals(combination, new int[]{0, 1, 3, 6});
        combination = Utilities.nextCombination(combination, independentNodes, 4);
        assertEquals(combination, new int[]{0, 1, 4, 5});
        combination = Utilities.nextCombination(combination, independentNodes, 4);
        assertEquals(combination, new int[]{0, 1, 4, 6});
        combination = Utilities.nextCombination(combination, independentNodes, 4);
        assertEquals(combination, new int[]{0, 1, 5, 6});
        combination = Utilities.nextCombination(combination, independentNodes, 4);
        assertEquals(combination, new int[]{0, 2, 4, 5});
        combination = Utilities.nextCombination(combination, independentNodes, 4);
        assertEquals(combination, new int[]{0, 2, 4, 6});
        combination = Utilities.nextCombination(combination, independentNodes, 4);
        assertEquals(combination, new int[]{0, 2, 5, 6});
        combination = Utilities.nextCombination(combination, independentNodes, 4);
        assertEquals(combination, new int[]{0, 4, 5, 6});
        combination = Utilities.nextCombination(combination, independentNodes, 4);
        assertEquals(combination, new int[]{1, 4, 5, 6});
        combination = Utilities.nextCombination(combination, independentNodes, 4);
        assertEquals(combination, new int[]{2, 4, 5, 6});
        combination = Utilities.nextCombination(combination, independentNodes, 4);
        assertNull(combination);
    }

    @Test
    public void nextCombinationPower3() {
        BitSet[] independentNodes = Utilities.getIndependentNodes(nodes, adjacency);
        int[] combination;
        combination = Utilities.nextCombination(null, independentNodes, 3);
        assertEquals(combination, new int[]{0, 1, 3});
        // ...
        combination = new int[] {0, 5, 6};
        combination = Utilities.nextCombination(combination, independentNodes, 3);
        assertEquals(combination, new int[]{1, 3, 6});
        combination = Utilities.nextCombination(combination, independentNodes, 2);
        assertEquals(combination, new int[]{1, 4, 5});
        combination = Utilities.nextCombination(combination, independentNodes, 2);
        assertEquals(combination, new int[]{1, 4, 6});
        combination = Utilities.nextCombination(combination, independentNodes, 2);
        assertEquals(combination, new int[]{1, 5, 6});
        combination = Utilities.nextCombination(combination, independentNodes, 2);
        assertEquals(combination, new int[]{2, 4, 5});
        combination = Utilities.nextCombination(combination, independentNodes, 2);
        assertEquals(combination, new int[]{2, 4, 6});
        combination = Utilities.nextCombination(combination, independentNodes, 2);
        assertEquals(combination, new int[]{2, 5, 6});
        combination = Utilities.nextCombination(combination, independentNodes, 2);
        assertEquals(combination, new int[]{4, 5, 6});
        combination = Utilities.nextCombination(combination, independentNodes, 2);
        assertNull(combination);
    }

    @Test
    public void nextCombinationPower2() {
        BitSet[] independentNodes = Utilities.getIndependentNodes(nodes, adjacency);
        int[] combination;
        combination = Utilities.nextCombination(null, independentNodes, 2);
        assertEquals(combination, new int[]{0, 1});
        //....
        combination = new int[] {0, 6};
        combination = Utilities.nextCombination(combination, independentNodes, 2);
        assertEquals(combination, new int[]{1, 3});
        combination = Utilities.nextCombination(combination, independentNodes, 2);
        assertEquals(combination, new int[]{1, 4});
        combination = Utilities.nextCombination(combination, independentNodes, 2);
        assertEquals(combination, new int[]{1, 5});
        combination = Utilities.nextCombination(combination, independentNodes, 2);
        assertEquals(combination, new int[]{2, 4});
        combination = Utilities.nextCombination(combination, independentNodes, 2);
        assertEquals(combination, new int[]{2, 5});
        combination = Utilities.nextCombination(combination, independentNodes, 2);
        assertEquals(combination, new int[]{2, 6});
//      Повтор комбинации {1, 3}
//      combination = Utilites.nextCombination(combination, independentNodes, 2);
//      assertEquals(combination, new int[]{3, 1});
        combination = Utilities.nextCombination(combination, independentNodes, 2);
        assertEquals(combination, new int[]{3, 6});
//      Повтор комбинации {1, 4}
//        combination = Utilites.nextCombination(combination, independentNodes, 2);
//        assertEquals(combination, new int[]{4, 1});
        // ...
        combination = new int[]{5, 4};
        combination = Utilities.nextCombination(combination, independentNodes, 2);
        assertEquals(combination, new int[]{5, 6});
        combination = Utilities.nextCombination(combination, independentNodes, 2);
        assertNull(combination);
    }

    @Test
    public void nextCombinationPower1() {
        BitSet[] independentNodes = Utilities.getIndependentNodes(nodes, adjacency);
        int[] combination;
        combination = Utilities.nextCombination(null, independentNodes, 1);
        assertEquals(combination, new int[]{0});
        combination = Utilities.nextCombination(combination, independentNodes, 1);
        assertEquals(combination, new int[]{1});
        combination = Utilities.nextCombination(combination, independentNodes, 1);
        assertEquals(combination, new int[]{2});
        combination = Utilities.nextCombination(combination, independentNodes, 1);
        assertEquals(combination, new int[]{3});
        combination = Utilities.nextCombination(combination, independentNodes, 1);
        assertEquals(combination, new int[]{4});
        combination = Utilities.nextCombination(combination, independentNodes, 1);
        assertEquals(combination, new int[]{5});
        combination = Utilities.nextCombination(combination, independentNodes, 1);
        assertEquals(combination, new int[]{6});
        combination = Utilities.nextCombination(combination, independentNodes, 1);
        assertNull(combination);
    }

}