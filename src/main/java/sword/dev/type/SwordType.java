package sword.dev.type;

public class SwordType {
    public static class Utf8 extends SwordType.PrimitiveType {
        public Utf8() {
            super("utf8");
        }
    }

    public static class Int extends SwordType.PrimitiveType {
        private final int bitWidth;
        private final boolean isSigned = true;

        public Int(int bitWidth) {
            super("int" + bitWidth);
            this.bitWidth = bitWidth;
        }

        public int getBitWidth() {
            return bitWidth;
        }
    }

    public static class TinyInt extends SwordType.PrimitiveType {
        public TinyInt() {
            super("tinyint");
        }
    }

    public static abstract class PrimitiveType extends SwordType {
        private final String name;

        protected PrimitiveType(String name) {
            this.name = name;
        }

        public boolean isComplex() {
            return false;
        }

        public String getName() {
            return name;
        }
    }
}
