package farijo.com.starcraft_bo_shower.network;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Teddy on 10/02/2018.
 */

class BOStream {

    private final Socket socket;

    BOStream(Socket socket) {
        this.socket = socket;
    }

    BOInputStream getInputStream() throws IOException {
        return new BOInputStream(socket.getInputStream());
    }

    BOInputStream getBufferedInputStream() throws IOException {
        return new BOInputStream(new BufferedInputStream(socket.getInputStream()));
    }

    BOOutputStream getOutputStream() throws IOException {
        return new BOOutputStream(socket.getOutputStream());
    }
}
