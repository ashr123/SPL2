package test.java;

import bgu.spl.a2.VersionMonitor;
import org.junit.Test;

import static org.junit.Assert.*;

public class VersionMonitorTest
{
	@Test
	public void getVersion()
	{
		VersionMonitor versionMonitor=new VersionMonitor();
		assertEquals(0, versionMonitor.getVersion());
		versionMonitor.inc();
		assertEquals(1, versionMonitor.getVersion());
	}

	@Test
	public void inc()
	{
		VersionMonitor versionMonitor=new VersionMonitor();
		int version=versionMonitor.getVersion();
		versionMonitor.inc();
		assertEquals(version+1, versionMonitor.getVersion());
	}

	@Test
	public void await()
	{
		VersionMonitor versionMonitor=new VersionMonitor();
		Thread thread=new Thread(() ->
		                         {
			                         try
			                         {
				                         versionMonitor.await(0);
			                         }
			                         catch (InterruptedException e)
			                         {
			                         }
		                         });
		assertEquals(Thread.State.WAITING, thread.getState());
		versionMonitor.inc();
		assertNotEquals(Thread.State.WAITING, thread.getState());
	}
}