import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.SuspendingMutex;

import bgu.spl.a2.sim.Warehouse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.Promise;

@RunWith(Parameterized.class)
public class WarehouseTest {
	public static final int NUM_OF_THREADS = 50;
	public CountDownLatch latch;
	//public Warehouse test_warehouse;
	public AtomicInteger num;
	public AtomicInteger sec_num;
	public ArrayList<Thread> thread_bank;
	
	@Before
	public void SetUp(){//*************Change the "add computer" method to whatever you wrote
						//to aquire a computer from the warehouse.
		//test_warehouse = new Warehouse();
		latch = new CountDownLatch(NUM_OF_THREADS);
		Warehouse.addComputer(new Computer("A", 11, 10));
		thread_bank = new ArrayList<>();
	}
	
	/*
	 * Having a few threads fighting over computers.
	 * How it's going to happen: each thread 
	 */
//	    @Rule
//	    public Timeout globalTimeout = Timeout.seconds(10); // 10 seconds max per method tested

	    @Parameterized.Parameters
	    public static List<Object[]> data() {
	        return Arrays.asList(new Object[100][0]);
	    }
	
	@Test
	public void testWarehouse() {
		for (int i = 0; i < NUM_OF_THREADS; i++){
			Thread t = new Thread(() -> multithread_catch_computers());
			thread_bank.add(t);
		}
		for (Thread tr: thread_bank){
			tr.start();
		}
		try {
			latch.await();
		} catch (InterruptedException e) {

		}
	}

	private void multithread_catch_computers() {
		boolean recieved_comp = false;
		SuspendingMutex comp_mutex = Warehouse.getSuspendingMutex("A");
		Promise<Computer> temp_comp_promise = comp_mutex.down();
		temp_comp_promise.subscribe(() -> {
			latch.countDown();
		});
		while (!recieved_comp){
			if (temp_comp_promise.isResolved()){
				recieved_comp = true;
				temp_comp_promise.get().checkAndSign(new ArrayList<String>(3), new HashMap<String, Integer>());
				comp_mutex.up();
			}
		}
	}
	


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
