package com.msd.robot.application

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN)
class NotAllowedException(s: String) : RuntimeException(s)
