package mx.nic.jool.pktgen.proto.l4;

import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.Payload;

/**
 * A header representing the meat of an IPv4 ping.
 */
public class Icmpv4InfoHeader extends Icmpv4Header {

	public Icmpv4InfoHeader() {
		type = 8;
		code = 0;
	}

	@Override
	public Header createClone() {
		return createCloneIcmp(new Icmpv4InfoHeader());
	}

	@Override
	public String getShortName() {
		return "i4info";
	}

	@Override
	protected Header getNextHeader() {
		return new Payload();
	}

	@Override
	public void randomize() {
		super.randomize();

		ThreadLocalRandom random = ThreadLocalRandom.current();
		this.type = random.nextBoolean() ? 0 : 8;
	}
}
