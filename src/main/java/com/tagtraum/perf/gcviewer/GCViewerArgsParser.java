package com.tagtraum.perf.gcviewer;

import com.tagtraum.perf.gcviewer.exp.DataWriterType;
import com.tagtraum.perf.gcviewer.exp.impl.ISummaryExportFormatter;
import com.tagtraum.perf.gcviewer.model.GCResource;
import com.tagtraum.perf.gcviewer.model.GcResourceFile;
import com.tagtraum.perf.gcviewer.model.GcResourceSeries;
import com.tagtraum.perf.gcviewer.util.MemorySizeUnitType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Parser for commandline arguments. 
 * 
 * @author engineersamuel
 */
public class GCViewerArgsParser {
    private static final int ARG_POS_GCFILE = 0;
    private static final int ARG_POS_SUMMARY_FILE = 1;
    private static final int ARG_POS_CHART_FILE = 2;
    
    private int argumentCount;
    private String chartFilePath;
    private String gcFile;
    private String summaryFilePath;
    private DataWriterType type = DataWriterType.SUMMARY;
    private MemorySizeUnitType memoryUnit = null;
    Map<String, Object> configuration = new TreeMap<>();

    public int getArgumentCount() {
        return argumentCount;
    }
    
    public String getChartFilePath() {
        return chartFilePath;
    }

    public GCResource getGcResource() {
        List<String> files = Arrays.asList(gcFile.split(";"));
        List<GCResource> resources = files.stream().map(GcResourceFile::new).collect(Collectors.toList());
        if (resources.isEmpty())
            throw new IllegalStateException("Found no valid resource!");

        if (resources.size() == 1) {
            return resources.get(0);
        }
        else {
            return new GcResourceSeries(resources);
        }
    }
    
    public String getSummaryFilePath() {
        return summaryFilePath;
    }

    public DataWriterType getType() { 
        return type; 
    }

    public MemorySizeUnitType getMemoryUnit() {
        return memoryUnit;
    }

    public Map<String, Object> getConfiguration() {
        return configuration;
    }

    /**
     * Parse arguments given in parameter. If an illegal argument is given, an exception is thrown.
     * 
     * @param args command line arguments to be parsed
     * @throws GCViewerArgsParserException notify about illegal argument
     */
    public void parseArguments(String[] args) throws GCViewerArgsParserException {
        List<String> argsList = new ArrayList<String>(Arrays.asList(args));
        int typeIdx = argsList.indexOf("-t");

        // If there is a -t and there is a string after, set the type
        if (typeIdx != -1 && argsList.size() > (typeIdx + 1)) {
            type = parseType(argsList.get(typeIdx + 1));
            // Chomp these two from the array to prevent any order issues
            argsList.remove(typeIdx);
            argsList.remove(typeIdx);
        } 
        else if (typeIdx != -1) {
            // No specific type set, just keep the default
            argsList.remove(typeIdx);
        }

        int memUnitIdx = argsList.indexOf("-m");

        // If there is a -m and there is a string after, set the memoryUnit
        if (memUnitIdx != -1 && argsList.size() > (memUnitIdx + 1)) {
            memoryUnit = parseMemType(argsList.get(memUnitIdx + 1));
            configuration.put(ISummaryExportFormatter.MEMORY_UNIT, memoryUnit);
            // Chomp these two from the array to prevent any order issues
            argsList.remove(memUnitIdx);
            argsList.remove(memUnitIdx);
        }
        else if (memUnitIdx != -1) {
            // No specific type set, just keep the default
            argsList.remove(memUnitIdx);
        }

        argumentCount = argsList.size();
        gcFile = safeGetArgument(argsList, ARG_POS_GCFILE);
        summaryFilePath = safeGetArgument(argsList, ARG_POS_SUMMARY_FILE);
        chartFilePath = safeGetArgument(argsList, ARG_POS_CHART_FILE);
    }

    private DataWriterType parseType(String type) throws GCViewerArgsParserException {
        try {
            return DataWriterType.valueOf(type);
        }
        catch (IllegalArgumentException e) {
            throw new GCViewerArgsParserException(type);
        }
    }

    private MemorySizeUnitType parseMemType(String type) throws GCViewerArgsParserException {
        try {
            return MemorySizeUnitType.valueOf(type);
        }
        catch (IllegalArgumentException e) {
            throw new GCViewerArgsParserException(type);
        }
    }

    private String safeGetArgument(List<String> arguments, int index) {
        if (arguments.size() > index) {
            return arguments.get(index);
        }
        else {
            return null;
        }
    }

}
