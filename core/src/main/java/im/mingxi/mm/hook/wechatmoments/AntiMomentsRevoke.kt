package im.mingxi.mm.hook.wechatmoments

import android.content.ContentValues
import android.util.Log
import com.highcapable.kavaref.KavaRef.Companion.resolve
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.dexkit.DexDesc
import im.mingxi.miko.util.dexkit.IFinder
import im.mingxi.miko.util.xpcompat.XPHelpers
import org.luckypray.dexkit.DexKitBridge
import java.lang.reflect.Method


@FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class AntiMomentsRevoke : SwitchHook(), IFinder {
    override val name: String
        get() = "朋友圈防撤回"
    override val uiItemLocation: String
        get() = FuncRouter.EXPLORE

    private val snsSqlHelperRawQuery = DexDesc("$simpleTAG.Method.SnsSqlHelper")
    private val snsSqlHelperUpdate =
        DexDesc("$simpleTAG.Method.SnsSqlHelperUpdate")
    private val snsSqlHelperExecSQL =
        DexDesc("$simpleTAG.Method.SnsSqlHelperExecSQL")
    private val sqlBean =
        DexDesc("$simpleTAG.Method.SqlBean")

    override fun initOnce(): Boolean {
        snsSqlHelperUpdate.toMethod().hookBeforeIfEnable { param ->
            if (param.args[0].toString() == "SnsInfo") {
                val sqlBeanCls = sqlBean.toMethod().declaringClass
                val findMethodExact: Method = sqlBeanCls.resolve().firstMethod {
                    name = "parseFrom"
                }.self.apply { isAccessible = true }
                val findMethodExact2: Method = sqlBeanCls.resolve().firstMethod {
                    name = "toByteArray"
                }.self.apply { isAccessible = true }
                val contentValues = param.args[1] as ContentValues
                if (contentValues.containsKey("sourceType") && contentValues.getAsInteger("sourceType")
                        .toInt() == 0
                ) {
                    contentValues.put("sourceType", 2)
                    val invoke = findMethodExact.invoke(
                        loader.loadClass("com.tencent.mm.protocal.protobuf.TimeLineObject")
                            .newInstance(), contentValues.get("content")
                    )
                    val objectField: Any? = XPHelpers.getObjectField(invoke, "ContentDesc")
                    if ((objectField is String) && !objectField.toString().startsWith("(已删除)")) {
                        XPHelpers.setObjectField(
                            invoke,
                            "ContentDesc",
                            "(已删除)$objectField"
                        )
                    }
                    contentValues.put(
                        "content",
                        findMethodExact2.invoke(invoke) as ByteArray?
                    )
                    return@hookBeforeIfEnable
                }
            }
        }

        snsSqlHelperRawQuery.toMethod().hookBeforeIfEnable { param ->
            try {
                var str = param.args[0] as String
                str = str.replace("(sourceType & 2 != 0 )  AND", "").replace(
                    "(sourceType in (8,264,10,266,12,268,14,270,24,280,26,282,28,284,30,286,72,328,74,330,76,332,78,334,88,344,90,346,92,348,94,350,136,392,138,394,140,396,142,398,152,408,154,410,156,412,158,414,200,456,202,458,204,460,206,462,216,472,218,474,220,476,222,478))",
                    "(sourceType in (0,2,4,6,8,264,10,266,12,268,14,270,24,280,26,282,28,284,30,286,72,328,74,330,76,332,78,334,88,344,90,346,92,348,94,350,136,392,138,394,140,396,142,398,152,408,154,410,156,412,158,414,200,456,202,458,204,460,206,462,216,472,218,474,220,476,222,478))"
                )
                if (str.contains("WHERE SnsInfo.userName=")) {
                    str = str.replace("(snsId >=", "(1=1 or snsId >=")
                }

                if ((str.contains("from SnsInfo  where") || str.contains(
                        "FROM SnsInfo WHERE"
                    ))
                ) {
                    val sb = StringBuilder("where username not in(wxid_99999999999922")


                    sb.append(") AND ")
                    if (str.contains("where")) {
                        str = str.replace("where", sb.toString())
                    } else if (str.contains("WHERE")) {
                        str = str.replace("WHERE", sb.toString())
                    }
                }
                param.args[0] = str
            } catch (e: Exception) {
                Log.getStackTraceString(e).d()
            }
        }
        snsSqlHelperExecSQL.toMethod().hookBeforeIfEnable { param ->
            if (param.args[0].toString() == ("SnsInfo") && param.args[1].toString()
                    .contains("UPDATE SnsInfo SET sourceType = sourceType & -3 where")
            ) {
                param.resultTrue()
            }
        }
        return true
    }

    override fun dexFind(finder: DexKitBridge) {
        with(finder) {
            snsSqlHelperRawQuery.findDexMethod(finder) {
                searchPackages("com.tencent.mm.plugin.sns.storage")
                matcher {
                    usingEqStrings("com.tencent.mm.plugin.sns.storage.SnsSqliteDB", "rawQuery")
                }
            }
            snsSqlHelperUpdate.findDexMethod(finder) {
                searchPackages("com.tencent.mm.plugin.sns.storage")
                matcher {
                    usingStrings("com.tencent.mm.plugin.sns.storage.SnsSqliteDB", "update")
                }
            }
            snsSqlHelperExecSQL.findDexMethod(finder) {
                searchPackages("com.tencent.mm.plugin.sns.storage")
                matcher {
                    usingStrings("com.tencent.mm.plugin.sns.storage.SnsSqliteDB", "execSQL")
                }

            }
            sqlBean.findDexMethod(finder) {
                searchPackages("com.tencent.mm.protobuf")
                matcher {

                    name = "getData"
                    declaredClass {
                        fields {
                            add {
                                name = "OPCODE_PARSEFROM"
                            }
                            add {
                                name = "OPCODE_COMPUTESIZE"
                            }
                        }
                    }
                }
            }
        }


    }
}