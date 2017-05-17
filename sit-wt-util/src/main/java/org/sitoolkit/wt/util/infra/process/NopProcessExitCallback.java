package org.sitoolkit.wt.util.infra.process;

class NopProcessExitCallback implements ProcessExitCallback {

    @Override
    public void callback(int exitCode) {
        // NOP
    }

}
