package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Behavior: From this moment, reject any further changes in registration. And, close courses with
 * number of students less than 5.
 * <p>
 * Actor: Must be initially submitted to the department's actor.
 */
public class AnnounceAboutTheEndOfRegistrationPeriod extends Action<Boolean>
{
	public AnnounceAboutTheEndOfRegistrationPeriod()
	{
		setActionName("End Registeration");
	}

	@Override
	protected void start()
	{
		Collection<Action<?>> actions=new LinkedList<>();
		if (actorState instanceof DepartmentPrivateState)
		{
			for (String course : ((DepartmentPrivateState)actorState).getCourseList())
			{
				Action<Boolean> action=new Action<Boolean>()
				{
					@Override
					protected void start()
					{
						if (((CoursePrivateState)actorState).getRegistered()<5)
						{
							((CoursePrivateState)actorState).setAvailableSpots(-1);
							Collection<CloseACourse> a=new LinkedList<>();
							CloseACourse closeACourse=new CloseACourse(course);
							a.add(closeACourse);
							sendMessage(closeACourse, course, new CoursePrivateState());
							then(a, () -> complete(true));
						}
						else
						{
							((CoursePrivateState)actorState).setAvailableSpots(-1);
							complete(true);
						}
					}
				};
				sendMessage(action, course, new CoursePrivateState());
				actions.add(action);
			}
				then(actions,()->
				{
					synchronized (System.out)
					{
						System.out.println("End Of Registration");
					}
					complete(true);
				});
		}
	}
}