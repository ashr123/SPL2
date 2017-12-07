//package test.java;

import bgu.spl.a2.Promise;
import org.junit.Test;

import static org.junit.Assert.*;

public class PromiseTest
{
	@Test
	public void get()
	{
		Promise<Integer> p=new Promise<>();
		try
		{
			p.get();
			fail();
		}
		catch (IllegalStateException e)
		{
			try
			{
				p.resolve(5);
				assertEquals((int)p.get(), 5);
			}
			catch (IllegalStateException e1)
			{
				fail();
			}
		}
	}

	@Test
	public void isResolved()
	{
		Promise<Integer> promise=new Promise<>();
		assertFalse(promise.isResolved());
		try
		{
			promise.resolve(6);
		}
		catch (IllegalStateException e)
		{
			fail();
		}
		assertTrue(promise.isResolved());
	}

	@Test
	public void resolve()
	{
		try
		{
			Promise<Integer> promise=new Promise<>();
			for (int i=0; i<10; i++)
				promise.subscribe(() -> {
				});
			assertEquals(10, promise.getNumOfSubscribers());
			promise.resolve(5);
			try
			{
				promise.resolve(6);
				fail();
			}
			catch (IllegalStateException e)
			{
				assertEquals(5, (int)promise.get());
				assertEquals(0, promise.getNumOfSubscribers());
			}
			catch (Exception e)
			{
				fail();
			}
		}
		catch (Exception e)
		{
			fail();
		}
	}

	@Test
	public void subscribe()
	{
		Promise<Integer> promise=new Promise<>();
		for (int i=0; i<10; i++)
			promise.subscribe(() -> {
			});
		assertEquals(10, promise.getNumOfSubscribers());
		try
		{
			promise.resolve(5);
		}
		catch (IllegalStateException e)
		{
			fail();
		}
		assertEquals(0, promise.getNumOfSubscribers());
		promise.subscribe(() -> {
		});
		assertEquals(0, promise.getNumOfSubscribers());
	}
}