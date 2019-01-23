# SpringBootApplicationListerner

More often or not you have to use an API which usually expose some properties to configure their behavior so that it can configure to suit your needs. In one of my project we were using one custom API where we had to set some properties as System properties so that this custom API can read those Ssystem properties and intilaize themself when those API getting initilaized during application startup. 

If this were a pre spring boot application we would have configured a application listener class and configured in web.xml with those property declared in some property file and that prorperty file location configured as init parameters or conntext param.

The problem with our application was that first it was pure spring boot application. Being a sprinng boot applicatiion all the initialization propeties were kept in applicaction.properties and by default spring boot appliaction reads the application.properties (by default, no need to hard-code the file name  anywhere in the application) and loads all the propeties and inject as PropertySource in our application. 

first we did not wanted to have such listener class written where we have to tell the property file location and name explicitly and we wanted to keep using the same property file which spring boot was using (from wherever it loads those be it classpath or custom file lcation).
We wanted a solution to have spring boot lifecycle handle it rather then writing some solution which would be outside of spring initialization lifecycle for consistency purpose.

Another problem was that some of  our properties were encrypted using certain mechanism, So we needed some hook which 
1. Could read the application.prorperties file before any  bean gets initilized
2. Decrpty those properties using our custom decryption mechanism
3. Set required properties as System properties so that beans who reads these properties can have them.

