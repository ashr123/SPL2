import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;

public class Main
{
	public static void main(String[] args)
	{
		ActorThreadPool pool=new ActorThreadPool(1);
		pool.submit(new Action<Integer>()
		{
			@Override
			protected void start()
			{
				System.out.println("Hi :) :)");
			}
		}, "Actor1", new PrivateState()
		{
		});
		pool.start();
	}
}