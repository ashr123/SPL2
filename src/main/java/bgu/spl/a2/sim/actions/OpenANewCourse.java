package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.Collection;

/**
 * Behavior: This action opens a new course in a specified department. The course has an initially
 * available spaces and a list of prerequisites.
 * <p>
 * Actor: Must be initially submitted to the Department's actor.
 */
public class OpenANewCourse extends Action<Boolean>
{
	private String course;
	private int space;
	private Collection<String> prerequisites;

	public OpenANewCourse(String course, int space, Collection<String> prerequisites)
	{
		setActionName("Open Course");
		this.course=course;
		this.space=space;
		this.prerequisites=prerequisites;
	}

	@Override
	protected void start()
	{
		if (actorState instanceof DepartmentPrivateState)
		{
			((DepartmentPrivateState)actorState).getCourseList().add(course);
			CoursePrivateState CPS=new CoursePrivateState();
			CPS.setAvailableSpots(space);
			CPS.getPrequisites().addAll(prerequisites);
			sendMessage(new Action<String>()
			{
				@Override
				protected void start()
				{
					complete("Course added!");
					synchronized (System.out)
					{
						System.out.println("Course "+course+" added!!!");
					}
				}
			}, course, CPS);
			complete(true);
		}
		else
			complete(false);
	}
}