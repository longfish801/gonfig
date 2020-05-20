/*
 * Gonfig.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gonfig

/**
 * 設定値を保持するための特性です。<br/>
 * 設定値はリソース上の設定ファイルから参照し、
 * {@link groovy.util.ConfigObject}として保持します。<br/>
 * 設定ファイルは {@link groovy.util.ConfigSlurper}で
 * 解析できる形式で記述してください。<br/>
 * リソースの取得には {@link GropedResource}を用います。<br/>
 * 基底ディレクトリは設定しないため、デフォルト値である
 * カレントディレクトリとなります。<br/>
 * 設定ファイルを参照するためのリソース名は、デフォルトでは
 * クラスの単純名をすべて小文字に変換した文字列と
 * 拡張子(.groovy)を連結した文字列を用います。<br/>
 * {@link #getResourceFileName()}メソッド、
 * {@link #getResourceFileExtension()}メソッドを
 * オーバーライドすることでリソース名を変えることができます。<br/>
 * 本特性の実装時は {@link GropedResource#getClazz()}の
 * オーバーライドが必要なことに注意してください。
 * @version 0.1.02 2020/05/20
 * @author io.github.longfish801
 */
trait Gonfig extends GropedResource {
	/** リソースバンドル */
	static final ResourceBundle RSRC = ResourceBundle.getBundle(Gonfig.class.canonicalName)
	/** 設定値 */
	static final ConfigObject GONFIG = configObject("${resourceFileName}${resourceFileExtension}")
	
	/**
	 * キーに紐づく設定値を参照します。
	 * @param key キー
	 * @return キーに紐づく設定値
	 */
	static def $static_propertyMissing(String key) {
		return GONFIG.getAt(key)
	}
	
	/**
	 * 設定ファイル名を参照します。<br/>
	 * クラスの単純名をすべて小文字に変換した文字列を返します。<br/>
	 * 本メソッドをオーバーライドすることで設定ファイル名を変えることができます。
	 * @return 設定ファイル名
	 */
	static String getResourceFileName() {
		return clazz.simpleName.toLowerCase()
	}
	
	/**
	 * 設定ファイルの拡張子を参照します。<br/>
	 * 固定文字列".groovy"を返します。<br/>
	 * 本メソッドをオーバーライドすることで拡張子を変えることができます。
	 * @return 設定ファイルの拡張子
	 */
	static String getResourceFileExtension() {
		return RSRC.getString('CONFIG_FILE_EXTENSION')
	}
}
