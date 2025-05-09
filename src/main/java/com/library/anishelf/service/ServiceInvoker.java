package com.library.anishelf.service;

/**
 * The type Service invoker.
 */
public class ServiceInvoker {
    private ServiceHandler serviceHandler;

    /**
     * Sets service handler.
     *
     * @param serviceHandler the service handler
     */
    public void setServiceHandler(ServiceHandler serviceHandler) {
        this.serviceHandler = serviceHandler;
    }

    /**
     * Invoke service boolean.
     *
     * @return the boolean
     */
    public boolean invokeService() {
        if (serviceHandler != null) {
            return serviceHandler.handleRequest();
        }
        return false;
    }
}
