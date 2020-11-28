package com._98point6.droptoken;

import com._98point6.droptoken.exception.DropTokenInvalidInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 *
 */
public class DropTokenExceptionMapper implements ExceptionMapper<DropTokenInvalidInputException>  {
    private static final Logger logger = LoggerFactory.getLogger(DropTokenExceptionMapper.class);
    public Response toResponse(DropTokenInvalidInputException e) {
        logger.error("Unhandled exception.", e);
        return Response.
                status(e.getCode()).
                entity(e.getMessage()).
                type(MediaType.APPLICATION_JSON).
                build();
    }
}
