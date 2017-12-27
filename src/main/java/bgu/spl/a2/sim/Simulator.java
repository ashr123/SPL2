/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator
{
	public static ActorThreadPool actorThreadPool;

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

	}

	private class GsonAction
	{
		String Action;
		String Department;
		String Course;
		String Space;
		String Student;
		String Computer;
		List<String> Grade;
		List<String> Prerequisites;
		List<String> Students;
		List<String> Conditions;

		public GsonAction(String action, String department, String course, String space,
		                  String student, String computer, List<String> grade,
		                  List<String> prerequisites, List<String> students, List<String> conditions)
		{
			Action=action;
			Department=department;
			Course=course;
			Space=space;
			Student=student;
			Computer=computer;
			Grade=grade;
			Prerequisites=prerequisites;
			Students=students;
			Conditions=conditions;
		}
	}

	private static void DeSerializationJSON(String[] args) throws FileNotFoundException
	{
		Gson gson=new Gson();
		JsonReader reader=new JsonReader(new FileReader(Arrays.toString(args)));
		TempObject tempObject=gson.fromJson(reader, TempObject.class);
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