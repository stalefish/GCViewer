package com.tagtraum.perf.gcviewer.util;

public enum MemorySizeUnitType {
    M {
        @Override
        char charValue() {
            return 'M';
        }
    },
    K {
        @Override
        char charValue() {
            return 'K';
        }
    },
    B {
        @Override
        char charValue() {
            return 'B';
        }
    };

    abstract char charValue();

}
