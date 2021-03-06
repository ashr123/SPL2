package bgu.spl.a2.sim.privateStates;

import bgu.spl.a2.PrivateState;

import java.util.HashMap;

/**
 * this class describe student private state
 */
public class StudentPrivateState extends PrivateState
{
	private final HashMap<String, Integer> grades=new HashMap<>();
	private long signature;

	/**
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public StudentPrivateState()
	{
	}

	public HashMap<String, Integer> getGrades()
	{
		return grades;
	}

	public long getSignature()
	{
		return signature;
	}

	public void setSignature(long signature)
	{
		this.signature=signature;
	}
}