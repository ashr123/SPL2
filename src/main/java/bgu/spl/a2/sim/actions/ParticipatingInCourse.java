package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Behavior: This action should try to register the student in the course, if it succeeds, should add the
 * course to the grades sheet of the student, and give him a grade if supplied.
 * <p>
 * Actor: Must be initially submitted to the course's actor.
 */
public class ParticipatingInCourse extends Action<Boolean>
{
	private String student;
	private int grade;

	public ParticipatingInCourse(String student, int grade)
	{
		setActionName("Participate In Course");
		this.student=student;
		this.grade=grade;
	}

	@Override
	protected void start()
	{
		if (actorState instanceof CoursePrivateState &&
		    actorThreadPool.getPrivateState(student) instanceof StudentPrivateState)
		{
			if (((CoursePrivateState)actorState).getAvailableSpots()!= null && ((CoursePrivateState)actorState).getAvailableSpots()!=-1)
			{
				Boolean canRegister=true;
				for (String course : ((CoursePrivateState)actorState).getPrequisites())
				{
					if (((StudentPrivateState)actorThreadPool.getPrivateState(student))
							    .getGrades().get(course)!=null &&
					    ((StudentPrivateState)actorThreadPool.getPrivateState(student))
							    .getGrades().get(course)<56)
						canRegister=false;
				}
				if (canRegister)
				{
					Collection<Action<?>> actions=new LinkedList<>();
					int spaces=((CoursePrivateState)actorState).getAvailableSpots();
					((CoursePrivateState)actorState).setAvailableSpots(spaces-1);
					Action<Boolean> action=new Action<Boolean>()
					{
						@Override
						protected void start()
						{
							if (actorState instanceof StudentPrivateState)
							{
								if (((StudentPrivateState)actorState).getGrades()
								                                     .containsKey(ParticipatingInCourse.this.actorID))
									complete(false);
								else
								{
									((StudentPrivateState)actorState).getGrades()
									                                 .put(ParticipatingInCourse.this.actorID, grade);
									complete(true);
								}
							}
							else
								complete(false);
							synchronized (System.out)
							{
								System.out.println("Course "+ParticipatingInCourse.this.actorID+" has "+(getResult()
										                                                                         .get() ? "SUCCESSFULLY" : "NOT")+" been added to student "+actorID);
							}
						}
					};
					sendMessage(action, student, new StudentPrivateState());
					actions.add(action);
					then(actions, () -> {
						if (!action.getResult().get())
						{
							((CoursePrivateState)actorState).setAvailableSpots(spaces+1);
							complete(false);
						}
						else
						{
							((CoursePrivateState)actorState).getRegStudents().add(student);
							complete(true);
						}
					});
				}
				else
				{
					complete(false);
					synchronized (System.out)
					{
						System.out.println("student "+student+" does not have prerequisites for course "+actorID);
					}
				}
			}
			else
			{
				complete(false);
				synchronized (System.out)
				{
					System.out.println("student "+student+" does not have place in course "+actorID);
				}
			}
		}
		else
		{
			complete(false);
			synchronized (System.out)
			{
				System.out.println("student "+student+" has NOT been registered for course "+actorID);
			}
		}
	}
}