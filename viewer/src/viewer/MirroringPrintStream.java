package viewer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * A {@link PrintStream} that writes everything through to the real console
 * unchanged, while also handing each completed line to a
 * {@link ConsoleFrameParser}.
 * <p>
 * Installed with {@code System.setOut(...)}, this is the only thing that
 * "hijacks" the console: standard input, and everything the console shows,
 * stay exactly as they are. This class never touches {@code src/game} or
 * {@code src/edu} -- it only observes bytes that were already being printed.
 */
public final class MirroringPrintStream extends PrintStream {

    public MirroringPrintStream(PrintStream realOut, ConsoleFrameParser parser) {
        super(new LineTeeingOutputStream(realOut, parser), true, StandardCharsets.UTF_8);
    }

    /** Tees every byte to the real stream, and reports each completed line. */
    private static final class LineTeeingOutputStream extends OutputStream {

        private final OutputStream realOut;
        private final ConsoleFrameParser parser;
        private final ByteArrayOutputStream currentLine = new ByteArrayOutputStream();

        LineTeeingOutputStream(OutputStream realOut, ConsoleFrameParser parser) {
            this.realOut = realOut;
            this.parser = parser;
        }

        @Override
        public void write(int b) throws IOException {
            realOut.write(b);
            if (b == '\n') {
                parser.onLine(currentLine.toString(StandardCharsets.UTF_8));
                currentLine.reset();
            } else if (b != '\r') {
                currentLine.write(b);
            }
        }

        @Override
        public void flush() throws IOException {
            realOut.flush();
        }
    }
}
