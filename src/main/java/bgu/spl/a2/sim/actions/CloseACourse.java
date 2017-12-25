package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
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
	private String course;

	public CloseACourse(String course)
	{
		setActionName("Close Course");
		this.course=course;
	}

	@Override
	protected void start()
	{
		if (actorState instanceof DepartmentPrivateState &&
		    actorThreadPool.getPrivateState(course) instanceof CoursePrivateState &&
		    ((DepartmentPrivateState)actorState).getCourseList().contains(course))
		{
			Collection<Action<?>> actions=new LinkedList<>();
			Action<Boolean> action, finalAction;
			for (String student : ((CoursePrivateState)actorThreadPool.getPrivateState(course)).getRegStudents())
				{
					action=new Unregister(student, course);
					actions.add(action);
					sendMessage(action, course, new CoursePrivateState());
				}
				finalAction=new Action<Boolean>()
				{
					@Override
					protected void start()
					{
						if (actorState instanceof CoursePrivateState)
						{
							((CoursePrivateState)actorState).setAvailableSpots(-1);
							complete(true);
						}
						else
							complete(false);
						synchronized (System.out)
						{
							System.out.println("Available spots of course: "+course+" has "+(getResult().get() ? "SUCCESSFULLY" : "NOT")+" been changed to -1");
						}
					}
				};
				actions.add(finalAction);
				sendMessage(finalAction, course, new CoursePrivateState());
			then(actions, () -> {
				((DepartmentPrivateState)actorState).getCourseList().remove(course);
				synchronized (System.out)
				{
					System.out.println("Course: "+course+" has "+(finalAction.getResult().get() ?
					                                              "SUCCESSFULLY" : "NOT")+" been closed!!!");
				}
				complete(true);
			});
		}
	}
}