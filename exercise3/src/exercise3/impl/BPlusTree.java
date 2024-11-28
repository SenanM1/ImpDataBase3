package exercise3.impl;

import java.util.*;

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
        final List<Node> children = new ArrayList<>(ENTRY_COUNT + 1);

        boolean isLeaf() {
            return false;
        }

        @Override
        Node split() {
            // TODO: Impl.
            return null;
        }
    }

    final class LeafNode extends Node {
        final List<Record> records = new ArrayList<>(ENTRY_COUNT);
        LeafNode next;

        @Override
        Node split() {
            // TODO: Impl.
            return null;
        }

        @Override
        boolean isLeaf() {
            return true;
        }
    }

    private Node root;

    public BPlusTree() {
        // TODO: Impl.
    }

    public V insert(K key, V value) {
        // TODO: Impl.
        return null;
    }

    public V pointQuery(K key) {
        // TODO: Impl.
        return null;
    }

    public List<? extends Map.Entry<K, V>> rangeQuery(K minKey, K maxKey) {
        // TODO: Impl.
        return List.of();
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
