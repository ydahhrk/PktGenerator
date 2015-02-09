package mx.nic.jool.pktgen.proto.l3.exthdr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.proto.Protocol;

public class FragmentExt6Header implements Extension6Header {

	private Integer nextHeader = null;
	private int reserved = 0;
	private Integer fragmentOffset = null;
	private int res = 0;
	private Boolean mFlag = null;
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
	public Protocol getProtocol() {
		return Protocol.FRAGMENT_EXT6HDR;
	}

	@Override
	public String getShortName() {
		return "fext";
	}

	public void setIdentification(long identification) {
		this.identification = identification;
	}
	
}
