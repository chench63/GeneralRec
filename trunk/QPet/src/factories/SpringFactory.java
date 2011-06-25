package factories;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import flex.messaging.FactoryInstance;
import flex.messaging.FlexContext;
import flex.messaging.FlexFactory;
import flex.messaging.config.ConfigMap;
import flex.messaging.services.ServiceException;

/**
 * The FactoryFactory interface is implemented by factory components that
 * provide instances to the data services messaging framework. To configure data
 * services to use this factory, add the following lines to your
 * services-config.xml file (located in the WEB-INF/flex directory of your web
 * application).
 * 
 * <pre>
 * &lt;factories&gt;
 * &lt;factory id=&quot;spring&quot; class=&quot;flex.samples.factories.SpringFactory&quot; /&gt;
 * &lt;/factories&gt;
 * </pre>
 * 
 * You also must configure the web application to use spring and must copy the
 * spring.jar file into your WEB-INF/lib directory. To configure your app server
 * to use Spring, you add the following lines to your WEB-INF/web.xml file:
 * 
 * <pre>
 * &lt;context-param&gt;
 * &lt;param-name&gt;contextConfigLocation&lt;/param-name&gt;
 * &lt;param-value&gt;/WEB-INF/applicationContext.xml&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * 
 * &lt;listener&gt;
 * &lt;listener-class&gt;
 * org.springframework.web.context.ContextLoaderListener&lt;/listener-class&gt;
 * &lt;/listener&gt;
 * </pre>
 * 
 * Then you put your Spring bean configuration in WEB-INF/applicationContext.xml
 * (as per the line above). For example: BLAZEDS BlazeDS Developer Guide 191
 * 
 * <pre>
 * &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
 * &lt;!DOCTYPE beans PUBLIC &quot;-//SPRING//DTD BEAN//EN&quot;
 * &quot;http://www.springframework.org/dtd/spring-beans.dtd&quot;&gt;
 * 
 * &lt;beans&gt;
 * &lt;bean name=&quot;weatherBean&quot; class=&quot;dev.weather.WeatherService&quot; singleton=&quot;true&quot;/&gt;
 * &lt;/beans&gt;
 * </pre>
 * 
 * Now you are ready to define a Remoting Service destination that maps to this
 * existing service. To do this you'd add this to your
 * WEB-INF/flex/remoting-config.xml:
 * 
 * <pre>
 * &lt;destination id=&quot;WeatherService&quot;&gt;
 * &lt;properties&gt;
 * &lt;factory&gt;spring&lt;/factory&gt;
 * &lt;source&gt;weatherBean&lt;/source&gt;
 * &lt;/properties&gt;
 * &lt;/destination&gt;
 * </pre>
 */
public class SpringFactory implements FlexFactory {
	private static final String SOURCE = "source";

	/**
	 * This method can be used to initialize the factory itself. It is called
	 * with configuration parameters from the factory tag which defines the id
	 * of the factory.
	 */
	public void initialize(String id, ConfigMap configMap) {
	}

	/**
	 * This method is called when we initialize the definition of an instance
	 * which will be looked up by this factory. It should validate that the
	 * properties supplied are valid to define an instance. Any valid properties
	 * used for this configuration must be accessed to avoid warnings about
	 * unused configuration elements. If your factory is only used for
	 * application scoped components, this method can simply return a factory
	 * instance which delegates the creation of the component to the
	 * FactoryInstance's lookup method.
	 */
	public FactoryInstance createFactoryInstance(String id, ConfigMap properties) {
		SpringFactoryInstance instance = new SpringFactoryInstance(this, id,
				properties);
		instance.setSource(properties.getPropertyAsString(SOURCE, instance
				.getId()));
		return instance;
	} // end method createFactoryInstance()

	/**
	 * Returns the instance specified by the source and properties arguments.
	 * For the factory, this may mean constructing a new instance, optionally
	 * registering it in some other name space such as the session or JNDI, and
	 * then returning it or it may mean creating a new instance and returning
	 * it. This method is called for each request to operate on the given item
	 * by the system so it should be relatively efficient.
	 * <p>
	 * If your factory does not support the scope property, it report an error
	 * if scope is supplied in the properties BLAZEDS BlazeDS Developer Guide
	 * 192 for this instance.
	 * </p>
	 */
	public Object lookup(FactoryInstance inst) {
		SpringFactoryInstance factoryInstance = (SpringFactoryInstance) inst;
		return factoryInstance.lookup();
	}

	static class SpringFactoryInstance extends FactoryInstance {
		SpringFactoryInstance(SpringFactory factory, String id,
				ConfigMap properties) {
			super(factory, id, properties);
		}

		public String toString() {
			return "SpringFactory instance for id=" + getId() + " source="
					+ getSource() + " scope=" + getScope();
		}

		public Object lookup() {
			ApplicationContext appContext = WebApplicationContextUtils
					.getWebApplicationContext(FlexContext.getServletConfig().getServletContext());
			String beanName = getSource();
			try {
				return appContext.getBean(beanName);
			} catch (NoSuchBeanDefinitionException nexc) {
				ServiceException e = new ServiceException();
				String msg = "Spring service named '" + beanName
						+ "' does not exist.";
				e.setMessage(msg);
				e.setRootCause(nexc);
				e.setDetails(msg);
				e.setCode("Server.Processing");
				throw e;
			} catch (BeansException bexc) {
				ServiceException e = new ServiceException();
				String msg = "Unable to create Spring service named '"
						+ beanName + "' ";
				e.setMessage(msg);
				e.setRootCause(bexc);
				e.setDetails(msg);
				e.setCode("Server.Processing");
				throw e;
			}
		}
	}
}