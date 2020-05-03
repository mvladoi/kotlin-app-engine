/*
 * Copyright 2018 Google LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.demo




/* ktlint-disable no-wildcard-imports */
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.routing.*
import kotlinx.html.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.*







// Entry Point of the application as defined in resources/application.conf.
// @see https://ktor.io/servers/configuration.html#hocon-file

inline fun <reified T : Any> T.logger(): Logger = LoggerFactory.getLogger(T::class.java)

fun log(logger:Logger, id: String?) = runBlocking {
  
    //logger.info("Message from log function for $id")
    val job = launch {logRequest(logger, id)}
    logger.info("Message from log function for $id, working on thread ${Thread.currentThread().name} ")
}


 suspend fun logRequest(logger:Logger, id: String?) {
   logger.info("Message from coroutine for $id, working on thread ${Thread.currentThread().name} ")
    }



   

fun Application.main() {
    // This adds Date and Server headers to each response, and allows custom additional headers
    install(DefaultHeaders)
    // This uses use the logger to log every call (request/response)
    install(CallLogging)
     
   

    routing {
        // Here we use a DSL for building HTML on the route "/"
        // @see https://github.com/Kotlin/kotlinx.html



        get("/warn/{id}") {


            val id = call.parameters["id"]
            
           
            // Add log correlation to nest all log messages beneath request log in Log View
            
            

            logger().info("Message from main for $id, working on thread ${Thread.currentThread().name}")
            log(logger(), id)
            call.respondHtml {
                head {
                    title { +"Ktor on Google App Engine Standard" }
                }
                body {
                    p {
                        +"Hello there! This is Ktor running on Google Appengine Standard"
                    }
                }
            }
        }

        get("/error") {
            call.respondHtml {
                head {
                    title { +"Ktor on Google App Engine Standard" }
                }
                body {
                    p {
                        +"It's another route!"
                    }
                }
            }
        }
    }



}

