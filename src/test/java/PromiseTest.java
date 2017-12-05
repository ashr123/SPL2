package test.java;

import bgu.spl.a2.Promise;
import org.junit.Assert;
import org.junit.Test;

public class PromiseTest
{
	@Test
	public void get()
	{
		Promise<Integer> p=new Promise<>();
		try
		{
			p.get();
			Assert.fail();
		}
		catch (IllegalStateException e)
		{
			try
			{
				p.resolve(5);
				Assert.assertEquals((int)p.get(), 5);
			}
			catch (IllegalStateException e1)
			{
				Assert.fail();
			}
		}
	}

	@Test
	public void isResolved()
	{
		Promise<Integer> promise = new Promise<>();
		Assert.assertFalse(promise.isResolved());
		promise.resolve(6);
		Assert.assertTrue(promise.isResolved());
	}

	@Test
	public void resolve()
	{
		try
		{
			Promise<Integer> promise=new Promise<>();
			for (int i=0; i<10; i++)
				promise.subscribe(() -> {});
			Assert.assertEquals(10, promise.getNumOfSubscribers());
			promise.resolve(5);
			try
			{
				promise.resolve(6);
				Assert.fail();
			}
			catch (IllegalStateException e)
			{
				Assert.assertEquals(5, (int)promise.get());
				Assert.assertEquals(0, promise.getNumOfSubscribers());
			}
			catch (Exception e)
			{
				Assert.fail();
			}
		}
		catch (Exception e)
		{
			Assert.fail();
		}
	}

	@Test
	public void subscribe()
	{
		Promise<Integer> promise=new Promise<>();
		for (int i=0; i<10; i++)
			promise.subscribe(() -> {});
		Assert.assertEquals(10, promise.getNumOfSubscribers());
		promise.resolve(5);
		Assert.assertEquals(0, promise.getNumOfSubscribers());
		promise.subscribe(() -> {});
		Assert.assertEquals(0, promise.getNumOfSubscribers());
	}
}