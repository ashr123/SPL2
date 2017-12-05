package main.java.bgu.spl.a2.sim.privateStates;

import main.java.bgu.spl.a2.PrivateState;

import java.util.List;

/**
 * this class describe department's private state
 */
public class DepartmentPrivateState extends PrivateState
{
	private List<String> courseList;
	private List<String> studentList;

	/**
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public DepartmentPrivateState()
	{
		//TODO: replace method body with real implementation
		throw new UnsupportedOperationException("Not Implemented Yet.");
	}

	public List<String> getCourseList()
	{
		return courseList;
	}

	public List<String> getStudentList()
	{
		return studentList;
	}
}