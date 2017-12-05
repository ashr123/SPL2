package test.java;

import bgu.spl.a2.VersionMonitor;
import org.junit.Assert;
import org.junit.Test;

public class VersionMonitorTest
{
	@Test
	public void getVersion()
	{
		VersionMonitor versionMonitor=new VersionMonitor();
		Assert.assertEquals(0, versionMonitor.getVersion());
		versionMonitor.inc();
		Assert.assertEquals(1, versionMonitor.getVersion());
	}

	@Test
	public void inc()
	{
		VersionMonitor versionMonitor=new VersionMonitor();
		int version=versionMonitor.getVersion();
		versionMonitor.inc();
		Assert.assertEquals(version+1, versionMonitor.getVersion());
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
		Assert.assertEquals(Thread.State.WAITING, thread.getState());
		versionMonitor.inc();
		Assert.assertNotEquals(Thread.State.WAITING, thread.getState());
	}
}