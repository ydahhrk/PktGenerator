package mx.nic.jool.pktgen.proto.l4;

import java.io.IOException;

import mx.nic.jool.pktgen.type.Field;
import mx.nic.jool.pktgen.type.IntField;

/**
 * Yes, layer 4. It is layer 4 for most intents and purposes. Shut up.
 */
public abstract class IcmpHeader extends Layer4Header {

	private IntField type;
	private IntField code;
	private IntField checksum = new IntField("checksum", 16, null, IntField.FLAG_HEX);
	protected IntField rest1 = new IntField("rest1", 16, 0);
	private IntField rest2 = new IntField("rest2", 16, 0);

	private Field[] fields;

	public IcmpHeader(int defaultType, int defaultCode) {
		this.type = new IntField("type", 8, defaultType);
		this.code = new IntField("code", 8, defaultCode);
		this.fields = new Field[] { type, code, checksum, rest1, rest2 };
	}

	@Override
	public Field[] getFields() {
		return fields;
	}

	public abstract boolean csumIncludesPseudoheader();

	@Override
	public void postProcess() throws IOException {
		if (checksum.getValue() == null) {
			checksum.setValue(0);
			checksum.setValue(buildChecksum(csumIncludesPseudoheader()));
		}
	}

	@Override
	public void swapIdentifiers() {
		// No IDs.
	}

}
