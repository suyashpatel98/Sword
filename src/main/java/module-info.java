module sword {
    requires jdk.incubator.vector;
    requires jmh.core;
    requires jdk.unsupported;
    opens sword.dev.performance to org.openjdk.jmh.core;
}