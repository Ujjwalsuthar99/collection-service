package com.synoriq.synofin.collection.collectionservice.config.timeout;


import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.*;


@WebFilter("/*")
public class TimeoutFilter implements Filter {

    private static final long TIMEOUT_THRESHOLD_MS = 30000; // 30 seconds
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        // Initialization code, if any
//    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Start asynchronous processing
        AsyncContext asyncContext = httpRequest.startAsync(httpRequest, httpResponse);
        asyncContext.setTimeout(TIMEOUT_THRESHOLD_MS); // Set timeout

        // Schedule a task to interrupt the thread after the timeout threshold
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.schedule(() -> {
            if (!asyncContext.getResponse().isCommitted()) {
                // Timeout occurred, handle it
                asyncContext.complete();
                httpResponse.setStatus(HttpServletResponse.SC_REQUEST_TIMEOUT);
                try {
                    httpResponse.getWriter().write("Request timed out");
                    asyncContext.complete();
                    asyncContext.getResponse().flushBuffer();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, TIMEOUT_THRESHOLD_MS, TimeUnit.MILLISECONDS);

        // Execute the filter chain
        executorService.submit(() -> {
            try {
                chain.doFilter(request, response); // Continue the filter chain
                asyncContext.complete();
            } catch (IOException | ServletException e) {
                // Handle exceptions
                asyncContext.complete();
            }
        });
    }

    @Override
    public void destroy() {
        // Shutdown the executor service
        executorService.shutdown();
    }
}
