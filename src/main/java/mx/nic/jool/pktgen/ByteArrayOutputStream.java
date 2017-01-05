package mx.nic.jool.pktgen;

import java.io.IOException;

/**
 * For some stupid reason, {@link java.io.ByteArrayOutputStream} doesn't silence
 * some IOExceptions even though it's just writing to a byte array. This makes
 * caller code more convoluted than it needs to be.
 * 
 * This class more or less fixes that.
 */
public class ByteArrayOutputStream extends java.io.ByteArrayOutputStream {

	@Override
	public void write(byte[] b) {
		try {
			super.write(b);
		} catch (IOException e) {
			throw new RuntimeException("ByteArrayOutputStream threw an IOException.", e);
		}
	}

	/*
	 * {@link #close()} also sort of needs this treatment, but I'm not going to
	 * override it because I don't need it and also it could cause some trouble.
	 * See http://stackoverflow.com/questions/39648062.
	 */

}
