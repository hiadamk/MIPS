package server;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class NetworkingData {
    public static final String prefix = "SMSG";
    public static final String suffix = "EMSG";
    public static final String positionCode = "POS";
    public static final String collisionsCode = "COS";
    public static final String stopCode = "EXIT";
    public static final int STRING_LIMIT = 24;
    public static final Charset charset = StandardCharsets.US_ASCII;
}
