/*
 * GonfigSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io.github.longfish801.gonfig

import io.github.longfish801.gonfig.Config as config
import io.github.longfish801.gonfig.ConfigOtherName as configOtherName
import io.github.longfish801.gonfig.ConfigOtherExtension as configOtherExtension
import spock.lang.Specification

/**
 * Gonfigクラスのテスト。
 * @author io.github.longfish801
 */
class GonfigSpec extends Specification {
	def '$static_propertyMissing'(){
		expect: 'キーに紐づく設定値を参照します'
		config.str == 'これはテストです。'
		config.specialChars == '～①㈱'
		config.number == 11.7
		config.some.list == [ 1, 2, 3 ]
	}
	
	def 'getResourceFileName'(){
		expect: '設定ファイル名を変えることができます'
		configOtherName.str == 'これはテストです。'
		configOtherName.specialChars == '～①㈱'
		configOtherName.number == 11.7
		configOtherName.some.list == [ 1, 2, 3 ]
	}
	
	def 'getResourceFileExtension'(){
		expect: '拡張子を変えることができます'
		configOtherExtension.str == 'これはテストです。'
		configOtherExtension.specialChars == '～①㈱'
		configOtherExtension.number == 11.7
		configOtherExtension.some.list == [ 1, 2, 3 ]
	}
}
