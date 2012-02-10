/*
 * #%L
 * debox-photos
 * %%
 * Copyright (C) 2012 Debox
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.debox.photo.job;

import org.debox.photo.util.DirectoryCleaner;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import org.debox.photo.dao.AlbumDao;
import org.debox.photo.dao.PhotoDao;
import org.debox.photo.model.Album;
import org.debox.photo.model.Photo;
import org.debox.photo.util.ImageUtils;
import org.debox.photo.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class SyncJob implements FileVisitor<Path> {

    private static final Logger logger = LoggerFactory.getLogger(SyncJob.class);
    protected Path source;
    protected Path target;
    protected boolean aborted = false;
    /**
     * Default permissions for created directories and files, corresponding with 755 digit value.
     */
    protected static Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxr-xr-x");
    protected FileAttribute<Set<PosixFilePermission>> permissionsAttributes = PosixFilePermissions.asFileAttribute(permissions);
    
    protected static Map<Path, Path> paths = new HashMap<>();
    protected static Map<Path, Integer> photosCount = new HashMap<>();
    
    protected static PhotoDao photoDao = new PhotoDao();
    protected static AlbumDao albumDao = new AlbumDao();
    
    protected ExecutorService threadPool = Executors.newFixedThreadPool(Math.max(Runtime.getRuntime().availableProcessors() - 1, 1));
    
    protected List<Future> imageProcesses = new ArrayList<>();

    public SyncJob(Path source, Path target) {
        this.source = source;
        this.target = target;
    }

    public Path getSource() {
        return source;
    }

    public Path getTarget() {
        return target;
    }

    public void setSource(Path source) {
        this.source = source;
    }

    public void setTarget(Path target) {
        this.target = target;
    }
    
    public boolean isTerminated() {
        for (Future future : imageProcesses) {
            if (!future.isDone()) {
                return false;
            }
        }
        return true;
    }

    public long getTerminatedProcessesCount() {
        long result = 0;
        for (Future future : imageProcesses) {
            if (future.isDone()) {
                result++;
            }
        }
        return result;
    }

    public void process() throws IOException {
        threadPool = Executors.newFixedThreadPool(Math.max(Runtime.getRuntime().availableProcessors() - 1, 1));
        
        // Cleaning target path
        Files.walkFileTree(target, new DirectoryCleaner(target));

        // Create target if not exists
        if (!Files.exists(target)) {
            Files.createDirectory(target, permissionsAttributes);
        }

        // Launch sync
        Files.walkFileTree(source, this);
    }

    public void abort() throws IOException {
        aborted = true;
        threadPool.shutdownNow();
        imageProcesses = new ArrayList<>();
        Files.walkFileTree(target, new DirectoryCleaner(target));
        aborted = false;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attributes) throws IOException {
        logger.info("Begin directory visit: {}", path);

        if (aborted) {
            return FileVisitResult.TERMINATE;
        }

        if (!path.equals(this.source)) {
            try {
                Album album = albumDao.getAlbumBySourcePath(path.toString());
                if (album == null) {
                    album = new Album();
                    album.setId(StringUtils.randomUUID());
                    album.setName(path.getFileName().toString());
                    album.setSourcePath(path.toString());
                    album.setVisibility(Album.Visibility.PUBLIC);
                    
                    Album parent = albumDao.getAlbumBySourcePath(path.getParent().toString());
                    if (parent != null) {
                        album.setParentId(parent.getId());
                        album.setTargetPath(parent.getTargetPath() + File.separatorChar + album.getId());
                    } else {
                        album.setTargetPath(target.toString() + File.separatorChar + album.getId());
                    }
                    
                    
                    albumDao.save(album);
                }

                photosCount.put(path, 0);
                
                Path targetPathParent;
                if (path.getParent().equals(this.source)) {
                    targetPathParent = target;
                } else {
                    targetPathParent = paths.get(path.getParent());
                }

                Path targetPath = Paths.get(targetPathParent.toString(), album.getId());
                paths.put(path, targetPath);

                Files.createDirectory(targetPath, permissionsAttributes);

            } catch (SQLException ex) {
                logger.error("Unable to access database", ex);
                return FileVisitResult.TERMINATE;
            }
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) throws IOException {
        logger.info("File visited: {}", path);

        if (aborted) {
            return FileVisitResult.TERMINATE;
        }

        final String mimeType = Files.probeContentType(path);
        if (!ImageUtils.JPEG_MIME_TYPE.equals(mimeType)) {
            logger.warn("Tried to process a non-jpeg image, mime-type was: {}", mimeType);
            return FileVisitResult.CONTINUE;
        }

        Path targetAlbumPath = paths.get(path.getParent());
        String albumId = targetAlbumPath.getFileName().toString();

        try {
            Photo photo = photoDao.getPhotoBySourcePath(path.toString());
            if (photo == null) {
                photo = new Photo();
                photo.setId(StringUtils.randomUUID());
                photo.setName(path.getFileName().toString());
                photo.setAlbumId(albumId);
                photo.setSourcePath(path.toString());
                photo.setTargetPath(targetAlbumPath.toString());
                photoDao.save(photo);
                
            }
            for (Path directory : photosCount.keySet()) {
                if (path.startsWith(directory)) {
                    photosCount.put(directory, photosCount.get(directory) + 1);
                }
            }
            Future future = threadPool.submit(new ImageProcessor(path.toFile(), targetAlbumPath.toString(), photo.getId()));
            imageProcesses.add(future);
            
        } catch (SQLException ex) {
            logger.error("Unable to save photo in database", ex);
            return FileVisitResult.TERMINATE;
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path path, IOException exception) throws IOException {
        logger.error("Error visiting file " + path.toString(), exception);
        if (aborted) {
            return FileVisitResult.TERMINATE;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path path, IOException exception) throws IOException {
        logger.info("End directory visit: {}", path);
        if (aborted) {
            return FileVisitResult.TERMINATE;
        }
        try {
            Album album = albumDao.getAlbumBySourcePath(path.toFile().getAbsolutePath());
            Integer count = photosCount.get(path);
            if (count == null || album == null) {
                logger.error("Unable to set size, one of count ({}) or album ({}) is null for path " + path.toString(), count, album);
            } else {
                album.setPhotosCount(count);
                albumDao.save(album);
            }
            
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
        
        return FileVisitResult.CONTINUE;
    }
}
