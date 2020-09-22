package concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author long
 * @date 2020/9/22
 * <p>
 * 案例：阻塞队列版生产者消费者
 */
public class ProducerConsumerBlockingQueue {
    public static void main(String[] args) throws InterruptedException {
        MyData myData = new MyData(new ArrayBlockingQueue<>(10));
        new Thread(() -> {
            try {
                myData.produce();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                myData.consume();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        TimeUnit.SECONDS.sleep(5);
        myData.stop();
    }
}

class MyData {
    private volatile boolean flag = true;
    private BlockingQueue<Integer> blockingQueue = null;
    AtomicInteger atomicInteger = new AtomicInteger();

    public MyData(BlockingQueue<Integer> blockingQueue) {
        this.blockingQueue = blockingQueue;
        System.out.println("队列类型：" + blockingQueue.getClass().getName());
    }

    public void produce() throws InterruptedException {
        int data;
        while (flag) {
            data = atomicInteger.getAndIncrement();
            boolean offer = blockingQueue.offer(data, 2, TimeUnit.SECONDS);
            if (offer) {
                TimeUnit.SECONDS.sleep(1);
                System.out.println(Thread.currentThread().getName() + "插入队列成功：" + data);
            } else {
                System.out.println(Thread.currentThread().getName() + "插入队列失败：" + data);
            }
        }
        System.out.println("produce stop...");
    }

    public void consume() throws InterruptedException {
        Integer result;
        while (flag) {
            result = blockingQueue.poll(2, TimeUnit.SECONDS);
            if (result == null) {
                System.out.println(Thread.currentThread().getName() + "没有数据");
                flag = false;
            } else {
                System.out.println(Thread.currentThread().getName() + "获取到数据" + result);
            }
        }
        System.out.println("consume stop...");
    }

    public void stop() {
        flag = false;
    }
}
