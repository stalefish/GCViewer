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

    @Column(name = "gclogfile")
    private String gcLogFile;

    @Column(name = "startdate")
    private Date startDate;

    @Column(name = "starttime")
    private Time startTime;

    @Column(name = "enddate")
    private Date endDate;

    @Column(name = "endtime")
    private Time endTime;

    @Column(name = "totalheapallocmax")
    private int totalHeapAllocMax;

    @Column(name = "totalheapusedmax")
    private double totalHeapUsedMax;

    @Column(name = "totaltenuredallocmax")
    private double totalTenuredAllocMax;

    @Column(name = "totaltenuredusedmax")
    private double totalTenuredUsedMax;

    @Column(name = "totalyoungallocmax")
    private double totalYoungAllocMax;

    @Column(name = "totalyoungusedmax")
    private double totalYoungUsedMax;

    @Column(name = "totalpermallocmax")
    private double totalPermAllocMax;

    @Column(name = "totalpermusedmax")
    private double totalPermUsedMax;

    @Column(name = "footprintafterfullgc")
    private double footprintAfterFullGC;

    @Column(name = "slopeafterfullgc")
    private double slopeAfterFullGC;

    @Column(name = "freedmemorybyfullgc")
    private double freedMemoryByFullGC;

    @Column(name = "avgfreedmemorybyfullgc")
    private double avgFreedMemoryByFullGC;

    @Column(name = "avgfreedmemorybyfullgcstddev")
    private double avgFreedMemoryByFullGCStdDev;

    @Column(name = "avgfootprintaftergc")
    private double avgfootprintAfterGC;

    @Column(name = "avgfootprintaftergcstddev")
    private double avgfootprintAfterGCStdDev;

    @Column(name = "slopeaftergc")
    private double slopeAfterGC;

    @Column(name = "avgrelativepostgcinc")
    private double avgRelativePostGCInc;

    @Column(name = "freedmemorybygc")
    private double freedMemoryByGC;

    @Column(name = "avgfreedmemorybygc")
    private double avgFreedMemoryByGC;

    @Column(name = "avgfreedmemorybygcstddev")
    private double avgFreedMemoryByGCStdDev;

    @Column(name = "avgpromotion")
    private double avgPromotion;

    @Column(name = "promotiontotal")
    private double promotionTotal;

    @Column(name = "pausecount")
    private int pauseCount;

    @Column(name = "avgpause")
    private double avgPause;

    @Column(name = "avgpausestddev")
    private double avgPauseStdDev;

    @Column(name = "minpause")
    private double minPause;

    @Column(name = "maxpause")
    private double maxPause;

    @Column(name = "gcpausecount")
    private int gcPauseCount;

    @Column(name = "avggcpause")
    private double avgGCPause;

    @Column(name = "avggcpausestddev")
    private double avgGCPauseStdDev;

    @Column(name = "avgfullgcpause")
    private double avgFullGCPause;

    @Column(name = "accumpause")
    private double accumPause;

    @Column(name = "fullgcpause")
    private double fullGCPause;

    @Column(name = "gcpause")
    private double gcPause;

    @Column(name = "footprint")
    private long footprint;

    @Column(name = "freedmemory")
    private double freedMemory;

    @Column(name = "throughput")
    private double throughput;

    @Column(name = "totaltime")
    private long totalTime;

    @Column(name = "freedmemorypermin")
    private double freedMemoryPerMin;

    @Column(name = "gcperformance")
    private double gcPerformance;

    @Column(name = "fullgcperformance")
    private double fullGCPerformance;

}
