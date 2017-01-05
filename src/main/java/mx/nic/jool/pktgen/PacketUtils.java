package mx.nic.jool.pktgen;

public class PacketUtils {

	public static void write8BitInt(ByteArrayOutputStream out, Integer integer) {
		if (integer == null)
			integer = 0;

		integer = integer & 0xFF;
		out.write(integer);
	}

	public static void write16BitInt(ByteArrayOutputStream out, Integer integer) {
		if (integer == null)
			integer = 0;

		out.write((integer >> 8) & 0xFF);
		out.write((integer >> 0) & 0xFF);
	}

	public static void write32BitInt(ByteArrayOutputStream out, Long integer) {
		if (integer == null)
			integer = 0L;

		out.write((int) ((integer >> 24) & 0xFF));
		out.write((int) ((integer >> 16) & 0xFF));
		out.write((int) ((integer >> 8) & 0xFF));
		out.write((int) ((integer >> 0) & 0xFF));
	}

}
