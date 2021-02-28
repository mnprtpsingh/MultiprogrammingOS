
package os.process;

import os.job.*;
import os.ComputerSystem;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author mnprtpsingh
 */
public class Process {
    public final int pid;
    public final Job job;
    public final int arrivalTime;
    public ProcessState processState;

    private final ComputerSystem system;
    private int completionTime;
    private int timesInCPU;
    private int timeSpentInCPU;
    private int timesInIO;
    private int timeSpentInIO;
    private int timesPrempted;
    private int timesWaited;
    private int memoryAllocated;

    public Process(Job job, ComputerSystem system, int pid, int time) {
        this.pid = pid;
        this.job = job;
        this.arrivalTime = time;
        this.processState = ProcessState.READY;
        this.system = system;
        this.memoryAllocated = job.memoryRequired;
        this.timesInCPU = 0;
        this.timeSpentInCPU = 0;
        this.timesInIO = 0;
        this.timeSpentInIO = 0;
        this.timesPrempted = 0;
        this.timesWaited = 0;
    }

    public int getRemainingTime() {
        return this.job.getBurst().getRemainingTime();
    }

    public int getMemoryAllocated() {
        return this.memoryAllocated;
    }

    public void allocateMemory(int size) {
        this.memoryAllocated += size;
    }

    public void dispatch() {
        this.processState = ProcessState.RUNNING;
        this.timesInCPU++;
    }

    public void run() {
        Burst bst = this.job.getBurst();
        if (bst.getRemainingTime() > 0) bst.run();
        if (bst.getBurstType() == BurstType.CPU_BURST) this.timeSpentInCPU++;
        else this.timeSpentInIO++;
    }

    public void ready() {
        this.processState = ProcessState.READY;
        CPU_Burst bst = (CPU_Burst) this.job.getBurst();
        this.memoryAllocated += bst.getMemoryRequired();
    }

    public void preempt() {
        BurstType type = this.job.getBurst().getBurstType();
        if (type == BurstType.CPU_BURST) {
            this.processState = ProcessState.READY;
            this.timesPrempted++;
        } else this.processState = ProcessState.WAITING;
    }

    public void memoryWait() {
        this.processState = ProcessState.WAITING;
        this.timesWaited++;
    }

    public void ioWait() {
        this.processState = ProcessState.WAITING;
        this.timesInIO++;
    }

    public void kill() {
        this.processState = ProcessState.KILLED;
        this.completionTime = this.system.getClock();
        this.system.freeMemory(this.memoryAllocated);
    }

    public void terminate() {
        this.processState = ProcessState.TERMINATED;
        this.completionTime = this.system.getClock();
        this.system.freeMemory(this.memoryAllocated);
    }

    public void displayStatistics() {
        System.out.printf("\n\nProcess ID: %d\n", this.pid);
        System.out.printf("Name: %s\n", this.job.getName());
        System.out.printf("Arrival Time: %d\n", this.arrivalTime);
        System.out.printf("Number of Bursts: %d\n", this.job.numberOfBurst);

        System.out.printf("Number of times in CPU: %d\n", this.timesInCPU);
        System.out.printf("Time spent in CPU: %d\n", this.timeSpentInCPU);

        System.out.printf("Number of times performed IO: %d\n", this.timesInIO);
        System.out.printf("Time spent performing IO: %d\n", this.timeSpentInIO);

        System.out.printf("Number of times waiting for memory: %d\n", this.timesWaited);
        System.out.printf("Number of times preempted: %d\n", this.timesPrempted);

        System.out.printf("Completion Time: %d\n", this.completionTime);
        System.out.printf("Final State: %s\n", this.processState.toString());
    }

    public void displayStatistics(FileWriter writer) throws IOException {
        String s = System.lineSeparator();
        writer.write(s + s + "Process ID: " + Integer.toString(this.pid));
        writer.write(s + "Name: " + this.job.getName());
        writer.write(s + "Arrival Time: " + Integer.toString(this.arrivalTime));
        writer.write(s + "Number of Bursts: " + Integer.toString(this.job.numberOfBurst));

        writer.write(s + "Number of times in CPU: " + Integer.toString(this.timesInCPU));
        writer.write(s + "Time spent in CPU: " + Integer.toString(this.timeSpentInCPU));

        writer.write(s + "Number of times performed IO: " + Integer.toString(this.timesInIO));
        writer.write(s + "Time spent performing IO: " + Integer.toString(this.timeSpentInIO));

        writer.write(s + "Number of times waiting for memory: " + Integer.toString(this.timesWaited));
        writer.write(s + "Number of times preempted: " + Integer.toString(this.timesPrempted));

        writer.write(s + "Completion Time: " + Integer.toString(this.completionTime));
        writer.write(s + "Final State: " + this.processState.toString());
    }
}
