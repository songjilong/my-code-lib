package lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author long
 * @date 2020/9/22
 *
 * 循环打印：线程 A 打印 5 次，线程 B 打印 10 次，线程 C 打印 15 次，循环10次
 */
public class CyclePrint {
    public static void main(String[] args) {
        ShareResource shareResource = new ShareResource();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> shareResource.print5(), "A").start();
        }
        for (int i = 0; i < 10; i++) {
            new Thread(() -> shareResource.print10(), "B").start();
        }
        for (int i = 0; i < 10; i++) {
            new Thread(() -> shareResource.print15(), "C").start();
        }
    }
}

class ShareResource {
    /**
     * A:1  B:2  C:3
     */
    private int number = 1;
    private Lock lock = new ReentrantLock();
    private Condition c1 = lock.newCondition();
    private Condition c2 = lock.newCondition();
    private Condition c3 = lock.newCondition();

    public void print5() {
        lock.lock();
        try {
            while(number != 1) {
                c1.await();
            }
            for (int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread().getName() + "\t" + i);
            }
            number = 2;
            c2.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void print10() {
        lock.lock();
        try {
            while(number != 2) {
                c2.await();
            }
            for (int i = 0; i < 10; i++) {
                System.out.println(Thread.currentThread().getName() + "\t" + i);
            }
            number = 3;
            c3.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void print15() {
        lock.lock();
        try {
            while(number != 3) {
                c3.await();
            }
            for (int i = 0; i < 15; i++) {
                System.out.println(Thread.currentThread().getName() + "\t" + i);
            }
            number = 1;
            c1.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
