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



