package bgu.spl.a2.sim;

import bgu.spl.a2.Promise;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * this class is related to {@link Computer}
 * it indicates if a computer is free or not
 * <p>
 * Note: this class can be implemented without any synchronization.
 * However, using synchronization will be accepted as long as the implementation is blocking free.
 */
public class SuspendingMutex
{
	private final AtomicBoolean isBlocked=new AtomicBoolean();
	private final Queue<Promise<Computer>> promisesQueue=new ConcurrentLinkedQueue<>();
	private final Computer computer;

	/**
	 * Constructor
	 *
	 * @param computer
	 */
	public SuspendingMutex(Computer computer)
	{
		this.computer=computer;
	}

	/**
	 * Computer acquisition procedure
	 * Note that this procedure is non-blocking and should return immediately
	 *
	 * @return a promise for the requested computer
	 */
	public Promise<Computer> down()
	{
		Promise<Computer> promise=new Promise<>();
		if (isBlocked.compareAndSet(false, true))
			promise.resolve(computer);
		else
			promisesQueue.add(promise);
		return promise;
	}

	/**
	 * Computer return procedure
	 * releases a computer which becomes available in the warehouse upon completion
	 */
	public void up()
	{
		if (promisesQueue.isEmpty())
			isBlocked.set(false);
		else
			synchronized (this)
			{
				if (promisesQueue.isEmpty())
					isBlocked.set(false);
				else
					promisesQueue.poll().resolve(computer);
			}
	}
}