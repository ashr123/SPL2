/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.sim.actions.*;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator
{
	public static ActorThreadPool actorThreadPool;
	private static TempObject tempObject;

	/**
	 * Begin the simulation Should not be called before attachActorThreadPool()
	 */
	public static void start()
	{
		actorThreadPool.start();
	}

	/**
	 * attach an ActorThreadPool to the Simulator, this ActorThreadPool will be used to run the simulation
	 *
	 * @param myActorThreadPool - the ActorThreadPool which will be used by the simulator
	 */
	public static void attachActorThreadPool(ActorThreadPool myActorThreadPool)
	{
		actorThreadPool=myActorThreadPool;
	}

	/**
	 * shut down the simulation
	 * returns list of private states
	 */
	public static HashMap<String, PrivateState> end()
	{
		try
		{
			actorThreadPool.shutdown();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return new HashMap<>(actorThreadPool.getActors());
	}

	public static void main(String[] args)
	{
		try
		{
			deSerializationJSON(args[0]);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		start();
	}

	private class GsonAction
	{
		String Action;
		String Department;
		String Course;
		String Space;
		String Student;
		String Computer;
		String Number;
		List<String> Grade;
		List<String> Prerequisites;
		List<String> Students;
		List<String> Conditions;
		List<String> Preferences;

		public GsonAction(String action, String department, String course, String space,
		                  String student, String computer, String number, List<String> grade,
		                  List<String> prerequisites, List<String> students, List<String> conditions,
		                  List<String> preferences)
		{
			Action=action;
			Department=department;
			Course=course;
			Space=space;
			Student=student;
			Computer=computer;
			Number=number;
			Grade=grade;
			Prerequisites=prerequisites;
			Students=students;
			Conditions=conditions;
			Preferences=preferences;
		}
	}

	private static void deSerializationJSON(String args) throws FileNotFoundException
	{
		tempObject=new Gson().fromJson(new JsonReader(new FileReader(args)), TempObject.class);
		attachActorThreadPool(new ActorThreadPool(tempObject.numOfThreads));
		for (Computer computer : tempObject.computersList)
			Warehouse.addComputer(computer);
		makePhase(1);
	}

	private static void makePhase(int phase)
	{
		List<GsonAction> phaseList;
		switch (phase)
		{
			case 1:
				phaseList=tempObject.phase1;
				break;
			case 2:
				phaseList=tempObject.phase2;
				break;
			case 3:
				phaseList=tempObject.phase3;
				break;
			default:
				try
				{
					actorThreadPool.shutdown();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				return;
		}
		Action<Boolean> action=new Action<Boolean>()
		{
			@Override
			protected void start()
			{
				System.out.println("Starting phase "+phase+"...");
				Collection<Action<?>> actions=new LinkedList<>();
				for (GsonAction gsonAction : phaseList)
				{
					Action<?> action1;
					String actorID;
					PrivateState privateState;
					switch (gsonAction.Action)
					{
						case "Add Student":
							action1=new AddStudent(gsonAction.Student);
							actorID=gsonAction.Department;
							privateState=new DepartmentPrivateState();
							break;
						case "End Registeration":
							action1=new AnnounceAboutTheEndOfRegistrationPeriod();
							actorID=gsonAction.Department;
							privateState=new DepartmentPrivateState();
							break;
						case "Administrative Check":
							action1=new CheckAdministrativeObligations(gsonAction.Students, gsonAction.Computer,
							                                           gsonAction.Conditions);
							actorID=gsonAction.Department;
							privateState=new DepartmentPrivateState();
							break;
						case "Close Course":
							action1=new CloseACourse(gsonAction.Course);
							actorID=gsonAction.Department;
							privateState=new DepartmentPrivateState();
							break;
						case "Open Course":
							action1=new OpenANewCourse(gsonAction.Course, Integer.parseInt(gsonAction.Space), gsonAction.Prerequisites);
							actorID=gsonAction.Department;
							privateState=new DepartmentPrivateState();
							break;
						case "Add Spaces":
							action1=new OpenNewPlacesInACourse(Integer.parseInt(gsonAction.Number));
							actorID=gsonAction.Course;
							privateState=new CoursePrivateState();
							break;
						case "Participate In Course":
							action1=new ParticipatingInCourse(gsonAction.Student,
							                                  gsonAction.Grade.get(0)
							                                                  .equals("-") ? -1 : Integer.getInteger(
									                                  gsonAction.Grade.get(0)));
							actorID=gsonAction.Course;
							privateState=new CoursePrivateState();
							break;
						case "Register With Preferences":
							LinkedList<Integer> temp=new LinkedList<>();
							for (String grade : gsonAction.Grade)
								temp.add(Integer.parseInt(grade));
							action1=new RegisterWithPreferences(gsonAction.Student,
							                                    new LinkedList<>(gsonAction.Preferences), temp);
							actorID=gsonAction.Course;
							privateState=new CoursePrivateState();
							break;
						case "Unregister":
							action1=new Unregister(gsonAction.Student);
							actorID=gsonAction.Course;
							privateState=new CoursePrivateState();
							break;
						default:
							complete(false);
							System.out.println("couldn't recognize "+gsonAction.Action);
							return;
					}
					sendMessage(action1, actorID, privateState);
					actions.add(action1);
				}
				then(actions, () -> {
					complete(true);
					System.out.println("Finished phase "+phase+"!!!");
					makePhase(phase+1);
				});
			}
		};
		actorThreadPool.submit(action, "Simulator", new PrivateState()
		{
		});
	}

	public class TempObject
	{
		@SerializedName("threads")
		private int numOfThreads;
		@SerializedName("Computers")
		private List<Computer> computersList;
		@SerializedName("Phase 1")
		private List<GsonAction> phase1;
		@SerializedName("Phase 2")
		private List<GsonAction> phase2;
		@SerializedName("Phase 3")
		private List<GsonAction> phase3;

		TempObject(int numOfThreads, List<Computer> computersList, List<GsonAction> phase1, List<GsonAction> phase2, List<GsonAction> phase3)
		{
			this.numOfThreads=numOfThreads;
			this.computersList=computersList;
			this.phase1=phase1;
			this.phase2=phase2;
			this.phase3=phase3;
		}
	}
}