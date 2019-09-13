package vertexCover.advanced;

import core.Graph;

/**
 * {@link UndoStack} is a class which represents a single-linked-list.
 * Is is used to avoid copying a {@link Graph}, because creating a new {@link Graph} and copying all the data from the old one is very time-consuming.
 */
public class UndoStack {
    /**
     * Item to hold an operation to undo changes made to a {@link Graph}.
     */
    public abstract static class UndoItem { // abstract class because interface can't have fields (otherwise it would be better to use a FunctionalInterface)
        /**
         * Previous {@link UndoItem} in a {@link UndoStack}.
         */
        private UndoItem prev = null;

        /**
         * Runs code to undo changes made to a {@link Graph}.
         */
        public abstract void undo();
    }

    private UndoItem tail;
    private int size;

    /**
     * Constructs a new empty {@link UndoStack}.
     */
    public UndoStack() {
        tail = null;
        size = 0;
    }

    /**
     * Returns the number of {@link UndoItem}s in this {@link UndoStack}.
     *
     * @return the number of {@link UndoItem}s in this {@link UndoStack}
     */
    public int size() {
        return size;
    }

    /**
     * Returns the last (newest) {@link UndoItem} on this {@link UndoStack}.
     *
     * @return the last (newest) {@link UndoItem} on this {@link UndoStack}
     */
    public UndoItem pop() {
        if (size != 0) {
            UndoItem temp = tail;
            tail = tail.prev;
            size--;
            return temp;
        }
        return null;
    }

    /**
     * Appends the given {@link UndoItem} at the end of this {@link UndoStack}.
     *
     * @param ui is the {@link UndoItem} which will be added to this {@link UndoStack}
     */
    public void push(UndoItem ui) {
        ui.prev = tail;
        tail = ui;
        size++;
    }

}
