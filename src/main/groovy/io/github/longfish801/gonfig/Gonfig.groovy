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
 * リソース名はクラスの単純名をすべて小文字に変換した
 * 文字列と拡張子(.groovy)を連結したものとします。<br/>
 * 本特性の実装時は {@link GropedResource#getClazz()}の
 * オーバーライドが必要なことに注意してください。
 * @version 1.0.00 2020/04/09
 * @author io.github.longfish801
 */
trait Gonfig extends GropedResource {
	/** リソースバンドル */
	static final ResourceBundle RSRC = ResourceBundle.getBundle(Gonfig.class.canonicalName)
	/** 設定値 */
	static final ConfigObject GONFIG = configObject("${clazz.simpleName.toLowerCase()}${RSRC.getString('CONFIG_FILE_EXTENSION')}")
	
	/**
	 * キーに紐づく設定値を参照します。
	 * @param key キー
	 * @return キーに紐づく設定値
	 */
	static def $static_propertyMissing(String key) {
		return GONFIG.getAt(key)
	}
}
