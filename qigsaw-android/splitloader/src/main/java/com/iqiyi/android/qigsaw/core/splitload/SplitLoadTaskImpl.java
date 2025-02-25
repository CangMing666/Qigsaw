/*
 * MIT License
 *
 * Copyright (c) 2019-present, iQIYI, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.iqiyi.android.qigsaw.core.splitload;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.iqiyi.android.qigsaw.core.splitload.listener.OnSplitLoadListener;

import java.io.File;
import java.util.List;

final class SplitLoadTaskImpl extends SplitLoadTask {

    SplitLoadTaskImpl(@NonNull SplitLoadManager loadManager,
                      @NonNull List<Intent> splitFileIntents,
                      @Nullable OnSplitLoadListener loadListener) {
        super(loadManager, splitFileIntents, loadListener);
    }

    @Override
    SplitLoader createSplitLoader() {
        return new SplitLoaderImpl(appContext);
    }

    @Override
    ClassLoader loadCode(SplitLoader loader,
                         String splitName,
                         List<String> addedDexPaths,
                         File optimizedDirectory,
                         File librarySearchPath,
                         List<String> dependencies) throws SplitLoadException {

        SplitDexClassLoader classLoader = SplitApplicationLoaders.getInstance().getClassLoader(splitName);
        if (classLoader == null) {
            classLoader = loader.loadCode(splitName, addedDexPaths, optimizedDirectory, librarySearchPath, dependencies);
            SplitApplicationLoaders.getInstance().addClassLoader(classLoader);
        }
        return classLoader;
    }

    @Override
    void onSplitActivateFailed(ClassLoader classLoader) {
        if (classLoader instanceof SplitDexClassLoader) {
            SplitApplicationLoaders.getInstance().removeClassLoader((SplitDexClassLoader) classLoader);
        }
    }
}
