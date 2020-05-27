
package os.job;

import java.util.ArrayList;

/**
 *
 * @author mnprtpsingh
 */
public class Job {
    private final String name;
    private final ArrayList<Burst> bursts;
    private int currentBurst;
    
    public final int numberOfBurst;
    public final int totalCPUBurstTime;
    public final int totalIOBurstTime;
    public final int memoryRequired;
    
    public Job(String name, ArrayList<Burst> bursts) {
        this.name = name;
        this.bursts = bursts;
        this.numberOfBurst = bursts.size();
        
        CPU_Burst cpuBurst = (CPU_Burst) bursts.get(0);
        this.memoryRequired = cpuBurst.getMemoryRequired();
        
        int CPUBurstTime = 0;
        int IOBurstTime = 0;
        for (int i = 0; i < this.numberOfBurst; i++) {
            Burst burst = this.bursts.get(i);
            BurstType type = burst.getBurstType();
            int time = burst.getBurstTime();
            if (type == BurstType.CPU_BURST) CPUBurstTime += time;
            else if (type == BurstType.IO_BURST) IOBurstTime += time;
        }
        this.totalCPUBurstTime = CPUBurstTime;
        this.totalIOBurstTime = IOBurstTime;
        this.currentBurst = 0;
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean isLastBurst() {
        return (this.currentBurst == this.numberOfBurst - 1);
    }
    
    public Burst getBurst() {
        if (this.currentBurst >= this.numberOfBurst) return null;
        return this.bursts.get(this.currentBurst);
    }
    
    public Burst getNextBurst() {
        if (this.isLastBurst()) return null;
        this.currentBurst++;
        return this.getBurst();
    }
}
