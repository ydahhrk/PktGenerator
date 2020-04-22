package mx.nic.jool.pktgen.pojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.Stack;

import mx.nic.jool.pktgen.Util;
import mx.nic.jool.pktgen.enums.Layer;
import mx.nic.jool.pktgen.menu.MainMenuPrintable;
import mx.nic.jool.pktgen.proto.HeaderFactory;
import mx.nic.jool.pktgen.proto.l3.Ipv4Header;
import mx.nic.jool.pktgen.proto.l3.Ipv6Header;
import mx.nic.jool.pktgen.proto.l4.Layer4Header;

/**
 * An IP fragment. A subset of bytes from a packet which also has its own layer
 * 3 headers (when valid).
 * <p>
 * In this project, a {@link Packet} is <em>always</em> "made" out of fragments.
 * A {@link Fragment} object can represent the full packet if it was never
 * subdivided.
 * <p>
 * You might realize from the paragraph above that this class is partly more
 * akin to what people expects a "packet" to be, because in practice it
 * represents a bunch of glued bytes that travel together in a network.
 * Following such logic, this class might be renamed as "Packet", and the
 * current {@link Packet} would be renamed as "PacketGroup".
 * <p>
 * I still feel like the current naming is more aligned to the IP vision,
 * though. A "fragment" is the low-level actual byte sequence that is seen in
 * tcpdump captures (handled transparently by operating systems) and "packet" is
 * a logical aggregation of fragments (more meaningful as seen by upper layers).
 * <p>
 * It doesn't really matter. The separation between the {@link Packet} class and
 * the {@link Fragment} class is no longer as relevant as it used to be when I
 * employed this project to automate the generation of the basic graybox test
 * suite. It's more about manual creation of header sequences now.
 */
public class Fragment extends SliceableList<Header> implements MainMenuPrintable {

	/** Warning shutupper; I don't care about this. */
	private static final long serialVersionUID = 1L;

	public Fragment() {
		super();
	}

	public Fragment(Header... elements) {
		super(elements);
	}

	/**
	 * Returns the nexthdr identifier of the header that is after
	 * <code>header</code> within <code>packet</code>.
	 * 
	 * In other words, returns the correct value that <code>header</code>'s
	 * nexthdr field should have for <code>packet</code> to be valid.
	 */
	public int getNextHdr(Packet packet, Header header) {
		while (true) {
			Header next = this.getNext(header);
			if (next == null)
				throw new IllegalArgumentException("There are no valid nexthdrs after this. I don't know what to do.");

			if (next.getHdrIndex() >= 0)
				return next.getHdrIndex();

			if (next instanceof Payload) {
				// This is a "subsequent" fragment. We need to go find the layer
				// 4 header in the first fragment.
				for (Header currentHeader : packet.get(0)) {
					if (currentHeader instanceof Layer4Header) {
						return currentHeader.getHdrIndex();
					}
				}
				throw new IllegalArgumentException(
						"The first fragment lacks L4 headers. I don't know what to do. (Try assigning hdr4.protocol or frag.nextHeader manually.)");
			}

			header = next;
		}
	}

	/**
	 * Serializes this fragment into a byte sequence, as it would look in an
	 * actual wire, and writes it into file <code>fileName.pkt</code>.
	 */
	public void export(String fileName) throws IOException {
		if (!fileName.endsWith(".pkt"))
			fileName += ".pkt";

		File file = new File(fileName);
		if (file.exists())
			System.err.println("Warning: I'm rewriting file " + fileName);

		try (FileOutputStream out = new FileOutputStream(file)) {
			for (Header header : this) {
				out.write(header.toWire());
			}
		}
	}

	/**
	 * Returns the number of bytes that follow this fragment's starting batch of
	 * layer 3 headers. Intended to be used by field autocomputation utilities.
	 * <p>
	 * If the fragment does not start with a batch of layer 3 headers, the
	 * result is undefined. (This shouldn't be a problem. Users are not supposed
	 * to rely on field autocomputation if they intend to build an invalid
	 * packet.)
	 */
	public int getL3PayloadLength() {
		int result = 0;
		boolean foundL4 = false;

		if (size() == 0 || get(0).getLayer() == Layer.INTERNET) {
			System.err.println("Warning: Packet/Fragment does not start with a layer 3 header. "
					+ "I'm building a length that will likely end up being invalid.");
		}

		for (Header header : this) {
			if (header.getLayer().ht(Layer.INTERNET))
				foundL4 = true;
			if (foundL4)
				result += header.toWire().length;
		}

		return result;
	}

	@Override
	public void print(int tabs) {
		Util.printTabs(tabs);
		System.out.println("Packet so far:");
		if (isEmpty()) {
			Util.printTabs(tabs + 1);
			System.out.println("[Empty]");
		} else {
			for (int i = 0; i < size(); i++) {
				Util.printTabs(tabs + 1);
				System.out.printf("(%7d) %s\n", i, get(i).getClass().getSimpleName());
			}
		}
	}

	/**
	 * Returns the headers that precede <code>pivot</code> within this fragment.
	 * <p>
	 * Returns a stack because the only caller wants to iterate backwards.
	 */
	public Stack<Header> getHeaderBefore(Header pivot) {
		Stack<Header> result = new Stack<>();

		for (Header existingHeader : this) {
			if (existingHeader == pivot)
				break;
			result.push(existingHeader);
		}

		return result;
	}

	/**
	 * Returns the fragment stored in <code>file</code>.
	 */
	public static Fragment load(File file) throws IOException {
		Header currentHeader;

		try (FileInputStream in = new FileInputStream(file)) {
			int firstByte = in.read();
			switch (firstByte >> 4) {
			case 4:
				currentHeader = new Ipv4Header();
				break;
			case 6:
				currentHeader = new Ipv6Header();
				break;
			default:
				Scanner scanner = new Scanner(System.in);
				do {
					HeaderFactory.printStringProtocols();
					System.out.print("I can't recognize the packet's first header. What is it? ");
					currentHeader = HeaderFactory.forName(scanner.nextLine());
				} while (currentHeader == null);
			}
		}

		Fragment frag = new Fragment();

		// Yes, open it again. We already wasted one byte and I don't want to
		// handle that.
		try (FileInputStream in = new FileInputStream(file)) {
			do {
				Header nextHeader = currentHeader.loadFromStream(in);
				frag.add(currentHeader);
				currentHeader = nextHeader;
			} while (currentHeader != null);
		}

		return frag;
	}

	/**
	 * Randomizes the fields from this fragment's headers as much as possible.
	 * <p>
	 * Despite that, it attempts to build a valid fragment. Vital fields, such
	 * as header identifiers and lengths, are not randomized.
	 */
	public void randomize() {
		for (Header header : this)
			header.randomize();
	}

	/**
	 * Nullifies all the checksums found on this fragment's headers.
	 * <p>
	 * (The idea is to correct all checksums. Checksums left unset are
	 * autocomputed when the packet is committed into a file.)
	 */
	public void unsetAllChecksums() {
		for (Header header : this)
			header.unsetChecksum();
	}

	/**
	 * Nullifies all the length fields found on this fragment's headers.
	 * <p>
	 * (The idea is to correct all lengths. Lengths left unset are autocomputed
	 * when the packet is committed into a file.)
	 */
	public void unsetAllLengths() {
		for (Header header : this)
			header.unsetLengths();
	}

	/**
	 * Returns the aggregated length of this fragment's headers and payload, in
	 * bytes.
	 */
	public int getLength() {
		int length = 0;
		for (Header header : this)
			length += header.toWire().length;
		return length;
	}
}
