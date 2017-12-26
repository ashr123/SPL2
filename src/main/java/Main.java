import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.sim.actions.AddStudent;
import bgu.spl.a2.sim.actions.CloseACourse;
import bgu.spl.a2.sim.actions.OpenANewCourse;
import bgu.spl.a2.sim.actions.OpenNewPlacesInACourse;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Main
{
	public static void main(String[] args) throws InterruptedException
	{
		StudentPrivateState studentPrivateState=new StudentPrivateState();
		CoursePrivateState coursePrivateState=new CoursePrivateState();
		ActorThreadPool pool=new ActorThreadPool(3);
		AddStudent addStudent=new AddStudent("Roy");
		AddStudent addStudent2=new AddStudent("Toren");
		OpenANewCourse openANewCourse1=new OpenANewCourse("Logic",100,new LinkedList<>());
		OpenANewCourse openANewCourse2=new OpenANewCourse("Algabra", 100, new LinkedList<>());
		CloseACourse closeACourse=new CloseACourse("Algabra");
		OpenNewPlacesInACourse openNewPlacesInACourse=new OpenNewPlacesInACourse(200);
		pool.submit(addStudent2, "Computer Science", new DepartmentPrivateState());
		pool.submit(addStudent, "Software Engineering", new DepartmentPrivateState());
		pool.submit(openANewCourse1, "Computer Science",new DepartmentPrivateState());
		pool.submit(openANewCourse2, "Computer Science", new DepartmentPrivateState());
		pool.submit(closeACourse,"Computer Science",new DepartmentPrivateState());
		pool.submit(openNewPlacesInACourse,"Logic",new CoursePrivateState());
		pool.start();
		Thread.sleep(5000);
		pool.shutdown();
		System.out.println("END");
	}
}