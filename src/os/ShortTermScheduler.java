
package os;

import os.job.*;
import os.process.Process;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Comparator;

/**
 *
 * @author mnprtpsingh
 */
public class ShortTermScheduler {
    private final ComputerSystem system;
    private final PriorityQueue<Process> readyQueue;
    private final PriorityQueue<Process> ioWaitQueue;
    private final PriorityQueue<Process> memoryWaitQueue;
    private final PriorityQueue<Process> memoryAllocated;
    private final LinkedList<Process> killedQueue;
    private final LinkedList<Process> terminatedQueue;

    ShortTermScheduler(ComputerSystem system) {
        this.system = system;
        this.readyQueue = new PriorityQueue<>(new ProcessRemainingTimeComparator());
        this.ioWaitQueue = new PriorityQueue<>(new ProcessRemainingTimeComparator());
        this.memoryWaitQueue = new PriorityQueue<>(new ProcessMemoryAllocatedComparatorLess());
        this.memoryAllocated = new PriorityQueue<>(new ProcessMemoryAllocatedComparatorGreater());
        this.killedQueue = new LinkedList<>();
        this.terminatedQueue = new LinkedList<>();
    }

    public void createProcess(Job job) {
        int pid = this.system.getCounter();
        int arrTime = this.system.getClock();
        Process proc = new Process(job, this.system, pid, arrTime);
        this.readyQueue.add(proc);
        this.system.incrementCounter();
    }

    public boolean isTerminated() {
        if (!this.readyQueue.isEmpty()) return false;
        if (!this.ioWaitQueue.isEmpty()) return false;
        return this.memoryWaitQueue.isEmpty();
    }

    public boolean isDeadlocked() {
        if (!this.readyQueue.isEmpty()) return false;
        if (!this.ioWaitQueue.isEmpty()) return false;
        return !this.memoryWaitQueue.isEmpty();
    }

    public Process dispatchToCPU() {
        if (this.readyQueue.isEmpty()) return null;
        return this.readyQueue.remove();
    }

    public Process dispatchForIO() {
        if (this.ioWaitQueue.isEmpty()) return null;
        return this.ioWaitQueue.remove();
    }

    public boolean allotMemory() {
        if (!this.memoryWaitQueue.isEmpty()) {
            int availableMemory = this.system.getAvailableMemorySize();
            Process proc = this.memoryWaitQueue.element();
            CPU_Burst bst = (CPU_Burst) proc.job.getBurst();
            int memoryRequired = bst.getMemoryRequired();
            if (memoryRequired <= availableMemory) {
                this.memoryWaitQueue.remove();
                this.system.allocateMemory(memoryRequired);
                proc.ready();
                this.readyQueue.add(proc);
                return true;
            }
        }
        return false;
    }

    public PriorityQueue<Process> getReadyQueue() {
        return this.readyQueue;
    }

    public PriorityQueue<Process> getIOWaitQueue() {
        return this.ioWaitQueue;
    }

    public PriorityQueue<Process> getMemoryWaitQueue() {
        return this.memoryWaitQueue;
    }

    public PriorityQueue<Process> getMemoryAllocated() {
        return this.memoryAllocated;
    }

    public LinkedList<Process> getKilledQueue() {
        return this.killedQueue;
    }

    public LinkedList<Process> getTerminatedQueue() {
        return this.terminatedQueue;
    }
}

class ProcessRemainingTimeComparator implements Comparator<Process> {

    @Override
    public int compare(Process p1, Process p2) {
        int t1 = p1.getRemainingTime();
        int t2 = p2.getRemainingTime();
        if (t1 < t2) return -1;
        else if (t1 > t2) return 1;
        else if (p1.pid < p2.pid) return -1;
        else if (p1.pid > p2.pid) return 1;
        return 0;
    }
}

class ProcessMemoryAllocatedComparatorLess implements Comparator<Process> {

    @Override
    public int compare(Process p1, Process p2) {
        int t1 = p1.getMemoryAllocated();
        int t2 = p2.getMemoryAllocated();
        if (t1 < t2) return -1;
        else if (t1 > t2) return 1;
        else if (p1.pid < p2.pid) return -1;
        else if (p1.pid > p2.pid) return 1;
        return 0;
    }
}

class ProcessMemoryAllocatedComparatorGreater implements Comparator<Process> {

    @Override
    public int compare(Process p1, Process p2) {
        int t1 = p1.getMemoryAllocated();
        int t2 = p2.getMemoryAllocated();
        if (t1 > t2) return -1;
        else if (t1 < t2) return 1;
        else if (p1.pid > p2.pid) return -1;
        else if (p1.pid < p2.pid) return 1;
        return 0;
    }
}
