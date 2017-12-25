package bgu.spl.a2.sim.privateStates;

import bgu.spl.a2.PrivateState;

import java.util.LinkedList;
import java.util.List;

/**
 * this class describe course's private state
 */
public class CoursePrivateState extends PrivateState
{
	private Integer availableSpots;
	private Integer registered;
	private List<String> regStudents=new LinkedList<>();
	private List<String> prequisites=new LinkedList<>();
	private boolean endOfRegistration;
	/**
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public CoursePrivateState()
	{
	}

	public Integer getAvailableSpots()
	{
		return availableSpots;
	}

	public Integer getRegistered()
	{
		return registered;
	}

	public List<String> getRegStudents()
	{
		return regStudents;
	}

	public List<String> getPrequisites()
	{
		return prequisites;
	}

	public void setAvailableSpots(Integer availableSpots)
	{
		this.availableSpots=availableSpots;
	}

	public void setRegistered(Integer registered)
	{
		this.registered=registered;
	}

	public boolean isEndOfRegistration()
	{
		return endOfRegistration;
	}

	public void setEndOfRegistration(boolean endOfRegistration)
	{
		this.endOfRegistration=endOfRegistration;
	}
}