package mx.nic.jool.pktgen.proto.l3.exthdr;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.ByteArrayOutputStream;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.annotation.HeaderField;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.Payload;
import mx.nic.jool.pktgen.proto.HeaderFactory;

/**
 * https://tools.ietf.org/html/rfc2460#section-4.5
 */
public class FragmentExt6Header extends Extension6Header {

	public static final int LENGTH = 8;

	@HeaderField
	private Integer nextHeader = null;
	@HeaderField
	private int reserved = 0;
	@HeaderField
	private Integer fragmentOffset = null;
	@HeaderField
	private int res = 0;
	@HeaderField
	private Boolean mFlag = null;
	@HeaderField
	private long identification = 0;

	@Override
	public void postProcess(Packet packet, Fragment fragment) {
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
	public byte[] toWire() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PacketUtils.write8BitInt(out, nextHeader);
		PacketUtils.write8BitInt(out, reserved);

		int fragOffset = (fragmentOffset != null) ? fragmentOffset : 0;
		boolean mf = (mFlag != null) ? mFlag : false;
		PacketUtils.write16BitInt(out, (fragOffset & 0xFFF8) | ((res & 3) << 1) | (mf ? 1 : 0));

		PacketUtils.write32BitInt(out, identification);

		return out.toByteArray();
	}

	@Override
	public Header createClone() {
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

	@Override
	public String getName() {
		return "Fragment Header";
	}

	@Override
	public int getHdrIndex() {
		return 44;
	}

	@Override
	public Header loadFromStream(InputStream in) throws IOException {
		int[] header = PacketUtils.streamToIntArray(in, LENGTH);

		nextHeader = header[0];
		reserved = header[1];
		fragmentOffset = PacketUtils.joinBytes(header[2], header[3] & 0xF8);
		res = ((header[3] >> 1) & 0x3);
		mFlag = (header[3] & 0x1) == 1;
		identification = PacketUtils.joinBytes(header[4], header[5], header[6], header[7]);

		return (fragmentOffset == 0) ? HeaderFactory.forNexthdr(nextHeader) : new Payload();
	}

	@Override
	public void randomize() {
		ThreadLocalRandom random = ThreadLocalRandom.current();

		// nextHeader = null;
		reserved = 0;
		// fragmentOffset = null;
		res = (random.nextInt(10) > 0) ? random.nextInt(0x100) : 0;
		mFlag = random.nextBoolean();
		identification = random.nextLong(0x100000000L);
	}

	@Override
	public void unsetLengths() {
		// No lengths.
	}
}
