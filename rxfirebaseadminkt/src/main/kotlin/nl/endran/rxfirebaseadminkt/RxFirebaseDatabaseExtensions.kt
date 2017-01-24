/*
 * Copyright 2017 David Hardy
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

package nl.endran.rxfirebaseadminkt

import com.google.firebase.database.*
import nl.endran.rxfirebaseadmin.DataSnapshotMapper
import nl.endran.rxfirebaseadmin.RxFirebaseChildEvent
import nl.endran.rxfirebaseadmin.exceptions.RxFirebaseDataException
import rx.Observable
import rx.functions.Func1
import rx.subscriptions.Subscriptions

fun Query.observeValueEvent(): Observable<DataSnapshot> {
    return Observable.create { subscriber ->
        val valueEventListener = addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (!subscriber.isUnsubscribed) {
                            subscriber.onNext(dataSnapshot)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        if (!subscriber.isUnsubscribed) {
                            subscriber.onError(RxFirebaseDataException(error))
                        }
                    }
                })

        subscriber.add(Subscriptions.create { removeEventListener(valueEventListener) })
    }
}

fun Query.observeSingleValueEvent(): Observable<DataSnapshot> {
    return Observable.create { subscriber ->
        addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!subscriber.isUnsubscribed) {
                    subscriber.onNext(dataSnapshot)
                    subscriber.onCompleted()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if (!subscriber.isUnsubscribed) {
                    subscriber.onError(RxFirebaseDataException(error))
                }
            }
        })
    }
}

fun Query.observeChildEvent(): Observable<RxFirebaseChildEvent<DataSnapshot>> {
    return Observable.create { subscriber ->
        val childEventListener = addChildEventListener(
                object : ChildEventListener {

                    override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String) {
                        if (!subscriber.isUnsubscribed) {
                            subscriber.onNext(
                                    RxFirebaseChildEvent(dataSnapshot.key, dataSnapshot, previousChildName,
                                            RxFirebaseChildEvent.EventType.ADDED))
                        }
                    }

                    override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String) {
                        if (!subscriber.isUnsubscribed) {
                            subscriber.onNext(
                                    RxFirebaseChildEvent(dataSnapshot.key, dataSnapshot, previousChildName,
                                            RxFirebaseChildEvent.EventType.CHANGED))
                        }
                    }

                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                        if (!subscriber.isUnsubscribed) {
                            subscriber.onNext(RxFirebaseChildEvent(dataSnapshot.key, dataSnapshot,
                                    RxFirebaseChildEvent.EventType.REMOVED))
                        }
                    }

                    override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String) {
                        if (!subscriber.isUnsubscribed) {
                            subscriber.onNext(
                                    RxFirebaseChildEvent(dataSnapshot.key, dataSnapshot, previousChildName,
                                            RxFirebaseChildEvent.EventType.MOVED))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        if (!subscriber.isUnsubscribed) {
                            subscriber.onError(RxFirebaseDataException(error))
                        }
                    }
                })

        subscriber.add(Subscriptions.create { removeEventListener(childEventListener) })
    }
}

fun <T> Query.observeValueEvent(clazz: Class<T>): Observable<T> {
    return observeValueEvent(DataSnapshotMapper.of(clazz))
}

fun <T> Query.observeSingleValueEvent(clazz: Class<T>): Observable<T> {
    return observeSingleValueEvent(DataSnapshotMapper.of(clazz))
}

fun <T> Query.observeChildEvent(clazz: Class<T>): Observable<RxFirebaseChildEvent<T>> {
    return observeChildEvent(DataSnapshotMapper.ofChildEvent(clazz))
}

fun <T> Query.observeValueEvent(mapper: Func1<in DataSnapshot, out T>): Observable<T> {
    return observeValueEvent().map(mapper)
}

fun <T> Query.observeSingleValueEvent(
        mapper: Func1<in DataSnapshot, out T>): Observable<T> {
    return observeSingleValueEvent().map(mapper)
}

fun <T> Query.observeChildEvent(
        mapper: Func1<in RxFirebaseChildEvent<DataSnapshot>, out RxFirebaseChildEvent<T>>): Observable<RxFirebaseChildEvent<T>> {
    return observeChildEvent().map(mapper)
}
