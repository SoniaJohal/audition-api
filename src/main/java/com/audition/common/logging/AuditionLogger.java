package com.audition.common.logging;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

@Component
public class AuditionLogger {

    public void info(final Logger logger, final String message) {
        if (logger.isInfoEnabled()) {
            logger.info(message);
        }
    }

    public void info(final Logger logger, final String message, final Object object) {
        if (logger.isInfoEnabled()) {
            logger.info(message, object);
        }
    }

    public void debug(final Logger logger, final String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    public void warn(final Logger logger, final String message) {
        if (logger.isWarnEnabled()) {
            logger.warn(message);
        }
    }

    public void error(final Logger logger, final String message) {
        if (logger.isErrorEnabled()) {
            logger.error(message);
        }
    }

    public void logStandardProblemDetail(final Logger logger, final ProblemDetail problemDetail, final Exception e) {
        if (logger.isErrorEnabled()) {
            final var message = createStandardProblemDetailMessage(problemDetail);
            logger.error(message, e);
        }
    }

    public void logHttpStatusCodeError(final Logger logger, final String message, final Integer errorCode) {
        if (logger.isErrorEnabled()) {
            logger.error(createBasicErrorResponseMessage(errorCode, message) + "\n");
        }
    }

    private String createStandardProblemDetailMessage(final ProblemDetail standardProblemDetail) {
        if (standardProblemDetail == null) {
            return StringUtils.EMPTY;
        }

        final StringBuilder message = new StringBuilder();

        message.append("Title: ").append(standardProblemDetail.getTitle());
        appendMessage(message, "Status Code", String.valueOf(standardProblemDetail.getStatus()));
        appendMessage(message, "Detail", standardProblemDetail.getDetail());

        // Append additional information if available
        if (standardProblemDetail.getProperties() != null) {
            standardProblemDetail.getProperties().forEach((key, value) ->
                appendMessage(message, key, String.valueOf(value)));
        }

        return message.toString();
    }

    private void appendMessage(final StringBuilder builder, final String key, final String value) {
        builder.append(", ").append(key).append(": ").append(value);
    }

    private String createBasicErrorResponseMessage(final Integer errorCode, final String message) {
        return String.format("Encountered error %s, with errorCode %d", message, errorCode);
    }
}
