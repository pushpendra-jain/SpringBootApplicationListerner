# SpringBootApplicationListerner

More often or not you have to use an API which usually expose some properties to configure their behavior so that it can configure to suit your needs. In one of my project we were using one custom API where we had to set some properties as System properties so that this custom API can read those Ssystem properties and intilaize themself when those API getting initilaized during application startup. 

If this were a pre spring boot application we would have configured a application listener class and configured in web.xml with those property declared in some property file and that prorperty file location configured as init parameters or conntext param.
