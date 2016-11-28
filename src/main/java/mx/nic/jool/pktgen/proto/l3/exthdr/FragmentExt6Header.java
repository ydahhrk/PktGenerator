package mx.nic.jool.pktgen.proto.l3.exthdr;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.annotations.Readable;
import mx.nic.jool.pktgen.auto.Util;
import mx.nic.jool.pktgen.enums.Type;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.proto.PacketContentFactory;

public class FragmentExt6Header extends Extension6Header {

	public static final int LENGTH = 8;
	
	@Readable(defaultValue = "null", type = Type.INTEGER)
	private Integer nextHeader = null;
	
	@Readable(defaultValue = "0", type = Type.INT)
	private int reserved = 0;
	
	@Readable(defaultValue = "null", type = Type.INTEGER)
	private Integer fragmentOffset = null;
	
	@Readable(defaultValue = "0", type = Type.INT)
	private int res = 0;
	
	@Readable(defaultValue = "null", type = Type.BOOLEAN)
	private Boolean mFlag = null;
	
	@Readable(defaultValue = "0", type = Type.LONG)
	private long identification = 0;

	@Override
	public void readFromStdIn(FieldScanner scanner) {
		nextHeader = scanner.readInteger("Next Header", "auto");
		reserved = scanner.readInt("Reserved", 0);
		fragmentOffset = scanner.readInteger("Fragment Offset (bytes)", "auto");
		res = scanner.readInt("Res", 0);
		mFlag = scanner.readBoolean("M flag", null);
		identification = scanner.readLong("identification", 0);
	}

	@Override
	public void postProcess(Packet packet, Fragment fragment)
			throws IOException {
		if (nextHeader == null) {
			nextHeader = fragment.getNextHdr(packet, this);
		}
		if (mFlag == null) {
			mFlag = fragment != packet.get(packet.size() - 1);
		}
		if (fragmentOffset == null) {
			fragmentOffset = 0;
			for (Fragment currentFragment : packet) {
				if (fragment == currentFragment)
					break;
				fragmentOffset += currentFragment.getL3PayloadLength();
			}
		}
	}

	@Override
	public byte[] toWire() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PacketUtils.write8BitInt(out, nextHeader);
		PacketUtils.write8BitInt(out, reserved);

		int fragOffset = (fragmentOffset != null) ? fragmentOffset : 0;
		boolean mf = (mFlag != null) ? mFlag : false;
		PacketUtils.write16BitInt(out, (fragOffset & 0xFFF8) | ((res & 3) << 1)
				| (mf ? 1 : 0));

		PacketUtils.write32BitInt(out, identification);

		return out.toByteArray();
	}
	
	@Override
	public PacketContent createClone() {
		FragmentExt6Header result = new FragmentExt6Header();
		
		result.nextHeader = nextHeader;
		result.reserved = reserved;
		result.fragmentOffset = fragmentOffset;
		result.res = res;
		result.mFlag = mFlag;
		result.identification = identification;
		
		return result;
	}

	@Override
	public String getShortName() {
		return "fext";
	}

	public void setIdentification(long identification) {
		this.identification = identification;
	}

	@Override
	public void modifyHdrFromStdIn(FieldScanner scanner) {
		Util.modifyFieldValues(this, scanner);
	}

	@Override
	public int getHdrIndex() {
		return 44;
	}

	@Override
	public PacketContent loadFromStream(FileInputStream in) throws IOException {
		int[] header = Util.streamToArray(in, LENGTH);
		
		nextHeader = header[0];
		reserved = header[1];
		fragmentOffset = Util.joinBytes(header[2], header[3] & 0xF8);
		res = ((header[3] >> 1) & 0x3);
		mFlag = (header[3] & 0x1) == 1;
		identification = Util.joinBytes(header[4], header[5], header[6], header[7]);
		
		return PacketContentFactory.forNexthdr(nextHeader);
	}
	
	@Override
	public void randomize() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		
//		nextHeader = null;
		reserved = 0;
//		fragmentOffset = null;
		res = (random.nextInt(10) > 0) ? random.nextInt(0x100) : 0;
		mFlag = random.nextBoolean();
		identification = random.nextLong(0x100000000L);
	}
}