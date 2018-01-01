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

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator
{
	public static ActorThreadPool actorThreadPool;
	private static TempObject tempObject;
	private static CountDownLatch countDownLatch1, countDownLatch2, countDownLatch3;

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
	public static HashMap<String, PrivateState> end() throws InterruptedException
	{
		actorThreadPool.shutdown();
		return new HashMap<>(actorThreadPool.getActors());
	}

	public static void main(String[] args) throws IOException, InterruptedException
	{
		tempObject=new Gson().fromJson(new JsonReader(new FileReader(args[0])), TempObject.class);
		attachActorThreadPool(new ActorThreadPool(tempObject.numOfThreads));
		start();
		countDownLatch1=new CountDownLatch(tempObject.phase1.size());
		countDownLatch2=new CountDownLatch(tempObject.phase2.size());
		countDownLatch3=new CountDownLatch(tempObject.phase3.size());
		for (Computer computer : tempObject.computersList)
			Warehouse.addComputer(computer);
		makePhase(1);
		countDownLatch1.await();
		makePhase(2);
		countDownLatch2.await();
		makePhase(3);
		countDownLatch3.await();
		new ObjectOutputStream(new FileOutputStream("result.ser")).writeObject(end());
	}

	private static void makePhase(int phase)
	{
		List<GsonAction> phaseList;
		CountDownLatch countDownLatch;
		switch (phase)
		{
			case 1:
				phaseList=tempObject.phase1;
				countDownLatch=countDownLatch1;
				break;
			case 2:
				phaseList=tempObject.phase2;
				countDownLatch=countDownLatch2;
				break;
			case 3:
				phaseList=tempObject.phase3;
				countDownLatch=countDownLatch3;
				break;
			default:
				return;
		}
		for (GsonAction gsonAction : phaseList)
		{
			Action<?> action;
			String actorID;
			PrivateState privateState;
			switch (gsonAction.Action)
			{
				case "Add Student":
					action=new AddStudent(gsonAction.Student);
					actorID=gsonAction.Department;
					privateState=new DepartmentPrivateState();
					break;
				case "End Registeration":
					action=new AnnounceAboutTheEndOfRegistrationPeriod();
					actorID=gsonAction.Department;
					privateState=new DepartmentPrivateState();
					break;
				case "Administrative Check":
					action=new CheckAdministrativeObligations(gsonAction.Students,
					                                          gsonAction.Computer,
					                                          gsonAction.Conditions);
					actorID=gsonAction.Department;
					privateState=new DepartmentPrivateState();
					break;
				case "Close Course":
					action=new CloseACourse(gsonAction.Course);
					actorID=gsonAction.Department;
					privateState=new DepartmentPrivateState();
					break;
				case "Open Course":
					action=new OpenANewCourse(gsonAction.Course,
					                          Integer.parseInt(gsonAction.Space),
					                          gsonAction.Prerequisites);
					actorID=gsonAction.Department;
					privateState=new DepartmentPrivateState();
					break;
				case "Add Spaces":
					action=new OpenNewPlacesInACourse(Integer.parseInt(gsonAction.Number));
					actorID=gsonAction.Course;
					privateState=new CoursePrivateState();
					break;
				case "Participate In Course":
					action=new ParticipatingInCourse(gsonAction.Student,
					                                 gsonAction.Grade.get(0)
					                                                 .equals("-") ? -1 :
					                                 Integer.parseInt(gsonAction.Grade.get(0)));
					actorID=gsonAction.Course;
					privateState=new CoursePrivateState();
					break;
				case "Register With Preferences":
					action=new RegisterWithPreferences(gsonAction.Student,
					                                   new LinkedList<>(gsonAction.Preferences),
					                                   new LinkedList<>(gsonAction.Grade));
					actorID=gsonAction.Preferences.get(0);
					privateState=new CoursePrivateState();
					break;
				case "Unregister":
					action=new Unregister(gsonAction.Student);
					actorID=gsonAction.Course;
					privateState=new CoursePrivateState();
					break;
				default:
					synchronized (System.out)
					{
						System.out.println("Couldn't recognize "+gsonAction.Action);
					}
					return;
			}
			action.getResult().subscribe(countDownLatch::countDown);
			actorThreadPool.submit(action, actorID, privateState);
		}
	}

	private class TempObject
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
	}

	private class GsonAction
	{
		private String Action;
		private String Department;
		private String Course;
		private String Space;
		private String Student;
		private String Computer;
		private String Number;
		private List<String> Grade;
		private List<String> Prerequisites;
		private List<String> Students;
		private List<String> Conditions;
		private List<String> Preferences;
	}
}