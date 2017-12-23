import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;

import java.util.ArrayList;
import java.util.List;

public class Main
{
	public static void main(String[] args) throws InterruptedException
	{
		ActorThreadPool pool=new ActorThreadPool(1);
		pool.submit(new Action<Integer>()
		{
			@Override
			protected void start()
			{
				System.out.println("Hi I am action 1 :)");
				List<Action<Void>> actions=new ArrayList<>();
				Action action=new Action()
				{
					@Override
					protected void start()
					{
						System.out.println("Hi I am action 1.2");
					}
				};
				actions.add(action);
				sendMessage(action,"actor 2", new PrivateState()
				{
				});
				then(actions,()->{
					System.out.println("Hi I am action 1 Again:))))))");
					complete(2);
				});

			}
		}, "Actor1", new PrivateState()
		{
		});
		pool.start();
	}
}