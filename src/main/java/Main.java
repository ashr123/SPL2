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
import java.util.List;

public class Main
{
	public static void main(String[] args)
	{
		StudentPrivateState studentPrivateState=new StudentPrivateState();
		CoursePrivateState coursePrivateState=new CoursePrivateState();
		ActorThreadPool pool=new ActorThreadPool(10);
		AddStudent addStudent=new AddStudent("Roy");
		AddStudent addStudent2=new AddStudent("Toren");
		OpenANewCourse openANewCourseLogic=new OpenANewCourse("Logic",100,null);
		OpenANewCourse openANewCourseAlgabra=new OpenANewCourse("Algabra", 100, null);
		CloseACourse closeACourse=new CloseACourse("Algabra");
		OpenNewPlacesInACourse openNewPlacesInACourse=new OpenNewPlacesInACourse(200);
		pool.submit(addStudent2, "Computer Science", new DepartmentPrivateState());
		pool.submit(addStudent, "Software Engineering", new DepartmentPrivateState());
		pool.submit(openANewCourseLogic, "Computer Science",new DepartmentPrivateState());
		pool.submit(openANewCourseAlgabra, "Computer Science", new DepartmentPrivateState());
		pool.submit(closeACourse,"Computer Science",new DepartmentPrivateState());
		pool.submit(openNewPlacesInACourse,"Logic",new CoursePrivateState());
		pool.start();
		System.out.println("END");
	}
}