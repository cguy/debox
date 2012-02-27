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

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermissions;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.commons.io.FileUtils;
import org.debox.photo.dao.AlbumDao;
import org.debox.photo.dao.PhotoDao;
import org.debox.photo.model.Album;
import org.debox.photo.model.Photo;
import org.debox.photo.model.SynchronizationMode;
import org.debox.photo.model.ThumbnailSize;
import org.debox.photo.util.ImageUtils;
import org.debox.photo.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class SyncJob implements FileVisitor<Path> {

    private static final Logger logger = LoggerFactory.getLogger(SyncJob.class);
    /**
     * Default permissions for created directories and files, corresponding with 755 digit value.
     */
    protected static FileAttribute permissionsAttributes = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxrwx---"));
    protected static PhotoDao photoDao = new PhotoDao();
    protected static AlbumDao albumDao = new AlbumDao();
    
    protected Path source;
    protected Path target;
    
    protected Map<Album, Boolean> albums = new HashMap<>();
    protected Map<Photo, Boolean> photos = new HashMap<>();
    
    protected SynchronizationMode mode;
    protected boolean aborted = false;
    
    protected ExecutorService threadPool = Executors.newFixedThreadPool(Math.max(Runtime.getRuntime().availableProcessors() - 1, 1));
    protected List<Future> imageProcesses = new ArrayList<>();

    public SyncJob(Path source, Path target, SynchronizationMode mode) {
        this.source = source;
        this.target = target;
        this.mode = mode;
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
    
    public void setMode(SynchronizationMode mode) {
        this.mode = mode;
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
    
    public long getPhotosToProcess() {
        return imageProcesses.size();
    }
    
    public void process() throws IOException, SQLException {
        threadPool = Executors.newFixedThreadPool(Math.max(Runtime.getRuntime().availableProcessors() - 1, 1));

        // Cleaning target path
        if (SynchronizationMode.SLOW.equals(this.mode)) {
            FileUtils.deleteDirectory(target.toFile());
        }

        // Create target if not exists
        if (!Files.exists(target)) {
            Files.createDirectories(target, permissionsAttributes);
        }

        // Launch sync
        albums.clear();
        List<Album> existingAlbums = albumDao.getAlbums();
        for (Album existing : existingAlbums) {
            albums.put(existing, Boolean.FALSE);
        }
        
        photos.clear();
        List<Photo> existingPhotos = photoDao.getAll();
        for (Photo existing : existingPhotos) {
            photos.put(existing, Boolean.FALSE);
        }
        
        Files.walkFileTree(source, this);
    }

    public void abort() {
        aborted = true;
        threadPool.shutdownNow();
        imageProcesses.clear();
        albums.clear();
        photos.clear();
        aborted = false;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path currentPath, BasicFileAttributes attributes) throws IOException {
        logger.info("Begin directory visit: {}", currentPath);

        if (aborted) {
            return FileVisitResult.TERMINATE;

        } else if (currentPath.equals(this.source)) {
            return FileVisitResult.CONTINUE;
        }

        // Create album
        Album album = new Album();
        album.setId(StringUtils.randomUUID());
        album.setName(currentPath.getFileName().toString());
        album.setSourcePath(currentPath.toString());
        album.setVisibility(Album.Visibility.PRIVATE);
        album.setDownloadable(false);
        album.setPhotosCount(0);

        // Search for parent & for any already existing
        Album parent = null;
        boolean existing = false;
        for (Album current : albums.keySet()) {
            
            if (current.getSourcePath().equals(currentPath.toString())) {
                album = current;
                existing = true;
            } else if (current.getSourcePath().equals(currentPath.getParent().toString())) {
                parent = current;
                break;
            }
        }
        
        if (existing) {
            album.setPhotosCount(0);
        } else {
            if (parent == null) {
                album.setTargetPath(target.toString() + File.separatorChar + album.getId());
            } else {
                album.setParentId(parent.getId());
                album.setTargetPath(parent.getTargetPath() + File.separatorChar + album.getId());
            }
        }

        albums.put(album, Boolean.TRUE);


        // Create target path if not exists
        Path targetPath = Paths.get(album.getTargetPath());
        if (!Files.exists(targetPath)) {
            Files.createDirectories(targetPath, permissionsAttributes);
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) throws IOException {
        logger.info("File visited: {}", path);

        String mimeType = Files.probeContentType(path);
        if (!ImageUtils.JPEG_MIME_TYPE.equals(mimeType)) {
            logger.warn("Tried to process a non-jpeg image, mime-type was: {}", mimeType);
            return FileVisitResult.CONTINUE;

        } else if (aborted) {
            return FileVisitResult.TERMINATE;
        }

        // Increment photos count for parents, and search first parent
        Album album = null;
        for (Album current : albums.keySet()) {
            if (path.startsWith(current.getSourcePath())) {
                current.setPhotosCount(current.getPhotosCount() + 1);

                if (path.getParent().toString().equals(current.getSourcePath())) {
                    album = current;
                }
            }
        }
        
        Photo photo = new Photo();
        photo.setId(StringUtils.randomUUID());
        photo.setName(path.getFileName().toString());
        photo.setAlbumId(album.getId());
        photo.setSourcePath(path.toString());
        photo.setTargetPath(album.getTargetPath());
   
        for (Photo existing : photos.keySet()) {
            if (existing.equals(photo)) {
                photo.setId(existing.getId());
            }
        }

        photos.put(photo, Boolean.TRUE);
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

        } else if (!path.equals(source)) {
            return FileVisitResult.CONTINUE;
        }

        // End of synchronise, persist data
        try {
            List<Album> albumsToSave = new ArrayList<>();
            for (Entry<Album, Boolean> entry : albums.entrySet()) {
                Album album = entry.getKey();
                if (entry.getValue()) {
                    albumsToSave.add(album);
                } else {
                    FileUtils.deleteDirectory(new File(album.getTargetPath()));
                    albumDao.delete(album);
                }
            }
            albumDao.save(albumsToSave);
            
            List<Photo> existingPhotos = photoDao.getAll();
            for (Entry<Photo, Boolean> entry : photos.entrySet()) {
                Photo photo = entry.getKey();
                if (entry.getValue()) {
                    
                    if (!existingPhotos.contains(photo) && SynchronizationMode.NORMAL.equals(this.mode) || SynchronizationMode.SLOW.equals(this.mode)) {
                        ImageProcessor processor = new ImageProcessor(photo.getSourcePath(), photo.getTargetPath(), photo.getId());
                        Future future = threadPool.submit(processor);
                        imageProcesses.add(future);
                    }
                    
                    photoDao.save(photo);
                } else {
                    Files.deleteIfExists(Paths.get(photo.getTargetPath(), ThumbnailSize.LARGE.getPrefix() + photo.getId() + ".jpg"));
                    Files.deleteIfExists(Paths.get(photo.getTargetPath(), ThumbnailSize.SQUARE.getPrefix() + photo.getId() + ".jpg"));
                    photoDao.delete(photo);
                }
            }

            // Ensure clear
            albums.clear();
            photos.clear();
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
            return FileVisitResult.TERMINATE;
        }

        return FileVisitResult.TERMINATE;
    }
}
