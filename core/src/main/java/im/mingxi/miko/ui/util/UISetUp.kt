package im.mingxi.miko.ui.util

import im.mingxi.miko.hook.BaseComponentHook
import im.mingxi.miko.startup.HookInstaller

/*
 * 真的看的懂吗
 *
 */
object UISetUp {
    class Page(val name: String) {
        val wrappers = ArrayList<Wrapper>()
        val wrapperNames = ArrayList<String>()
    }

    class Wrapper(val name: String) {
        val items = ArrayList<BaseComponentHook>()
    }

    val pages = ArrayList<Page>()

    val pageNames = ArrayList<String>()

    fun setUp() {
        HookInstaller.uiList.forEach {
            with(it) {
                val pageName = uiItemLocation[0]
                val wrapperName = uiItemLocation[1]
                val name = it.name
                if (uiItemLocation.size == 3) {
                    if (!pageNames.contains(pageName)) {
                        val page = Page(pageName)
                        pages.add(page)
                        pageNames.add(pageName)
                    }
                    val pageInst = pages.find { it.name == pageName }!!
                    if (!pageInst.wrapperNames.contains(wrapperName)) {
                        val wrapper = Wrapper(wrapperName)
                        pageInst.wrappers.add(wrapper)
                        pageInst.wrapperNames.add(wrapperName)
                    }
                    val wrapper = pageInst.wrappers.find { it.name == wrapperName }!!
                    wrapper.items.add(this)
                }
            }
        }
    }
}