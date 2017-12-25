package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

/**
 * Behavior: This action adds a new student to a specified department.
 * <p>
 * Actor: Must be initially submitted to the Department's actor.
 */
public class AddStudent extends Action<Boolean>
{
	private String studentID;

	public AddStudent(String studentID)
	{
		setActionName("Add Student");
		this.studentID=studentID;
	}

	@Override
	protected void start()
	{
		if (actorState instanceof DepartmentPrivateState)
			((DepartmentPrivateState)actorState).getStudentList().add(studentID);
		sendMessage(new Action<String>()
		{
			@Override
			protected void start()
			{
				complete("Student added!");
				synchronized (System.out)
				{
					System.out.println("Student "+studentID+" added!!!");
				}
			}
		}, studentID, new StudentPrivateState());
		complete(true);
	}
}