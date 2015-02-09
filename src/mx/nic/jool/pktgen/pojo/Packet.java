package mx.nic.jool.pktgen.pojo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Packet extends SliceableList<Fragment> {

	/** Warning shutupper; I don't care about this. */
	private static final long serialVersionUID = 1L;
	
	public Packet() {
		super();
	}

	public Packet(PacketContent... content) {
		super(new Fragment(content));
	}
	
	public Packet(Fragment... frags) {
		super(frags);
	}

	public List<PacketContent> getL4ContentAfter(PacketContent content) {
		boolean foundContent = false;
		List<PacketContent> result = new ArrayList<>();

		for (Fragment fragment : this) {
			boolean foundLayer4 = false;

			for (PacketContent currentContent : fragment) {
				if (currentContent.getProtocol().getLayer() > 3)
					foundLayer4 = true;
				
				if (foundContent && foundLayer4)
					result.add(currentContent);

				if (currentContent == content)
					foundContent = true;
			}
		}

		return result;
	}

	public void export(String fileName) throws IOException {
		if (size() == 1) {
			get(0).export(fileName);
		} else {
			for (int x = 0; x < size(); x++) {
				get(x).export(fileName + x);
			}
		}
	}
	
	public void add(PacketContent... contents) {
		this.add(new Fragment(contents));
	}
	
	public void postProcess() throws IOException {
		ListIterator<Fragment> fragmentIterator = listIterator(size());
		while (fragmentIterator.hasPrevious()) {
			Fragment fragment = fragmentIterator.previous();
			ListIterator<PacketContent> contentIterator = fragment.listIterator(fragment.size());
			while (contentIterator.hasPrevious()) {
				PacketContent content = contentIterator.previous();
				content.postProcess(this, fragment);
			}
		}
	}

}
