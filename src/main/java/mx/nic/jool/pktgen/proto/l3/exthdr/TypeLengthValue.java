package mx.nic.jool.pktgen.proto.l3.exthdr;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.ByteArrayOutputStream;
import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.auto.Util;

public class TypeLengthValue {

	private static final int PAD1 = 0;

	private int optionType;
	private Integer optDataLen;
	private byte[] optionData;

	public static TypeLengthValue createPadding(int length) {
		if (length < 1)
			throw new IllegalArgumentException("Only positive amounts of padding are allowed.");

		if (length == 1)
			return new TypeLengthValue(PAD1);

		TypeLengthValue padn = new TypeLengthValue(1);
		padn.optDataLen = length - 2;
		padn.optionData = new byte[length - 2];
		return padn;
	}

	public static TypeLengthValue createRandom() {
		ThreadLocalRandom random = ThreadLocalRandom.current();

		int type = random.nextInt(256);
		TypeLengthValue result = new TypeLengthValue(type);
		result.optDataLen = random.nextInt(14);
		result.optionData = new byte[result.optDataLen];
		random.nextBytes(result.optionData);

		return result;
	}

	public TypeLengthValue(int optionType) {
		this.optionType = optionType;
	}

	public TypeLengthValue(InputStream in) throws IOException {
		optionType = in.read();
		if (optionType == PAD1)
			return;
		optDataLen = in.read();
		optionData = Util.streamToByteArray(in, optDataLen);
	}

	public void readFromStdIn(FieldScanner scanner) {
		if (optionType == PAD1)
			return;

		this.optDataLen = scanner.readInteger("Opt Data Len");
		this.optionData = scanner.readByteArray("Option Data");
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
		sb.append((optDataLen != null) ? optDataLen : "auto").append(",");
		if (optionData != null) {
			sb.append("(");
			for (byte data : optionData)
				sb.append(data);
			sb.append(")");
		}

		return sb.toString();
	}

}
