package com.smartmobilefactory.epubreader.model;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.support.v4.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipInputStream;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

public class Epub {

    private static final String TAG = Epub.class.getSimpleName();

    private File opfPath = null;
    private final File location;
    private final Book book;

    Epub(Book book, File location) {
        this.location = location;
        this.book = book;
    }

    public Book getBook() {
        return book;
    }

    /**
     * @return -1 if toc position not found
     */
    public int getSpinePositionForTocReference(TOCReference tocReference) {
        List<SpineReference> spineReferences = getBook().getSpine().getSpineReferences();
        for (int i = 0; i < spineReferences.size(); i++) {
            SpineReference spineReference = spineReferences.get(i);
            if (tocReference.getResourceId().equals(spineReference.getResourceId())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @return -1 if spine position not found
     */
    public int getTocPositionForSpineReference(SpineReference spineReference) {
        List<SpineReference> spineReferences = getBook().getSpine().getSpineReferences();
        for (int i = 0; i < spineReferences.size(); i++) {
            SpineReference spineReference2 = spineReferences.get(i);
            if (spineReference2.getResourceId().equals(spineReference.getResourceId())) {
                return getTocPositionForSpinePosition(i);
            }
        }
        return -1;
    }

    /**
     * @return -1 if spine position not found
     */
    public int getTocPositionForSpinePosition(int spinePosition) {
        List<TOCReference> tocReferences = getBook().getTableOfContents().getTocReferences();
        for (int i = 0; i < tocReferences.size(); i++) {
            TOCReference tocReference = tocReferences.get(i);
            int spinePositionForTocReference = getSpinePositionForTocReference(tocReference);
            if (spinePositionForTocReference == spinePosition) {
                return i;
            }
            if (spinePositionForTocReference > spinePosition) {
                return i - 1;
            }
        }
        return -1;
    }

    public File getOpfPath() {
        if (opfPath != null) {
            return opfPath;
        }
        opfPath = EpubStorageHelper.getOpfPath(this);
        return opfPath;
    }

    public File getLocation() {
        return location;
    }

    /**
     * @param context
     * @param uri     locale file uri. Asset uri is allowed
     * @throws IOException
     */
    @WorkerThread
    public static Epub fromUri(Context context, String uri) throws IOException {
        return EpubStorageHelper.fromUri(context, uri);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epub)) return false;

        Epub epub = (Epub) o;

        if (opfPath != null ? !opfPath.equals(epub.opfPath) : epub.opfPath != null) {
            return false;
        }
        if (location != null ? !location.equals(epub.location) : epub.location != null) {
            return false;
        }
        return book != null ? book.equals(epub.book) : epub.book == null;

    }

    @Override
    public int hashCode() {
        int result = opfPath != null ? opfPath.hashCode() : 0;
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (book != null ? book.hashCode() : 0);
        return result;
    }

}
