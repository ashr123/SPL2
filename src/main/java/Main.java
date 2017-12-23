import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;

import java.util.ArrayList;
import java.util.List;

public class Main
{
	public static void main(String[] args) throws InterruptedException
	{
		ActorThreadPool pool=new ActorThreadPool(1000);
		Action action1=new Action<Integer>()
		{
			@Override
			protected void start()
			{
				System.out.println("Hi I am action 1 :)");

				Action<Integer> action2=new Action<Integer>()
				{
					@Override
					protected void start()
					{
						System.out.println("Hi I am action 1.2");
						setActionName("action 2");
						complete(2);
					}
				};

				List<Action<Integer>> actions=new ArrayList<>();
				sendMessage(action2, "actor 2", new PrivateState()
				{
				});
				actions.add(action2);
				then(actions, () -> {
					System.out.println("Hi I am action 1 Again:))))))");
					complete(1);
					try
					{
						pool.shutdown();
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				});

			}
		};
		action1.setActionName("action 1");
		pool.submit(action1, "Actor1", new PrivateState()
		{
		});
		pool.start();
	}
}