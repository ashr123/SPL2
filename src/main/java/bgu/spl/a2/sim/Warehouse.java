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
	Map<Computer, SuspendingMutex> mutexMap=new ConcurrentHashMap<>();
}