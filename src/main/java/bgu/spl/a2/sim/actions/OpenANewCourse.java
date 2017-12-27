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
	private final String course;
	private final int space;
	private final Collection<String> prerequisites;

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
			sendMessage(new Action<Boolean>()
			{
				@Override
				protected void start()
				{
					complete(true);
					synchronized (System.out)
					{
						System.out.println("Course "+course+" has SUCCESSFULLY been added!!!");
					}
				}
			}, course, CPS);
			complete(true);
		}
		else
		{
			complete(false);
			synchronized (System.out)
			{
				System.out.println("Course "+course+" has NOT been added!!!");
			}
		}
	}
}