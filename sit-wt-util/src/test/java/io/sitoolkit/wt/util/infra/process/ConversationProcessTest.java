package io.sitoolkit.wt.util.infra.process;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.sitoolkit.wt.util.infra.process.ConversationProcess;
import io.sitoolkit.wt.util.infra.process.ConversationProcessContainer;
import io.sitoolkit.wt.util.infra.process.NopProcessExitCallback;
import io.sitoolkit.wt.util.infra.process.ProcessParams;

public class ConversationProcessTest {

    @Test
    public void testStartAddCallBack() {
        ProcessParams params = new ProcessParams();
        params.setCommand(testCommand());

        TestStdoutListener testListener = new TestStdoutListener();
        params.getStdoutListeners().add(testListener);

        params.getExitClallbacks().add(new NopProcessExitCallback());

        ConversationProcess process = ConversationProcessContainer.create();
        process.start(params);

        assertThat(testListener.getLines().isEmpty(), is(true));
    }

    @Test
    public void testStartWithoutCallBack() {
        ProcessParams params = new ProcessParams();
        params.setCommand(testCommand());

        TestStdoutListener testListener = new TestStdoutListener();
        params.getStdoutListeners().add(testListener);

        ConversationProcess process = ConversationProcessContainer.create();
        process.start(params);

        assertThat(testListener.getLines().isEmpty(), is(true));
    }

    @Test
    public void testStartWithProcessWaitAddCallback() {
        ProcessParams params = new ProcessParams();
        params.setCommand(testCommand());

        TestStdoutListener testListener = new TestStdoutListener();
        params.getStdoutListeners().add(testListener);

        params.getExitClallbacks().add(new NopProcessExitCallback());

        ConversationProcess process = ConversationProcessContainer.create();
        process.startWithProcessWait(params);

        assertLines(testListener.getLines());
    }

    @Test
    public void testStartWithProcessWaitWithoutCallback() {
        ProcessParams params = new ProcessParams();
        params.setCommand(testCommand());

        TestStdoutListener testListener = new TestStdoutListener();
        params.getStdoutListeners().add(testListener);

        ConversationProcess process = ConversationProcessContainer.create();
        process.startWithProcessWait(params);

        assertLines(testListener.getLines());
    }

    private List<String> testCommand() {
        File script = new File(getClass().getResource("testScript.cmd").getPath());

        List<String> command = new ArrayList<>();
        command.add("cmd");
        command.add("/c");
        command.add(script.getAbsolutePath());

        return command;
    }

    private void assertLines(List<String> lines) {
        String currentDir = System.getProperty("user.dir");
        assertThat(lines.get(0), is(""));
        assertThat(lines.get(1), is(currentDir + ">echo test script start "));
        assertThat(lines.get(2), is("test script start"));
        assertThat(lines.get(3), is(""));
        assertThat(lines.get(4), is(currentDir + ">echo test script end "));
        assertThat(lines.get(5), is("test script end"));
    }
}
