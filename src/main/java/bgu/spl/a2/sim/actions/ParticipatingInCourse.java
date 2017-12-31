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
	private final String student;
	private final int grade;

	public ParticipatingInCourse(String student, int grade)
	{
		setActionName("Participate In Course");
		this.student=student;
		this.grade=grade;
	}

	@Override
	protected void start()
	{
		if (actorState instanceof CoursePrivateState)
		{
			if (((CoursePrivateState)actorState).getAvailableSpots()!=null &&
			    ((CoursePrivateState)actorState).getAvailableSpots()>0)//If the course exists and has available spots
			{
				Collection<Action<?>> actions=new LinkedList<>();
				Action<Boolean> action=new Action<Boolean>()
				{
					@Override
					protected void start()
					{
						if (actorState instanceof StudentPrivateState)
						{
							if (((StudentPrivateState)actorState).getGrades()
							                                     .containsKey(ParticipatingInCourse.this
									                                                  .actorID))//If the course already exists in the student's records
								complete(false);
							else
							{
								for (String prerequisite : ((CoursePrivateState)ParticipatingInCourse
										                                                .this.actorState)
										                           .getPrerequisites())
									if (!((StudentPrivateState)actorState).getGrades()
									                                      .containsKey(prerequisite))
									{
										complete(false);
										synchronized (System.out)
										{
											System.out.println(
													"student "+student+" does not have prerequisites for course "+ParticipatingInCourse.this.actorID);
										}
										return;
									}
								((StudentPrivateState)actorState).getGrades()
								                                 .put(ParticipatingInCourse.this.actorID,
								                                      grade);
								complete(true);
							}
						}
						else
							complete(false);
						synchronized (System.out)
						{
							System.out.println("Course "+ParticipatingInCourse.this.actorID+" has "+
							                   (getResult().get() ? "SUCCESSFULLY" : "NOT")+" been added to student "+
							                   actorID);
						}
					}
				};
				sendMessage(action, student, new StudentPrivateState());
				actions.add(action);
				then(actions, () -> {
					if (!action.getResult().get())//Checks if the grade had not been added to the student
						complete(false);
					else
						if (((CoursePrivateState)actorState)
								    .getAvailableSpots()>0)//Checks if the course had not been closed of fulled
						{
							((CoursePrivateState)actorState).getRegStudents().add(student);
							((CoursePrivateState)actorState).setAvailableSpots(
									((CoursePrivateState)actorState).getAvailableSpots()-1);
							((CoursePrivateState)actorState).setRegistered(((CoursePrivateState)actorState)
									                                               .getRegistered()+1);
							complete(true);
						}
						else
							complete(false);
					if (!getResult().get() && action.getResult().get())
						sendMessage(new Action<Boolean>()
						{
							@Override
							protected void start()
							{
								if (actorState instanceof StudentPrivateState)
								{
									((StudentPrivateState)actorState).getGrades()
									                                 .remove(ParticipatingInCourse.this
											                                         .actorID);
									complete(true);
								}
								else
									complete(false);
							}
						}, student, new StudentPrivateState());

				});
			}
			else
			{
				complete(false);
				synchronized (System.out)
				{
					System.out.println("student "+student+" does not have place in course "+actorID+" or the course "+
					                   "is closed");
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