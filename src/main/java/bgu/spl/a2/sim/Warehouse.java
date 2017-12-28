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

	public static void addComputer(Computer computer)
	{
		mutexMap.put(computer.getComputerType(), new SuspendingMutex(computer));
	}

	public static SuspendingMutex getSuspendingMutex(String type)
	{
		return mutexMap.get(type);
	}
}