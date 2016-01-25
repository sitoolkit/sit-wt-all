package org.sitoolkit.wt.plugin.maven;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "sample")
public class GetSampleMojo extends AbstractMojo {

    @Parameter(defaultValue = "https://raw.githubusercontent.com/sitoolkit/sit-wt-all/master/sit-wt-runtime/")
    private URL baseUrl;

    @Parameter(defaultValue = "${basedir}")
    private File basedir;

    @Parameter(defaultValue = "src/main/webapp/input.html,src/main/webapp/terms.html,src/main/webapp/done.html,src/main/webapp/style.css,testscript/ExcelTestScript.xlsx,seleniumscript/SeleniumIDETestScript.html")
    private String resources;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        for (String res : resources.split(",")) {
            try {
                URL resUrl = new URL(baseUrl + res);
                File resFile = new File(basedir, res);

                getLog().info("downloading " + resUrl);

                FileUtils.copyURLToFile(resUrl, resFile);

                getLog().info("downloaded " + resFile.getAbsolutePath());
            } catch (IOException e) {
                getLog().error(e);
            }
        }
    }

}
