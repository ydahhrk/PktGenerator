package mx.nic.jool.pktgen.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A list from which getting useful sublists is fairly uncluttered.
 */
public abstract class SliceableList<T> extends ArrayList<T> {

	/** Warning shutupper; I don't care about this. */
	private static final long serialVersionUID = 1L;

	public SliceableList() {
		super();
	}

	@SafeVarargs
	public SliceableList(T... elements) {
		this();
		for (T t : elements) {
			add(t);
		}
	}

	/**
	 * Returns a list of elements containing all the elements after
	 * <code>t</code>.
	 * <p>
	 * <code>t</code> is <em>assumed</em> to be a member of this list, otherwise
	 * unchecked exception.
	 */
	public List<T> sliceExclusive(T t) {
		int fromIndex = getIndexOf(t) + 1;
		int toIndex = this.size();
		return this.subList(fromIndex, toIndex);
	}

	/**
	 * Returns a list of elements containing all the elements after and
	 * including <code>t</code>.
	 * <p>
	 * <code>t</code> is <em>assumed</em> to be a member of this list, otherwise
	 * unchecked exception.
	 */
	public List<T> sliceInclusive(T t) {
		int fromIndex = getIndexOf(t);
		int toIndex = this.size();
		return this.subList(fromIndex, toIndex);
	}

	/**
	 * Returns the list element right after <code>t</code>.
	 * <p>
	 * <code>t</code> is <em>assumed</em> to be a member of this list, otherwise
	 * unchecked exception.
	 */
	public T getNext(T t) {
		int id = getIndexOf(t) + 1;
		return (id < this.size()) ? this.get(id) : null;
	}

	/**
	 * Returns the list element right before <code>t</code>.
	 * <p>
	 * <code>t</code> is <em>assumed</em> to be a member of this list, otherwise
	 * unchecked exception.
	 */
	public T getPrevious(T t) {
		int id = getIndexOf(t) - 1;
		return (id >= 0) ? this.get(id) : null;
	}

	private int getIndexOf(T t) {
		int index = this.indexOf(t);
		if (index == -1)
			throw new NoSuchElementException("The element does not belong to this list.");
		return index;
	}

}
