package bgu.spl.a2.sim;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class Computer
{
	@SerializedName("Type")
	private final String computerType;
	@SerializedName("Sig Fail")
	private long failSig;
	@SerializedName("Sig Success")
	private long successSig;

	public Computer(String computerType)
	{
		this.computerType=computerType;
	}

	public Computer(String computerType, long successSig, long failSig)
	{
		this(computerType);
		this.successSig=successSig;
		this.failSig=failSig;
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
}