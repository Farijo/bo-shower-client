package farijo.com.starcraft_bo_shower.network.custom_server;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by Teddy on 11/02/2018.
 */

class BOOutputStream extends OutputStream {

    private OutputStream out;

    BOOutputStream(@NonNull OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    void writeFilename(String path) throws IOException {
        write(ByteBuffer.wrap((path + '\n').getBytes()).array());
    }
}
