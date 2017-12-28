package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.Collection;
import java.util.LinkedList;

public class RegisterWithPreferences extends Action<Boolean>
{
	private final String student;
	private final LinkedList<String> preferences;
	private final LinkedList<Integer> grades;

	public RegisterWithPreferences(String student, LinkedList<String> preferences, LinkedList<Integer> grades)
	{
		setActionName("Register With Preferences");
		this.student=student;
		this.preferences=preferences;
		this.grades=grades;
	}

	@Override
	protected void start()
	{
		if (actorState instanceof CoursePrivateState && !grades.isEmpty())
		{
			String preference=preferences.removeFirst();
			Action<Boolean> register=new ParticipatingInCourse(student, grades.removeFirst());
			sendMessage(register, preference, new CoursePrivateState());
			Collection<Action<Boolean>> actions=new LinkedList<>();
			actions.add(register);
			then(actions, () -> {
				if (!register.getResult().get())
					start();
				else
				{
					complete(true);
					synchronized (System.out)
					{
						System.out.println("Student "+student+" has SUCCESSFULLY been registered to course "+preference);
					}
				}
			});
		}
		else
		{
			complete(false);
			synchronized (System.out)
			{
				System.out.println("Student "+student+" has NOT been registered to any course!!!");
			}
		}
	}
}