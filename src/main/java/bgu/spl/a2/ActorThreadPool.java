package bgu.spl.a2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
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
		//throw new UnsupportedOperationException("Not Implemented Yet.");
		threads=new Thread[nThreads];
		for (int i=0; i<nThreads; i++)
			threads[i]=new Thread(() -> {
				while (true)
				{
					boolean flag=false;
					for (Map.Entry<String, ConcurrentLinkedQueue<Action<?>>> entry : actorQueue.entrySet())
					{
						if (/*actorIsNotBlocked.get(entry.getKey()).get()*/actorIsNotBlocked.get(entry.getKey())
						                                                                    .compareAndSet(false, true))
						{
							Action<?> action=actorQueue.get(entry.getKey()).poll();
							if (action!=null)
							{
								flag=true;
								action.handle(this, entry.getKey(), getPrivateState(entry.getKey()));
							}
							actorIsNotBlocked.get(entry.getKey()).set(true);
							if (flag)
								versionMonitor.inc();
						}
					}
					if(!flag)
					{
						try
						{
							versionMonitor.await(versionMonitor.getVersion());
						}
						catch (InterruptedException ignored) { }
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
		//throw new UnsupportedOperationException("Not Implemented Yet.");
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
		//throw new UnsupportedOperationException("Not Implemented Yet.");
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
	public synchronized void submit(Action<?> action, String actorId, PrivateState actorState)
	{
		//throw new UnsupportedOperationException("Not Implemented Yet.");
		if (!getActors().containsKey(actorId))
		{
			getActors().put(actorId, actorState);
			actorIsNotBlocked.put(actorId, new AtomicBoolean(true));
			actorQueue.put(actorId, new ConcurrentLinkedQueue<>());
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
		// TODO: replace method body with real implementation
		throw new UnsupportedOperationException("Not Implemented Yet.");
	}

	/**
	 * start the threads belongs to this thread pool
	 */
	public void start()
	{
		// TODO: replace method body with real implementation
		throw new UnsupportedOperationException("Not Implemented Yet.");
	}
}