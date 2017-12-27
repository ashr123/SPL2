package bgu.spl.a2.sim;

import java.util.List;
import java.util.Map;

public class Computer
{
	private String computerType;
	private long failSig;
	private long successSig;
	private final SuspendingMutex suspendingMutex=new SuspendingMutex(this);

	public Computer(String computerType)
	{
		this.computerType=computerType;
	}

	public Computer(String computerType, long failSig, long successSig)
	{
		this(computerType);
		this.failSig=failSig;
		this.successSig=successSig;
	}

	/**
	 * this method checks if the courses' grades fulfill the conditions
	 *
	 * @param courses       courses that should be pass
	 * @param coursesGrades courses' grade
	 * @return a signature if coursesGrades grades meet the conditions
	 */
	public long checkAndSign(List<String> courses, Map<String, Integer> coursesGrades)
	{
		for (String course : courses)
			if (coursesGrades.get(course)==null || coursesGrades.get(course)<56)
				return failSig;
		return successSig;
	}

	public String getComputerType()
	{
		return computerType;
	}

	public long getFailSig()
	{
		return failSig;
	}

	public long getSuccessSig()
	{
		return successSig;
	}

	public SuspendingMutex getSuspendingMutex()
	{
		return suspendingMutex;
	}
}