# gonfig

## Overview

Keep static settings.  
You can use groovy.util.ConfigSlurper as if it were java.util.ResourceBundle.

This is individual development, for self-learning.  
No support such as troubleshooting, answering inquiries, and so on.

## Features

* Settings are written in setting file on the file system or resource.
* Settings are described in a notation that can be analyzed by [ConfigSlurper](http://docs.groovy-lang.org/latest/html/gapi/groovy/util/ConfigSlurper.html).
* Setting file is loaded when a static variable is initialized.
* Setting file can be placed in multiple candidates.
  You can override old settings, if new setting file is placed in a higher priority location.
* Simple internationalization is supported.
  Setting file with default locale in the file name will be read first.

## Sample Code

This is setting file (src/test/resources/sample/sampleconfig.groovy).
Key, and its corresponding value.

```
str = 'これはテストです。'
specialChars = '～①㈱'
number = 11.7
some.list = [ 1, 2, 3 ]
```

The class that holds the settings (src/test/groovy/sample/grope/Config.groovy).
If you implement Gonfig, setting file is loaded when initializing a static variable.
As a prerequisite for implementing Gonfig, you must store class in the static variable clazz.
The static variable resourceFileName specifies the name of setting file.

```
package sample.grope

import io.github.longfish801.gonfig.Gonfig

class Config implements Gonfig {
	static final Class clazz = Config.class
	static final String resourceFileName = 'sampleconfig'
}
```

This class refers to settings (src/test/groovy/sample/Sample.groovy).
As keyword is used at import statement. If you write "config.${key}", you can refer to the value.

```
package sample

import sample.grope.Config as config

class Sample {
	static void main(String[] args){
		config.str == 'これはテストです。'
		config.specialChars == '～①㈱'
		config.number == 11.7
		config.some.list == [ 1, 2, 3 ]
	}
}
```

This sample code is executed in the execSamples task, see build.gradle.

## Next Step

Please see the [documentation](https://longfish801.github.io/maven/gonfig/) for more detail.

