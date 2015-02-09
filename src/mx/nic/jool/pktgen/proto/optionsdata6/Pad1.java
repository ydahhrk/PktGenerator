package mx.nic.jool.pktgen.proto.optionsdata6;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Stack;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.pojo.PacketContent;


public class Pad1 implements TypeLengthValue {

	private int optionType = 0x00;
	
	@Override
	public void readFromStdIn(FieldScanner scanner) {
		// no code 
	}

	@Override
	public void postProcess(PacketContent previousHeader,
			Stack<PacketContent> payload) throws IOException {
		// no code
	}

	@Override
	public byte[] toWire() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PacketUtils.write8BitInt(out, optionType);

		return out.toByteArray();
	}

	@Override
	public OptionDataTypes getType() {
		return OptionDataTypes.PAD1;
	}

}
