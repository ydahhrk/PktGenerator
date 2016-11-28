package mx.nic.jool.pktgen.proto.optionsdata4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.annotations.Readable;
import mx.nic.jool.pktgen.auto.Util;
import mx.nic.jool.pktgen.enums.Type;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;

public class InetTimestamp extends Ipv4OptionHeader {

	private static final int DEFAULT_LENGTH = 4;
	
	private int optionType;
	@Readable(defaultValue = "auto", type = Type.INTEGER)
	private Integer length;
	@Readable(defaultValue = "5", type = Type.INT)
	private int pointer;
	@Readable(defaultValue = "0", type = Type.INT)
	private int overflow;
	@Readable(defaultValue = "0", type = Type.INT)
	private int flag;
	
	private int lengthBlankSpace;
	
	@Override
	public void readFromStdIn(FieldScanner scanner) {
		optionType = 68;//scanner.readInt("Option Type", 68);
		length = scanner.readInteger("Length", "auto");
		pointer = scanner.readInt("Pointer", 5);
		overflow = scanner.readInt("Overflow", 0);
		validFlags();
		do {
			flag = scanner.readInt("Flag", 0);
		} while (!isValidFlag());
		lengthBlankSpace = calculateBlankSpace(scanner); 

	}
	
	private int calculateBlankSpace(FieldScanner scanner) {
		int blankSpaces;
		do {
			if (this.flag == 0) {
				blankSpaces = scanner.readInt("How many timestamps", 1);
				if (blankSpaces <= 9)
					break;
				else
					System.err.println("Must be a value less or equals than 9");
			} else {
				blankSpaces = scanner.readInt("How many IPv4 and timestamps", 1);
				blankSpaces *= 2;
				if (blankSpaces <= 8)
					break;
				else
					System.err.println("Must be a value less or equals than 4");
			}
		} while (true);
		
		return blankSpaces;
	}

	private void validFlags() {
		System.out.println("*** Flags ***");
		System.out.println(" 0 - Timestamps only");
		System.out.println(" 1 - IPv4 Address + TimeStamp");
		System.out.println(" 3 - Specified IPv4 Address + Timestamp (Not implemented in pojo yet)");
	}
	
	private boolean isValidFlag() {
		if (flag == 0 || flag == 1 || flag == 3) {
			return true;
		}
		System.err.println("The number that you insert it's not valid.\n"
				+ "Try Again!...");
		return false;
	}

	@Override
	public byte[] toWire() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		PacketUtils.write8BitInt(out, optionType);
		PacketUtils.write8BitInt(out, length);
		PacketUtils.write8BitInt(out, pointer);
		PacketUtils.write8BitInt(out, ((overflow & 0xF) << 4) | (flag & 0xF));
		
		for (int i = 0; i < lengthBlankSpace; i++) {
			PacketUtils.write32BitInt(out, 0L);
		}
		
		return out.toByteArray();
	}
	
	@Override
	public PacketContent createClone() {
		InetTimestamp result = new InetTimestamp();
		
		result.optionType = optionType;
		result.length = length;
		result.pointer = pointer;
		result.overflow = overflow;
		result.flag = flag;
		result.lengthBlankSpace = lengthBlankSpace;
		
		return result;
	}

	@Override
	public String getShortName() {
		return "tsopt";
	}

	@Override
	public void postProcess(Packet packet, Fragment fragment)
			throws IOException {
		if (length == null) {
			length = DEFAULT_LENGTH + (lengthBlankSpace * 4);
		}
	}

	@Override
	public void modifyHdrFromStdIn(FieldScanner scanner) {
		Util.modifyFieldValues(this, scanner);
		while (!isValidFlag()) {
			flag = scanner.readInt("Flag", 0);
		} 
		lengthBlankSpace = calculateBlankSpace(scanner);	
	}	

	@Override
	public void randomize() {
		throw new IllegalArgumentException("Sorry; InetTimestamps are not supported in random mode yet.");
	}
}
