package mx.nic.jool.pktgen.pojo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.annotation.HeaderField;
import mx.nic.jool.pktgen.enums.Layer;

/**
 * A packet content consisting of an arbitrary sequence of bytes.
 */
public class Payload implements PacketContent {

	@HeaderField
	private byte[] bytes;

	public Payload() {
		this(4);
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
	public byte[] toWire() {
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
