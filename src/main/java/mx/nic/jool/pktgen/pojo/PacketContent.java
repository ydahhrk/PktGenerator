package mx.nic.jool.pktgen.pojo;

import java.io.FileInputStream;
import java.io.IOException;

import mx.nic.jool.pktgen.FieldScanner;

public interface PacketContent {

	public void readFromStdIn(FieldScanner scanner);
	
	public byte[] toWire() throws IOException;

	public String getShortName();

	public void postProcess(Packet packet, Fragment fragment)
			throws IOException;
	
	public PacketContent createClone();
	
	public void modifyHdrFromStdIn(FieldScanner scanner);

	public int getHdrIndex();
	
	public int getLayer();

	public PacketContent loadFromStream(FileInputStream in) throws IOException;

	public void randomize();
}
