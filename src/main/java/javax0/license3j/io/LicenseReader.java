package javax0.license3j.io;

import javax0.license3j.License;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Reads a license from some input.
 */
public class LicenseReader implements Closeable {

    private final InputStream is;

    /**
     * Create a new license reader that will read the license from the input stream.
     * Note that using this version of LicenseReader does not provide any protection
     * against enormously and erroneously large input. The caller has to make sure
     * that the source of the input stream is really a license file and that it is not
     * too large for the application with the actual memory settings.
     *
     * @param is the input stream from which the license is to be read
     */
    public LicenseReader(InputStream is) {
        this.is = is;
    }

    /**
     * Create a new license reader that will read the license from the file.
     * If the size of the file is larger than the given limit then this
     * constrcutor will throw illegal argument exception.
     *
     * @param file  the file that contains the license
     * @param limit the maximum number of bytes of the license that the program can handle
     * @throws FileNotFoundException if the file cannot be found
     */
    public LicenseReader(File file, long limit) throws FileNotFoundException {
        this(new FileInputStream(file));
        if (file.length() > limit) {
            throw new IllegalArgumentException("License file is too long.");
        }
    }

    /**
     * Create a new license reader that will read the license from the file. This
     * constructor simply opens an input stream from the file and calls {@link #LicenseReader(InputStream)}.
     * See the notes there about the size limits.
     *
     * @param file the file that contains the license
     * @throws FileNotFoundException if the file cannot be found
     */
    public LicenseReader(File file) throws FileNotFoundException {
        this(new FileInputStream(file));
    }

    /**
     * Create a new license reader that will read the license from the file named. This
     * constructor simply opens an input stream from the file and calls {@link #LicenseReader(File, long)}.
     * See the notes there about the size limits.
     *
     * @param fileName the name of the file
     * @param limit    the maximum number of bytes of the license that the program can handle
     * @throws FileNotFoundException if the file cannot be found
     */
    public LicenseReader(String fileName, long limit) throws FileNotFoundException {
        this(new File(fileName), limit);
    }

    /**
     * Create a new license reader that will read the license from the file named. This
     * constructor simply opens an input stream from the file and calls {@link #LicenseReader(File)}.
     * See the notes there about the size limits.
     *
     * @param fileName the name of the file
     * @throws FileNotFoundException if the file cannot be found
     */
    public LicenseReader(String fileName) throws FileNotFoundException {
        this(new File(fileName));
    }


    /**
     * Read the license from the input assuming the license is binary formatted.
     *
     * @return the license created from the file
     * @throws IOException when the file cannot be read
     */
    public License read() throws IOException {
        return read(IOFormat.BINARY);
    }

    /**
     * Read the license from the input assuming that the format of the license on the input has the format specified by
     * the argument.
     *
     * @param format the assumed format of the license, can be {@link IOFormat#STRING},
     *               {@link IOFormat#BASE64} or {@link IOFormat#BINARY}
     * @return the license
     * @throws IOException if the input cannot be read
     */
    public License read(IOFormat format) throws IOException {
        switch (format) {
            case BINARY:
                return License.Create.from(ByteArrayReader.readInput(is));
            case BASE64:
                return License.Create.from(Base64.getDecoder().decode(ByteArrayReader.readInput(is)));
            case STRING:
                return License.Create.from(new String(ByteArrayReader.readInput(is), StandardCharsets.UTF_8));
        }
        throw new IllegalArgumentException("License format " + format + " is unknown.");
    }

    @Override
    public void close() throws IOException {
        if (is != null) {
            is.close();
        }
    }
}
