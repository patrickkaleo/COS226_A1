
import java.util.concurrent.atomic.AtomicReference;

public class MCS implements Lock {

    //QNode class
    private class QNode {
        QNode next=null;
        boolean locked=false;
    }

    public MCS() {
        this.tail=new AtomicReference<QNode>(null);
        this.myNode = new ThreadLocal<QNode>() {
            protected QNode initialValue() {
                return new QNode();
            }
        };
    }

    AtomicReference<QNode> tail;
    ThreadLocal<QNode> myNode;

    @Override
    public void lock() {
        QNode current=this.myNode.get(); //node of the thread trying to lock
        QNode pred=this.tail.getAndSet(current); // set the tail to the current node, and return the previous tail value

        //was there a node already before me??
        if(pred!=null) {
            //true? then i become that threads next
            pred.next=current;
            current.locked=true; //lock my node and spin till the current node give me the lock
            while(current.locked) {}
        }
    }

    @Override
    public void unlock() {
        QNode current=this.myNode.get();
        if(current.next==null) {
            if(this.tail.compareAndSet(current, null))
                return; //makes the queue empty meaning this node was the only one
            while(current.next==null) {} //spin till you can hand off the lock
        }

        //hand off the lock to the next thread
        current.next.locked=false;
        current.next = null;
    }
}