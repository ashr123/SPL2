package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Behavior: The department's secretary have to allocate one of the computers available in the warehouse, and check for each student if he meets some administrative obligations. The computer generates a signature and save it in the private state of the students.
 * <p>
 * Actor: Must be initially submitted to the department's actor.
 */
public class CheckAdministrativeObligations extends Action<Boolean>
{
	private final List<String> students;
	private final String computer;
	private final List<String> conditions;

	public CheckAdministrativeObligations(List<String> students, String computer, List<String> conditions)
	{
		setActionName("Administrative Check");
		this.students=students;
		this.computer=computer;
		this.conditions=conditions;
	}

	@Override
	protected void start()
	{
		if (actorState instanceof DepartmentPrivateState)
		{
			Promise<Computer> promise=Warehouse.getSuspendingMutex(computer).down();
			Collection<Action<?>> actions=new LinkedList<>();
			promise.subscribe(() -> {
				for (String student : students)
				{
					Action<Boolean> action=new Action<Boolean>()
					{
						@Override
						protected void start()
						{
							if (actorState instanceof StudentPrivateState)
							{
								((StudentPrivateState)actorState).setSignature(promise.get().checkAndSign(conditions,
								                                                                          ((StudentPrivateState)actorState)
										                                                                          .getGrades()));
								complete(true);
							}
							else
								complete(false);
						}
					};
					actions.add(action);
					sendMessage(action, student, new StudentPrivateState());
				}
			});
			then(actions, () -> {
				Warehouse.getSuspendingMutex(computer).up();
				if (!getResult().isResolved())
					complete(true);
				synchronized (System.out)
				{
					System.out.println(
							"Administrative Obligations for department "+actorID+" has SUCCESSFULLY been checked!!!");
				}
			});
		}
		else
		{
			complete(false);
			synchronized (System.out)
			{
				System.out.println("Administrative Obligations for department "+actorID+" has NOT been checked!!!");
			}
		}
	}
}