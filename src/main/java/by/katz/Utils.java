package by.katz;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

    public static void beep() {
        Toolkit.getDefaultToolkit().beep();
    }

    public static String readFromStream(InputStream inputStream) throws IOException {
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        final byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1)
            result.write(buffer, 0, length);
        return result.toString("UTF-8");
    }
}
