package mx.nic.jool.pktgen.proto.optionsdata4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;

public class RecordRoute extends Ipv4OptionHeader {

	public static final int DEFAULT_LENGTH = 3;

	private int optionType;
	private Integer optionLength;
	private int pointer;

	private List<Inet4Address> routeData;

	@Override
	public void readFromStdIn(FieldScanner scanner) {
		boolean newIn4Addr;
		printRecordRouteTypes();

		do {
			optionType = scanner.readInt("Option Type", 7);
		} while (!isValidOptionType());

		optionLength = scanner.readInteger("Option Length");
		pointer = scanner.readInt("Pointer (multiples of 4)", 4);

		routeData = new ArrayList<>();
		do {
			newIn4Addr = scanner.readBoolean("Add an IPv4 Address", false);
			if (!newIn4Addr)
				break;
			routeData.add(scanner.readAddress4("IPv4 Address"));
		} while (newIn4Addr);

		if (pointer % 4 != 0) {
			System.err.println("The pointer wasn't in multiples of 4, ");
			boolean defaultValue = false;
			defaultValue = scanner.readBoolean("Default value of pointer", true);
			if (defaultValue)
				pointer = 4;
		}
	}

	private void printRecordRouteTypes() {
		System.out.println("Option Types values: ");
		System.out.println("  7 - Record Route");
		System.out.println("131 - Loose Source and Record Route");
		System.out.println("137 - Strict Source and Record Route");
	}

	private boolean isValidOptionType() {
		if (optionType == 7 || optionType == 131 || optionType == 137) {
			return true;
		}
		System.err.println("Invalid Option Type, Try again!..");
		return false;
	}

	@Override
	public byte[] toWire() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PacketUtils.write8BitInt(out, optionType);
		PacketUtils.write8BitInt(out, optionLength);
		PacketUtils.write8BitInt(out, pointer);

		for (Inet4Address in4Addr : routeData) {
			out.write(in4Addr.getAddress());
		}

		return out.toByteArray();
	}

	@Override
	public PacketContent createClone() {
		RecordRoute result = new RecordRoute();

		result.optionType = optionType;
		result.optionLength = optionLength;
		result.pointer = pointer;
		result.routeData = new ArrayList<>();
		for (Inet4Address address : routeData) {
			result.routeData.add(address);
		}

		return result;
	}

	@Override
	public String getShortName() {
		return "rropt";
	}

	@Override
	public void postProcess(Packet packet, Fragment fragment) throws IOException {
		if (optionLength == null) {
			optionLength = 3; /* The first 3 octects. */
			optionLength += routeData.size() * 4;
		}

	}

	@Override
	public void modifyFromStdIn(FieldScanner scanner) {
		readFromStdIn(scanner);
	}

	public void randomize() {
		throw new IllegalArgumentException("Sorry; RecordRoutes are not supported in random mode yet.");
	}

	@Override
	public void unsetLengths() {
		this.optionLength = null;
	}
}
