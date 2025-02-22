package com.tagtraum.perf.gcviewer.exp.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Time;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Map;

import com.tagtraum.perf.gcviewer.exp.AbstractDataWriter;
import com.tagtraum.perf.gcviewer.exp.db.GCSummary;
import com.tagtraum.perf.gcviewer.model.GCModel;
import com.tagtraum.perf.gcviewer.util.*;

import javax.persistence.EntityManager;

/**
 * SummaryDataWriter writes a csv-file of quite a few parameters of the {@link GCModel} class.
 * <p>
 * It is intended to be used from a command line version of GCViewer.
 * <p>
 * TODO:   datetime fields like 'totalTime' need to separate out their units.
 *
 * @author sean
 */
public class SummaryDataWriter extends AbstractDataWriter {

    private ISummaryExportFormatter formatter;
    private MemorySizeUnitType memorySizeUnitType;

    /*
     * field formatters
     */
    private NumberFormat pauseFormatter;
    private MemoryFormat footprintSlopeFormatter;
    private NumberFormat percentFormatter;
    private NumberFormat gcTimeFormatter;
    private MemoryFormat footprintFormatter;
    private NumberFormat throughputFormatter;
    private TimeFormat totalTimeFormatter;
    private MemoryFormat freedMemoryPerMinFormatter;
    private MemoryFormat sigmaMemoryFormatter;

    public SummaryDataWriter(OutputStream out) {
        //use the default csv formatter
        this(out, null);
    }

    /**
     * Constructor for SummaryDatWriter with additional <code>configuration</code> parameter.
     *
     * @param out OutputStream, where the output should be written to
     * @param configuration Configuration for this SummaryDataWriter; expected contents of the parameter:
     * <ul>
     * <li>String: <code>ISummaryExportFormatter.NAME</code></li>
     * <li>Object: instance of class implementing ISummaryExportFormatter
     * </ul>
     */
    public SummaryDataWriter(OutputStream out, Map<String, Object> configuration) {
        super(out, configuration);

        // don't use "configuration" parameter directly - it may be null
        this.formatter = (ISummaryExportFormatter) getConfiguration().get(ISummaryExportFormatter.NAME);
        if (this.formatter == null) {
            this.formatter = new CsvSummaryExportFormatter();
        }
        this.memorySizeUnitType = (MemorySizeUnitType) getConfiguration().get(ISummaryExportFormatter.MEMORY_UNIT);
        initialiseFormatters();
    }

    private void initialiseFormatters() {
        pauseFormatter = NumberFormat.getInstance();
        pauseFormatter.setMaximumFractionDigits(5);

        totalTimeFormatter = new TimeFormat();

        gcTimeFormatter = NumberFormat.getInstance();
        gcTimeFormatter.setMaximumFractionDigits(2);

        throughputFormatter = NumberFormat.getInstance();
        throughputFormatter.setMaximumFractionDigits(2);

        footprintFormatter = memorySizeUnitType != null ? new MemoryFormatFixed(memorySizeUnitType) : new MemoryFormat();

        sigmaMemoryFormatter = memorySizeUnitType != null ? new MemoryFormatFixed(memorySizeUnitType) : new MemoryFormat();

        footprintSlopeFormatter = memorySizeUnitType != null ? new MemoryFormatFixed(memorySizeUnitType) : new MemoryFormat();

        freedMemoryPerMinFormatter = memorySizeUnitType != null ? new MemoryFormatFixed(memorySizeUnitType) : new MemoryFormat();

        percentFormatter = NumberFormat.getInstance();
        percentFormatter.setMaximumFractionDigits(1);
        percentFormatter.setMinimumFractionDigits(1);
    }

    private void exportValue(PrintWriter writer, String tag, boolean bValue) {
        exportValue(writer, tag, Boolean.toString(bValue), "bool");
    }

    private void exportValue(PrintWriter writer, String tag, String value, String units) {
        String strFormatted = formatter.formatLine(tag, value, units);
        writer.println(strFormatted);
    }

    @Override
    public void write(GCModel model) throws IOException {
        int lastIndexOfSlash = model.getURL().getFile().lastIndexOf('/');

        exportValue(out,
                "gcLogFile",
                model.getURL().getFile().substring(lastIndexOfSlash >= 0 ? lastIndexOfSlash + 1 : 0),
                "-");
        exportValue(out,
                "startDate",
                model.getFirstDateStamp() != null ? model.getFirstDateStamp().toLocalDate().toString() : "",
                "-");
        exportValue(out,
                "startTime",
                model.getFirstDateStamp() != null ? model.getFirstDateStamp().toLocalTime().toString() : "",
                "-");
        exportValue(out,
                "endDate",
                model.getLastEventAdded() != null && model.getLastEventAdded().getDatestamp() != null ? model.getLastEventAdded().getDatestamp().toLocalDate().toString() : "",
                "-");
        exportValue(out,
                "endTime",
                model.getLastEventAdded() != null && model.getLastEventAdded().getDatestamp() != null ? model.getLastEventAdded().getDatestamp().toLocalTime().toString() : "",
                "-");

        GCSummary summary = new GCSummary();
        summary.setGcLogFile(model.getURL().getFile().substring(lastIndexOfSlash >= 0 ? lastIndexOfSlash + 1 : 0));
        if ( model.getFirstDateStamp() != null ) {
            summary.setStartDate(java.sql.Date.valueOf(model.getFirstDateStamp().toLocalDate()));
            summary.setStartTime(Time.valueOf(model.getFirstDateStamp().toLocalTime()));
        }
        if ( model.getLastEventAdded() != null && model.getLastEventAdded().getDatestamp() != null ) {
            summary.setEndDate(java.sql.Date.valueOf(model.getLastEventAdded().getDatestamp().toLocalDate()));
            summary.setEndTime(Time.valueOf(model.getLastEventAdded().getDatestamp().toLocalTime()));
        }

        exportMemorySummary(out, model);
        exportMemorySummary(summary, model);

        exportPauseSummary(out, model);
        exportPauseSummary(summary, model);

        exportOverallSummary(out, model);
        exportOverallSummary(summary, model);
        out.flush();
        persist(summary);
    }


    private void persist(GCSummary gcSummary) {
        EntityManager em = null;
        try {
            em = PersistenceManager.INSTANCE.getEntityManager();
            em.getTransaction().begin();
            em.persist(gcSummary);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if ( em != null) {
                em.close();
            }
        }

    }
    public void exportSummaryFromModel(GCModel model, String filePath) throws IOException {
        FileWriter outFile = new FileWriter(filePath);
        PrintWriter out = new PrintWriter(outFile);

        int lastIndexOfSlash = model.getURL().getFile().lastIndexOf('/');
        exportValue(out,
                "gcLogFile",
                model.getURL().getFile().substring(lastIndexOfSlash >= 0 ? lastIndexOfSlash + 1 : 0),
                "-");

        exportMemorySummary(out, model);
        exportPauseSummary(out, model);
        exportOverallSummary(out, model);

        out.flush();
        out.close();
    }

    private void exportOverallSummary(PrintWriter out, GCModel model) {
        FormattedValue formed = footprintFormatter.formatToFormatted(model.getFootprint());
        exportValue(out, "footprint", formed.getValue(), formed.getUnits());

        formed = footprintFormatter.formatToFormatted(model.getFreedMemory());
        exportValue(out, "freedMemory", formed.getValue(), formed.getUnits());

        if (model.hasCorrectTimestamp()) {
            exportValue(out, "throughput", throughputFormatter.format(model.getThroughput()), "%");
            formed = totalTimeFormatter.formatToFormatted(new Date((long)model.getRunningTime()*1000l));
            exportValue(out, "totalTime", formed.getValue(), formed.getUnits());

            formed = freedMemoryPerMinFormatter.formatToFormatted(model.getFreedMemory()/model.getRunningTime()*60.0);
            exportValue(out, "freedMemoryPerMin", formed.getValue(), formed.getUnits() + "/min");
        }
        else {
            exportValue(out, "throughput", "n.a.", "%");
            exportValue(out, "totalTime", "n.a.", "s");
            exportValue(out, "freedMemoryPerMin", "n.a.", "M/min");
        }

        final boolean gcDataAvailable = model.getGCPause().getN() > 0;
        if (gcDataAvailable) {
            formed = footprintFormatter.formatToFormatted(model.getFreedMemoryByGC().getSum()/model.getGCPause().getSum());
            exportValue(out, "gcPerformance", formed.getValue(), formed.getUnits() + "/s");
        }
        else {
            exportValue(out, "gcPerformance", "n.a.", "M/s");
        }

        final boolean fullGCDataAvailable = model.getFullGCPause().getN() > 0;

        if (fullGCDataAvailable) {
            formed = footprintFormatter.formatToFormatted(model.getFreedMemoryByFullGC().getSum()/model.getFullGCPause().getSum());
            exportValue(out, "fullGCPerformance", formed.getValue(), formed.getUnits() + "/s");
        }
        else {
            exportValue(out, "fullGCPerformance", "n.a.", "M/s");
        }
    }

    private void exportOverallSummary(GCSummary summary, GCModel model) {
        summary.setFootprint(model.getFootprint());
        summary.setFreedMemory(model.getFreedMemory());
        summary.setThroughput(model.getThroughput());
        summary.setTotalTime((long)model.getRunningTime());
        summary.setFreedMemoryPerMin(model.getFreedMemory()/model.getRunningTime()*60.0);

        final boolean gcDataAvailable = model.getGCPause().getN() > 0;
        if (gcDataAvailable) {
            summary.setGcPerformance(model.getFreedMemoryByGC().getSum()/model.getGCPause().getSum());
        }
        final boolean fullGCDataAvailable = model.getFullGCPause().getN() > 0;

        if (fullGCDataAvailable) {
            summary.setFullGCPerformance(model.getFreedMemoryByFullGC().getSum()/model.getFullGCPause().getSum());
        }
    }

    private void exportPauseSummary(PrintWriter out, GCModel model) {
        final boolean pauseDataAvailable = model.getPause().getN() != 0;
        final boolean gcDataAvailable = model.getGCPause().getN() > 0;
        final boolean fullGCDataAvailable = model.getFullGCPause().getN() > 0;

        if (pauseDataAvailable) {
            exportValue(out, "pauseCount", "" + model.getPause().getN(), "-");

            exportValue(out, "avgPauseIsSig", isSignificant(model.getPause().average(), model.getPause().standardDeviation()) );
            exportValue(out, "avgPause", pauseFormatter.format(model.getPause().average()), "s");
            exportValue(out, "avgPause\u03c3", pauseFormatter.format(model.getPause().standardDeviation()), "s");

            exportValue(out, "minPause", pauseFormatter.format(model.getPause().getMin()), "s");
            exportValue(out, "maxPause", pauseFormatter.format(model.getPause().getMax()), "s");

            if (gcDataAvailable) {
                exportValue(out, "gcPauseCount", "" + model.getGCPause().getN(), "-");

                exportValue(out, "avgGCPauseIsSig", isSignificant(model.getGCPause().average(), model.getGCPause().standardDeviation()) );
                exportValue(out, "avgGCPause", pauseFormatter.format(model.getGCPause().average()), "s");
                exportValue(out, "avgGCPause\u03c3", pauseFormatter.format(model.getGCPause().standardDeviation()), "s");
            }
            else {
                exportValue(out, "avgGCPause", "n.a.", "s");
            }

            if (fullGCDataAvailable) {
                exportValue(out, "fullGcPauseCount", "" + model.getFullGCPause().getN(), "-");

                exportValue(out, "avgFullGCPauseIsSig", isSignificant(model.getFullGCPause().average(), model.getFullGCPause().standardDeviation()));
                exportValue(out, "avgFullGCPause", pauseFormatter.format(model.getFullGCPause().average()), "s");
                exportValue(out, "avgFullGCPause\u03c3", pauseFormatter.format(model.getFullGCPause().standardDeviation()), "s");

                exportValue(out, "minFullGCPause", pauseFormatter.format(model.getFullGCPause().getMin()), "s");
                exportValue(out, "maxFullGCPause", pauseFormatter.format(model.getFullGCPause().getMax()), "s");
            }
            else {
                exportValue(out, "avgFullGCPause", "n.a.", "s");
            }
        }
        else {
            exportValue(out, "avgPause", "n.a.", "s");
            exportValue(out, "minPause", "n.a.", "s");
            exportValue(out, "maxPause", "n.a.", "s");
            exportValue(out, "avgGCPause", "n.a.", "s");
            exportValue(out, "avgFullGCPause", "n.a.", "s");
        }
        exportValue(out, "accumPause", gcTimeFormatter.format(model.getPause().getSum()), "s");
        exportValue(out, "fullGCPause", gcTimeFormatter.format(model.getFullGCPause().getSum()), "s");
        exportValue(out, "fullGCPausePc", percentFormatter.format(model.getFullGCPause().getSum()*100.0/model.getPause().getSum()), "%");
        exportValue(out, "gcPause", gcTimeFormatter.format(model.getGCPause().getSum()), "s");
        exportValue(out, "gcPausePc", percentFormatter.format(model.getGCPause().getSum()*100.0/model.getPause().getSum()), "%");
    }

    private void exportPauseSummary(GCSummary summary, GCModel model) {
        final boolean pauseDataAvailable = model.getPause().getN() != 0;
        final boolean gcDataAvailable = model.getGCPause().getN() > 0;
        final boolean fullGCDataAvailable = model.getFullGCPause().getN() > 0;

        if (pauseDataAvailable) {
            summary.setPauseCount(model.getPause().getN());
            summary.setAvgPause(model.getPause().average());
            summary.setAvgPauseStdDev(model.getPause().standardDeviation());
            summary.setMinPause(model.getPause().getMin());
            summary.setMaxPause(model.getPause().getMax());
            if (gcDataAvailable) {
                summary.setGcPauseCount(model.getGCPause().getN());
                summary.setAvgGCPause(model.getGCPause().average());
                summary.setAvgGCPauseStdDev(model.getGCPause().standardDeviation());
            }
            if (fullGCDataAvailable) {
                // G1GC - if we have a full, we are in the dog house
            }
        }
        summary.setAccumPause(model.getPause().getSum());
        summary.setFullGCPause(model.getFullGCPause().getSum());
        summary.setGcPause(model.getGCPause().getSum());
    }

    private boolean isSignificant(final double average, final double standardDeviation) {
        // at least 68.3% of all points are within 0.75 to 1.25 times the average value
        // Note: this may or may not be a good measure, but it at least helps to mark some bad data as such
        return average-standardDeviation > 0.75 * average;
    }

    private void exportMemorySummary(PrintWriter out, GCModel model) {
        FormattedValue formed = footprintFormatter.formatToFormatted(model.getHeapAllocatedSizes().getMax());
        exportValue(out, "totalHeapAllocMax", formed.getValue(), formed.getUnits());
        formed = footprintFormatter.formatToFormatted(model.getHeapUsedSizes().getMax());
        exportValue(out, "totalHeapUsedMax", formed.getValue(), formed.getUnits());
        exportValue(out, "totalHeapUsedMaxpc", percentFormatter.format(model.getHeapUsedSizes().getMax() * 100.0 / model.getHeapAllocatedSizes().getMax()), "%");

        if (model.getTenuredAllocatedSizes().getN() == 0) {
            exportValue(out, "totalTenuredAllocMax", "n/a", "M");
            exportValue(out, "totalTenuredUsedMax", "n/a", "M");
            exportValue(out, "totalTenuredUsedMaxpc", "n/a", "%");
        } else {
            formed = footprintFormatter.formatToFormatted(model.getTenuredAllocatedSizes().getMax());
            exportValue(out, "totalTenuredAllocMax", formed.getValue(), formed.getUnits());
            formed = footprintFormatter.formatToFormatted(model.getTenuredUsedSizes().getMax());
            exportValue(out, "totalTenuredUsedMax", formed.getValue(), formed.getUnits());
            exportValue(out, "totalTenuredUsedMaxpc", percentFormatter.format(model.getTenuredUsedSizes().getMax() * 100.0 / model.getTenuredAllocatedSizes().getMax()), "%");
        }

        if (model.getYoungAllocatedSizes().getN() == 0) {
            exportValue(out, "totalYoungAllocMax", "n/a", "M");
            exportValue(out, "totalYoungUsedMax", "n/a", "M");
            exportValue(out, "totalYoungUsedMaxpc", "n/a", "%");
        } else {
            formed = footprintFormatter.formatToFormatted(model.getYoungAllocatedSizes().getMax());
            exportValue(out, "totalYoungAllocMax", formed.getValue(), formed.getUnits());
            formed = footprintFormatter.formatToFormatted(model.getYoungUsedSizes().getMax());
            exportValue(out, "totalYoungUsedMax", formed.getValue(), formed.getUnits());
            exportValue(out, "totalYoungUsedMaxpc", percentFormatter.format(model.getYoungUsedSizes().getMax() * 100.0 / model.getYoungAllocatedSizes().getMax()), "%");
        }

        if (model.getPermAllocatedSizes().getN() == 0) {
            exportValue(out, "totalPermAllocMax", "n/a", "M");
            exportValue(out, "totalPermUsedMax", "n/a", "M");
            exportValue(out, "totalPermUsedMaxpc", "n/a", "%");
        } else {
            formed = footprintFormatter.formatToFormatted(model.getPermAllocatedSizes().getMax());
            exportValue(out, "totalPermAllocMax", formed.getValue(), formed.getUnits());
            formed = footprintFormatter.formatToFormatted(model.getPermUsedSizes().getMax());
            exportValue(out, "totalPermUsedMax", formed.getValue(), formed.getUnits());
            exportValue(out, "totalPermUsedMaxpc", percentFormatter.format(model.getPermUsedSizes().getMax() * 100.0 / model.getPermAllocatedSizes().getMax()), "%");
        }

        // check whether we have full gc data at all
        final boolean fullGCDataVailable = model.getFootprintAfterFullGC().getN() != 0;
        final boolean fullGCSlopeDataVailable = model.getFootprintAfterFullGC().getN() > 1;

        if (!fullGCDataVailable) {
            exportValue(out, "footprintAfterFullGC", "n.a.", "M");
            exportValue(out, "slopeAfterFullGC", "n.a.", "M/s");
            exportValue(out, "freedMemoryByFullGC", "n.a.", "M");
            exportValue(out, "avgFreedMemoryByFullGC", "n.a.", "M");
            exportValue(out, "avgFreedMemoryByFullGC\u03c3", "n.a.", "M");
            exportValue(out, "avgFreedMemoryByFullGCisSig", "n.a.", "bool");
        }
        else {
            formed = footprintFormatter.formatToFormatted(model.getFootprintAfterFullGC().average());
            exportValue(out, "avgfootprintAfterFullGC", formed.getValue(), formed.getUnits());
            formed = sigmaMemoryFormat(model.getFootprintAfterFullGC().standardDeviation());
            exportValue(out, "avgfootprintAfterFullGC\u03c3", formed.getValue(), formed.getUnits());
            exportValue(out, "avgfootprintAfterFullGCisSig", isSignificant(model.getFootprintAfterFullGC().average(),
                    model.getFootprintAfterFullGC().standardDeviation()));
            formed = footprintFormatter.formatToFormatted(model.getFreedMemoryByFullGC().getSum());
            exportValue(out, "freedMemoryByFullGC", formed.getValue(), formed.getUnits());
            exportValue(out, "freedMemoryByFullGCpc", percentFormatter.format(model.getFreedMemoryByFullGC().getSum()*100.0/model.getFreedMemory()), "%");

            formed = footprintFormatter.formatToFormatted(model.getFreedMemoryByFullGC().average());
            exportValue(out, "avgFreedMemoryByFullGC", formed.getValue(), formed.getUnits() + "/coll");
            formed = sigmaMemoryFormat(model.getFreedMemoryByFullGC().standardDeviation());
            exportValue(out, "avgFreedMemoryByFullGC\u03c3", formed.getValue(), formed.getUnits() + "/coll");
            exportValue(out, "avgFreedMemoryByFullGCisSig", isSignificant(model.getFreedMemoryByFullGC().average(),
                    model.getFreedMemoryByFullGC().standardDeviation()));
            if (fullGCSlopeDataVailable) {
                formed = footprintSlopeFormatter.formatToFormatted(model.getPostFullGCSlope().slope());
                exportValue(out, "slopeAfterFullGC", formed.getValue(), formed.getUnits() + "/s");

                formed = footprintSlopeFormatter.formatToFormatted(model.getRelativePostFullGCIncrease().slope());
                exportValue(out, "avgRelativePostFullGCInc", formed.getValue(), formed.getUnits() + "/coll");
            }
            else {
                exportValue(out, "slopeAfterFullGC", "n.a.", "M/s");
                exportValue(out, "avgRelativePostFullGCInc", "n.a.", "M/coll");
            }
        }
        // check whether we have gc data at all (or only full gc)
        final boolean gcDataAvailable = model.getFootprintAfterGC().getN() != 0;

        if (!gcDataAvailable) {
            exportValue(out, "footprintAfterGC", "n.a.", "M");
            exportValue(out, "slopeAfterGC", "n.a.", "M/s");
            exportValue(out, "freedMemoryByGC", "n.a.", "M");
            exportValue(out, "avgFreedMemoryByGC", "n.a.", "M/coll");
            exportValue(out, "avgRelativePostGCInc", "n.a.", "M/coll");
        }
        else {
            formed = footprintFormatter.formatToFormatted(model.getFootprintAfterGC().average());
            exportValue(out, "avgfootprintAfterGC", formed.getValue(), formed.getUnits());

            formed = sigmaMemoryFormat(model.getFootprintAfterGC().standardDeviation());
            exportValue(out, "avgfootprintAfterGC\u03c3", formed.getValue(), formed.getUnits());
            exportValue(out, "avgfootprintAfterGCisSig", isSignificant(model.getFootprintAfterGC().average(),
                    model.getFootprintAfterGC().standardDeviation()));
            if (fullGCDataVailable && model.getRelativePostGCIncrease().getN() != 0) {
                formed = footprintSlopeFormatter.formatToFormatted(model.getPostGCSlope());
                exportValue(out, "slopeAfterGC", formed.getValue(), formed.getUnits() + "/s");

                formed = footprintSlopeFormatter.formatToFormatted(model.getRelativePostGCIncrease().average());
                exportValue(out, "avgRelativePostGCInc", formed.getValue(), formed.getUnits() + "/coll");
            }
            else {
                exportValue(out, "slopeAfterGC", "n.a.", "M/s");
                exportValue(out, "avgRelativePostGCInc", "n.a.", "M/coll");
            }

            formed = footprintFormatter.formatToFormatted(model.getFreedMemoryByGC().getSum());
            exportValue(out, "freedMemoryByGC", formed.getValue(), formed.getUnits());

            exportValue(out, "freedMemoryByGCpc", percentFormatter.format(model.getFreedMemoryByGC().getSum()*100.0/model.getFreedMemory()), "%");

            formed = footprintFormatter.formatToFormatted(model.getFreedMemoryByGC().average());
            exportValue(out, "avgFreedMemoryByGC", formed.getValue(), formed.getUnits() + "/coll");
            formed = sigmaMemoryFormat(model.getFreedMemoryByGC().standardDeviation());
            exportValue(out, "avgFreedMemoryByGC\u03c3", formed.getValue(), formed.getUnits() + "/coll");
            exportValue(out, "avgFreedMemoryByGCisSig", isSignificant(model.getFreedMemoryByGC().average(),
                    model.getFreedMemoryByGC().standardDeviation()));
        }

        final boolean promotionDataAvailable = model.getPromotion().getN() != 0;

        if (!promotionDataAvailable) {
            exportValue(out, "avgPromotion", "n.a.", "M");
            exportValue(out, "promotionTotal", "n.a.", "M");
        }
        else {
            formed = footprintFormatter.formatToFormatted(model.getPromotion().average());
            exportValue(out, "avgPromotion", formed.getValue(), formed.getUnits());
            formed = footprintFormatter.formatToFormatted(model.getPromotion().getSum());
            exportValue(out, "promotionTotal", formed.getValue(), formed.getUnits());
        }
    }

    private FormattedValue sigmaMemoryFormat(double value) {
        if (Double.isNaN(value)) {
            StringBuffer buffer = new StringBuffer("NaN");
            return new FormattedValue(buffer, ' ');
        }
        return sigmaMemoryFormatter.formatToFormatted(value);
    }


    private void exportMemorySummary(GCSummary summary, GCModel model) {
        summary.setTotalHeapAllocMax(model.getHeapAllocatedSizes().getMax());
        summary.setTotalHeapUsedMax(model.getHeapUsedSizes().getMax());

        if (model.getTenuredAllocatedSizes().getN() != 0) {
            summary.setTotalTenuredAllocMax(model.getTenuredAllocatedSizes().getMax());
            summary.setTotalTenuredUsedMax(model.getTenuredUsedSizes().getMax());
        }

        if (model.getYoungAllocatedSizes().getN() != 0) {
            summary.setTotalYoungAllocMax(model.getYoungAllocatedSizes().getMax());
            summary.setTotalYoungUsedMax(model.getYoungUsedSizes().getMax());
        }

        if (model.getPermAllocatedSizes().getN() != 0) {
            summary.setTotalPermAllocMax(model.getPermAllocatedSizes().getMax());
            summary.setTotalPermUsedMax(model.getPermUsedSizes().getMax());
        }

        final boolean gcDataAvailable = model.getFootprintAfterGC().getN() != 0;
        if (gcDataAvailable) {
            summary.setAvgfootprintAfterGC(model.getFootprintAfterGC().average());
            summary.setAvgfootprintAfterGCStdDev(model.getFootprintAfterGC().standardDeviation());
            summary.setFreedMemoryByGC(model.getFreedMemoryByGC().getSum());
            summary.setAvgFreedMemoryByGC(model.getFreedMemoryByGC().average());
            summary.setAvgFreedMemoryByGCStdDev(model.getFreedMemoryByGC().standardDeviation());
        }
        final boolean promotionDataAvailable = model.getPromotion().getN() != 0;
        if (promotionDataAvailable) {
            summary.setAvgPromotion(model.getPromotion().average());
            summary.setPromotionTotal(model.getPromotion().getSum());
        }
    }

}

