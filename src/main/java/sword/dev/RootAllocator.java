package sword.dev;

import sword.dev.exceptions.OutOfMemoryException;

public class RootAllocator implements BufferAllocator {
    private final long limit;
    private long allocated;

    public RootAllocator() {
        this(Long.MAX_VALUE);
    }

    public RootAllocator(long limit) {
        this.limit = limit;
        this.allocated = 0;
    }

    @Override
    public synchronized SwordBuf allocate(int size) throws OutOfMemoryException {
        if (size < 0) {
            throw new IllegalArgumentException("Cannot allocate negative size");
        }
        if (allocated + size > limit) {
            throw new OutOfMemoryException("Cannot allocate " + size + " bytes. Current allocation: " + allocated + ", Limit: " + limit);
        }
        try {
            SwordBuf buffer = new SwordBuf(this, (int)size);
            allocated += size;
            return buffer;
        } catch (Exception e) {
            throw new OutOfMemoryException("Failed to allocate buffer: " + e.getMessage());
        }
    }

    @Override
    public synchronized void free(SwordBuf buffer) {
        if (buffer == null) {
            return;
        }
        long size = buffer.capacity();
        allocated -= size;
        if (allocated < 0) {
            allocated = 0; // This shouldn't happen, but let's be safe
        }
    }

    public synchronized long getAllocatedMemory() {
        return allocated;
    }

    public long getLimit() {
        return limit;
    }

    public synchronized void close() {
        // In a real implementation, you might want to ensure all buffers are freed
        allocated = 0;
    }
}
