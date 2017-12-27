package bgu.spl.a2.sim;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * represents a warehouse that holds a finite amount of computers
 * and their suspended mutexes.
 * releasing and acquiring should be blocking free.
 */
public class Warehouse
{
	private static final Map<String, SuspendingMutex> mutexMap=new ConcurrentHashMap<>();

	public static void addComputer(String type, long sigSuccess, long sigFail)
	{
		mutexMap.put(type, new SuspendingMutex(new Computer(type, sigSuccess, sigFail)));
	}

	public static SuspendingMutex getSuspendingMutex(String type)
	{
		return mutexMap.get(type);
	}
}