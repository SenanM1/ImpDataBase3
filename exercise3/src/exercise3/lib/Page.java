package exercise3.lib;

import java.util.Iterator;

/**
 * Represents a physical page of fixed size for storing records of type T
 */
public interface Page<T> extends Convertable, Iterable<T> {
    /**
     * @return the bytes left for storing items inside this page
     */
    int getFreeSpace();

    /**
     * @return the size of a single record
     */
    int getRecordSize();

    /**
     * Stores the given element in this page
     *
     * @param element the element to store
     * @return the id to access the stored item within the page
     */
    short store(T element);

    /**
     * Deletes the record for the given id from this page
     *
     * @param id the id of the record to remove
     */
    void delete(short id);

    /**
     * @param id the id of the item to retrieve
     * @return the record for the given id
     */
    T get(short id);

    /**
     * @return an iterator enumerating all valid ids in this page
     */
    Iterator<Short> ids();

    /**
     * @return an iterator enumerating all records stored in this page
     */
    default Iterator<T> iterator() {
        final Iterator<Short> ids = ids();
        return new Iterator<T>() {

            @Override
            public boolean hasNext() {
                return ids.hasNext();
            }

            @Override
            public T next() {
                return get(ids.next());
            }
        };
    }

    default void print() {
        StringBuilder str = new StringBuilder();

        for(var it = this.ids(); it.hasNext(); ) {
            Short id = it.next();
            str.append(id).append(": ").append(this.get(id)).append('\n');
        }

        System.out.println(str);
    }
}
