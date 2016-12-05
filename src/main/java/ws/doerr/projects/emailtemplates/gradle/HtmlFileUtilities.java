/*
 *  **************************************************************************
 *                     Copyright © 2016-2016 Credico USA, LLC
 *                            All Rights Reserved.
 *      ------------------------------------------------------------------
 *
 *      Credico USA, LLC
 *      525 West Monroe, Suite 900
 *      Chicago, IL 60661
 *      software@credicousa.com
 *
 *      ------------------------------------------------------------------
 *  Copyright © 2014-2016. Credico USA, LLC. All Rights Reserved.
 *  Permission to use, copy, modify, and distribute this software and its
 *  documentation for educational, research, and not-for-profit purposes,
 *  without fee and without a signed licensing agreement, is hereby granted,
 *  provided that this copyright notice, this paragraph and the following two
 *  paragraphs appear in all copies, modifications, and distributions.
 *  Contact Credico USA, LLC. at software@credicousa.com for
 *  commercial licensing opportunities.
 *
 *  IN NO EVENT SHALL CREDICO USA, LLC. BE LIABLE TO ANY PARTY FOR DIRECT,
 *  INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST
 *  PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION,
 *  EVEN IF CREDICO HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  CREDICO USA, LLC. SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *  PARTICULAR PURPOSE. THE SOFTWARE AND ACCOMPANYING DOCUMENTATION, IF ANY,
 *  PROVIDED HEREUNDER IS PROVIDED "AS IS". CREDICO USA, LLC. HAS NO OBLIGATION
 *  TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *  **************************************************************************
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
 * @author Greg Doerr <greg.doerr@credicousa.com>
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
