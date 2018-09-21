package io.sitoolkit.wt.gui.domain.sample;

import java.io.File;

@FunctionalInterface
public interface SampleCreatedCallback {

    void onCreated(File sampleDir);
}
