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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.*;
import org.apache.commons.lang3.tuple.Pair;
import org.debox.imaging.AlbumDateReader;
import org.debox.imaging.ImageUtils;
import org.debox.imaging.ThumbnailGenerator;
import org.debox.photo.dao.AlbumDao;
import org.debox.photo.dao.PhotoDao;
import org.debox.photo.model.*;
import org.debox.photo.server.ApplicationContext;
import org.debox.photo.util.FileUtils;
import org.debox.photo.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corentin Guy <corentin.guy@debox.fr>
 */
public class SyncJob implements FileVisitor<Path>, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SyncJob.class);
    
    protected static PhotoDao photoDao = new PhotoDao();
    protected static AlbumDao albumDao = new AlbumDao();
    protected AlbumDateReader albumDateReader;
    protected ForkJoinPool forkJoinPool = new ForkJoinPool();
    
    protected Path source;
    protected Path target;
    protected String ownerId;
    
    protected Map<Album, Boolean> albums = new LinkedHashMap<>();
    protected Map<Photo, Boolean> photos = new LinkedHashMap<>();
    
    protected SynchronizationMode mode;
    protected boolean forceCheckDates = false;
    protected boolean aborted = false;
    protected boolean memoryProcessing = false;
    
    protected ExecutorService threadPool = Executors.newFixedThreadPool(Math.max(Runtime.getRuntime().availableProcessors() - 1, 1));
    protected List<Future> imageProcesses = new ArrayList<>();
    protected List<ForkJoinTask> exifReaderProcesses = new ArrayList<>();

    public SyncJob(Path source, Path target, SynchronizationMode mode, boolean forceCheckDates) {
        this.source = source;
        this.target = target;
        this.mode = mode;
        this.forceCheckDates = forceCheckDates;
    }
    
    @Override
    public void run() {
        try {
            // Reset & cleaning somes resources
            memoryProcessing = true;
            threadPool = Executors.newFixedThreadPool(Math.max(Runtime.getRuntime().availableProcessors() - 1, 1));
            forkJoinPool = new ForkJoinPool();
            albumDateReader = null;
            albums.clear();
            photos.clear();
            
            // Get existing photos & albums from DB
            List<Album> existingAlbums = albumDao.getAllAlbums();
            for (Album existing : existingAlbums) {
                albums.put(existing, Boolean.FALSE);
            }

            List<Photo> existingPhotos = photoDao.getAll();
            for (Photo existing : existingPhotos) {
                photos.put(existing, Boolean.FALSE);
            }

            // Cleaning target path
            if (SynchronizationMode.SLOW.equals(this.mode)) {
                FileUtils.deleteDirectory(target.toFile());
            }

            // Create target if not exists
            if (!Files.exists(target)) {
                FileUtils.createDirectories(target);
            }

            // Launch directory scan
            Files.walkFileTree(source, this);
            
        } catch (SQLException | IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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

    public void setForceCheckDates(boolean forceCheckDates) {
        this.forceCheckDates = forceCheckDates;
    }

    public boolean isTerminated() {
        for (Future future : imageProcesses) {
            if (!future.isDone()) {
                return false;
            }
        }
        boolean albumReadTaskDone = albumDateReader == null || (albumDateReader != null && albumDateReader.isTerminated());
        return !memoryProcessing && albumReadTaskDone;
    }
    
    public long getNumberToProcess() {
        long result = getPhotosToProcess();
        if (albumDateReader != null) {
            result += albumDateReader.getNumbertoProcess();
    }
        return result;
    }
    
    public long getNumberProcessed() {
        long result = getTerminatedProcessesCount();
        if (albumDateReader != null) {
            result += albumDateReader.getNumberProcessed();
    }
        return result;
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
    
    public void abort() {
        aborted = true;
        threadPool.shutdownNow();
        forkJoinPool.shutdownNow();
        albumDateReader = null;
        imageProcesses.clear();
        exifReaderProcesses.clear();
        albums.clear();
        photos.clear();
        aborted = false;
        memoryProcessing = false;
        forceCheckDates = false;
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
        album.setRelativePath(StringUtils.substringAfter(currentPath.toString(), this.source.toString()));
        album.setPublic(false);
        album.setDownloadable(false);
        album.setOwnerId(ownerId);

        // Search for parent & for any already existing
        Album parent = null;
        boolean existing = false;
        for (Album current : albums.keySet()) {
            String currentRelativePath = this.source + current.getRelativePath();
            if (currentRelativePath.equals(currentPath.toString())) {
                album = current;
                existing = true;
            } else if (currentRelativePath.equals(currentPath.getParent().toString())) {
                parent = current;
            }
        }
        //in any case, set the photo count to 0
        album.setPhotosCount(0);
        if (!existing && parent != null) {
            album.setParentId(parent.getId());
        }

        albums.put(album, Boolean.TRUE);
        
        // Create target path if not exists
        Path targetPath = Paths.get(this.target + album.getRelativePath());
        if (!Files.exists(targetPath)) {
            FileUtils.createDirectories(targetPath);
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
            String currentPathParent = StringUtils.substringAfter(path.getParent().toString(), this.source.toString());
            if (currentPathParent.equals(current.getRelativePath())) {
                current.setPhotosCount(current.getPhotosCount() + 1);
                album = current;
            }
        }
        
        Photo photo = new Photo();
        photo.setId(StringUtils.randomUUID());
        photo.setFilename(path.getFileName().toString());
        photo.setTitle(path.getFileName().toString());
        photo.setAlbumId(album.getId());
        photo.setRelativePath(album.getRelativePath());

        for (Photo existing : photos.keySet()) {
            if (existing.equals(photo)) {
                photo.setId(existing.getId());
                break;
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

        Configuration configuration = ApplicationContext.getInstance().getConfiguration();
        String sourcePath = configuration.get(Configuration.Key.SOURCE_PATH);
        String targetPath = configuration.get(Configuration.Key.TARGET_PATH);
        
        // End of synchronise, persist data
        try {
            List<Album> albumsToSave = new ArrayList<>();
            for (Entry<Album, Boolean> entry : albums.entrySet()) {
                Album album = entry.getKey();
                if (entry.getValue()) {
                    albumsToSave.add(album);
                } else {
                    String filePath = this.target.toString() + album.getRelativePath();
                    try {
                        FileUtils.deleteDirectory(new File(filePath));
                    } catch (IOException ex) {
                        logger.error("Unable to delete file {}", filePath);
                    }
                    albumDao.delete(album);
                }
            }
            
            List<Photo> existingPhotos = photoDao.getAll();
            List<Photo> photosToSave = new ArrayList<>();
            List<Photo> photosToDelete = new ArrayList<>();
            
            for (Entry<Photo, Boolean> entry : photos.entrySet()) {
                Photo photo = entry.getKey();
                if (entry.getValue()) {
                    
                    if (!existingPhotos.contains(photo) && SynchronizationMode.NORMAL.equals(this.mode) || SynchronizationMode.SLOW.equals(this.mode)) {
                        ThumbnailGenerator processor = new ThumbnailGenerator(sourcePath, photo, targetPath, ThumbnailSize.LARGE, ThumbnailSize.SQUARE);
                        Future future = threadPool.submit(processor);
                        imageProcesses.add(future);
                    }
                    
                    photosToSave.add(photo);
                } else {
                    Files.deleteIfExists(Paths.get(ImageUtils.getTargetPath(targetPath, photo, ThumbnailSize.LARGE)));
                    Files.deleteIfExists(Paths.get(ImageUtils.getTargetPath(targetPath, photo, ThumbnailSize.SQUARE)));
                    photosToDelete.add(photo);
                }
            }
            
            photoDao.delete(photosToDelete);

            albumDateReader = new AlbumDateReader(sourcePath, albumsToSave, photosToSave, forceCheckDates);
            Pair<List<Album>, List<Photo>> modifiedLists = forkJoinPool.invoke(albumDateReader);
            albumDao.save(modifiedLists.getLeft());
            photoDao.save(modifiedLists.getRight());

            // Ensure clear
            albums.clear();
            photos.clear();
            
            memoryProcessing = false;
            
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
            return FileVisitResult.TERMINATE;
        }

        return FileVisitResult.TERMINATE;
    }

}
