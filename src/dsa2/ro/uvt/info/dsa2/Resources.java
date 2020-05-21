package ro.uvt.info.dsa2;

import java.io.InputStream;

public class Resources {

    public static InputStream getResourceFile(String name) {
        InputStream in = Resources.class.getResourceAsStream(name);
        return in;
    }

}
