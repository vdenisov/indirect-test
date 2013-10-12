package org.plukh.indirecttest.work;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.plukh.indirecttest.TestException;

import java.io.*;

public class IOWork implements Work {
    private static final Logger log = LogManager.getLogger(IOWork.class);

    private File tempFile;
    private static final int BYTES_TO_WRITE = 10;

    @Override
    public void init() throws TestException {
        try {
            tempFile = File.createTempFile("indirecttest-", null);
        } catch (IOException e) {
            throw new TestException("Error creating temporary file", e);
        }
    }

    @Override
    public void work() {
        //Prepare buffer
        byte[] buf = new byte[BYTES_TO_WRITE];
        for (int i = 0; i < buf.length; ++i) {
            buf[i] = (byte) i;
        }

        try {
            //Write file
            final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile));
            out.write(buf);
            out.close();

            //Read file
            final byte[] inbuf = new byte[BYTES_TO_WRITE];
            final BufferedInputStream in = new BufferedInputStream(new FileInputStream(tempFile));

            int r = 0;
            while (r < BYTES_TO_WRITE && (r += in.read(inbuf, r, inbuf.length - r)) != -1) {
                //log.trace("Read " + r + " bytes from the stream");
            }

            in.close();
        } catch (IOException e) {
            log.error("Error working with temp file", e);
        }
    }

    @Override
    public void dispose() {
        tempFile.delete();
    }
}
