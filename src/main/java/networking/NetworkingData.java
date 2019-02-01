package networking;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class NetworkingData {
	public static final String PREFIX = "SMSG";
	public static final String SUFFIX = "EMSG";
	public static final String POSITION_CODE = "POS";
	public static final String COLLISION_CODE = "COS";
	public static final String STOP_CODE = "EXIT"; 
	public static final int STRING_LIMIT = 24;
	public static final Charset CHARSET = StandardCharsets.US_ASCII;
	public static final int PORT = 3232; // the client port that server sends to.
	public static final String START_CODE = "START";
}