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

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.Query
import nl.endran.rxfirebaseadmin.DataSnapshotMapper
import nl.endran.rxfirebaseadmin.RxFirebaseChildEvent
import nl.endran.rxfirebaseadmin.RxFirebaseDatabase
import rx.Observable
import rx.functions.Func1

fun Query.observeValueEvent(): Observable<DataSnapshot> {
    return RxFirebaseDatabase.observeValueEvent(this)
}

fun Query.observeSingleValueEvent(): Observable<DataSnapshot> {
    return RxFirebaseDatabase.observeSingleValueEvent(this)
}

fun Query.observeChildEvent(): Observable<RxFirebaseChildEvent<DataSnapshot>> {
    return RxFirebaseDatabase.observeChildEvent(this)
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
