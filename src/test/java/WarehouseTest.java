import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.SuspendingMutex;
import bgu.spl.a2.sim.Warehouse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@RunWith(Parameterized.class)
public class WarehouseTest {
	private static final int NUM_OF_THREADS = 50;
	private CountDownLatch latch;
	private ArrayList<Thread> thread_bank;
	
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
	        return Arrays.asList(new Object[1000][0]);
	    }
	
	@Test
	public void testWarehouse() {
		for (int i = 0; i < NUM_OF_THREADS; i++){
			Thread t = new Thread(this::multithread_catch_computers);
			thread_bank.add(t);
		}
		for (Thread tr: thread_bank){
			tr.start();
		}
		try {
			latch.await();
		} catch (InterruptedException ignored) {

		}
	}

	private void multithread_catch_computers() {
		boolean recieved_comp = false;
		SuspendingMutex comp_mutex = Warehouse.getSuspendingMutex("A");
		Promise<Computer> temp_comp_promise = comp_mutex.down();
		temp_comp_promise.subscribe(() -> latch.countDown());
		while (!recieved_comp){
			if (temp_comp_promise.isResolved()){
				recieved_comp = true;
				temp_comp_promise.get().checkAndSign(new ArrayList<>(3), new HashMap<>());
				comp_mutex.up();
			}
		}
	}
	


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
