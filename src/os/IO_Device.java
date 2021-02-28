
package os;

import os.process.Process;

/**
 *
 * @author mnprtpsingh
 */
public class IO_Device {
    private Process busyProcess;

    public Process getBusyProcess() {
        return this.busyProcess;
    }

    public void setBusyProcess(Process proc) {
        this.busyProcess = proc;
    }
}
