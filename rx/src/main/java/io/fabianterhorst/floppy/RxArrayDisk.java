package io.fabianterhorst.floppy;

import org.nustaq.serialization.FSTConfiguration;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Cancellable;

/**
 * Created by fabianterhorst on 13.06.17.
 */

public class RxArrayDisk extends ArrayDisk {

    RxArrayDisk(String name, String path, FSTConfiguration config) {
        super(name, path, config);
    }

    public <T> Observable<T> observe(final String key) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(final ObservableEmitter<T> e) throws Exception {
                addOnWriteListener(key, new OnWriteListener<T>() {
                    @Override
                    public void onWrite(T object) {
                        if (!e.isDisposed()) {
                            e.onNext(object);
                        }
                        e.setCancellable(new Cancellable() {
                            @Override
                            public void cancel() throws Exception {
                                removeListener(key);
                            }
                        });
                    }
                });
            }
        });
    }
}
