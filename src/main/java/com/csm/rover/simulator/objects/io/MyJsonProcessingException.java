package com.csm.rover.simulator.objects.io;

import com.fasterxml.jackson.core.JsonProcessingException;

public class MyJsonProcessingException extends JsonProcessingException {

    public MyJsonProcessingException(String msg) {
        super(msg);
    }

    public MyJsonProcessingException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }

    public MyJsonProcessingException(Throwable rootCause) {
        super(rootCause);
    }

}
