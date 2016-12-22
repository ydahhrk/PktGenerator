package mx.nic.jool.pktgen.proto.optionsdata6;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Stack;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.pojo.PacketContent;

public class PadN implements TypeLengthValue {

	private int optionType;
	private int optionDataLength;
	private byte[] optionData;

	@Override
	public void readFromStdIn(FieldScanner scanner) {
		optionType = 0x01;
		optionDataLength = scanner.readInt("Option Data Length", 4);

		if (optionDataLength < 0) {
			optionDataLength = 0;
		}

		optionData = new byte[optionDataLength];

		if (optionDataLength > 0) {
			for (int x = 0; x < optionDataLength; x++) {
				optionData[x] = 0;
			}
		}

	}

	public PadN() {
		// nothing to do here.
	}

	public PadN(int optionDataLength) {
		this.optionType = 0x01;
		this.optionDataLength = optionDataLength;
		this.optionData = new byte[optionDataLength];

		if (this.optionDataLength > 0) {
			for (int x = 0; x < this.optionDataLength; x++) {
				this.optionData[x] = 0;
			}
		}

	}

	@Override
	public void postProcess(PacketContent previousHeader, Stack<PacketContent> payload) throws IOException {
		// no code
	}

	@Override
	public byte[] toWire() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PacketUtils.write8BitInt(out, optionType);
		PacketUtils.write8BitInt(out, optionDataLength);
		if (optionDataLength > 0)
			out.write(optionData);

		return out.toByteArray();
	}

	@Override
	public OptionDataTypes getType() {
		return OptionDataTypes.PADN;
	}

}
