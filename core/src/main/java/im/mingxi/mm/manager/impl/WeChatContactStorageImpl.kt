package im.mingxi.mm.manager.impl

import android.annotation.SuppressLint
import android.database.Cursor
import im.mingxi.miko.util.Reflex
import im.mingxi.mm.model.Conservation

class WeChatContactStorageImpl {
    companion object {
        lateinit var conversationStorage: Any
        lateinit var sqliteDB: Any
    }

    private fun getSQLRawQueryResult(condition: String): Cursor =
        Reflex.findMethodObj(sqliteDB).setParamsLength(3)
            .setParams(String::class.java, Array<String>::class.java, Int::class.java).get()
            .invoke(sqliteDB, condition, null, 2) as Cursor

    fun getAllFriends(): List<Conservation> =
        getSQLRawQueryResult("SELECT rcontact.username,rcontact.nickname,rcontact.type,rcontact.conRemark,rcontact.quanPin,rcontact.pyInitial,rcontact.conRemarkPYFull,rcontact.conRemarkPYShort FROM rcontact WHERE rcontact.username NOT LIKE 'gh_%' AND rcontact.username NOT LIKE 'fake_%' AND rcontact.username NOT LIKE '%@app' AND rcontact.username NOT LIKE '%@chatroom' AND rcontact.username NOT LIKE '%@fakeuser' AND rcontact.username NOT LIKE '%@qqim' AND rcontact.username NOT IN ('filehelper','weixin', 'qqsafe') AND rcontact.type NOT IN (0, 33) ")
            .toContactList().filter { it.username.isNotEmpty() && it.type == 3 }


    fun getAllGroups(): List<Conservation> =
        getSQLRawQueryResult("SELECT rcontact.username,rcontact.nickname,rcontact.type,rcontact.conRemark,rcontact.quanPin,rcontact.pyInitial,rcontact.conRemarkPYFull,rcontact.conRemarkPYShort FROM rcontact WHERE rcontact.username LIKE '%@chatroom' AND rcontact.type NOT IN (0, 33)")
            .toContactList().filter { it.username.isNotEmpty() && it.type != 8 }

    @SuppressLint("Range")
    fun Cursor.toContactList(): List<Conservation> {
        return mutableListOf<Conservation>().apply {
            if (moveToFirst()) {
                do {
                    add(
                        Conservation(
                            username = getString(getColumnIndex("username")),
                            nickname = getString(getColumnIndex("nickname")),
                            type = getInt(getColumnIndex("type")),
                            conRemark = getString(getColumnIndex("conRemark")),
                            quanPin = getString(getColumnIndex("quanPin")),
                            pyInitial = getString(getColumnIndex("pyInitial")),
                            conRemarkPYFull = getString(getColumnIndex("conRemarkPYFull")),
                            conRemarkPYShort = getString(getColumnIndex("conRemarkPYShort"))
                        )
                    )
                } while (moveToNext())
            }
            close()
        }
    }

}