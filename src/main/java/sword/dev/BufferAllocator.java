package sword.dev;

public interface BufferAllocator {
    SwordBuf allocate(int size);
    void free(SwordBuf buffer);
}
