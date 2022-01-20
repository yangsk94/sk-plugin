package com.sk

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory


/**
 * Created by wangkai on 2021/12/22 16:31

 * Desc build.gradle配置Pgyer和飞书的配置的参数
 *
 *  BuildParams {
 *      pgyer {
 *
 *      }
 *
 *      feishu {
 *
 *      }
 *  }
 */
open class BuildParams constructor(objectFactory: ObjectFactory) {

    var pgyer: Pgyer? = Pgyer()
    var feishu: Feishu? = Feishu()

    var ding: Ding? = Ding()

    init {
        pgyer = objectFactory.newInstance(Pgyer::class.java)
        feishu = objectFactory.newInstance(Feishu::class.java)
        ding = objectFactory.newInstance(Ding::class.java)
    }

    fun pgyer(action: Action<Pgyer>) {
        action.execute(pgyer)
    }

    fun feishu(action: Action<Feishu>) {
        action.execute(feishu)
    }

    open class Pgyer {
        var _api_key: String = ""
        var appKey: String = ""
        var userKey: String = ""
    }

    open class Feishu {
        var hookUrl: String = ""
    }

    open class Ding {
        //https://oapi.dingtalk.com/robot/send?access_token=b84b296e594428164fe1916340d59c2d12754af27ead416c4f858d28da5d3d91
        var accessToken: String = ""
    }

    open class DingLinkReq(
        var text: String = "",
        var title: String = "",
        var picUrl: String = "",
        var messageUrl: String = "",
        var content: String = "测试",
    ) {

        companion object {
            fun getLink(
                buildCreated: String,
                buildVersion: String,
                buildQRCodeURL: String,
                buildShortcutUrl: String
            ): DingLinkReq {
                return DingLinkReq(buildCreated, buildVersion, buildQRCodeURL, buildShortcutUrl)
            }
        }
    }

}