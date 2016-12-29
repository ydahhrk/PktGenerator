package mx.nic.jool.pktgen.pojo;

import java.util.ArrayList;
import java.util.List;

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
	 */
	public List<T> sliceExclusive(T t) {
		int fromIndex = this.indexOf(t) + 1;
		int toIndex = this.size();
		return this.subList(fromIndex, toIndex);
	}

	/**
	 * Returns a list of elements containing all the elements after and
	 * including <code>t</code>.
	 */
	public List<T> sliceInclusive(T t) {
		int fromIndex = this.indexOf(t);
		int toIndex = this.size();
		return this.subList(fromIndex, toIndex);
	}

	/**
	 * Returns the content after <code>content</code>.
	 */
	public T getNext(T content) {
		int id = this.indexOf(content) + 1;
		return (id < this.size()) ? this.get(id) : null;
	}

	/**
	 * Returns the content right before <code>content</code>.
	 */
	public T getPrevious(T content) {
		int id = this.indexOf(content) - 1;
		return (id >= 0) ? this.get(id) : null;
	}

}
