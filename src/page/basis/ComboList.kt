package page.basis

import common.io.assets.Admin
import common.io.assets.Admin.StaticPermitted
import common.io.assets.AssetLoader
import common.io.assets.AssetLoader.AssetHeader
import common.io.assets.AssetLoader.AssetHeader.AssetEntry
import common.io.json.JsonEncoder
import common.io.json.Test
import common.io.json.Test.JsonTest_0.JsonD
import common.io.json.Test.JsonTest_2
import common.pack.Source.AnimLoader
import common.pack.Source.ResourceLocation
import common.pack.Source.SourceAnimLoader
import common.pack.Source.SourceAnimSaver
import common.pack.Source.Workspace
import common.pack.Source.ZipSource
import common.util.stage.EStage
import common.util.stage.StageMap
import common.util.stage.StageMap.StageMapInfo
import common.util.unit.Combo
import common.util.unit.UnitLevel
import io.BCPlayer
import page.JL
import page.anim.AnimBox
import page.support.ListJtfPolicy
import page.support.SortTable
import page.view.ViewBox
import page.view.ViewBox.Conf
import page.view.ViewBox.Controller
import page.view.ViewBox.VBExporter
import utilpc.Interpret
import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JLabel
import javax.swing.JList

internal class ComboList : JList<Combo?>() {
    protected fun setList(lf: List<Combo?>) {
        setListData(lf.toTypedArray())
    }

    companion object {
        private const val serialVersionUID = 1L
    }

    init {
        setCellRenderer(object : DefaultListCellRenderer() {
            private static
            val serialVersionUID = 1L
            override fun getListCellRendererComponent(l: JList<*>?, o: Any?, ind: Int, s: Boolean, f: Boolean): Component {
                val c: Combo? = o as Combo?
                val jl = super.getListCellRendererComponent(l, o, ind, s, f) as JLabel
                jl.text = Interpret.comboInfo(c)
                return jl
            }
        })
    }
}
