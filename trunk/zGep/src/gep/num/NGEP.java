package gep.num;

import gep.GEP;
import AdaGep.AdaGep;

/**
 * ÊýÖµGEP
 */
public class NGEP extends GEP
{
	public NGEP()
	{
		addFunctionFactory(new PlusFactory());
		addFunctionFactory(new MinusFactory());
		addFunctionFactory(new MultiplyFactory());
		addFunctionFactory(new DivideFactory());
		addFunctionFactory(new NegativeFactory());
		addFunctionFactory(new SqrtFactory());
		addFunctionFactory(new ExpFactory());
		addFunctionFactory(new LogFactory());
		addFunctionFactory(new SinFactory());
		addFunctionFactory(new CosFactory());
		addFunctionFactory(new TanFactory());
		addFunctionFactory(new AbsFactory());
	}
}
