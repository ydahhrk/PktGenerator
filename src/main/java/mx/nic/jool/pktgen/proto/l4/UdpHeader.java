package mx.nic.jool.pktgen.proto.l4;

import java.io.IOException;

import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.shortcut.Shortcut;
import mx.nic.jool.pktgen.pojo.shortcut.SwapIdentifiersShortcut;
import mx.nic.jool.pktgen.type.Field;
import mx.nic.jool.pktgen.type.IntField;

/**
 * https://tools.ietf.org/html/rfc768
 */
public class UdpHeader extends Layer4Header {

	private IntField src = new IntField("src", 16, 2000);
	private IntField dst = new IntField("dst", 16, 4000);
	private IntField length = new IntField("length", 16, null);
	private IntField checksum = new IntField("checksum", 16, null, IntField.FLAG_HEX);

	private Field[] fields = new Field[] { src, dst, length, checksum };

	@Override
	public Field[] getFields() {
		return fields;
	}

	@Override
	public void postProcess() throws IOException {
		if (length.getValue() == null) {
			int length = 0;
			for (Header header = this; header != null; header = header.getNext())
				length += header.getLength();
			this.length.setValue(length);
		}

		if (checksum.getValue() == null) {
			checksum.setValue(0);
			checksum.setValue(buildChecksum(true));
		}
	}

	@Override
	public String getName() {
		return "UDP Header";
	}

	@Override
	public int getHdrIndex() {
		return 17;
	}

	@Override
	public Shortcut[] getShortcuts() {
		return new Shortcut[] { new SwapIdentifiersShortcut() };
	}

	@Override
	public void swapIdentifiers() {
		int tmp = src.getValue();
		src.setValue(dst.getValue());
		dst.setValue(tmp);
	}
}
