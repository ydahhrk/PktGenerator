package mx.nic.jool.pktgen.proto.optionsdata6;

import java.io.IOException;
import java.util.Stack;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.pojo.PacketContent;

public interface TypeLengthValue {
	public void readFromStdIn(FieldScanner scanner);

	/**
	 * 
	 * @param previousHeader
	 *            careful with reading from here; it hasn't been post-processed
	 *            yet.
	 * @param payload
	 * @throws IOException
	 */
	public void postProcess(PacketContent previousHeader, Stack<PacketContent> payload) throws IOException;

	public byte[] toWire() throws IOException;

	public OptionDataTypes getType();
}
