import java.util.concurrent.atomic.AtomicLong;
/*Optional Helper Runner Class*/
public class Runner 
{

    public final int numberOfThreads;
    public final int iterations;
    public final Auction auction;
    public final Lock lock;
    //extra variable
    private final int[] bidsWon;

    private final AtomicLong totalWaitingTime = new AtomicLong(0);

    public Runner(int numberOfThreads,int iterations,Auction auction,Lock lock) 
    {
        this.numberOfThreads = numberOfThreads;
        this.iterations = iterations;
        this.auction = auction;
        this.lock = lock;
        //initialising extra variable
        this.bidsWon = new int[numberOfThreads];
    }

    public void run() throws InterruptedException 
    {
        Thread[] threads = new Thread[numberOfThreads];

        for(int i = 0; i < numberOfThreads; i++) 
        {
            final int bidderId = i;

            threads[i] = new Thread(() -> {
                bidder(bidderId);
            });
        }

        long startTime = System.nanoTime();

        for(Thread thread : threads) 
        {
            thread.start();
        }

        for(Thread thread : threads) 
        {
            thread.join();
        }

        long endTime = System.nanoTime();

        reportResults(endTime - startTime);
    }

    /*Defines the behaviour of an individual bidder. Note you have to decide how to incorporate your lock.*/
    public void bidder(int bidderId) 
    {
       for(int i=0; i<iterations; i++){
        //record the time right before trying to grab the marker
        long waitStart =System.nanoTime();

        lock.lock();
        //add the time we spent waiting in line to get our custom metric

        long waitEnd = System.nanoTime();
        totalWaitingTime.addAndGet(waitEnd - waitStart);

        try{
            double currentBid = auction.getHighestBid();
            double newBid = currentBid + 10.0;
            auction.placeBid(bidderId, newBid);

            //track that this specific bidder successfully placed a bid
            bidsWon[bidderId]++;
        } finally{
            lock.unlock();
        }
       }
    }

    /*Optional Helper: Records and reports the results of the experiment.*/
    public void reportResults(long executionTime) 
    {
        System.out.println("Experiment Results");

        //converting ns to ms for readability
        System.out.println("Total Execution Time: " + (executionTime/1_000_000.0) + "ms");
        System.out.println("Final Highest Bid: " + auction.getHighestBid());

        int totalBids=0;
        for(int i=0; i<numberOfThreads; i++){
            System.out.println("Bidder " + i + " won " + bidsWon[i] + " bids.");
            totalBids += bidsWon[i];
        }

        System.out.println("Total Bids Placed: " + totalBids);

        //extra metric tracked
        System.out.println("Total Waiting Time across all threads: " + (totalWaitingTime.get() / 1_000_000.0) + " ms");
    }
}