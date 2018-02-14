package farijo.com.starcraft_bo_shower.network.custom_server;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;

/**
 * Created by Teddy on 11/02/2018.
 */

class BOInputStream extends InputStream {

    private InputStream in;

    BOInputStream(@NonNull InputStream in) {
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }

    String readBOFilename() throws IOException {
        byte[] dataSize = new byte[2];
        read(dataSize);
        int size = ByteBuffer.wrap(dataSize).order(ByteOrder.BIG_ENDIAN).getShort();
        if (size <= 0) {
            return null;
        }
        byte[] dataFileName = new byte[size];
        read(dataFileName, 0, size);
        return new String(dataFileName, 0, size);
    }

    byte[] readBOFile() throws IOException {
        int size = readShort();
        if (size <= 0) {
            return null;
        }
        byte[] dataFile = new byte[size];
        int readCount = 0;
        while (readCount < size) {
            int toRead = size - readCount;
            if(toRead > 4096) {
                toRead = 4096;
            }
            readCount += read(dataFile, readCount, toRead);
        }
        return dataFile;
    }

    private short readShort() throws IOException{
        byte[] dataSize = new byte[2];
        read(dataSize);
        return ByteBuffer.wrap(dataSize).order(ByteOrder.BIG_ENDIAN).getShort();
    }

    long readDate() throws IOException{
        byte[] data = new byte[12];
        read(data);
        Calendar calendar = Calendar.getInstance();
        final long NOW = calendar.getTimeInMillis();
        calendar.setTimeInMillis(0);
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);
        calendar.set(Calendar.YEAR, buffer.getShort(0));
        calendar.set(Calendar.MONTH, buffer.getShort(2)-1);
        calendar.set(Calendar.DAY_OF_MONTH, buffer.getShort(4));
        calendar.set(Calendar.HOUR_OF_DAY, buffer.getShort(6));
        calendar.set(Calendar.MINUTE, buffer.getShort(8));
        calendar.set(Calendar.SECOND, buffer.getShort(10));
        if(calendar.getTimeInMillis() > NOW) {
            return NOW;
        }
        return calendar.getTimeInMillis();
    }
}
