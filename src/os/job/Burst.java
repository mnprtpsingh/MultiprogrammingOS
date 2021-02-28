
package os.job;

/**
 *
 * @author mnprtpsingh
 */
public class Burst {
    private final int burstTime;
    private final BurstType burstType;
    private int remainingTime;
       
    Burst(int time, BurstType type) {
        this.burstTime = time;
        this.burstType = type;
        this.remainingTime = time;
    }
    
    public int getBurstTime() {
        return this.burstTime;
    }
    
    public BurstType getBurstType() {
        return this.burstType;
    }
    
    public int getRemainingTime() {
        return this.remainingTime;
    }
    
    public void run() {
        this.remainingTime--;
    }
}
