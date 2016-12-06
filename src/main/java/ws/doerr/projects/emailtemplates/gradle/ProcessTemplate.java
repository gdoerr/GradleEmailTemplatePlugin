/*
 * The MIT License
 *
 * Copyright 2016 Greg Doerr.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ws.doerr.projects.emailtemplates.gradle;

import java.nio.file.Paths;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.util.CollectionUtils;
import ws.doerr.projects.emailtemplates.TemplateProcessor;

/**
 *
 * @author Greg Doerr
 */
public class ProcessTemplate extends DefaultTask {
    private static final Logger LOG = Logger.getLogger(ProcessTemplate.class.getName());

    private final SimpleDateFormat formatter;

    private @Input Boolean removeComments = true;
    private final List<String> sourcePackages = new ArrayList<>();
    private final Map<String, String> meta = new HashMap<>();

    public ProcessTemplate() {
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    @TaskAction
    public void run() throws Exception {
        TemplateProcessor processor = new TemplateProcessor(meta, removeComments);

        sourcePackages.forEach(sourcePackage -> {
        Set<String> sources = HtmlFileUtilities.getSourcesPaths(getProject().getProjectDir(), sourcePackage);
            for(String source : sources) {
                try {
                    LOG.log(Level.SEVERE, "");
                    processor.process(Paths.get(source), Paths.get(HtmlFileUtilities.getTargetFilename(getProject().getProjectDir(), source)));
                } catch(Exception ex) {
                    LOG.log(Level.WARNING, MessageFormat.format("Exception processing {0}", source), ex);
                    //throw(ex);
                }
            }
        });
    }

    public static void addTask(Project project) {
        Map<String, Object> args = new HashMap<>();
        args.put("type", ProcessTemplate.class);
        args.put("group", "processtemplate");
        args.put("description", "Process HTML Email Templates");

        project.task(args, "emailtemplates");
    }

    public Boolean getRemoveComments() {
        return removeComments;
    }

    public void setRemoveComments(Boolean removeComments) {
        this.removeComments = removeComments;
    }

    public @Input List<String> getSourcePackages() {
        return CollectionUtils.stringize(sourcePackages);
    }

    public void setSourcePackages(String... args) {
        sourcePackages.clear();
        sourcePackages.addAll(Arrays.asList(args));
    }

    public void sourcePackages(String... args) {
        sourcePackages.addAll(Arrays.asList(args));
    }

    @SuppressWarnings("rawtypes")
    public @Input Map getMeta() {
        return Collections.unmodifiableMap(meta);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    void setMeta(Map meta) {
        this.meta.clear();
        this.meta.putAll(meta);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    void meta(Map meta) {
        this.meta.putAll(meta);
    }
}
