package mx.nic.jool.pktgen.pojo;

import mx.nic.jool.pktgen.pojo.shortcut.FastPayloadShortcut;
import mx.nic.jool.pktgen.pojo.shortcut.FilePayloadShortcut;
import mx.nic.jool.pktgen.pojo.shortcut.PaddingPayloadShortcut;
import mx.nic.jool.pktgen.pojo.shortcut.Shortcut;
import mx.nic.jool.pktgen.pojo.shortcut.TruncatePayloadShortcut;
import mx.nic.jool.pktgen.type.ByteArrayField;
import mx.nic.jool.pktgen.type.Field;

/**
 * A "Header" whose only field is an arbitrary sequence of bytes. Often placed
 * at the end of a packet, and contains the actual information the user wants to
 * transmit.
 * <p>
 * (It's kind of ironic that this one is kind of an outlier in that it's the
 * only one that's not technically a "header", and yet it's the only one that
 * matters at the end of the day.)
 */
public class Payload extends Header {

	private ByteArrayField bytes;
	private Field[] fields;

	public static Payload zeroes(int length) {
		byte[] bytes = new byte[length];
		return new Payload(bytes);
	}
	
	/**
	 * Will initialize {@link #bytes} using incrementing numbers.
	 * <p>
	 * ie. bytes = new byte[] { 0, 1, 2, 3, 4, 5, ... };
	 */
	public static Payload monotonic(int size, int offset) {
		byte[] bytes = new byte[size];
		for (int x = 0; x < size; x++)
			bytes[x] = (byte) (x + offset);
		return new Payload(bytes);
	}

	public Payload(byte[] bytes) {
		this.bytes = new ByteArrayField("bytes", bytes);
		this.fields = new Field[] { this.bytes };
	}

	@Override
	public Field[] getFields() {
		return fields;
	}

	@Override
	public String getName() {
		return "Payload";
	}

	@Override
	public void postProcess() {
		// No code
	}

	public byte[] getBytes() {
		return this.bytes.getValue();
	}

	public void setBytes(byte[] bytes) {
		this.bytes.setValue(bytes);
	}

	@Override
	public int getHdrIndex() {
		return -1;
	}

	@Override
	public void swapIdentifiers() {
		// No identifiers.
	}

	@Override
	public Shortcut[] getShortcuts() {
		return new Shortcut[] { //
				new FastPayloadShortcut(), //
				new FilePayloadShortcut(), //
				new PaddingPayloadShortcut(), //
				new TruncatePayloadShortcut(), //
		};
	}

}
