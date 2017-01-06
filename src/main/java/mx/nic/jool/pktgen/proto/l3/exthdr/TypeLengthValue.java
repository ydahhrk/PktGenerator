package mx.nic.jool.pktgen.proto.l3.exthdr;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.ByteArrayOutputStream;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.annotation.HeaderField;
import mx.nic.jool.pktgen.auto.Util;

public class TypeLengthValue {

	private static final int PAD1 = 0;

	@HeaderField
	private int optionType;
	@HeaderField
	private Integer optDataLen;
	@HeaderField
	private byte[] optionData;

	public static TypeLengthValue createPadding(int length) {
		if (length < 1)
			throw new IllegalArgumentException("Only positive amounts of padding are allowed.");

		return (length == 1) //
				? new TypeLengthValue(PAD1, null, null) //
				: new TypeLengthValue(1, length - 2, new byte[length - 2]);
	}

	public static TypeLengthValue createRandom() {
		ThreadLocalRandom random = ThreadLocalRandom.current();

		int type = random.nextInt(256);
		Integer optDataLen = random.nextInt(14);
		byte[] optionData = new byte[optDataLen];
		random.nextBytes(optionData);

		return new TypeLengthValue(type, optDataLen, optionData);
	}
	
	public static TypeLengthValue createFromInputStream(InputStream in) throws IOException {
		int type = in.read();
		if (type == PAD1)
			return new TypeLengthValue(PAD1, null, null);
		
		Integer optDataLen = in.read();
		byte[] optionData = Util.streamToByteArray(in, optDataLen);
		return new TypeLengthValue(type, optDataLen, optionData);
	}

	public TypeLengthValue() {
		this(PAD1, null, null);
	}

	private TypeLengthValue(int optionType, Integer optDataLen, byte[] optionData) {
		this.optionType = optionType;
		this.optDataLen = optDataLen;
		this.optionData = optionData;
	}

	public byte[] toWire() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PacketUtils.write8BitInt(out, optionType);
		if (optionType != PAD1) {
			PacketUtils.write8BitInt(out, (optDataLen != null) ? optDataLen : (2 + optionData.length));
			out.write(optionData);
		}

		return out.toByteArray();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(optionType).append(",");
		sb.append((optDataLen != null) ? optDataLen : "(auto)").append(",");
		sb.append((optionData != null) ? Arrays.toString(optionData) : "(auto)");

		return sb.toString();
	}

}
