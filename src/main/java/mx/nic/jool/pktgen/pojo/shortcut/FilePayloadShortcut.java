package mx.nic.jool.pktgen.pojo.shortcut;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.Payload;

public class FilePayloadShortcut implements Shortcut {

	@Override
	public String getName() {
		return "file";
	}

	@Override
	public void apply(Header header, String fileName) {
		if (!(header instanceof Payload))
			throw new IllegalArgumentException("Header is not Payload. Don't know what to do.");
		Payload payload = (Payload) header;

		if (!fileName.endsWith(".pkt"))
			fileName += ".pkt";
		Path filePath = Paths.get(fileName);

		byte[] fileContent;
		try {
			fileContent = Files.readAllBytes(filePath);
		} catch (IOException e) {
			throw new RuntimeException("Could not open file '" + fileName + "'", e);
		}

		int currentLength = payload.getBytes().length;
		if (currentLength > fileContent.length) {
			throw new IllegalArgumentException("Payload should length " + currentLength + ", but file has only " //
					+ fileContent.length + " bytes.");
		} else if (currentLength < fileContent.length) {
			fileContent = Arrays.copyOfRange(fileContent, 0, currentLength);
		}

		payload.setBytes(fileContent);
	}

}
