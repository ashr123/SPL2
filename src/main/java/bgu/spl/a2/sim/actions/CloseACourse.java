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
	private String course;

	public CloseACourse(String course)
	{
		setActionName("Close Course");
		this.course=course;
	}

	@Override
	protected void start()
	{
		Collection<Unregister> actions=new LinkedList<>();
		if (actorState instanceof DepartmentPrivateState &&
		    actorThreadPool.getPrivateState(course) instanceof CoursePrivateState &&
		    ((DepartmentPrivateState)actorState).getCourseList().contains(course))
		{
			for (String student : ((CoursePrivateState)actorThreadPool.getPrivateState(course)).getRegStudents())
				{
					Unregister action=new Unregister(student, course);
					actions.add(action);
					sendMessage(action, course, new CoursePrivateState());
				}
			then(actions, () -> {
				((DepartmentPrivateState)actorState).getCourseList().remove(course);
				synchronized (System.out)
				{
					System.out.println("Course: "+course+" has been closed!!!");
				}
				complete(false);
			});
		}
	}
}