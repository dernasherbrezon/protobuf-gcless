package com.google.code.proto.gcless;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class ReadBufferedBytesTest {

	@Test
	public void testIssue14() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String expected = "test string bytes data";
		ProtobufOutputStream.writeBytes(1, new String(expected).getBytes(), baos);
		CurrentCursor cursor = new CurrentCursor();
		BufferedInputStream is = new BufferedInputStream(baos.toByteArray());
		ProtobufInputStream.readTag(is, cursor);
		byte[] readData = ProtobufInputStream.readBytes(is, cursor);
		assertEquals(expected, new String(readData));
	}

	private static class BufferedInputStream extends InputStream {

		private final ByteArrayInputStream bais;

		BufferedInputStream(byte[] data) {
			this.bais = new ByteArrayInputStream(data);
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			int readBytes = Math.min(len, (int) (len * 2 * Math.random()));
			if( readBytes == 0 ) {
				readBytes = len;
			}
			return super.read(b, off, readBytes);
		}

		@Override
		public int read() throws IOException {
			return bais.read();
		}

	}

}
