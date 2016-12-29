package mx.nic.jool.pktgen.auto;

import java.io.IOException;
import java.io.InputStream;

import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;

public class Util {

	public static void writePacket(String fileName, PacketContent... content) throws IOException {
		writePacket(fileName, new Packet(content));
	}

	public static void writePacket(String fileName, Packet packet) throws IOException {
		packet.postProcess();
		packet.export(fileName);
	}

	public static int[] streamToArray(InputStream in, int arrayLength) throws IOException {
		byte[] bytes = new byte[arrayLength];
		int bytesRead = 0;
		do {
			bytesRead = in.read(bytes, bytesRead, arrayLength - bytesRead);
		} while (bytesRead < arrayLength);

		int[] ints = new int[bytes.length];
		for (int i = 0; i < bytes.length; i++)
			ints[i] = bytes[i] & 0xFF; // "& 0xFF" is a cumbersome means to
										// ensure the result is positive.
		return ints;
	}

	public static int joinBytes(int[] array, int int1, int int2) {
		return joinBytes(array[int1], array[int2]);
	}

	public static int joinBytes(int... ints) {
		int result = 0;
		for (int current : ints)
			result = (result << 8) | current;
		return result;
	}

	public static void printTabs(int tabs) {
		for (int i = 0; i < tabs; i++)
			System.out.print("\t");
	}
}
