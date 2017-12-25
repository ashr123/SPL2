package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

/**
 * Behavior: If the student is enrolled in the course, this action should unregister him (update the
 * list of students of course, remove the course from the grades sheet of the student and increases the
 * number of available spaces).
 * <p>
 * Actor: Must be initially submitted to the course's actor.
 */
public class Unregister extends Action<Boolean>
{
	private String studentID, course;

	public Unregister(String studentID, String course)
	{
		setActionName("Unregister");
		this.studentID=studentID;
		this.course=course;
	}

	@Override
	protected void start()
	{
		if (actorState instanceof CoursePrivateState &&
		    actorThreadPool.getPrivateState(studentID) instanceof StudentPrivateState &&
		    (((CoursePrivateState)actorState).getRegStudents().contains(studentID)))
		{
			if (((StudentPrivateState)actorThreadPool.getPrivateState(studentID)).getGrades().remove(course)!=null)
			{
				((CoursePrivateState)actorState).setRegistered(((CoursePrivateState)actorState).getRegistered()-1);
				((CoursePrivateState)actorState).getRegStudents().remove(studentID);
			}
			synchronized (System.out)
			{
				System.out.println("Student: "+studentID+" has been removed from course "+course+"!!!");
			}
		}
	}
}