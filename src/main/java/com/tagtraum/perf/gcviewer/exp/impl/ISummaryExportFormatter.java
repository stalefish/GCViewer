/**
 * 
 */
package com.tagtraum.perf.gcviewer.exp.impl;

/**
 * @author sean
 *
 */
public interface ISummaryExportFormatter {
    static final String NAME = "summaryExportFormatter";
    static final String MEMORY_UNIT = "memoryUnit";
    String formatLine(String tag, String value, String units);
}
