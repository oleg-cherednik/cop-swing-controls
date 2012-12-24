package cop.swing;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Oleg Cherednik
 * @since 09.04.2012
 */
public class ArrayIterator<E> implements Iterator<E> {

	private final E[] array;
	private int index = 0;

	public ArrayIterator(E... array) {
		this.array = array;
	}

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other words, returns <tt>true</tt> if <tt>next</tt>
	 * would return an element rather than throwing an exception.)
	 * 
	 * @return <tt>true</tt> if the iterator has more elements.
	 */
	public boolean hasNext() {
		if (this.array == null) {
			return false;
		}
		return index >= 0 && index < this.array.length;
	}

	/**
	 * Returns the next element in the iteration.
	 * 
	 * @return the next element in the iteration.
	 * @exception NoSuchElementException iteration has no more elements.
	 */
	public E next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return array[index++];
	}

	/**
	 * Not supported operation
	 * 
	 * @throws UnsupportedOperationException
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
