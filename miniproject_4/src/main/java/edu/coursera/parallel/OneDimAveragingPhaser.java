package edu.coursera.parallel;

import java.util.concurrent.Phaser;
import java.io.*;

/**
 * Wrapper class for implementing one-dimensional iterative averaging using
 * phasers.
 */
public final class OneDimAveragingPhaser {
    /**
     * Default constructor.
     */
    private OneDimAveragingPhaser() {
    }

    /**
     * Sequential implementation of one-dimensional iterative averaging.
     *
     * @param iterations The number of iterations to run
     * @param myNew A double array that starts as the output array
     * @param myVal A double array that contains the initial input to the
     *        iterative averaging problem
     * @param n The size of this problem
     */
    public static void runSequential(final int iterations, final double[] myNew,
            final double[] myVal, final int n) {
        double[] next = myNew;
        double[] curr = myVal;

        for (int iter = 0; iter < iterations; iter++) {
            for (int j = 1; j <= n; j++) {
                next[j] = (curr[j - 1] + curr[j + 1]) / 2.0;
            }
            double[] tmp = curr;
            curr = next;
            next = tmp;
        }
    }

    /**
     * An example parallel implementation of one-dimensional iterative averaging
     * that uses phasers as a simple barrier (arriveAndAwaitAdvance).
     *
     * @param iterations The number of iterations to run
     * @param myNew A double array that starts as the output array
     * @param myVal A double array that contains the initial input to the
     *        iterative averaging problem
     * @param n The size of this problem
     * @param tasks The number of threads/tasks to use to compute the solution
     */
    public static void runParallelBarrier(final int iterations,
            final double[] myNew, final double[] myVal, final int n,
            final int tasks) {
        Phaser ph = new Phaser(0);
        ph.bulkRegister(tasks);
//        System.err.printf("array len %d, n val : %d\n", myNew.length, n);

        Thread[] threads = new Thread[tasks];

        for (int ii = 0; ii < tasks; ii++) {
            final int i = ii;
            threads[ii] = new Thread(() -> {
                double[] threadPrivateMyVal = myVal;
                double[] threadPrivateMyNew = myNew;

                final int chunkSize = (n + tasks - 1) / tasks;
                final int left = (i * chunkSize) + 1;
                int right = (left + chunkSize) - 1;
                if (right > n) right = n;

                for (int iter = 0; iter < iterations; iter++) {
                    for (int j = left; j <= right; j++) {
                        threadPrivateMyNew[j] = (threadPrivateMyVal[j - 1]
                            + threadPrivateMyVal[j + 1]) / 2.0;
                    }
                    ph.arriveAndAwaitAdvance();

                    double[] temp = threadPrivateMyNew;
                    threadPrivateMyNew = threadPrivateMyVal;
                    threadPrivateMyVal = temp;
                }
            });
            threads[ii].start();
        }

        for (int ii = 0; ii < tasks; ii++) {
            try {
                threads[ii].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A parallel implementation of one-dimensional iterative averaging that
     * uses the Phaser.arrive and Phaser.awaitAdvance APIs to overlap
     * computation with barrier completion.
     *
     * TODO Complete this method based on the provided runSequential and
     * runParallelBarrier methods.
     *
     * @param iterations The number of iterations to run
     * @param myNew A double array that starts as the output array
     * @param myVal A double array that contains the initial input to the
     *              iterative averaging problem
     * @param n The size of this problem
     * @param tasks The number of threads/tasks to use to compute the solution
     */
    public static void runParallelFuzzyBarrier(final int iterations,
            final double[] myNew, final double[] myVal, final int n,
            final int tasks) {
        Phaser [] phs = new Phaser[tasks];
        for (int i = 0; i < tasks; i++) phs[i] = new Phaser(1);
        Thread [] thr = new Thread[tasks];
        final int chunk_size = (n + tasks - 1) / tasks;
        for (int ii = 0; ii < tasks; ii++) {
            final int i = ii;
            thr[i] = new Thread(() -> {
                double [] my_val_t = myVal;
                double [] my_new_t = myNew;
                final int s_idx = chunk_size * i + 1;
                final int e_idx = Math.min(s_idx + chunk_size - 1, n);
                for (int iter_cnt = 0; iter_cnt < iterations; iter_cnt++) {
                    my_new_t[s_idx] = (my_val_t[s_idx - 1] + my_val_t[s_idx + 1]) / 2.0;
                    my_new_t[e_idx] = (my_val_t[e_idx - 1] + my_val_t[e_idx + 1]) / 2.0;
                    phs[i].arrive();
                    for (int idx = s_idx + 1; idx <= e_idx - 1; idx++) {
                        my_new_t[idx] = (my_val_t[idx - 1] + my_val_t[idx + 1]) / 2.0;
                    }
                    if (i > 0) phs[i - 1].awaitAdvance(iter_cnt);
                    if (i < tasks - 1) phs[i + 1].awaitAdvance(iter_cnt);
                    //perform swapping
                    double [] temp_val = my_val_t;
                    my_val_t = my_new_t;
                    my_new_t = temp_val;
                }

            });
            thr[i].start();
        }
        for (int ii = 0; ii < tasks; ii++) {
            try {
                thr[ii].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
