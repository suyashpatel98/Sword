package sword.dev;

public class BitVectorHelper {

    private BitVectorHelper() {
        // Utility class, no instances
    }

    public static void setBit(SwordBuf buffer, int index) {
        int byteIndex = index / 8;
        int bitIndex = index % 8;
        byte currentByte = buffer.getByte(byteIndex);
        byte newByte = (byte) (currentByte | (1 << bitIndex));
        buffer.setByte(byteIndex, newByte);
    }

    public static void unsetBit(SwordBuf buffer, int index) {
        int byteIndex = index / 8;
        int bitIndex = index % 8;
        byte currentByte = buffer.getByte(byteIndex);
        byte newByte = (byte) (currentByte & ~(1 << bitIndex));
        buffer.setByte(byteIndex, newByte);
    }

    public static boolean getBit(SwordBuf buffer, int index) {
        int byteIndex = index / 8;
        int bitIndex = index % 8;
        byte currentByte = buffer.getByte(byteIndex);
        return (currentByte & (1 << bitIndex)) != 0;
    }

    public static int getNullCount(SwordBuf buffer, int valueCount) {
        int nullCount = 0;
        int byteCount = (valueCount + 7) / 8;

        for (int i = 0; i < byteCount; i++) {
            byte currentByte = buffer.getByte(i);
            nullCount += 8 - Integer.bitCount(currentByte & 0xFF);
        }

        // Adjust for the last byte if valueCount is not a multiple of 8
        int remainder = valueCount % 8;
        if (remainder != 0) {
            byte lastByte = buffer.getByte(byteCount - 1);
            int mask = (1 << remainder) - 1;
            nullCount -= 8 - Integer.bitCount(lastByte & mask);
        }

        return nullCount;
    }

    public static void setValidityBuffer(SwordBuf buffer, int index, boolean isValid) {
        if (isValid) {
            setBit(buffer, index);
        } else {
            unsetBit(buffer, index);
        }
    }
}
