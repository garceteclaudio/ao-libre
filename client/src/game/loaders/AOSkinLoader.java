/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package game.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;
import game.utils.Skins.AOSkin;

public class AOSkinLoader extends AsynchronousAssetLoader<AOSkin, AOSkinLoader.AOSkinParameter> {
    public AOSkinLoader() {
        super(new InternalFileHandleResolver());
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, AOSkinParameter parameter) {
        return null;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, AOSkinParameter parameter) {
    }

    @Override
    public AOSkin loadSync(AssetManager manager, String fileName, FileHandle file, AOSkinParameter parameter) {
        Log.debug("Skins", "Loading skin");
        AOSkin aoSkin = newSkin(file);
        Log.debug("Skins", "Skin loaded");
        return aoSkin;
    }

    private AOSkin newSkin(FileHandle file) {
        return new AOSkin(file);
    }

    static public class AOSkinParameter extends AssetLoaderParameters<AOSkin> {
    }
}
