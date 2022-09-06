package sizhe.chen.currency;

/**
 * 1. 存在一个对象，他有一个int类型的属性counter，该成员变量的初始值是0/
 * 2. 创建两个线程，其中一个线程对counter +1 ，另一个线程对counter -1，
 * 3. 输出该对象的变量的counter的值
 * 4. 最终的输出结果应该为101010101
 */
public class WaitNotifyDemo {

    private int counter;

    private Object lock = new Object();

    public static void main(String[] args) {
        WaitNotifyDemo demo = new WaitNotifyDemo();
        new Thread(new AddExecutor(demo)).start();
        new Thread(new AddExecutor(demo)).start();
        new Thread(new AddExecutor(demo)).start();
        new Thread(new AddExecutor(demo)).start();
        new Thread(new AddExecutor(demo)).start();
        new Thread(new SubExecutor(demo)).start();
        new Thread(new SubExecutor(demo)).start();
        new Thread(new SubExecutor(demo)).start();
        new Thread(new SubExecutor(demo)).start();
        new Thread(new SubExecutor(demo)).start();

    }

    public void add() throws InterruptedException {
        synchronized (lock) {
            while (counter == 1) {
                lock.wait();
            }
            counter++;
            System.out.print(counter);
            lock.notifyAll();
        }
    }

    public void sub() throws InterruptedException {
        synchronized (lock) {
            while (counter == 0) {
                lock.wait();
            }
            counter--;
            System.out.print(counter);
            lock.notifyAll();
        }
    }


}

class AddExecutor implements Runnable {

    final private WaitNotifyDemo demo;

    AddExecutor(WaitNotifyDemo demo) {
        this.demo = demo;
    }


    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                demo.add();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class SubExecutor implements Runnable {

    final private WaitNotifyDemo demo;

    SubExecutor(WaitNotifyDemo demo) {
        this.demo = demo;
    }


    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                demo.sub();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
