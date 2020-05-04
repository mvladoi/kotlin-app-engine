# Ktor on Google App Engine Standard

To download and run this sample, download the code with the commands below, and
then follow the steps in the [Community Tutorial][tutorial].

```sh


[tutorial]: https://cloud.google.com/community/tutorials/kotlin-ktor-app-engine-java8


a.git clone https://github.com/mvladoi/kotlin-app-engine.git
b.Change the argument in build.gradle file

appengine.deploy.projectId = 'your-project'
appengine.deploy.version = 'version1'

c. Run 
./gradlew appengineDeploy


2.If we perform the coroutine on the Dispatcher main and wait for the job in a non blocking way, logs are logged as expected. 


fun log(logger:Logger, id: String?) = runBlocking {
  
    //logger.info("Message from log function for $id")
    val job = launch {logRequest(logger, id)}
    logger.info("Message from log function for $id, working on thread ${Thread.currentThread().name} ")
}


 suspend fun logRequest(logger:Logger, id: String?) {
   logger.info("Message from coroutine for $id, working on thread ${Thread.currentThread().name} ")
    }


Output:


2020-05-03 20:15:11.223 CEST
[g~project/version1.426417677329189686].<stdout>: 2020-05-03 18:15:11.223 [Request11C12D56] INFO  i.ktor.util.pipeline.PipelineContext - Message from main for 1115, working on thread Request11C12D56


2020-05-03 20:15:11.224 CEST
[g~project/version1.426417677329189686].<stdout>: 2020-05-03 18:15:11.224 [Request11C12D56] INFO  i.ktor.util.pipeline.PipelineContext - Message from log function for 1115, working on thread Request11C12D56 


2020-05-03 20:15:11.225 CEST
[g~project/version1.426417677329189686].<stdout>: 2020-05-03 18:15:11.225 [Request11C12D56] INFO  i.ktor.util.pipeline.PipelineContext - Message from coroutine for 1115, working on thread Request11C12D56 



3. If we change the context to Dispatcher IO then we have two cases:

a. We make a request and wait. The job is spanned but the main thread exits and the logs are never logged.
b. We make two request in a short period of time. The job is spanned and the logs from the first request are logged on the second request. 

fun log(logger:Logger, id: String?) = runBlocking {
  
    //logger.info("Message from log function for $id")
    val job = launch (Dispatchers.IO){logRequest(logger, id)}
    logger.info("Message from log function for $id, working on thread ${Thread.currentThread().name} ")
}


 suspend fun logRequest(logger:Logger, id: String?) {
   logger.info("Message from coroutine for $id, working on thread ${Thread.currentThread().name} ")
    }



a.Output for 1114
Make a single request with id 1114 


2020-05-03 20:15:11.223 CEST
[g~project/version1.426417677329189686].<stdout>: 2020-05-03 18:15:11.223 [Request11C12D56] INFO  i.ktor.util.pipeline.PipelineContext - Message from main for 1114, working on thread Request11C12D56


2020-05-03 20:15:11.224 CEST
[g~project/version1.426417677329189686].<stdout>: 2020-05-03 18:15:11.224 [Request11C12D56] INFO  i.ktor.util.pipeline.PipelineContext - Message from log function for 1114, working on thread Request11C12D56 




b.Output for 115
Make two request with id 1114 and 1115 in a short period of time


2020-05-03 20:15:11.223 CEST
[g~project/version1.426417677329189686].<stdout>: 2020-05-03 18:15:11.223 [Request11C12D56] INFO  i.ktor.util.pipeline.PipelineContext - Message from main for 1115, working on thread Request11C12D56


2020-05-03 20:15:11.224 CEST
[g~project/version1.426417677329189686].<stdout>: 2020-05-03 18:15:11.224 [Request11C12D56] INFO  i.ktor.util.pipeline.PipelineContext - Message from log function for 1115, working on thread Request11C12D56 


2020-05-03 20:15:11.225 CEST
[g~project/version1.426417677329189686].<stdout>: 2020-05-03 18:15:11.225 [Request11C12D56] INFO  i.ktor.util.pipeline.PipelineContext - Message from coroutine for 1114, working on thread Request11C12D56 




