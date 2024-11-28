package exercise3.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class BPlusTreeTest {

    private BPlusTree<Integer, String> tree;

    @BeforeEach
    void setUp() {
        tree = new BPlusTree<>();
    }

    @Test
    void testLeafNodeSplit() {
        BPlusTree<Integer, String>.LeafNode leaf = tree.new LeafNode();
        for (int i = 0; i < BPlusTree.ENTRY_COUNT; i++) {
            BPlusTree<Integer, String>.Record record = tree.new Record(i);
            record.setValue("Value" + i);
            leaf.records.add(record);
        }

        BPlusTree<Integer, String>.LeafNode newLeaf = (BPlusTree<Integer, String>.LeafNode) leaf.split();

        assertEquals(BPlusTree.ENTRY_COUNT / 2, leaf.records.size());
        assertEquals(BPlusTree.ENTRY_COUNT / 2, newLeaf.records.size());
        assertEquals(newLeaf, leaf.next);
        assertEquals(leaf.records.get(leaf.records.size() - 1).getKey(),
                newLeaf.records.get(0).getKey() - 1);
    }

    @Test
    void testIndexNodeSplit() {
        BPlusTree<Integer, String>.IndexNode index = tree.new IndexNode();
        for (int i = 0; i < BPlusTree.ENTRY_COUNT; i++) {
            index.keys.add(i);
            index.children.add(tree.new LeafNode());
        }
        index.children.add(tree.new LeafNode());

        BPlusTree<Integer, String>.IndexNode newIndex = (BPlusTree<Integer, String>.IndexNode) index.split();


        assertEquals(BPlusTree.ENTRY_COUNT / 2 + 1, index.keys.size());
        assertEquals(BPlusTree.ENTRY_COUNT / 2 - 1, newIndex.keys.size());
        assertEquals(index.keys.get(index.keys.size() - 1) + 1, newIndex.keys.get(0));
    }

    @Test
    void testPointQueryAndInsert() {
        tree.insert(1, "One");
        tree.insert(2, "Two");
        tree.insert(3, "Three");

        assertEquals("One", tree.pointQuery(1));
        assertEquals("Two", tree.pointQuery(2));
        assertEquals("Three", tree.pointQuery(3));
        assertNull(tree.pointQuery(4)); // Key not present.
    }

    @Test
    void testRangeQuery() {
        for (int i = 1; i <= 10; i++) {
            tree.insert(i, "Value" + i);
        }

        List<? extends Map.Entry<Integer, String>> result = tree.rangeQuery(3, 7);

        assertEquals(5, result.size());
        for (int i = 3; i <= 7; i++) {
            assertEquals("Value" + i, result.get(i - 3).getValue());
        }
    }
}
