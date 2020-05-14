package mx.nic.jool.pktgen.pojo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Packet {

	private String name;
	private Header firstHeader;
	private Header lastHeader;

	public Packet(String name) {
		this.name = name;
	}

	public void add(Header header) {
		if (firstHeader == null) {
			firstHeader = header;
			lastHeader = header;
		} else {
			lastHeader.setNext(header);
			lastHeader = header;
		}
	}

	/**
	 * Autocomputes the header fields of this packet that the user left undefined.
	 */
	private void postProcess() throws IOException {
		// Header fields often depend on subsequent headers (eg. checksums need
		// all subheaders to be already initialized), so we'll do this in
		// reverse order (from last to first).
		for (Header header = lastHeader; header != null; header = header.getPrevious())
			header.postProcess();
	}

	/**
	 * Serializes this fragment into a byte sequence, as it would look in an actual
	 * wire, and writes it into file <code>name.pkt</code>.
	 */
	public void export() throws IOException {
		String fileName = name;
		if (!fileName.endsWith(".pkt"))
			fileName += ".pkt";

		postProcess();

		System.out.println("Writing packet:");
		for (Header header = firstHeader; header != null; header = header.getNext())
			System.out.println(header);

		File file = new File(fileName);
		if (file.exists())
			System.err.println("Warning: I'm rewriting file " + fileName);

		try (FileOutputStream out = new FileOutputStream(file)) {
			for (Header header = firstHeader; header != null; header = header.getNext()) {
				out.write(header.toWire());
			}
		}
	}

}
