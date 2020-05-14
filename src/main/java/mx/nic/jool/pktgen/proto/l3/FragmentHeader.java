package mx.nic.jool.pktgen.proto.l3;

import java.io.IOException;

import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.shortcut.Shortcut;
import mx.nic.jool.pktgen.type.Field;
import mx.nic.jool.pktgen.type.IntField;

public class FragmentHeader extends Header {

	private IntField nextHeader = new IntField("nextHeader", 8, null);
	private IntField reserved = new IntField("reserved", 8, 0);
	private IntField fragmentOffset = new IntField("fragmentOffset", 13, 0);
	private IntField res = new IntField("res", 2, 0);
	private IntField m = new IntField("m", 1, 0);
	private IntField identification = new IntField("identification", 32, 0);

	private Field[] fields = new Field[] { //
			nextHeader, reserved, fragmentOffset, res, m, //
			identification, //
	};
	
	@Override
	public String getName() {
		return "Fragment";
	}

	@Override
	public int getHdrIndex() {
		return 44;
	}

	@Override
	public Field[] getFields() {
		return fields;
	}

	@Override
	public Shortcut[] getShortcuts() {
		return new Shortcut[0];
	}

	@Override
	public void postProcess() throws IOException {
		if (nextHeader.getValue() == null) {
			nextHeader.setValue(getNextHdr());
		}
	}

	@Override
	public void swapIdentifiers() {
		// No identifiers
	}

}
