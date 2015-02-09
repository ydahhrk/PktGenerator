package mx.nic.jool.pktgen.pojo;

import java.io.IOException;
import java.util.Arrays;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.proto.Protocol;

public class Payload implements PacketContent {

	private byte[] bytes;

	public Payload() {
		this(56);
	}
	
	public Payload(int size) {
		this(size, 0);		
	}
	
	public Payload(int size, int offset) {
		bytes = new byte[size];
		for (int x = 0; x < size; x++) {
			bytes[x] = (byte) (x + offset);
		}
	}
	
	@Override
	public void readFromStdIn(FieldScanner scanner) {
		boolean readFromFile = false;
		byte[] tempFile = null;

		readFromFile = scanner.readBoolean("Read from file?", false);

		do {
			if (!readFromFile) {
				break;
			}

			tempFile = scanner.readFile();

			if (tempFile == null) {
				System.err.println("No se pudo leer el archivo correctamente.");
				readFromFile = scanner.readBoolean("Try again?", false);
			}
		} while (tempFile == null);

		if (tempFile != null) {
			boolean customLength = scanner.readBoolean("custom length", false);
			if (customLength) {
				System.out.println("Actual length: " + tempFile.length);
				int length = scanner.readInt("Payload length", 4);
				bytes = Arrays.copyOf(tempFile, length);
			} else {

				// bytes = new byte[tempFile2.length];
				// for (int i = 0; i < tempFile2.length; i++) {
				// bytes[i] = (byte) tempFile2[i];
				// }
				bytes = tempFile;
			}
		} else {
			int length = scanner.readInt("Payload length", 4);
			int payloadNumber = 0;
			bytes = new byte[length];
			boolean customPayload;
			customPayload = scanner.readBoolean(
					"Automatic Insert [0,1,2,3..255,0,1,2...]", true);
			if (customPayload) {
				for (int i = 0; i < length; i++) {
					bytes[i] = (byte) payloadNumber;
					payloadNumber++;
					if (payloadNumber > 255)
						payloadNumber = 0;
				}
			} else {
				for (int x = 0; x < length; x++) {
					bytes[x] = (byte) scanner.readInt("byte " + x,
							payloadNumber);
					payloadNumber++;
					if (payloadNumber > 255)
						payloadNumber = 0;
				}
			}
		}

		if (bytes.length % 2 == 1) {
			// See pkt.CsumBuilder#write(byte[]).
			System.out
					.println("Warning: If you append stuff after this payload, "
							+ "your checksums will go bananas.");
		}
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
	public Protocol getProtocol() {
		return Protocol.PAYLOAD;
	}

	@Override
	public String getShortName() {
		return "payload";
	}

	@Override
	public void postProcess(Packet packet, Fragment fragment)
			throws IOException {
		// No code
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

}
