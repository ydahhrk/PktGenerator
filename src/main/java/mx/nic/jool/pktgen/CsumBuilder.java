package mx.nic.jool.pktgen;

import java.io.IOException;
import java.io.OutputStream;

public class CsumBuilder extends OutputStream {

	public static ChecksumStatus checksumStatus = ChecksumStatus.CORRECT;
	private long accumulated = 0;

	private void sum(int byte1, int byte2) {
		accumulated += byte1 << 8 | byte2;

		long carry = accumulated >> 16;
		if (carry != 0)
			accumulated = (accumulated & 0xFFFF) + carry;
	}

	@Override
	public void write(int arg0) throws IOException {
		sum((arg0 >> 24) & 0xFF, (arg0 >> 16) & 0xFF);
		sum((arg0 >> 8) & 0xFF, (arg0 >> 0) & 0xFF);
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		/* TODO does not support headers with uneven length. */
		for (int x = 0; x < bytes.length; x += 2) {
			int byte1 = bytes[x];
			if (byte1 < 0)
				byte1 += 256;
			int byte2 = (x + 1 != bytes.length) ? bytes[x + 1] : 0;
			if (byte2 < 0)
				byte2 += 256;
			sum(byte1, byte2);
		}
	}

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
			break;
		case ZERO:
			result = 0xFFFF;
			break;
		}

		return ~result;
	}

}
