package concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author long
 * @date 2020/9/22
 *
 * 案例：传统版生产者消费者
 */
public class ProdConsumerTraditional {
    public static void main(String[] args) throws InterruptedException {
        ShareResource shareResource = new ShareResource();
        new Thread(() -> shareResource.decrement(), "B").start();
        TimeUnit.SECONDS.sleep(1);
        new Thread(() -> shareResource.increment(), "A").start();
    }
}

class ShareResource {
    /**
     * A:0   B:1
     */
    private int number = 0;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void increment() {
        lock.lock();
        try {
            while(number != 0) {
                //等待
                condition.await();
            }
            number++;
            System.out.println(Thread.currentThread().getName() + "\t" + number);
            condition.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void decrement() {
        lock.lock();
        try {
            while(number != 1) {
                //等待
                condition.await();
            }
            number--;
            System.out.println(Thread.currentThread().getName() + "\t" + number);
            condition.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
