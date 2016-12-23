package mx.nic.jool.pktgen.proto.l3.exthdr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.proto.optionsdata6.OptionDataTypes;
import mx.nic.jool.pktgen.proto.optionsdata6.Pad1;
import mx.nic.jool.pktgen.proto.optionsdata6.PadN;
import mx.nic.jool.pktgen.proto.optionsdata6.TypeLengthValue;

public class DestinationOptionExt6Header extends Extension6Header {

	private Integer nextHeader;
	private Integer hdrExtLength;
	private List<TypeLengthValue> tlvList;

	@Override
	public void readFromStdIn(FieldScanner scanner) {
		boolean autoPadding;
		/*
		 * se asigna dos debido a los dos campos siguientes, que son de 8 bits
		 * cada uno.
		 */
		int octectsLength = 2;

		nextHeader = scanner.readProtocol("Next Header");
		hdrExtLength = scanner.readInteger("Header Extension Length");
		tlvList = new ArrayList<>();

		OptionDataTypes optionType;
		do {
			System.out.println("Actual header size: " + octectsLength + " bytes.");
			System.out.println("8, 16 or 32 bytes is recommended.");

			Integer nextOptionInt = scanner.readOptionDataType("Next Option Data Type", "exit");

			if (nextOptionInt == null)
				break;

			optionType = OptionDataTypes.fromInt(nextOptionInt);

			if (optionType == null) {
				System.out.println("Invalid; try again.");
				continue;
			}

			TypeLengthValue typeLengthValue = null;
			switch (optionType) {
			case PAD1:
				typeLengthValue = new Pad1();
				break;
			case PADN:
				typeLengthValue = new PadN();
				break;
			}

			typeLengthValue.readFromStdIn(scanner);
			tlvList.add(typeLengthValue);

			do {
				try {
					octectsLength += typeLengthValue.toWire().length;
					break;
				} catch (IOException e) {
					e.printStackTrace();
				}
			} while (true);

		} while (true);

		boolean validLength = (octectsLength == 8) || (octectsLength == 16) || (octectsLength == 32);

		if (!validLength) {
			System.err.println("The length of this header is invalid.");
			System.err.println("Do you want to auto-fix it?");

			autoPadding = scanner.readBoolean("autoPadding", false);
			if (autoPadding) {
				autoPadding(octectsLength);
			} else {
				System.err.println("Don't know what might happen if you send this dpkt.");
			}
		}

	}

	private void autoPadding(int actualHdrLength) {
		int pad;
		int remainder = actualHdrLength % 8;
		if (remainder != 0) {
			pad = 8 - remainder;
			if (pad == 1) {
				addAutoPad1();
			} else {
				addAutoPadN(pad);
			}
		}

		// if (actualHdrLength < 8) {
		// pad = 8 - actualHdrLength;
		// if (pad == 1) {
		// addAutoPad1();
		// } else {
		// addAutoPadN(pad);
		// }
		// } else if (actualHdrLength < 16) {
		// pad = 16 - actualHdrLength;
		// if (pad == 1) {
		// addAutoPad1();
		// } else {
		// addAutoPadN(pad);
		// }
		// } else if (actualHdrLength < 32) {
		// pad = 32 - actualHdrLength;
		// if (pad == 1) {
		// addAutoPad1();
		// } else {
		// addAutoPadN(pad);
		// }
		// } else {
		// System.err.println("Warning: The number of bytes exceed the RFC 2460
		// recommendations. \n"
		// + "I won't add padding.");
		// }
	}

	private void addAutoPadN(int numberOfPadding) {
		TypeLengthValue padN;
		padN = new PadN(numberOfPadding - 2);
		tlvList.add(padN);
	}

	private void addAutoPad1() {
		TypeLengthValue pad1;
		pad1 = new Pad1();
		tlvList.add(pad1);
	}

	@Override
	public void postProcess(Packet packet, Fragment fragment) throws IOException {
		if (nextHeader == null) {
			nextHeader = fragment.getNextHdr(packet, this);
		}

		if (hdrExtLength == null) {
			/* The number 2 it's this header is "2 octects long" by default. */
			int length = 2;
			for (TypeLengthValue content : tlvList) {
				length += content.toWire().length;
			}
			hdrExtLength = (length / 8) > 0 ? (length / 8) - 1 : 0;
			/*
			 * TODO: verificar que el tamaño de esta cabecera sea de 32 octetos,
			 * 16, u 8 octetos. en caso de que no cumpla con alguno de esos
			 * tamaños de octetos retornar un warning o ajustarlo con "paddings"
			 */
		}

	}

	@Override
	public byte[] toWire() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PacketUtils.write8BitInt(out, nextHeader);
		PacketUtils.write8BitInt(out, hdrExtLength);
		for (TypeLengthValue content : tlvList) {
			out.write(content.toWire());
		}

		return out.toByteArray();
	}

	@Override
	public PacketContent createClone() {
		DestinationOptionExt6Header result = new DestinationOptionExt6Header();

		result.nextHeader = nextHeader;
		result.hdrExtLength = hdrExtLength;
		result.tlvList = tlvList;

		return result;
	}

	@Override
	public String getShortName() {
		return "dext";
	}

	@Override
	public void modifyFromStdIn(FieldScanner scanner) {
		readFromStdIn(scanner);
	}

	@Override
	public int getHdrIndex() {
		return 60;
	}

	@Override
	public PacketContent loadFromStream(InputStream in) throws IOException {
		throw new IllegalArgumentException("Sorry; DestOptExt headers are not supported in load-from-file mode yet.");
	}

	@Override
	public void randomize() {
		throw new IllegalArgumentException("Sorry; DestOptExt headers are not supported in random mode yet.");
	}

	@Override
	public void unsetLengths() {
		this.hdrExtLength = null;
	}
}
