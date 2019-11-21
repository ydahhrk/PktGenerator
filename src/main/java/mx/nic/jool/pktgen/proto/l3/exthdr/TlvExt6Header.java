package mx.nic.jool.pktgen.proto.l3.exthdr;

import java.io.IOException;
import java.io.InputStream;

import mx.nic.jool.pktgen.ByteArrayOutputStream;
import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.annotation.HeaderField;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.proto.HeaderFactory;

public abstract class TlvExt6Header extends Extension6Header {

	@HeaderField
	private Integer nextHeader;
	@HeaderField
	private Integer hdrExtLength;
	/**
	 * Must not be null! (see
	 * {@link FieldScanner#read(Object, java.lang.reflect.Field)}.)
	 */
	@HeaderField
	private TypeLengthValueList tlvs = new TypeLengthValueList();

	private int computePadding(int length) {
		return (length % 8 == 0) ? 0 : (8 - (length % 8));
	}

	@Override
	public void postProcess(Packet packet, Fragment fragment) {
		if (nextHeader == null) {
			nextHeader = fragment.getNextHdr(packet, this);
		}

		if (hdrExtLength == null) {
			// Add padding. Must wrap to a multiple of 8.
			int length = 2 + tlvs.getLength();
			length += computePadding(length);

			// "Length of the Hop-by-Hop Options header in 8-octet units, ...
			hdrExtLength = length / 8;
			if (length % 8 != 0)
				hdrExtLength++;

			// ... not including the first 8 octets."
			hdrExtLength--;
		}
	}

	@Override
	public byte[] toWire() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PacketUtils.write8BitInt(out, nextHeader);
		PacketUtils.write8BitInt(out, hdrExtLength);

		byte[] tlvsByteArray = tlvs.toWire();
		out.write(tlvsByteArray);

		int padding = computePadding(2 + tlvsByteArray.length);
		if (padding > 0)
			out.write(TypeLengthValue.createPadding(padding).toWire());

		return out.toByteArray();
	}

	@Override
	public Header createClone() {
		TlvExt6Header result = instanceSelf();

		result.nextHeader = nextHeader;
		result.hdrExtLength = hdrExtLength;
		result.tlvs = tlvs;

		return result;
	}

	protected abstract TlvExt6Header instanceSelf();

	@Override
	public Header loadFromStream(InputStream in) throws IOException {
		int[] header = PacketUtils.streamToIntArray(in, 2);

		nextHeader = header[0];
		hdrExtLength = header[1];
		tlvs.loadFromStream(in, hdrExtLength);

		return HeaderFactory.forNexthdr(nextHeader);
	}

	@Override
	public void randomize() {
		tlvs.randomize();
	}

	@Override
	public void unsetLengths() {
		this.hdrExtLength = null;
	}
}
