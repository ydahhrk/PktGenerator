package mx.nic.jool.pktgen;

public class BitArrayOutputStream {

	private byte[] bytes;
	private int bytesOffset;

	private byte bits;
	private int bitOffset;

	public BitArrayOutputStream(int size) {
		this.bytes = new byte[size];
	}

	/**
	 * Writes <code>bit</code>'s rightmost bit in <code>bits</code>.
	 */
	private void writeBit(int bit) {
		bits |= ((bit & 1) << (7 - bitOffset));
		bitOffset++;
		if (bitOffset == 8) {
			writeByte(bits);
			bits = 0;
			bitOffset = 0;
		}
	}

	private void writeByte(byte data) {
		bytes[bytesOffset++] = data;
	}

	public void write(byte[] bytes) {
		for (int i = 0; i < bytes.length; i++)
			writeByte(bytes[i]);
	}

	public void write(int bits, int bitCount) {
		for (int b = 0; b < bitCount; b++)
			writeBit(bits >> (bitCount - b - 1));
	}

	public byte[] toByteArray() {
		if (bitOffset != 0)
			throw new IllegalArgumentException("There are leftover bits.");
		return bytes;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("bytes:");
		for (int i = 0; i < bytesOffset; i++) {
			sb.append(bytes[i]).append(",");
		}
		
		sb.append("\nbits:");
		for (int i = 0; i < 8; i++)
			sb.append((bits >> (7 - i)) & 1);
		sb.append(" bitOffset:").append(bitOffset);

		return sb.toString();
	}

}
