package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Behavior: This action should close a course. Should unregister all the registered students in the
 * course and remove the course from the department courses' list and from the grade sheets of the
 * students. The number of available spaces of the closed course should be updated to -1. DO NOT
 * remove its actor. After closing the course, all the request for registration should be denied.
 * <p>
 * Actor: Must be initially submitted to the department's actor.
 */
public class CloseACourse extends Action<Boolean>
{
	private final String course;

	public CloseACourse(String course)
	{
		setActionName("Close Course");
		this.course=course;
	}

	@Override
	protected void start()
	{
		if (actorState instanceof DepartmentPrivateState &&
		    ((DepartmentPrivateState)actorState).getCourseList().contains(course))
		{
			Action<Boolean> action=new Action<Boolean>()
			{
				@Override
				protected void start()
				{
					if (actorState instanceof CoursePrivateState)
					{
						Collection<Action<?>> actions=new LinkedList<>();
						((CoursePrivateState)actorState).setAvailableSpots(-1);
						for (String student : ((CoursePrivateState)actorState).getRegStudents())
						{
							Unregister unregister=new Unregister(student);
							actions.add(unregister);
							sendMessage(unregister, course, new CoursePrivateState());
						}
						then(actions, () -> {
							complete(true);
							synchronized (System.out)
							{
								System.out.println("Available spots of course: "+course+" has SUCCESSFULLY been changed to -1");
							}
						});
					}
					else
					{
						complete(false);
						synchronized (System.out)
						{
							System.out.println("Available spots of course: "+course+" has NOT been changed to -1");
						}
					}
				}
			};
			Collection<Action<?>> actions=new LinkedList<>();
			sendMessage(action, course, new CoursePrivateState());
			actions.add(action);
			then(actions, () -> {
				complete(action.getResult().get());
				System.out.println("Course: "+course+" has "+(action.getResult().get() ? "SUCCESSFULLY" : "NOT")+" been closed!!!");
			});
		}
		else
		{
			complete(false);
			System.out.println("Course: "+course+" has NOT been closed!!!");
		}
	}
}