package mx.nic.jool.pktgen;

import java.io.IOException;
import java.io.OutputStream;

/**
 * It is assumed to be the typical IP checksum: A negated sum of of some
 * protocol-dependent sequence of 16-bit words.
 */
public class ChecksumBuilder extends OutputStream {

	/** The type of checksum this builder should generate. */
	public static ChecksumStatus checksumStatus = ChecksumStatus.CORRECT;
	/**
	 * Current sum of the bytes added to the checksum.
	 * <p>
	 * Please do not edit this number in any way unless it's via
	 * {@link #sum(int, int)} or you know what you're doing.
	 */
	private long accumulated = 0;
	/**
	 * Did we write an odd number of bytes in the checksum during the last
	 * write?
	 * <p>
	 * This is relevant because the user needs to be warned when this happens.
	 */
	boolean wroteUnevenLength = false;

	/**
	 * Adds the 16-bit word formed by concatenating <code>byte1</code> and
	 * <code>byte2</code> to this checksum.
	 */
	private void sum(int byte1, int byte2) {
		if (wroteUnevenLength) {
			System.err.println("Warning: Content is being added to this checksum after an uneven-sized byte batch.");
			// BTW: Saying "The result is probably going to be an incorrect
			// checksum" is inaccurate, because the user *might* be trying to
			// create an incorrect checksum, in which case the corruption
			// *could* end up fixing it :p
			System.err.println("The result is probably not going to be the intended checksum.");
			wroteUnevenLength = false;
		}

		accumulated += byte1 << 8 | byte2;

		long carry = accumulated >> 16;
		if (carry != 0)
			accumulated = (accumulated & 0xFFFF) + carry;
	}

	/**
	 * Adds the 32-bit word <code>word</code> to this checksum.
	 */
	@Override
	public void write(int word) throws IOException {
		sum((word >> 24) & 0xFF, (word >> 16) & 0xFF);
		sum((word >> 8) & 0xFF, (word >> 0) & 0xFF);
	}

	/**
	 * Adds the <code>bytes</code> sequence of bytes to this checksum.
	 * <p>
	 * Bear in mind that checksums sum groups of 16-bit words, not bytes, and
	 * the last byte is the only one not required to have a couple. (If it
	 * doesn't, the checksum couples it with an imaginary zero.) In other words,
	 * <code>bytes</code> is assumed to be an even-sized array, unless it is the
	 * last group of bytes you intend to add to this checksum.
	 * <p>
	 * Normally, this is not a problem because all existing headers lengths are
	 * aligned to 16-bit boundaries by design, on very explicit purpose.
	 * However, if you're building an invalid packet and you have some
	 * intermediate header that's not even-sized (such as a misplaced payload),
	 * this code will likely not generate the checksum you want.
	 */
	@Override
	public void write(byte[] bytes) throws IOException {
		for (int x = 0; x < bytes.length; x += 2) {
			int byte1 = bytes[x];
			if (byte1 < 0)
				byte1 += 256;
			int byte2 = (x + 1 != bytes.length) ? bytes[x + 1] : 0;
			if (byte2 < 0)
				byte2 += 256;
			sum(byte1, byte2);
		}

		wroteUnevenLength = (bytes.length & 1) == 1;
	}

	/**
	 * Call this method to retrieve the correctly-formatted version of the
	 * checksum accumulated so far.
	 */
	public int finish(ChecksumStatus status) {
		int result = 0;

		if (status == null)
			status = checksumStatus;

		switch (status) {
		case CORRECT:
			result = (int) (accumulated & 0xFFFF);
			break;
		case WRONG:
			accumulated += 1;
			result = (int) (accumulated & 0xFFFF);
			if (result == 0xFFFF)
				result = 2;
			break;
		case ZERO:
			result = 0xFFFF;
			break;
		}

		return ~result;
	}

}
