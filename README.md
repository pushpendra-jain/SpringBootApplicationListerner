# SpringBootApplicationListerner

Problem Statement : More often or not you have to use an API which usually expose some properties to configure their behavior so that it can configure to suit your needs. In one of my project we were using one custom API where we had to set some properties as System properties so that this custom API can read those Ssystem properties and intilaize themself when those API getting initilaized during application startup. 

If this were a pre spring boot application we would have configured a application listener class and configured in web.xml with those property declared in some property file and that prorperty file location configured as init parameters or conntext param.

The problem with our application was that first it was pure spring boot application. Being a sprinng boot applicatiion all the initialization propeties were kept in applicaction.properties and by default spring boot appliaction reads the application.properties (by default, no need to hard-code the file name  anywhere in the application) and loads all the propeties and inject as PropertySource in our application. 

first we did not wanted to have such listener class written where we have to tell the property file location and name explicitly and we wanted to keep using the same property file which spring boot was using (from wherever it loads those be it classpath or custom file lcation).
We wanted a solution to have spring boot lifecycle handle it rather then writing some solution which would be outside of spring initialization lifecycle for consistency purpose.

Another problem was that some of  our properties were encrypted using certain mechanism, So we needed some hook which 
1. Could read the application.prorperties file before any  bean gets initialized
2. Decrpty those properties using our custom decryption mechanism
3. Set required properties as System properties so that beans who reads these properties can have them.
4. Have datasource bean created which would require decrypted password for creating any connection.

We look for solution on the internet and couldn't find any readymade solutions for these points. In thsi we will discuss how we handled first 3 points from above issues. 

Solution : Though spring does not provide any built in solution for handling our above mention problem but we found that it does provide eventListener classes which could provide us application hooks at different level of application cycle.For our problem statment we have decided to implement ##org.springframework.context.ApplicationListener which could listen for ##ApplicationEnvironmentPreparedEvent. From spring-bbot documentation ##https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/context/event/ApplicationEnvironmentPreparedEvent.html, Event published when a SpringApplication is starting up and the Environment is first available. This is exactly what we needed.
1. Before this event gets triggered we can be sure that spring has already read all the properties from spring config file (application.prooperties or whatever name we decided to have) this being part of spring-boot lifecycle
2. We can be also sure that none of the beans are initlized when control comes to this listener, so we can dio any preprocerssing we need.

So to implement this solution we just need two steps -
1. Implement this ApplicationListener which would listen for ApplicationEnvironmentPreparedEvent, where we can read the properties from spring environment (which spring read from config file) by overriding ##onApplicationEvent method.
2. Register our listener with spring-boot

Here is sample demo -
1. Implement the ApplicationListener
```java

public class SystemPropertiesLoader implements ApplicationListener<ApplicationEnvironemntPreparedEvent>
{

  public void onApplicationEvent(ApplicationEnvironemntPreparedEvent event){
    // Get the spring Environemnt from event
    ConfigurableEnvironment cenv = event.getEnvironment(); // provides hook for all spring environemtn configurations
    
    // read the desired property from configurable environment and then set that as System property
    String desiredPropertyValue = cenv.getProperty("DesiredPropertyName");
    
    System.setProperty("systemPropertyName", dersiredPropertyValue);
  
  }
```
2. Register the listener

  2.1 If we are using spring-boot's embadded server
  ```java
  public staic void main (String[] args){
    SpringApplication springApp = new SpringApplication(ProjectConfigurationClass configClass);
    
    //This is how we will register the listener with spring boot lifecycle
    springApp.addListener(new SystemPropertiesLoader());
    
    springApp.run();
  }
  ``` 
  2.2 If we are planning of creating a war file and planning to deploy on external server, then we will have to register this listener via overriding the "configure" method of Abstract class SpringBootServletInitializer". To know more about this class and why we need this read this https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/web/servlet/support/SpringBootServletInitializer.html and this https://www.baeldung.com/spring-boot-servlet-initializer
  
  ```java
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
    return builder.listeners(new SystemPropertiesLoader());
  }
  
  ```
  
 And voila, you have the hook and all the properties available as SystemProperties and you can be sure that these properties will be set as System properties before any bean gets initialized in spring-boot lifecycle. We do not need to worry of any dependecy of beans or order of the beans to make sure that bean which is setting the system properties gets loaded before the one which is consuming it.
 
 This is specially useful when we have some beans which comes as API/jars where order of those beans in not in our control and we can not make them dependent on others or we can not set the order of those.
 
Setting the SystemProperties was one of the use case specific to our requirement but the point is this hook can be used for any pre-processing logic before any of the beans start getting initialized in spring-boot lifecycle.

