package mx.nic.jool.pktgen.pojo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.Stack;

import mx.nic.jool.pktgen.enums.Layer;
import mx.nic.jool.pktgen.proto.PacketContentFactory;
import mx.nic.jool.pktgen.proto.l3.Ipv4Header;
import mx.nic.jool.pktgen.proto.l3.Ipv6Header;
import mx.nic.jool.pktgen.proto.l4.Layer4Header;

public class Fragment extends SliceableList<PacketContent> {

	/** Warning shutupper; I don't care about this. */
	private static final long serialVersionUID = 1L;

	public Fragment() {
		super();
	}

	public Fragment(PacketContent... elements) {
		super(elements);
	}

	public byte[] toWire() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		for (PacketContent content : this) {
			out.write(content.toWire());
		}

		return out.toByteArray();
	}

	public int getNextHdr(Packet packet, PacketContent content) {
		while (true) {
			PacketContent next = this.getNext(content);
			if (next == null)
				throw new IllegalArgumentException("There are no valid nexthdrs after this. I don't know what to do.");

			if (next.getHdrIndex() >= 0)
				return next.getHdrIndex();

			if (next instanceof Payload) {
				// This is a "subsequent" fragment. We need to go find the layer
				// 4 header in the first fragment.
				for (PacketContent currentContent : packet.get(0)) {
					if (currentContent instanceof Layer4Header) {
						return currentContent.getHdrIndex();
					}
				}
				throw new IllegalArgumentException("The first fragment lacks L4 headers. I don't know what to do.");
			}

			content = next;
		}
	}

	public void export(String fileName) throws IOException {
		fileName += ".pkt";

		File file = new File(fileName);
		if (file.exists())
			System.out.println("Warning: I'm rewriting file " + fileName);

		FileOutputStream out = new FileOutputStream(file);
		try {
			for (PacketContent content : this) {
				out.write(content.toWire());
			}
		} finally {
			out.close();
		}
	}

	public int getL3PayloadLength() throws IOException {
		int result = 0;
		boolean foundL4 = false;

		for (PacketContent content : this) {
			if (content.getLayer().ht(Layer.INTERNET))
				foundL4 = true;
			if (foundL4)
				result += content.toWire().length;
		}

		return result;
	}

	public void print() {
		System.out.println("Packet so far:");
		if (isEmpty()) {
			System.out.println("\t[Empty]");
		} else {
			for (int i = 0; i < size(); i++) {
				System.out.print("\t\t(");
				System.out.printf("%7d", i);
				System.out.print(") ");
				System.out.println(get(i).getClass().getSimpleName());
			}
		}
	}

	/**
	 * Returns a stack because the only caller wants to iterate backwards.
	 */
	public Stack<PacketContent> getContentBefore(PacketContent pivot) {
		Stack<PacketContent> result = new Stack<>();

		for (PacketContent content : this) {
			if (content == pivot)
				break;
			result.push(content);
		}

		return result;
	}

	public static Fragment load(File file) throws IOException {
		PacketContent currentContent;

		try (FileInputStream in = new FileInputStream(file)) {
			int firstByte = in.read();
			switch (firstByte >> 4) {
			case 4:
				currentContent = new Ipv4Header();
				break;
			case 6:
				currentContent = new Ipv6Header();
				break;
			default:
				Scanner scanner = new Scanner(System.in);
				do {
					PacketContentFactory.printStringProtocols();
					System.out.print("I can't recognize the packet's first header. What is it? ");
					currentContent = PacketContentFactory.forName(scanner.nextLine());
				} while (currentContent == null);
			}
		}

		Fragment frag = new Fragment();

		// Yes, open it again. We already wasted one byte and I don't want to
		// handle that.
		try (FileInputStream in = new FileInputStream(file)) {
			do {
				PacketContent nextContent = currentContent.loadFromStream(in);
				frag.add(currentContent);
				currentContent = nextContent;
			} while (currentContent != null);
		}

		return frag;
	}

	public void randomize() {
		for (PacketContent content : this)
			content.randomize();
	}

	public void unsetAllChecksums() {
		for (PacketContent content : this)
			content.unsetChecksum();
	}

	public void unsetAllLengths() {
		for (PacketContent content : this)
			content.unsetLengths();
	}
}
