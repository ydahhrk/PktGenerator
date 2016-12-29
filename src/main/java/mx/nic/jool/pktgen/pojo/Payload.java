package mx.nic.jool.pktgen.pojo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.enums.Layer;

/**
 * A packet content consisting of an arbitrary sequence of bytes.
 */
public class Payload extends PacketContent {

	private byte[] bytes;

	public Payload() {
		this(56);
	}

	public Payload(int size) {
		this(size, 0);
	}

	/**
	 * Will initialize {@link #bytes} using incrementing numbers.
	 * 
	 * ie. bytes = new byte[] { 0, 1, 2, 3, 4, 5, ... };
	 */
	public Payload(int size, int offset) {
		bytes = new byte[size];
		for (int x = 0; x < size; x++)
			bytes[x] = (byte) (x + offset);
	}

	@Override
	public void readFromStdIn(FieldScanner scanner) {
		bytes = readBytesFromFile(scanner);
		if (bytes != null)
			return;

		bytes = readBytesFromStdin(scanner);
	}

	private byte[] readBytesFromFile(FieldScanner scanner) {
		boolean readFromFile = scanner.readBoolean("Read from file?", false);
		if (!readFromFile)
			return null;

		byte[] result = scanner.readFile();
		if (result == null)
			return null;

		System.out.println("Length: " + result.length);
		boolean customLength = scanner.readBoolean("Truncate?", false);
		if (customLength) {
			int length = scanner.readInt("New lengh", 4);
			result = Arrays.copyOf(result, length);
		}

		return result;
	}

	private byte[] readBytesFromStdin(FieldScanner scanner) {
		int length = scanner.readInt("Length", 4);
		byte[] result = new byte[length];
		boolean auto = scanner.readBoolean("Automatic insert (0,1,2,3..255,0,1,2,...)", true);
		for (int i = 0; i < length; i++)
			result[i] = (byte) (auto ? (i & 0xFF) : scanner.readInt("byte " + i, i % 0xFF));

		if (result.length % 2 == 1) {
			// See pkt.CsumBuilder#write(byte[]).
			System.out.println("Warning: If you append stuff after this payload, your checksums will go bananas.");
		}

		return result;
	}

	@Override
	public byte[] toWire() throws IOException {
		return bytes;
	}

	@Override
	public PacketContent createClone() {
		Payload result = new Payload();

		result.bytes = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			result.bytes[i] = bytes[i];
		}

		return result;
	}

	@Override
	public String getShortName() {
		return "payload";
	}

	@Override
	public void postProcess(Packet packet, Fragment fragment) throws IOException {
		// No code
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	@Override
	public void modifyFromStdIn(FieldScanner scanner) {
		readFromStdIn(scanner);
	}

	@Override
	public int getHdrIndex() {
		return -1;
	}

	@Override
	public Layer getLayer() {
		return Layer.APPLICATION;
	}

	@Override
	public PacketContent loadFromStream(InputStream in) throws IOException {
		ByteArrayOutputStream builder = new ByteArrayOutputStream();
		byte[] buffer = new byte[256];

		do {
			int bytesRead = in.read(buffer);
			if (bytesRead == -1) {
				bytes = builder.toByteArray();
				return null;
			}
			builder.write(buffer, 0, bytesRead);
		} while (true);
	}

	@Override
	public void randomize() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		random.nextBytes(bytes);
	}

	@Override
	public void unsetChecksum() {
		// No checksums.
	}

	@Override
	public void unsetLengths() {
		// No lengths.
	}
}
