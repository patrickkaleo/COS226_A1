
import java.util.concurrent.atomic.AtomicReference;

public class CLH implements Lock {
    private class QNode {
        public boolean locked=false;
    }


    AtomicReference<QNode> tail;
    ThreadLocal<QNode> myNode;
    ThreadLocal<QNode> myPred;

    public CLH() {
        this.tail = new AtomicReference<>(null);
        this.myNode = new ThreadLocal<QNode>() {
            protected QNode initialValue() {
                return new QNode();
            }
        };

        this.myPred = new ThreadLocal<QNode>() {
            protected QNode initialValue() {
                return null;
            }
        };
    }

    @Override
    public void lock() {
        QNode qnode = myNode.get();
        qnode.locked=true;
        QNode pred = this.tail.getAndSet(qnode);
        while(pred.locked) {/*WAIT FOR THE PRED TO BE FREE */}
    }

    @Override
    public void unlock() {
        QNode qnode = myNode.get();
        qnode.locked = false;
        myNode.set(myPred.get());
    }
}