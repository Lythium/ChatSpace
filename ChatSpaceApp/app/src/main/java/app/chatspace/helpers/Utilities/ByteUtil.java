package app.chatspace.helpers.Utilities;

/***
 * Class to perform actions on byte arrays.
 *
 * @author Misels Kaporins
 */
public class ByteUtil
{
    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * Converts byte array to its string representation.
     *
     * Based on the implementation provided here:
     * http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
     * @param bytes Byte array
     * @return Returns a string based on the provided byte array
     */
    public static String bytesToHex(byte[] bytes)
    {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++)
        {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
