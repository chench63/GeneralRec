package gep.bool;

import gep.GEP;

/**
 * ÊýÖµGEP
 */
public class BGEP extends GEP
{
	public BGEP()
	{
		addFunctionFactory(new AndFactory());
		addFunctionFactory(new OrFactory());
		addFunctionFactory(new NotFactory());
		addFunctionFactory(new XorFactory());
		addFunctionFactory(new BFactory());
		addFunctionFactory(new IfFactory());
	}
}
