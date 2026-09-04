public class Main {

    public static void main(String[] args) throws InterruptedException 
    {
        
        int[] numberOfThreads = {2, 4, 8, 16}; /*Change*/
        int numberOfRuns = 3;
        int iterations = 200;

        System.out.println("Threads | TTAS (ms) | MCS (ms) | CLH (ms)");
        System.out.println();

        for (int i = 0; i < numberOfThreads.length; i++) {
            int threadCount = numberOfThreads[i];
            long ttasTotalTime = 0;
            long mcsTotalTime = 0;
            long clhTotalTime = 0;
            System.out.println("\n----- " + threadCount + " THREADS -----");

            for (int j = 0; j < numberOfRuns; j++) {
                Auction auction =new Auction(AuctionUtils.generateItemName());
                TTAS ttas = new TTAS();
                Runner runner = new Runner(threadCount,iterations,auction,ttas);
                ttasTotalTime += runner.run();
            }

            for (int j = 0; j < numberOfRuns; j++) {
                Auction auction =new Auction(AuctionUtils.generateItemName());
                MCS mcs = new MCS();
                Runner runner = new Runner(threadCount,iterations,auction,mcs);
                mcsTotalTime += runner.run();
            }

            for (int j = 0; j < numberOfRuns; j++) {
                Auction auction =new Auction(AuctionUtils.generateItemName());
                CLH clh = new CLH();
                Runner runner = new Runner(threadCount,iterations,auction,clh);
                clhTotalTime += runner.run();
            }

            double ttasAve = (double) ttasTotalTime / numberOfRuns / 1000000;
            double mcsAve = (double) mcsTotalTime / numberOfRuns / 1000000;
            double clhAve = (double) clhTotalTime / numberOfRuns / 1000000;

            System.out.printf("%-7d | %-10.3f | %-8.3f | %-8.3f%n", threadCount,
                ttasAve, mcsAve, clhAve
            );
        }
    }
}