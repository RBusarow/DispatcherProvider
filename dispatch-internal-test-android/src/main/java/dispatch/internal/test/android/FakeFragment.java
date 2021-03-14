/*
 * Copyright (C) 2021 Rick Busarow
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dispatch.internal.test.android;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.Nullable;

public class FakeFragment extends Fragment {

    LifecycleOwner fragmentLifecycleOwner;
    MutableLiveData<LifecycleOwner> fakeViewLifecycleOwnerLiveData = new MutableLiveData<>(null);
    private LifecycleOwner fakeViewLifecycleOwner = null;

    public FakeFragment(LifecycleOwner fragmentLifecycleOwner) {
        this.fragmentLifecycleOwner = fragmentLifecycleOwner;
    }

    public void setFakeViewLifecycleOwner(@Nullable LifecycleOwner lifecycleOwner) {
        fakeViewLifecycleOwner = lifecycleOwner;
        fakeViewLifecycleOwnerLiveData.postValue(lifecycleOwner);
    }

    @NonNull
    @Override
    public LifecycleOwner getViewLifecycleOwner() {
        return fakeViewLifecycleOwner;
    }

    @NonNull
    @Override
    public LiveData<LifecycleOwner> getViewLifecycleOwnerLiveData() {
        return fakeViewLifecycleOwnerLiveData;
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return fragmentLifecycleOwner.getLifecycle();
    }

}
