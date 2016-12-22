package mx.nic.jool.pktgen;

import java.io.IOException;
import java.io.OutputStream;

public class PacketUtils {

	public static void write8BitInt(OutputStream out, Integer integer) throws IOException {
		if (integer == null)
			integer = 0;

		integer = integer & 0xFF;
		out.write(integer);
	}

	public static void write16BitInt(OutputStream out, Integer integer) throws IOException {
		if (integer == null)
			integer = 0;

		out.write((integer >> 8) & 0xFF);
		out.write((integer >> 0) & 0xFF);
	}

	public static void write32BitInt(OutputStream out, Long integer) throws IOException {
		if (integer == null)
			integer = 0L;

		out.write((int) ((integer >> 24) & 0xFF));
		out.write((int) ((integer >> 16) & 0xFF));
		out.write((int) ((integer >> 8) & 0xFF));
		out.write((int) ((integer >> 0) & 0xFF));
	}

}
