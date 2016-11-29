package mx.nic.jool.pktgen.proto.l4;

import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.pojo.Payload;

public class Icmpv4InfoHeader extends Icmpv4Header {

	public Icmpv4InfoHeader() {
		type = 8;
		code = 0;
	}

	@Override
	public PacketContent createClone() {
		return createCloneIcmp(new Icmpv4InfoHeader());
	}

	@Override
	public String getShortName() {
		return "i4info";
	}

	@Override
	protected PacketContent getNextContent() {
		return new Payload();
	}

	@Override
	public void randomize() {
		super.randomize();
		
		ThreadLocalRandom random = ThreadLocalRandom.current();
		this.type = random.nextBoolean() ? 0 : 8;
	}
}
