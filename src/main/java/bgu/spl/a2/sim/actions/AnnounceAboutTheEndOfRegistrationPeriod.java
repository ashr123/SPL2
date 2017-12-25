package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
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
		Collection<CloseACourse> actions=new LinkedList<>();
		if (actorState instanceof DepartmentPrivateState)
		{
			for (String course : ((DepartmentPrivateState)actorState).getCourseList())
				if (((CoursePrivateState)actorThreadPool.getPrivateState(course)).getRegistered()<5)
				{
					CloseACourse action=new CloseACourse(course);
					actions.add(action);
					sendMessage(action, course, new CoursePrivateState());
				}
				else
					((CoursePrivateState)actorThreadPool.getPrivateState(course)).setEndOfRegistration(true);
			then(actions, () -> {
				synchronized (System.out)
				{
					System.out.println("All Courses Gets Announce About End Of Registration");
				}
				complete(false);
			});
		}
	}
}