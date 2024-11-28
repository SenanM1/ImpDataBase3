package exercise3.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Order 8 | m = 8

public class BPlusTree<K extends Comparable<? super K>, V> {
    static final int ENTRY_COUNT = 8;

    final class Record implements Comparable<Map.Entry<K, V>>, Map.Entry<K, V> {
        final K key;
        V value;

        Record(K key) {
            this.key = key;
        }

        @Override
        public int compareTo(Map.Entry<K, V> o) {
            return this.key.compareTo(o.getKey());
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            return this.value = value;
        }
    }

    abstract static class Node {
        abstract Node split();

        abstract boolean isLeaf();
    }

    final class IndexNode extends Node {
        final List<K> keys = new ArrayList<>(ENTRY_COUNT);
        final List<Node> children = new ArrayList<>(ENTRY_COUNT - 1);

        boolean isLeaf() {
            return false;
        }

        @Override
        Node split() {
            // TODO: Impl.
            int middle = ENTRY_COUNT / 2;

            IndexNode newIndex = new IndexNode();

            newIndex.keys.addAll(keys.subList(middle + 1, keys.size()));

            newIndex.children.addAll(children.subList(middle + 1, children.size()));

            keys.subList(middle + 1, keys.size()).clear();

            children.subList(middle + 1, children.size()).clear();

            return newIndex;
        }
    }

    final class LeafNode extends Node {
        final List<Record> records = new ArrayList<>(ENTRY_COUNT);
        LeafNode next;

        @Override
        Node split() {
            // TODO: Impl.
            int middle = ENTRY_COUNT / 2;

            LeafNode newLeaf = new LeafNode();

            newLeaf.records.addAll(records.subList(middle, records.size()));

            records.subList(middle, records.size()).clear();

            newLeaf.next = this.next;
            this.next = newLeaf;

            return newLeaf;
        }

        @Override
        boolean isLeaf() {
            return true;
        }
    }

    private Node root;

    public BPlusTree() {
        // TODO: Impl.
        this.root = new LeafNode();
    }

    private IndexNode findIndexNode(Node current, IndexNode target) {
        if (current.isLeaf()) {
            return null;
        }

        IndexNode indexNode = (IndexNode) current;

        if (indexNode == target) {
            return indexNode;
        }

        for (Node child : indexNode.children) {
            IndexNode result = findIndexNode(child, target);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    public V insert(K key, V value) {
        // TODO: Impl.

        Record temp = new Record(key);
        temp.setValue(value);

        if (root.isLeaf()) {
            if (((LeafNode) root).records.size() > ENTRY_COUNT - 1) {
                LeafNode newLeaf = (LeafNode) root.split();
                IndexNode newIndex = new IndexNode();
                newIndex.keys.add(newLeaf.records.get(0).key);
                newIndex.children.add(root);
                newIndex.children.add(newLeaf);
                root = newIndex;
            }

            LeafNode leaf = (LeafNode) findLeaf(key);
            if (leaf != null) {
                insertIntoLeaf(leaf, temp);
            }
        } else {
            if (((IndexNode) root).keys.size() > ENTRY_COUNT - 1) {
                IndexNode newIndex = (IndexNode) root.split();
                IndexNode index = new IndexNode();
                index.keys.add(newIndex.keys.get(0));
                index.children.add(root);
                index.children.add(newIndex);
                root = index;
            }

            IndexNode newIndex = (IndexNode) findIndexNode(root, (IndexNode) root);
            if (newIndex != null) {
                insertIntoIndexNode(newIndex, temp);
            }
        }

        return null;
    }

    private void insertIntoIndexNode(IndexNode newIndex, Record record) {
        int i = 0;
        while (i < newIndex.keys.size() && newIndex.keys.get(i).compareTo(record.getKey()) < 0) {
            i++;
        }
        Node child = newIndex.children.get(i);
        if (child.isLeaf()) {
            LeafNode leaf = (LeafNode) child;
            insertIntoLeaf(leaf, record);
        } else {
            insertIntoIndexNode((IndexNode) child, record);
        }
    }

    private void insertIntoLeaf(LeafNode leaf, Record record) {
        int i = 0;
        while (i < leaf.records.size() && leaf.records.get(i).getKey().compareTo(record.getKey()) < 0) {
            i++;
        }
        leaf.records.add(i, record);
    }

    private Node findLeaf(K key) {
        if (root == null) {
            return null;
        }
        Node node = root;
        while (!node.isLeaf()) {
            IndexNode indexNode = (IndexNode) node;
            int i = 0;
            while (i < indexNode.keys.size() && key.compareTo(indexNode.keys.get(i)) > 0) {
                i++;
            }
            node = indexNode.children.get(i);
        }
        return node;
    }

    public V pointQuery(K key) {
        // TODO: Impl.
        Node node = (LeafNode) findLeaf(key);

        if (root == null || node == null) {
            return null;
        }

        if (node.isLeaf()) {
            LeafNode leaf = (LeafNode) node;

            for (Record record : leaf.records) {
                if (record.key.equals(key)) {
                    return record.value;
                }
            }
        }

        return null;
    }

    public List<? extends Map.Entry<K, V>> rangeQuery(K minKey, K maxKey) {
        // TODO: Impl.
        List<Map.Entry<K, V>> result = new ArrayList<>();

        Node node = findLeaf(minKey);

        if (root == null || node == null) {
            return List.of();
        }

        while (node != null && node.isLeaf()) {
            LeafNode leaf = (LeafNode) node;

            for (Record record : leaf.records) {
                if (record.key.compareTo(minKey) >= 0 && record.key.compareTo(maxKey) <= 0) {
                    result.add(record);
                } else if (record.key.compareTo(maxKey) > 0) {
                    return result;
                }
            }

            node = leaf.next;
        }

        return result;
    }

    public static void main(String[] args) {
        BPlusTree<Integer, String> tree = new BPlusTree<>();
        for (int i = 0; i < 100; i++) {
            tree.insert(i, "" + i);

            for (int j = 0; j <= i; j++) {
                if (!tree.pointQuery(i).equals("" + i)) {
                    throw new RuntimeException("Key not found: " + j);
                }
            }

            if (tree.rangeQuery(0, i).size() != i + 1) {
                throw new RuntimeException("Range query failed at key " + i);
            }
        }
    }
}
