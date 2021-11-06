package com.msd.application

/**
 * Gets thrown if there is a problem with the connection to another MicroService or if an internal problem occurs
 * in another MicroService during processing of one of our requests.
 */
class ClientException(s: String) : RuntimeException(s)
