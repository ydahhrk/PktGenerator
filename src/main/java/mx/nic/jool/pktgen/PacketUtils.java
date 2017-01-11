package mx.nic.jool.pktgen;

import java.io.IOException;
import java.io.InputStream;

/**
 * Several static methods to read and write packet fields.
 */
public class PacketUtils {

	/**
	 * Assumes only the lower 8 bits of <code>integer</code> matter and writes
	 * them to <code>out</code>. (As a single-byte unsigned number.)
	 */
	public static void write8BitInt(ByteArrayOutputStream out, Integer integer) {
		if (integer == null)
			integer = 0;

		integer = integer & 0xFF;
		out.write(integer);
	}

	/**
	 * Assumes only the lower 16 bits of <code>integer</code> matter and writes
	 * them to <code>out</code>. (As a 2-byte unsigned big-endian number.)
	 */
	public static void write16BitInt(ByteArrayOutputStream out, Integer integer) {
		if (integer == null)
			integer = 0;

		out.write((integer >> 8) & 0xFF);
		out.write((integer >> 0) & 0xFF);
	}

	/**
	 * Assumes only the lower 32 bits of <code>integer</code> matter and writes
	 * them to <code>out</code>. (As a 4-byte unsigned big-endian number.)
	 */
	public static void write32BitInt(ByteArrayOutputStream out, Long integer) {
		if (integer == null)
			integer = 0L;

		out.write((int) ((integer >> 24) & 0xFF));
		out.write((int) ((integer >> 16) & 0xFF));
		out.write((int) ((integer >> 8) & 0xFF));
		out.write((int) ((integer >> 0) & 0xFF));
	}

	/**
	 * Reads <code>arrayLength</code> bytes from <code>in</code> and returns
	 * them as an integer array array. (One integer per byte.)
	 */
	public static int[] streamToIntArray(InputStream in, int arrayLength) throws IOException {
		byte[] bytes = streamToByteArray(in, arrayLength);

		int[] ints = new int[bytes.length];
		for (int i = 0; i < bytes.length; i++)
			ints[i] = bytes[i] & 0xFF; // "& 0xFF" is a cumbersome means to
										// ensure the result is positive.
		return ints;
	}

	/**
	 * Reads <code>arrayLength</code> bytes from <code>in</code> and returns
	 * them as a byte array.
	 * <p>
	 * (Shit; this sounds like I should have offloaded it to some apache
	 * library. Oh well, maybe later.)
	 */
	public static byte[] streamToByteArray(InputStream in, int arrayLength) throws IOException {
		byte[] bytes = new byte[arrayLength];
		int bytesRead = 0;
		do {
			bytesRead = in.read(bytes, bytesRead, arrayLength - bytesRead);
		} while (bytesRead < arrayLength);

		return bytes;
	}

	/**
	 * Concatenates the <code>int1</code>th and <code>int2</code>th bytes of
	 * <code>array</code> into a 16-bit number and returns it.
	 */
	public static int joinBytes(int[] array, int int1, int int2) {
		return joinBytes(array[int1], array[int2]);
	}

	/**
	 * Builds a number by contatenating the <code>ints</code> members at bit
	 * level. Returns the result.
	 */
	public static int joinBytes(int... ints) {
		int result = 0;
		for (int current : ints)
			result = (result << 8) | current;
		return result;
	}

}
