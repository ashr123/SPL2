package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

/**
 * Behavior: This action should increase the number of available spaces for the course
 * <p>
 * Actor: Must be initially submitted to the course's actor.
 */
public class OpenNewPlacesInACourse extends Action<Boolean>
{
	private int number;

	public OpenNewPlacesInACourse(int number)
	{
		setActionName("Add Spaces");
		this.number=number;
	}

	@Override
	protected void start()
	{
		if (actorState instanceof CoursePrivateState)
			((CoursePrivateState)actorState)
					.setAvailableSpots(((CoursePrivateState)actorState).getAvailableSpots()+number);
		synchronized (System.out)
		{
			System.out.println(number+" spaces has been added to course "+ actorID);
		}
		complete(true);
	}
}