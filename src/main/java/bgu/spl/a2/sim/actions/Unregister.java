package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Behavior: If the student is enrolled in the course, this action should unregister him (update the
 * list of students of course, remove the course from the grades sheet of the student and increases the
 * number of available spaces).
 * <p>
 * Actor: Must be initially submitted to the course's actor.
 */
public class Unregister extends Action<Boolean>
{
	private final String studentID;

	public Unregister(String studentID)
	{
		setActionName("Unregister");
		this.studentID=studentID;
	}

	@Override
	protected void start()
	{
		if (actorState instanceof CoursePrivateState &&
		    actorThreadPool.getPrivateState(studentID) instanceof StudentPrivateState)
		{
			Action<Boolean> action=new Action<Boolean>()
			{
				@Override
				protected void start()
				{
					if (actorState instanceof StudentPrivateState)
						complete(((StudentPrivateState)actorState).getGrades().remove(Unregister.this.actorID)!=null);
					else
						complete(false);
//					synchronized (System.out)
//					{
//						System.out.println(
//								"Student: "+studentID+" has "+(getResult().get() ? "SUCCESSFULLY" : "NOT")+" been removed "+Unregister.this.actorID+" from it's grades sheet");
//					}
				}
			};
			Collection<Action<?>> actions=new LinkedList<>();
			actions.add(action);
			sendMessage(action, studentID, new StudentPrivateState());
			then(actions, () -> {
				if (action.getResult().isResolved() && action.getResult().get())
				{
					((CoursePrivateState)actorState).setRegistered(((CoursePrivateState)actorState)
							                                               .getRegistered()-1);
					complete(((CoursePrivateState)actorState).getRegStudents().remove(studentID));
				}
				else
					complete(false);
//				synchronized (System.out)
//				{
//					System.out.println("Student: "+studentID+" has "+(getResult().get() ? "SUCCESSFULLY" : "NOT")+" been removed from course "+actorID+"!!!");
//				}
			});
		}
		else
		{
			complete(false);
//			synchronized (System.out)
//			{
//				System.out.println("Student: "+studentID+" has "+(getResult()
//						                                                  .get() ? "SUCCESSFULLY" : "NOT")+" been removed from course "+actorID+"!!!");
//			}
		}
	}
}