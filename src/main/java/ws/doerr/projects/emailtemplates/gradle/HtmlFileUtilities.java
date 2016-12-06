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

import com.google.common.base.Joiner;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Greg Doerr
 */
public class HtmlFileUtilities {
    private static final String SOURCE_PATH = File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator;
    private static final String IGNORE_SOURCE_DIR = "inliner.ignore";
    private static final String TARGET_PATH = File.separator + "build" + File.separator + "resources" + File.separator + "main" + File.separator;

    public static Set<String> getSourcesPaths(File projectRoot, String sourceProject) {
        Set<String> rc = new HashSet<>();

        try {
            String sourceRoot = projectRoot.getAbsolutePath() + SOURCE_PATH + Joiner.on(File.separator).join(sourceProject.split("\\."));
            Files.walkFileTree(Paths.get(sourceRoot), new GetSourcePaths(rc));
        } catch(Exception ex) {}

        return rc;
    }

    public static String getTargetPaths(File projectRoot) {
        return projectRoot.getAbsolutePath() + TARGET_PATH;
    }

    public static String getTargetFilename(File projectRoot, String source) {
        return getTargetPaths(projectRoot) + source.substring(source.lastIndexOf(File.separatorChar) + 1);
    }

    private static class GetSourcePaths implements FileVisitor<Path> {

        Set<String> paths;

        GetSourcePaths(Set<String> paths) {
            this.paths = paths;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            if(Files.exists(dir.resolve(IGNORE_SOURCE_DIR)))
                return FileVisitResult.SKIP_SUBTREE;

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if(file.toString().endsWith(".html")) {
                paths.add(file.toString());
            }

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }

    }
}
