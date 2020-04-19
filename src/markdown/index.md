# gonfig

[TOC levels=2-6]

## 概要

　設定を静的に保持します。

　個人が学習のために開発したものです。
　故障対応や問合せ回答などのサポートはしていません。

## 特徴

* 設定値はファイルシステムあるいはリソースの設定ファイルに記述します。
* 設定内容は [ConfigSlurper](http://docs.groovy-lang.org/latest/html/gapi/groovy/util/ConfigSlurper.html)で解析できる記法で記述します。
* 設定ファイルは static変数の初期化時に読みこみます。
* 設定ファイルは複数の候補に配置できます。
  より優先度の高い場所に新しい設定ファイルを置くことで、古い設定を上書きできます。

## サンプルコード

　設定ファイル（src/test/resources/sample/config.groovy）です。
　キーと、それに対応する値を記述しています。

```
str = 'これはテストです。'
specialChars = '～①㈱'
number = 11.7
some.list = [ 1, 2, 3 ]
```

　設定を保持するクラス（src/test/groovy/sample/grope/Config.groovy）です。
　Gonfigを実装することで、static変数の初期化時に設定ファイルを読みこみます。
　getClazzメソッドをオーバーライドする必要があることに留意してください。

```
package sample.grope

import io.github.longfish801.gonfig.Gonfig

class Config implements Gonfig {
	static Class getClazz(){
		return Config.class
	}
}
```

　設定を参照するクラス（src/test/groovy/sample/Sample.groovy）です。
　import文で asキーワードを用いています。"config.${キー}"と記述するだけで値を参照できます。

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

　このサンプルコードは build.gradle内の execSampleタスクで実行しています。

## GitHubリポジトリ

* [gonfig](https://github.com/longfish801/gonfig)

## Groovydoc

* [Groovydoc](groovydoc/)

## Mavenリポジトリ

　本ライブラリの JARファイルを [GitHub上の Mavenリポジトリ](https://github.com/longfish801/maven)で公開しています。
　build.gradleの記述例を以下に示します。

```
repositories {
	mavenCentral()
	maven { url 'https://longfish801.github.io/maven/' }
}

dependencies {
	implementation group: 'io.github.longfish801', name: 'gonfig', version: '0.1.00';
}
```

## 設定ファイル

　設定ファイル名のフォーマットは以下のとおりです。
　Gonfigを実装したクラス名が Someならば、設定ファイル名は some.groovyとなります。

```
${Gonfigを実装したクラスの名前をすべて小文字にした文字列}.groovy
```

　Someクラスのパッケージが sample.gropeだったとします。
　カレントフォルダが /foo、クラスパスが /booに通っていたとします。
　以下の順番で some.groovyを探します。初めにみつかったファイルを読みこみます。

1. ファイルシステム /foo/some.groovy
2. ファイルシステム /foo/sample.grope/some.groovy
3. ファイルシステム /foo/sample/grope/some.groovy
4. クラスパス /boo/some.groovy
5. クラスパス /boo/sample/grope/some.groovy

