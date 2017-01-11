package mx.nic.jool.pktgen.pojo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import mx.nic.jool.pktgen.enums.Layer;

/**
 * A logical aggregation of fragments. See {@link Fragment}.
 */
public class Packet extends SliceableList<Fragment> {

	/** Warning shutupper; I don't care about this. */
	private static final long serialVersionUID = 1L;

	public Packet() {
		super();
	}

	public Packet(Fragment... frags) {
		super(frags);
	}

	/**
	 * Computes and returns a list containing all the logical high layer (4 and
	 * above) headers after <code>pivot</code>. Intended to be used by field
	 * autocomputation utilities.
	 * <p>
	 * By "logical" I mean that it also contains the relevant headers found in
	 * subsequent fragments.
	 * <p>
	 * The overall packet is assumed to be valid. (This shouldn't be a problem.
	 * Users are not supposed to rely on field autocomputation if they intend to
	 * build an invalid packet.)
	 */
	public List<Header> getUpperLayerHeadersAfter(Header pivot) {
		boolean foundPivot = false;
		List<Header> result = new ArrayList<>();

		for (Fragment fragment : this) {
			boolean foundLayer4 = false;

			for (Header currentHeader : fragment) {
				if (currentHeader.getLayer().ht(Layer.INTERNET))
					foundLayer4 = true;

				if (foundPivot && foundLayer4)
					result.add(currentHeader);

				if (currentHeader == pivot)
					foundPivot = true;
			}
		}

		return result;
	}

	/**
	 * Serializes this packet's fragments into byte sequences, as they would
	 * look in actual wires, and writes each of them in a dedicated file whose
	 * name is an enumeration prefixed with <code>fileName</code>.
	 */
	public void export(String fileName) throws IOException {
		if (size() == 1) {
			get(0).export(fileName);
		} else {
			for (int x = 0; x < size(); x++) {
				get(x).export(fileName + x);
			}
		}
	}

	/**
	 * Autocomputes the header fields of this packet that the user left
	 * undefined.
	 */
	public void postProcess() throws IOException {
		// Header fields often depend on subsequent headers (eg. checksums need
		// all subheaders to be already initialized), so we'll do this in
		// reverse order (from last to first).
		ListIterator<Fragment> fragmentIterator = listIterator(size());
		while (fragmentIterator.hasPrevious()) {
			Fragment fragment = fragmentIterator.previous();
			ListIterator<Header> headerIterator = fragment.listIterator(fragment.size());
			while (headerIterator.hasPrevious()) {
				Header header = headerIterator.previous();
				header.postProcess(this, fragment);
			}
		}
	}

	/**
	 * Randomizes the fields from this packet's headers as much as possible.
	 * <p>
	 * Despite that, it attempts to build a valid packet. Vital fields, such as
	 * header identifiers and lengths, are not randomized.
	 */
	public void randomize() {
		for (Fragment frag : this)
			frag.randomize();
	}
}
