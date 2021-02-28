
package os;

import os.process.Process;
import os.job.*;

/**
 *
 * @author mnprtpsingh
 */
public class ComputerSystem {
    private final int totalRAM;
    private final int osMemorySize;
    private int memoryUsage;
    private int counter;
    private int clock;
    private double utilization;

    private final CPU cpu;
    private final IO_Device ioDevice;
    private final LongTermScheduler lts;
    private final ShortTermScheduler sts;

    ComputerSystem(int ram, int osMemorySize) {
        this.cpu = new CPU();
        this.ioDevice = new IO_Device();
        this.lts = new LongTermScheduler(this);
        this.sts = new ShortTermScheduler(this);

        this.totalRAM = ram;
        this.osMemorySize = osMemorySize;
        this.memoryUsage = osMemorySize;
        this.counter = 0;
        this.clock = 0;
    }

    public int getRAM() {
        return this.totalRAM;
    }

    public int getCounter() {
        return this.counter;
    }

    public void incrementCounter() {
        this.counter++;
    }

    public int getClock() {
        return this.clock;
    }

    private void incrementClock() {
        this.clock++;
    }

    public LongTermScheduler getLongTermScheduler() {
        return this.lts;
    }

    public ShortTermScheduler getShortTermScheduler() {
        return this.sts;
    }

    public int getAvailableMemorySize() {
        return (this.totalRAM - this.memoryUsage);
    }

    public boolean allocateMemory(int request) {
        if (request <= 0) return false;
        if (this.getAvailableMemorySize() < request) return false;
        this.memoryUsage += request;
        return true;
    }

    public boolean freeMemory(int size) {
        if (size <= 0) return false;
        if (size > this.memoryUsage - this.osMemorySize) return false;
        this.memoryUsage -= size;
        return true;
    }

    public void dispatch() {
        Process proc = this.cpu.getRunningProcess();
        if (proc == null) {
            proc = this.sts.dispatchToCPU();
            if (proc != null) {
                proc.dispatch();
                this.cpu.setRunningProcess(proc);
            }
        }

        proc = this.ioDevice.getBusyProcess();
        if (proc == null) {
            proc = this.sts.dispatchForIO();
            this.ioDevice.setBusyProcess(proc);
        }
    }

    @SuppressWarnings("empty-statement")
    public boolean run() {
        Process runningProc = this.cpu.getRunningProcess();
        Process busyProc = this.ioDevice.getBusyProcess();
        if (runningProc == null && busyProc == null) {
            if (this.sts.isTerminated() && !this.lts.hasJob()) return false;
            while (this.sts.isDeadlocked()) {
                Process proc = this.sts.getMemoryAllocated().remove();
                this.sts.getMemoryWaitQueue().remove(proc);
                proc.kill();
                this.sts.getKilledQueue().add(proc);
                while (this.sts.allotMemory());
            }
            if (this.sts.isTerminated() && !this.lts.hasJob()) return false;

            this.dispatch();
            runningProc = this.cpu.getRunningProcess();
            busyProc = this.ioDevice.getBusyProcess();
        }

        if (runningProc != null) {
            runningProc.run();
            this.utilization++;
        }

        if (busyProc != null) {
            busyProc.run();
        }

        this.incrementCounter();
        this.incrementClock();

        if (this.clock % 200 == 0) {
            while (this.lts.loadJob());
        }

        if (runningProc != null && runningProc.getRemainingTime() == 0) {
            this.cpu.setRunningProcess(null);
            if (runningProc.job.isLastBurst()) {
                runningProc.terminate();
                this.sts.getTerminatedQueue().add(runningProc);
            } else {
                runningProc.job.getNextBurst();
                runningProc.ioWait();
                this.sts.getIOWaitQueue().add(runningProc);
            }
        }

        if (busyProc != null && busyProc.getRemainingTime() == 0) {
            this.ioDevice.setBusyProcess(null);
            if (busyProc.job.isLastBurst()) {
                busyProc.terminate();
                this.sts.getTerminatedQueue().add(busyProc);
            } else {
                CPU_Burst bst = (CPU_Burst) busyProc.job.getNextBurst();
                int size = bst.getMemoryRequired();
                if (size > this.getAvailableMemorySize()) {
                    busyProc.memoryWait();
                    this.sts.getMemoryWaitQueue().add(busyProc);
                    this.sts.getMemoryAllocated().add(busyProc);
                } else {
                    this.allocateMemory(size);
                    busyProc.ready();
                    this.sts.getReadyQueue().add(busyProc);
                }
            }
        }

        while (this.sts.allotMemory());

        Process proc = this.sts.dispatchToCPU();
        runningProc = this.cpu.getRunningProcess();
        if (runningProc != null && proc != null) {
            int time = runningProc.getRemainingTime();
            if (time > proc.getRemainingTime()) {
                runningProc.preempt();
                this.sts.getReadyQueue().add(runningProc);
                proc.dispatch();
                this.cpu.setRunningProcess(proc);
            } else {
                this.sts.getReadyQueue().add(proc);
            }
        } else if (runningProc == null && proc != null) {
            proc.dispatch();
            this.cpu.setRunningProcess(proc);
        }

        proc = this.sts.dispatchForIO();
        busyProc = this.ioDevice.getBusyProcess();
        if (busyProc != null && proc != null) {
            int time = busyProc.getRemainingTime();
            if (time > proc.getRemainingTime()) {
                busyProc.preempt();
                this.sts.getIOWaitQueue().add(busyProc);
                this.ioDevice.setBusyProcess(proc);
            } else {
                this.sts.getIOWaitQueue().add(proc);
            }
        } else if (busyProc == null && proc != null) {
            this.ioDevice.setBusyProcess(proc);
        }
        return true;
    }

    public void sleep(int time) {
        this.clock += time;
    }

    public double getCPUUtilization() {
        return this.utilization / this.clock;
    }
}
