package mx.nic.jool.pktgen.proto.l4;

import java.io.IOException;

import mx.nic.jool.pktgen.pojo.shortcut.Shortcut;
import mx.nic.jool.pktgen.pojo.shortcut.SwapIdentifiersShortcut;
import mx.nic.jool.pktgen.type.BoolField;
import mx.nic.jool.pktgen.type.Field;
import mx.nic.jool.pktgen.type.IntField;

/**
 * https://tools.ietf.org/html/rfc793#section-3.1
 */
public class TcpHeader extends Layer4Header {

	private IntField src = new IntField("src", 16, 2000);
	private IntField dst = new IntField("dst", 16, 4000);
	private IntField sequenceNumber = new IntField("seqnum", 32, 0);
	private IntField acknowledgmentNumber = new IntField("acknum", 32, 0);
	private IntField dataOffset = new IntField("dataOffset", 4, 20 >> 2);
	private IntField reserved = new IntField("reserved", 3, 0);
	private BoolField ns = new BoolField("ns", false);
	private BoolField cwr = new BoolField("cwr", false);
	private BoolField ece = new BoolField("ece", false);
	private BoolField urg = new BoolField("urg", false);
	private BoolField ack = new BoolField("ack", false);
	private BoolField psh = new BoolField("psh", false);
	private BoolField rst = new BoolField("rst", false);
	private BoolField syn = new BoolField("syn", true);
	private BoolField fin = new BoolField("fin", false);
	private IntField windowSize = new IntField("windowSize", 16, 100);
	private IntField checksum = new IntField("csum", 16, null, IntField.FLAG_HEX);
	private IntField urgentPointer = new IntField("urgentPointer", 16, 0);

	private Field[] fields = new Field[] { //
			src, dst, //
			sequenceNumber, //
			acknowledgmentNumber, //
			dataOffset, reserved, ns, cwr, ece, urg, ack, psh, rst, syn, fin, windowSize, //
			checksum, urgentPointer, //
	};

	@Override
	public Field[] getFields() {
		return fields;
	}

	@Override
	public void postProcess() throws IOException {
		if (checksum.getValue() == null) {
			checksum.setValue(0);
			checksum.setValue(buildChecksum(true));
		}
	}

	@Override
	public String getName() {
		return "TCP Header";
	}

	@Override
	public int getHdrIndex() {
		return 6;
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
