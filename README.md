# DB Preferences

[![GitHub license](https://img.shields.io/badge/license-Apache%20Version%202.0-blue.svg)](https://github.com/sbrukhanda/fragmentviewpager/blob/master/LICENSE.txt)
[![Build Status](https://travis-ci.org/hannesa2/DbPreferences.svg?branch=master)](https://travis-ci.org/hannesa2/DbPreferences)
[![](https://jitpack.io/v/hannesa2/DbPreferences.svg)](https://jitpack.io/#hannesa2/DbPreferences)

An encrypted alternative to SharedPreferences. It's based on secure Room/SQlite and uses sqlcipher

### Usage

##### Create an instance of DbPreferences and use get() or put() methodes on it

You need an enum which implements ConfigKey to access values, eg.

###### Kotlin
```Kotlin
public enum MyConfigKeys implements ConfigKey {
    KEY_OBJECT_A, // your key A
    KEY_STRING_B; // your key B ... and so on
}
```

and init it with your secret
```Kotlin
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        DbPreferences.init(this, "your secret")
    }
    ...
 }
 ```   

###### Java
```java
// get it
TestClass testClass = new DbPreferences().get(MyConfigKeys.KEY_OBJECT_A, TestClass.class);

// or save
new DbPreferences().put(MyConfigKeys.KEY_OBJECT_A, testClassToStore);
```
###### Kotlin
```kotlin
// get
val testClass = DbPreferences().get<TestClass>(MyConfigKeys.KEY_OBJECT_A, TestClass::class.java)
//or save
DbPreferences().put(MyConfigKeys.KEY_OBJECT_A, testClassToStore)
```

Handle a list
###### Kotlin
```kotlin
//save
DbPreferences().put(MyConfigKeys.KEY_LIST, listSource)

// get
val listType = object : TypeToken<ArrayList<TestClass>>() {
}.type
var myList: ArrayList<TestClass>? = DbPreferences().get(MyConfigKeys.KEY_LIST, listType)
```

That's it !

## Download 
Repository available on https://jitpack.io/#hannesa2/DBpreferences

```Gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
```Gradle
dependencies {
    implementation 'com.github.hannesa2:DbPreferences:$latestVersion'
}

```

### Performance

On a Samsung S4 mini 
* a read needs ~1 ms
* a write needs ~  3 ms

## License 
```
Copyright 2018 DBPreferences Autors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```


