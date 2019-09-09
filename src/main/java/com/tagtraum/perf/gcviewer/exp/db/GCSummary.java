package com.tagtraum.perf.gcviewer.exp.db;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

@Data
@Entity
@Table(name="gc_summary")
public class GCSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "gcLogFile")
    private String gcLogFile;

    @Column(name = "startDate")
    private Date startDate;

    @Column(name = "startTime")
    private Time startTime;

    @Column(name = "endDate")
    private Date endDate;

    @Column(name = "endTime")
    private Time endTime;

    @Column(name = "totalHeapAllocMax")
    private int totalHeapAllocMax;

    @Column(name = "totalHeapUsedMax")
    private double totalHeapUsedMax;

    @Column(name = "totalTenuredAllocMax")
    private double totalTenuredAllocMax;

    @Column(name = "totalTenuredUsedMax")
    private double totalTenuredUsedMax;

    @Column(name = "totalYoungAllocMax")
    private double totalYoungAllocMax;

    @Column(name = "totalYoungUsedMax")
    private double totalYoungUsedMax;

    @Column(name = "totalPermAllocMax")
    private double totalPermAllocMax;

    @Column(name = "totalPermUsedMax")
    private double totalPermUsedMax;

    @Column(name = "footprintAfterFullGC")
    private double footprintAfterFullGC;

    @Column(name = "slopeAfterFullGC")
    private double slopeAfterFullGC;

    @Column(name = "freedMemoryByFullGC")
    private double freedMemoryByFullGC;

    @Column(name = "avgFreedMemoryByFullGC")
    private double avgFreedMemoryByFullGC;

    @Column(name = "avgFreedMemoryByFullGCStdDev")
    private double avgFreedMemoryByFullGCStdDev;

    @Column(name = "avgfootprintAfterGC")
    private double avgfootprintAfterGC;

    @Column(name = "avgfootprintAfterGCStdDev")
    private double avgfootprintAfterGCStdDev;

    @Column(name = "slopeAfterGC")
    private double slopeAfterGC;

    @Column(name = "avgRelativePostGCInc")
    private double avgRelativePostGCInc;

    @Column(name = "freedMemoryByGC")
    private double freedMemoryByGC;

    @Column(name = "avgFreedMemoryByGC")
    private double avgFreedMemoryByGC;

    @Column(name = "avgFreedMemoryByGCStdDev")
    private double avgFreedMemoryByGCStdDev;

    @Column(name = "avgPromotion")
    private double avgPromotion;

    @Column(name = "promotionTotal")
    private double promotionTotal;

    @Column(name = "pauseCount")
    private int pauseCount;

    @Column(name = "avgPause")
    private double avgPause;

    @Column(name = "avgPauseStdDev")
    private double avgPauseStdDev;

    @Column(name = "minPause")
    private double minPause;

    @Column(name = "maxPause")
    private double maxPause;

    @Column(name = "gcPauseCount")
    private int gcPauseCount;

    @Column(name = "avgGCPause")
    private double avgGCPause;

    @Column(name = "avgGCPauseStdDev")
    private double avgGCPauseStdDev;

    @Column(name = "avgFullGCPause")
    private double avgFullGCPause;

    @Column(name = "accumPause")
    private double accumPause;

    @Column(name = "fullGCPause")
    private double fullGCPause;

    @Column(name = "gcPause")
    private double gcPause;

    @Column(name = "footprint")
    private long footprint;

    @Column(name = "freedMemory")
    private double freedMemory;

    @Column(name = "throughput")
    private double throughput;

    @Column(name = "totalTime")
    private long totalTime;

    @Column(name = "freedMemoryPerMin")
    private double freedMemoryPerMin;

    @Column(name = "gcPerformance")
    private double gcPerformance;

    @Column(name = "fullGCPerformance")
    private double fullGCPerformance;

}
