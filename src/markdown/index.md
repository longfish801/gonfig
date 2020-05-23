# gonfig

[TOC levels=2-6]

## 概要

　設定を静的に保持します。
　groovy.util.ConfigSlurperを java.util.ResourceBundleのように利用できます。

　個人が学習のために開発したものです。
　故障対応や問合せ回答などのサポートはしていません。

## 特徴

* 設定値はファイルシステムあるいはリソースの設定ファイルに記述します。
* 設定内容は [ConfigSlurper](http://docs.groovy-lang.org/latest/html/gapi/groovy/util/ConfigSlurper.html)で解析できる記法で記述します。
* 設定ファイルは static変数の初期化時に読みこみます。
* 設定ファイルは複数の候補に配置できます。
  より優先度の高い場所に新しい設定ファイルを置くことで、古い設定を上書きできます。
* 簡単な国際化に対応しています。
  ファイル名にデフォルトロケールを付与した設定ファイルを優先的に読みこみます。

## サンプルコード

　設定ファイル（src/test/resources/sample/sampleconfig.groovy）です。
　キーと、それに対応する値を記述しています。

```
str = 'これはテストです。'
specialChars = '～①㈱'
number = 11.7
some.list = [ 1, 2, 3 ]
```

　設定を保持するクラス（src/test/groovy/sample/grope/Config.groovy）です。
　Gonfigを実装することで、static変数の初期化時に設定ファイルを読みこみます。
　Gonfigを実装する必要条件として、static変数 clazzに自クラスを格納してください。
　static変数 resourceFileNameに設定ファイル名を指定しています。

```
package sample.grope

import io.github.longfish801.gonfig.Gonfig

class Config implements Gonfig {
	static final Class clazz = Config.class
	static final String resourceFileName = 'sampleconfig'
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

　このサンプルコードは build.gradle内の execSamplesタスクで実行しています。

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

　設定ファイル名のフォーマットはデフォルトで以下のとおりです。
　設定ファイルのファイル名、拡張子をデフォルトから変更したい場合は Gonfig特性の getResourceFileNameメソッド、getResourceFileExtensionメソッドをそれぞれオーバーライド（もしくは static変数 resourceFileName、resourceFileExtensionを定義）してください。

```
${Gonfigを実装したクラスの単純名をすべて小文字に変換した文字列}.groovy
```

　簡単な国際化に対応しています。
　まずはファイル名の末尾に半角アンダーバー(_)＋[デフォルトロケール](https://docs.oracle.com/javase/jp/8/docs/api/java/util/Locale.html#getDefault--)を連結したファイルを探します。
　デフォルトロケールを付与したファイルがみつからない場合は、デフォルトロケールを付与しないファイルを探します。

　Gonfigを実装した、パッケージ sample.gropeに属す Someクラスがあったとします。
　デフォルトロケールが "ja_JP"、カレントフォルダが /foo、クラスパスが /booに通っていたとします。
　まずは以下の箇所から some_ja_JP.groovyを探します。次に some.groovyを探します。初めにみつかったファイルを読みこみます。

1. ファイルシステム /foo
2. ファイルシステム /foo/sample.grope
3. ファイルシステム /foo/sample/grope
4. クラスパス /boo
5. クラスパス /boo/sample/grope

## 改版履歴

0.1.01
: build.gradleに fix, masterup, releaseタスクを追加しました。

0.1.02
: 設定ファイルを参照するためのリソース名を改変可能にしました。

0.1.03
: 簡単な国際化に対応しました。

## 改版履歴

0.1.01
: build.gradleに fix, masterup, releaseタスクを追加しました。

0.1.02
: 設定ファイルを参照するためのリソース名を改変可能にしました。

