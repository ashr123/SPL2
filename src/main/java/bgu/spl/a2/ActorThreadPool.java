package bgu.spl.a2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * represents an actor thread pool - to understand what this class does please
 * refer to your assignment.
 * <p>
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class ActorThreadPool
{
	private final Map<String, PrivateState> actors=new ConcurrentHashMap<>();
	private final Map<String, AtomicBoolean> actorIsNotBlocked=new ConcurrentHashMap<>();
	private final Map<String, ConcurrentLinkedQueue<Action<?>>> actorQueue=new ConcurrentHashMap<>();
	private final Thread[] threads;
	private final VersionMonitor versionMonitor=new VersionMonitor();
	private final Semaphore semaphoreForSubmit=new Semaphore(1, true);
	private final AtomicBoolean isStopped=new AtomicBoolean();

	/**
	 * creates a {@link ActorThreadPool} which has nThreads. Note, threads
	 * should not get started until calling to the {@link #start()} method.
	 * <p>
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 *
	 * @param nThreads the number of threads that should be started by this thread
	 *                 pool
	 */
	public ActorThreadPool(int nThreads)
	{
		threads=new Thread[nThreads];
		for (int i=0; i<nThreads; i++)
			threads[i]=new Thread(() -> {
				while (!isStopped.get())
				{
					int currVer=versionMonitor.getVersion();
					boolean flag=false;
					for (Map.Entry<String, ConcurrentLinkedQueue<Action<?>>> entry : actorQueue.entrySet())
						if (actorIsNotBlocked.get(entry.getKey()).compareAndSet(false, true))
						{
							Action<?> action=entry.getValue().poll();
							if (action!=null)
							{
								flag=true;
								action.handle(this, entry.getKey(), getPrivateState(entry.getKey()));
							}
							actorIsNotBlocked.get(entry.getKey()).set(false);
						}
					if (!flag)
						try
						{
							versionMonitor.await(currVer);
						}
						catch (InterruptedException ignored)
						{
						}
				}
			});
	}

	/**
	 * getter for actors
	 *
	 * @return actors
	 */
	public Map<String, PrivateState> getActors()
	{
		return actors;
	}

	/**
	 * getter for actor's private state
	 *
	 * @param actorId actor's id
	 * @return actor's private state
	 */
	public PrivateState getPrivateState(String actorId)
	{
		return getActors().get(actorId);
	}

	/**
	 * submits an action into an actor to be executed by a thread belongs to
	 * this thread pool
	 *
	 * @param action     the action to execute
	 * @param actorId    corresponding actor's id
	 * @param actorState actor's private state (actor's information)
	 */
	public void submit(Action<?> action, String actorId, PrivateState actorState)
	{
		if (!getActors().containsKey(actorId))
		{
			try
			{
				semaphoreForSubmit.acquire();
			}
			catch (InterruptedException ignored)
			{
			}
			if (!getActors().containsKey(actorId))
			{
				getActors().put(actorId, actorState);
				actorIsNotBlocked.put(actorId, new AtomicBoolean());
				actorQueue.put(actorId, new ConcurrentLinkedQueue<>());
			}
			semaphoreForSubmit.release();
		}
		actorQueue.get(actorId).add(action);
		versionMonitor.inc();
	}

	/**
	 * closes the thread pool - this method interrupts all the threads and waits
	 * for them to stop - it is returns *only* when there are no live threads in
	 * the queue.
	 * <p>
	 * after calling this method - one should not use the queue anymore.
	 *
	 * @throws InterruptedException if the thread that shut down the threads is interrupted
	 */
	public void shutdown() throws InterruptedException
	{
		isStopped.set(true);
		versionMonitor.inc();
		for (Thread thread : threads)
			if (thread!=Thread.currentThread())
				thread.join();
	}

	/**
	 * start the threads belongs to this thread pool
	 */
	public void start()
	{
		isStopped.set(false);
		for (Thread thread : threads)
			thread.start();
	}
}