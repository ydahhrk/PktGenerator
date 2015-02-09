package mx.nic.jool.pktgen.pojo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import mx.nic.jool.pktgen.proto.Protocol;

public class Fragment extends SliceableList<PacketContent> {

	/** Warning shutupper; I don't care about this. */
	private static final long serialVersionUID = 1L;

	public Fragment() {
		super();
	}

	public Fragment(PacketContent... elements) {
		super(elements);
	}

	public byte[] toWire() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		for (PacketContent content : this) {
			out.write(content.toWire());
		}

		return out.toByteArray();
	}

	public int getNextHdr(Packet packet, PacketContent content) {
		PacketContent next = this.getNext(content);
		if (next == null) {
			throw new IllegalArgumentException("Layer 3 header is the last "
					+ "PacketContent. I don't know what I'm supposed to do.");
		}

		Protocol result = next.getProtocol();

		if (result == Protocol.PAYLOAD) {
			// This is a "subsequent" fragment. We need to go find the layer 4
			// header in the first fragment.
			for (PacketContent currentContent : packet.get(0)) {
				Protocol currentProto = currentContent.getProtocol();
				if (currentProto.getLayer() == 4) {
					result = currentProto;
					break;
				}
			}
		}

		if (result == Protocol.PAYLOAD) {
			throw new IllegalArgumentException("There is no layer 4 header "
					+ "in the first fragment. I don't know what I'm supposed "
					+ "to do.");
		}

		return result.toWire();
	}

	public void export(String fileName) throws IOException {
		fileName += ".pkt";
		
		File file = new File(fileName);
		if (file.exists())
			System.out.println("Warning: I'm rewriting file " + fileName);

		FileOutputStream out = new FileOutputStream(file);
		try {
			for (PacketContent content : this) {
				out.write(content.toWire());
			}
		} finally {
			out.close();
		}
	}

	public int getL3PayloadLength() throws IOException {
		int result = 0;
		boolean foundL4 = false;

		for (PacketContent content : this) {
			if (content.getProtocol().getLayer() > 3)
				foundL4 = true;
			if (foundL4)
				result += content.toWire().length;
		}

		return result;
	}

}
