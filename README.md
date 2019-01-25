# SpringBootApplicationListener



**Problem Statement :** More often or not we have to use an API which exposes some properties to configure and based on those properties values API behavior can be adjusted. In one of my project we were using one custom API where we had to set some pre-defined properties exposed by the 3rd party API as System properties, so that this custom API can read those System properties to initialize itself during application startup. 



If this were a pre spring boot application or a plain web application we would have configured a application listener class and configured in web.xml with those properties declared in some property file and that prorperty file location configured as init parameters or conntext param.



The problem with our application was that it was pure spring boot application i.e it was using as much functionality of spring boot as possible. Being a spring boot applicatiion all the initialization propeties were kept in "application.properties" which spring boot appliction reads (by default, no need to hard-code the file name  anywhere in the application) and loads all the propeties and inject as PropertySource in our application. 


We did not wanted to have any listener class written where we have to hard-code property file location or its name explicitly and we wanted to keep using the same "application.properties" file which spring boot was using (from wherever it loads that, be it classpath or custom file lcation). 
We wanted a solution to have spring boot lifecycle handle it rather then writing some solution which would be outside of spring initialization lifecycle for consistency purpose.



We needed some hook which 


1. Could read the application.prorperties file (more precisely properties declared in it) before any bean gets initialized

2. Decrypt some encrypted properties specially passwords using our custom decryption mechanism

3. Set required properties as System properties after reading some properties from step 1, so that beans who needs these propertiesto initialize itselves.

We looked for solution on the web and couldn't find any readymade solutions for these points. In this I will show how we handled these problems.


**Solution :** Though spring does not provide any built in solution for handling above mention problem but we found that it does provide eventListener classes which could provide us application hooks at different level of spring-boot application life cycle. For our problem statment,  we decided to implement ##org.springframework.context.ApplicationListener which could listen for **ApplicationEnvironmentPreparedEvent**. From [spring-boot documentation](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/context/event/ApplicationEnvironmentPreparedEvent.html)- **Event published when a SpringApplication is starting up and the Environment is first available.** This is exactly what we needed i.e. -

1. When this event gets triggered we can be sure that spring has already read all the properties from spring config file (application.prooperties or whatever name we decided to have) as this being part of spring-boot lifecycle

2. We can be also be sure that none of the beans are initlized at the point when control comes to this listener, so we can do any pre-processing we need.

To implement this solution we just need two steps -

**1. Implement this ApplicationListener which would listen for ApplicationEnvironmentPreparedEvent, where we can read the properties from spring environment (which spring read from config file) by overriding ##onApplicationEvent method.**

**2. Register our listener with spring-boot**

Here is sample demo for incorporating this solution in any spring-boot application -

**1. Implement the ApplicationListener interface**

```java

public class SystemPropertiesLoader implements ApplicationListener<ApplicationEnvironemntPreparedEvent>
{

  @Override
  public void onApplicationEvent(ApplicationEnvironemntPreparedEvent event){
    // Get the spring Environemnt from event
    ConfigurableEnvironment cenv = event.getEnvironment(); // provides hook for all spring environemtn configurations
    
    // read the desired property from configurable environment and then set that as System property
    String desiredPropertyValue = cenv.getProperty("DesiredPropertyName");
    
    System.setProperty("systemPropertyName", dersiredPropertyValue);
  
  }
```
**2. Register the listener with Spring-boot**


   **2.1 If we are using spring-boot's embadded server**
   
   
  ```java
  public staic void main (String[] args){
    SpringApplication springApp = new SpringApplication(ProjectConfigurationClass configClass);
    
    //This is how we will register the listener with spring boot lifecycle
    springApp.addListener(new SystemPropertiesLoader());
    
    springApp.run();
  }
  ``` 
  
  
  **2.2 If we are planning of creating traditional  web application to deploy on external server, then we will have to register this listener by overriding the "configure" method of Abstract class SpringBootServletInitializer". To know more about this class and how and why we need this read [this from spring documentation](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/web/servlet/support/SpringBootServletInitializer.html) and [this from baeldung](https://www.baeldung.com/spring-boot-servlet-initializer)**
  
  
  ```java
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
    return builder.listeners(new SystemPropertiesLoader());
  }
  
  ```
  
And voila, no hard coding of any file name or its location, you have the hook and all the properties available as SystemProperties and you can be sure that these properties will be set as System properties before any bean gets initialized in spring-boot lifecycle. We do not need to worry of any dependency of beans or order of the beans to make sure that bean which is setting the system properties gets loaded before the one which is consuming it.
 
This is specially useful when we have some beans which comes as API/jars where order of those beans in not in our control and we can not make them dependent on others or we can not set the order of those.
 
Setting the SystemProperties was one of the use case specific to our requirement but the point is this hook can be used for any pre-processing logic before any of the beans start getting initialized in spring-boot lifecycle.

