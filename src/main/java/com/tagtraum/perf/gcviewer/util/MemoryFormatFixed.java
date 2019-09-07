package com.tagtraum.perf.gcviewer.util;

public class MemoryFormatFixed extends MemoryFormat {

    private final MemorySizeUnitType memorySizeUnitType;

    public MemoryFormatFixed(MemorySizeUnitType memorySizeUnitType) {
        this.memorySizeUnitType = memorySizeUnitType;
    }

    @Override
    protected MemorySizeUnitType determineUnit(double bytes) {
        return memorySizeUnitType;
    }
}
