/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gwtproject.j2cl.mojo;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "clean")
public class CleanMojo extends AbstractJ2CLMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            getLog().info("CleanMojo up J2CL-specific temp files");
            final Map<String, File> workingDirs = getWorkingDirs();
            for (File file : workingDirs.values()) {
                getLog().info("Deleting if exists: " + file.getPath());
                if (file.exists()) {
                    recursivelyDeleteDir(file.toPath());
                    getLog().warn("Failed to delete " + file.getPath());
                }
            }
        } catch (Throwable t) {
            throw new MojoFailureException(t.getMessage());
        }
    }

    private void recursivelyDeleteDir(Path path) throws IOException {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    recursivelyDeleteDir(entry);
                }
            }
        }
        Files.delete(path);
    }
}
